package com.github.yeriomin.playstoreapi;

import okhttp3.Headers;
import okhttp3.Request;
import okio.Buffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class MockOkHttpClientAdapter extends OkHttpClientAdapter {

	private List<Request> requests = new ArrayList<Request>();

    @Override
    byte[] request(Request.Builder requestBuilder, Map<String, String> headers) throws IOException {
        byte[] body = new byte[0];
		Request request = requestBuilder.headers(Headers.of(headers)).build();
		this.requests.add(request);
        String fileName = getBodyFileName(request);
        System.out.println("Checking if " + fileName + " exists");
        URL url = getClass().getClassLoader().getResource(fileName);
        if (null != url) {
            String path = java.net.URLDecoder.decode(new File(url.getFile()).getAbsolutePath(), "UTF-8");
            System.out.println("Body FOUND. Reading from disk. " + path);
            body = read(path);
        } else {
            System.out.println("Body NOT found. Making a live request.");
            try {
                body = super.request(requestBuilder, headers);
                write(fileName, body);
            } catch (GooglePlayException e) {
                System.out.println("Error making a live request: " + e.getMessage());
            }
        }
        return body;
    }

	List<Request> getRequests() {
		return this.requests;
	}

    private byte[] read(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Could not read from " + path + ": " + e.getMessage());
        }
        return null;
    }

    static byte[] getBodyBytes(Request request) {
        if (request.body() == null) {
            return null;
        }
        Request copy = request.newBuilder().build();
        Buffer buffer = new Buffer();
        try {
            copy.body().writeTo(buffer);
            return buffer.readByteArray();
        } catch (IOException e) {
            System.out.println("Could not read body");
        }
        return null;
    }

    static Map<String, String> parseQueryString(byte[] query) {
        Map<String, String> vars = new HashMap<String, String>();
        String[] pairs = new String(query).split("\\&");
        for (String pair: pairs) {
            String[] fields = pair.split("=");
            if (fields.length > 0) {
                try {
                    vars.put(URLDecoder.decode(fields[0], "UTF-8"), fields.length == 1 ? null : URLDecoder.decode(fields[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // Unlikely
                }
            }
        }
        return vars;
    }

    private static void write(String path, byte[] body) {
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(path);
            stream.write(body);
            stream.close();
        } catch (IOException e) {
            System.out.println("Could not write to " + path + ": " + e.getMessage());
        }
    }

    private static String getBodyFileName(Request request) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("request");
        fileName.append(request.url().encodedPath().replace("/", "."));
        for (String key: request.url().queryParameterNames()) {
            for (String value: request.url().queryParameterValues(key)) {
                fileName.append(".").append(key).append(".").append(value.replace(":", "."));
            }
        }
        byte[] body = getBodyBytes(request);
        if (null != body) {
            fileName.append(".").append(Arrays.hashCode(body));
        }
        fileName.append(".bin");
        return fileName.toString();
    }
}
