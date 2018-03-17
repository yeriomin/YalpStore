package in.dragons.galaxy.adapters;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import in.dragons.galaxy.Util;

public class DebugHttpClientAdapter extends NativeHttpClientAdapter {

    static private final String DEBUG_DIRECTORY = "galaxy-store-debug";
    static private File dumpDirectory;

    public DebugHttpClientAdapter() {
        dumpDirectory = new File(Environment.getExternalStorageDirectory(), DEBUG_DIRECTORY);
        dumpDirectory.mkdirs();
    }

    @Override
    protected byte[] request(HttpURLConnection connection, byte[] body, Map<String, String> headers) throws IOException {
        String url = connection.getURL().getPath() + "." + connection.getURL().getQuery();
        write(getFileName(url, true, true), getRequestHeaders(headers).getBytes());
        write(getFileName(url, true, false), body);
        byte[] responseBody;
        IOException exception = null;
        try {
            responseBody = super.request(connection, body, headers);
            write(getFileName(url, false, false), responseBody);
        } catch (IOException e) {
            exception = e;
            responseBody = new byte[0];
        } finally {
            write(getFileName(url, false, true), getResponseHeaders(connection.getHeaderFields()).getBytes());
            if (null != exception) {
                throw exception;
            }
        }
        return responseBody;
    }

    private static String getRequestHeaders(Map<String, String> headers) {
        StringBuilder requestHeaders = new StringBuilder();
        for (String key : headers.keySet()) {
            requestHeaders.append(key).append(": ").append(headers.get(key)).append("\n");
        }
        return requestHeaders.toString();
    }

    private static String getResponseHeaders(Map<String, List<String>> headers) {
        StringBuilder responseHeaders = new StringBuilder();
        if (null == headers) {
            return "";
        }
        for (String key : headers.keySet()) {
            List<String> header = headers.get(key);
            if (null == header || header.isEmpty()) {
                continue;
            }
            if (TextUtils.isEmpty(key)) {
                responseHeaders.append(header.get(0)).append("\n");
            } else {
                for (String duplicate : header) {
                    responseHeaders.append(key).append(": ").append(duplicate).append("\n");
                }
            }
        }
        return responseHeaders.toString();
    }

    private static void write(String fileName, byte[] body) {
        if (null == body) {
            return;
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(dumpDirectory, fileName).getAbsolutePath());
            stream.write(body);
        } catch (IOException e) {
            // Failure to log the request is not very important
        } finally {
            Util.closeSilently(stream);
        }
    }

    private static String getFileName(String url, boolean request, boolean headers) {
        return new StringBuilder()
                .append(System.currentTimeMillis())
                .append(".")
                .append(request ? "request" : "response")
                .append(url.replace("&", ".").replace("=", ".").replace("/", ".").replace(":", "."))
                .append(headers ? ".txt" : ".bin")
                .toString()
                ;
    }
}
