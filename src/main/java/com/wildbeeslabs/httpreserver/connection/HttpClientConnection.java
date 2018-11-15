/*
 * The MIT License
 *
 * Copyright 2018 WildBees Labs.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.wildbeeslabs.httpreserver.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Simple HTTP client connection implementation
 *
 * @author alexander.rogalskiy
 * @version 1.0
 * @since 2017-12-12
 *
 */
public class HttpClientConnection implements Runnable {

    /**
     * Default logger instance
     */
    private static final Logger LOGGER = LogManager.getLogger(HttpClientConnection.class);

    public static final byte[] DEFAULT_SERVER_RESPONSE = ("HTTP/1.1 200 OK\r\n"
            + "Content-length: 2\r\n"
            + "\r\n"
            + "OK").getBytes(StandardCharsets.UTF_8);
    public static final byte[] DEFAULT_SERVICE_UNAVAILABLE = ("HTTP/1.1 5-3 Service unavailable\r\n").getBytes(StandardCharsets.UTF_8);

    private final Socket client;

    public HttpClientConnection(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                final String request = getRequest();
                this.client.getOutputStream().write(DEFAULT_SERVER_RESPONSE);
                this.client.getOutputStream().write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                this.client.getOutputStream().write(request.getBytes(StandardCharsets.UTF_8));
                this.client.getOutputStream().write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ex) {
            try {
                getLogger().error(String.format("ERROR: cannot send response, message={%s}", ex.getMessage()));
                this.client.close();
            } catch (IOException ex1) {
                getLogger().error(String.format("ERROR: cannot close client socket, message: {%s}", ex1.getMessage()));
            }
        }
    }

    private String getRequest() throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        String line, result = "";
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            result += line;
        }
        return result;
    }

    public void serviceUnavailable() {
        try {
            IOUtils.buffer(this.client.getOutputStream()).write(DEFAULT_SERVICE_UNAVAILABLE);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected Logger getLogger() {
        return LOGGER;
    }
}
