// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.github.yeriomin.yalpstore.delta;

import android.content.Context;

import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.model.App;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * A Java implementation of the "bspatch" algorithm based on the BSD-2 licensed source code
 * available here: https://github.com/mendsley/bsdiff. This implementation supports a maximum file
 * size of 2GB for all binaries involved (old, new and patch binaries).
 *
 * https://github.com/andrewhayden/archive-patcher
 */
public class BSDiff extends Patcher {
    /**
     * Standard header found at the start of every patch.
     */
    private static final String SIGNATURE = "ENDSLEY/BSDIFF43";

    /**
     * Default buffer size is 50 kibibytes, a reasonable tradeoff between size and speed.
     */
    private static final int PATCH_BUFFER_SIZE = 1024 * 50;

    /**
     * Masks the upper bit of a long, used to determine if a long is positive or negative.
     */
    private static final long NEGATIVE_LONG_SIGN_MASK = 1L << 63;

    /**
     * The patch is typically compressed and the input stream is decompressing on-the-fly. A small
     * buffer greatly improves efficiency on complicated patches with lots of short directives.
     */
    private static final int PATCH_STREAM_BUFFER_SIZE = 4 * 1024;

    /**
     * Complicated patches with lots of short directives result in many calls to write small amounts
     * of data. A buffer greatly improves efficiency for these patches.
     */
    private static final int OUTPUT_STREAM_BUFFER_SIZE = 16 * 1024;

    public BSDiff(Context context, App app) {
        super(context, app);
    }

    /**
     * Applies a patch from |patchData| to the data in |oldData|, writing the result to |newData|.
     *
     * @param oldData   data to which the patch should be applied
     * @param newData   stream to write the new artifact to
     * @param patchData stream to read patch instructions from
     * @throws PatchFormatException if the patch stream is invalid
     * @throws IOException          if unable to read or write any of the data
     */
    public static void applyPatch(
        RandomAccessFile oldData, OutputStream newData, InputStream patchData)
        throws PatchFormatException, IOException {
        patchData = new BufferedInputStream(patchData, PATCH_STREAM_BUFFER_SIZE);
        newData = new BufferedOutputStream(newData, OUTPUT_STREAM_BUFFER_SIZE);
        try {
            applyPatchInternal(oldData, newData, patchData);
        } finally {
            newData.flush();
        }
    }

    /**
     * Does the work of the public applyPatch method.
     */
    private static void applyPatchInternal(
        final RandomAccessFile oldData,
        final OutputStream newData,
        final InputStream patchData)
        throws PatchFormatException, IOException {
        final byte[] signatureBuffer = new byte[SIGNATURE.length()];
        try {
            readFully(patchData, signatureBuffer, 0, signatureBuffer.length);
        } catch (IOException e) {
            throw new PatchFormatException("truncated signature");
        }

        String signature = new String(signatureBuffer, 0, signatureBuffer.length, "US-ASCII");
        if (!SIGNATURE.equals(signature)) {
            throw new PatchFormatException("bad signature");
        }

        // Sanity-check: ensure a-priori knowledge matches patch expectations
        final long oldSize = oldData.length();
        if (oldSize > Integer.MAX_VALUE) {
            throw new PatchFormatException("bad oldSize");
        }
        final long newSize = readBsdiffLong(patchData);
        if (newSize < 0 || newSize > Integer.MAX_VALUE) {
            throw new PatchFormatException("bad newSize");
        }

        // These buffers are used for performing transformations and copies. They are not stateful.
        final byte[] buffer1 = new byte[PATCH_BUFFER_SIZE];
        final byte[] buffer2 = new byte[PATCH_BUFFER_SIZE];

        // Offsets into |oldData| and |newData|.
        long oldDataOffset = 0; // strobes |oldData| in order specified by the patch file
        long newDataBytesWritten = 0; // monotonically increases from 0 .. |expectedNewSize|

        while (newDataBytesWritten < newSize) {
            // Read "control data" for the operation. There are three values here:
            // 1. |diffSegmentLength| defines a number of "similar" bytes that can be transformed
            //    from |oldData| to |newData| by applying byte-by-byte addends. The addend bytes are
            //    read from |patchData|. If zero, no "similar" bytes are transformed in this
            //    operation.
            final long diffSegmentLength = readBsdiffLong(patchData);

            // 2. |copySegmentLength| defines a number of identical bytes that can be copied from
            //    |oldData| to |newData|. If zero, no identical bytes are copied in this operation.
            final long copySegmentLength = readBsdiffLong(patchData);

            // 3. |offsetToNextInput| defines a relative offset to the next position in |oldData| to
            //    jump do after the current operation completes. Strangely, this compensates for
            //    |diffSegmentLength| but not for |copySegmentLength|, so |diffSegmentLength| must
            //    be accumulated into |oldDataOffset| while |copySegmentLength| must NOT be.
            final long offsetToNextInput = readBsdiffLong(patchData);

            // Sanity-checks
            if (diffSegmentLength < 0 || diffSegmentLength > Integer.MAX_VALUE) {
                throw new PatchFormatException("bad diffSegmentLength");
            }
            if (copySegmentLength < 0 || copySegmentLength > Integer.MAX_VALUE) {
                throw new PatchFormatException("bad copySegmentLength");
            }
            if (offsetToNextInput < Integer.MIN_VALUE || offsetToNextInput > Integer.MAX_VALUE) {
                throw new PatchFormatException("bad offsetToNextInput");
            }

            final long expectedFinalNewDataBytesWritten =
                newDataBytesWritten + diffSegmentLength + copySegmentLength;
            if (expectedFinalNewDataBytesWritten > newSize) {
                throw new PatchFormatException("expectedFinalNewDataBytesWritten too large");
            }

            final long expectedFinalOldDataOffset = oldDataOffset + diffSegmentLength + offsetToNextInput;
            if (expectedFinalOldDataOffset > oldSize) {
                throw new PatchFormatException("expectedFinalOldDataOffset too large");
            }
            if (expectedFinalOldDataOffset < 0) {
                throw new PatchFormatException("expectedFinalOldDataOffset is negative");
            }

            // At this point everything is known to be sane, and the operations should all succeed.
            oldData.seek(oldDataOffset);
            if (diffSegmentLength > 0) {
                transformBytes((int) diffSegmentLength, patchData, oldData, newData, buffer1, buffer2);
            }
            if (copySegmentLength > 0) {
                pipe(patchData, newData, buffer1, (int) copySegmentLength);
            }
            newDataBytesWritten = expectedFinalNewDataBytesWritten;
            oldDataOffset = expectedFinalOldDataOffset;
        }
    }

    /**
     * Transforms bytes from |oldData| into |newData| by applying byte-for-byte addends from
     * |patchData|. The number of bytes consumed from |oldData| and |patchData|, as well as the
     * number of bytes written to |newData|, is |diffLength|. The contents of the buffers are
     * ignored and overwritten, and no guarantee is made as to their contents when this method
     * returns. This is the core of the bsdiff patching algorithm. |buffer1.length| must equal
     * |buffer2.length|, and |buffer1| and |buffer2| must be distinct objects.
     *
     * @param diffLength the length of the BsDiff entry (how many bytes to read and apply).
     * @param patchData  the input stream from the BsDiff patch containing diff bytes. This stream
     *                   must be positioned so that the first byte read is the first addend to be
     *                   applied to the first byte of data to be read from |oldData|.
     * @param oldData    the old file, for the diff bytes to be applied to. This input source must be
     *                   positioned so that the first byte read is the first byte of data to which the
     *                   first byte of addends from |patchData| should be applied.
     * @param newData    the stream to write the resulting data to.
     * @param buffer1    temporary buffer to use for data transformation; contents are ignored, may be
     *                   overwritten, and are undefined when this method returns.
     * @param buffer2    temporary buffer to use for data transformation; contents are ignored, may be
     *                   overwritten, and are undefined when this method returns.
     */
    // Visible for testing only
    static void transformBytes(
        final int diffLength,
        final InputStream patchData,
        final RandomAccessFile oldData,
        final OutputStream newData,
        final byte[] buffer1,
        final byte[] buffer2)
        throws IOException {
        int numBytesLeft = diffLength;
        while (numBytesLeft > 0) {
            final int numBytesThisRound = Math.min(numBytesLeft, buffer1.length);
            oldData.readFully(buffer1, 0, numBytesThisRound);
            readFully(patchData, buffer2, 0, numBytesThisRound);
            for (int i = 0; i < numBytesThisRound; i++) {
                buffer1[i] += buffer2[i];
            }
            newData.write(buffer1, 0, numBytesThisRound);
            numBytesLeft -= numBytesThisRound;
        }
    }

    /**
     * Reads a long value in little-endian, signed-magnitude format (the format used by the C++
     * bsdiff implementation).
     *
     * @param in the stream to read from
     * @return the long value
     * @throws PatchFormatException if the value is negative zero (unsupported)
     * @throws IOException          if unable to read all 8 bytes from the stream
     */
    // Visible for testing only
    static final long readBsdiffLong(InputStream in) throws PatchFormatException, IOException {
        long result = 0;
        for (int bitshift = 0; bitshift < 64; bitshift += 8) {
            result |= ((long) in.read()) << bitshift;
        }

        if (result == NEGATIVE_LONG_SIGN_MASK) {
            // "Negative zero", which is valid in signed-magnitude format.
            // NB: No sane patch generator should ever produce such a value.
            throw new PatchFormatException("read negative zero");
        }

        if ((result & NEGATIVE_LONG_SIGN_MASK) != 0) {
            result = -(result & ~NEGATIVE_LONG_SIGN_MASK);
        }

        return result;
    }

    /**
     * Read exactly the specified number of bytes into the specified buffer.
     *
     * @param in          the input stream to read from
     * @param destination where to write the bytes to
     * @param startAt     the offset at which to start writing bytes in the destination buffer
     * @param numBytes    the number of bytes to read
     * @throws IOException if reading from the stream fails
     */
    // Visible for testing only
    static void readFully(
        final InputStream in, final byte[] destination, final int startAt, final int numBytes)
        throws IOException {
        int numRead = 0;
        while (numRead < numBytes) {
            int readNow = in.read(destination, startAt + numRead, numBytes - numRead);
            if (readNow == -1) {
                throw new IOException("truncated input stream");
            }
            numRead += readNow;
        }
    }

    /**
     * Use an intermediate buffer to pipe bytes from an InputStream directly to an OutputStream. The
     * buffer's contents may be destroyed by this operation.
     *
     * @param in         the stream to read bytes from.
     * @param out        the stream to write bytes to.
     * @param buffer     the buffer to use for copying bytes; must have length > 0
     * @param copyLength the number of bytes to copy from the input stream to the output stream
     */
    // Visible for testing only
    static void pipe(
        final InputStream in, final OutputStream out, final byte[] buffer, int copyLength)
        throws IOException {
        while (copyLength > 0) {
            int maxCopy = Math.min(buffer.length, copyLength);
            readFully(in, buffer, 0, maxCopy);
            out.write(buffer, 0, maxCopy);
            copyLength -= maxCopy;
        }
    }

    @Override
    protected boolean patchSpecific() throws IOException {
        RandomAccessFile originalApkRaf = null;
        FileOutputStream destinationOutputStream = null;
        FileInputStream patchInputStream = null;
        try {
            originalApkRaf = new RandomAccessFile(originalApk, "r");
            destinationOutputStream = new FileOutputStream(destinationApk);
            patchInputStream = new FileInputStream(patch);
            applyPatch(originalApkRaf, destinationOutputStream, patchInputStream);
            return true;
        } finally {
            Util.closeSilently(originalApkRaf);
            Util.closeSilently(destinationOutputStream);
            Util.closeSilently(patchInputStream);
        }
    }

    public static class PatchFormatException extends IOException {

        /**
         * Constructs a new exception with the specified message.
         * @param message the message
         */
        public PatchFormatException(String message) {
            super(message);
        }
    }
}
