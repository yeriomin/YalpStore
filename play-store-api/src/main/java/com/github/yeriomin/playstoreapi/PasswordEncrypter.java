package com.github.yeriomin.playstoreapi;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class PasswordEncrypter {

    // The Google public key
    private static final String googleDefaultPublicKey = "AAAAgMom/1a/v0lblO2Ubrt60J2gcuXSljGFQXgcyZWveWLEwo6prwgi3iJIZdodyhKZQrNWp5nKJ3srRXcUW+F1BD3baEVGcmEgqaLZUNBjm057pKRI16kB0YppeGx5qIQ5QjKzsR8ETQbKLNWgRY0QRNVz34kMJR3P/LgHax/6rmf5AAAAAwEAAQ==";

    @SuppressWarnings("static-access")
    public static String encrypt(String login, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        // First of all, let's convert Google login public key from base64
        // to PublicKey, and then calculate SHA-1 of the key:

        // 1. Converting Google login public key from base64 to byte[]
        byte[] binaryKey = Base64.decode(googleDefaultPublicKey, 0);

        // 2. Calculating the first BigInteger
        int i = readInt(binaryKey, 0);
        byte [] half = new byte[i];
        System.arraycopy(binaryKey, 4, half, 0, i);
        BigInteger firstKeyInteger = new BigInteger(1, half);

        // 3. Calculating the second BigInteger
        int j = readInt(binaryKey, i + 4);
        half = new byte[j];
        System.arraycopy(binaryKey, i + 8, half, 0, j);
        BigInteger secondKeyInteger = new BigInteger(1, half);

        // 4. Let's calculate SHA-1 of the public key, and put it to signature[]:
        // signature[0] = 0 (always 0!)
        // signature[1...4] = first 4 bytes of SHA-1 of the public key
        byte[] sha1 = MessageDigest.getInstance("SHA-1").digest(binaryKey);
        byte[] signature = new byte[5];
        signature[0] = 0;
        System.arraycopy(sha1, 0, signature, 1, 4);

        // 5. Use the BigInteger's (see calculations above) to generate
        // a PublicKey object
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(firstKeyInteger, secondKeyInteger));

        // It's time to encrypt our password:
        // 1. Let's create Cipher:
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA1ANDMGF1PADDING");

        // 2. Then concatenate the login and password (use "\u0000" as a separator):
        String combined = login + "\u0000" + password;

        // 3. Then converting the string to bytes
        byte[] plain = combined.getBytes("UTF-8");

        // 4. and encrypt the bytes with the public key:
        cipher.init(cipher.PUBLIC_KEY, publicKey);
        byte[] encrypted = cipher.doFinal(plain);

        // 5. Add the result to a byte array output[] of 133 bytes length:
        // output[0] = 0 (always 0!)
        // output[1...4] = first 4 bytes of SHA-1 of the public key
        // output[5...132] = encrypted login+password ("\u0000" is used as a separator)
        byte[] output = new byte [133];
        System.arraycopy(signature, 0, output, 0, signature.length);
        System.arraycopy(encrypted, 0, output, signature.length, encrypted.length);

        // Done! Just encrypt the result as base64 string and return it
        return Base64.encodeToString(output, Base64.URL_SAFE + Base64.NO_WRAP);
    }

    // Aux. method, it takes 4 bytes from a byte array and turns the bytes to int
    private static int readInt(byte[] arrayOfByte, int start) {
        return 0x0 | (0xFF & arrayOfByte[start]) << 24 | (0xFF & arrayOfByte[(start + 1)]) << 16 | (0xFF & arrayOfByte[(start + 2)]) << 8 | 0xFF & arrayOfByte[(start + 3)];
    }
}
