package com.wildbeeslabs.httpreserver.server;

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
import com.wildbeeslabs.httpreserver.connection.HttpClientConnection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.commons.lang3.StringUtils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Simple HTTP response server abstract implementation
 *
 * @author alexander.rogalskiy
 * @version 1.0
 * @since 2017-12-12
 *
 */
public abstract class HttpReServer {

    /**
     * Default logger instance
     */
    private final Logger LOGGER = LogManager.getLogger(getClass());
    /*
     * Default server port
     */
    public static final int DEFAULT_PORT = 8080;
    /*
     * Default maximum queue length
     */
    private static final int DEFAULT_MAX_QUEUE_LENGTH = 100;

    public void init(final String[] args) {
        getLogger().info(String.format("Initializing reserver instance with args: {%s}", StringUtils.join(args, "|")));
    }

    protected void run(int port) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(port, getMaxQueueLength());
        while (!Thread.currentThread().isInterrupted()) {
            final Socket client = serverSocket.accept();
            handle(new HttpClientConnection(client));
        }
    }

    protected int getMaxQueueLength() {
        return DEFAULT_MAX_QUEUE_LENGTH;
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    protected abstract void handle(final HttpClientConnection clientConnection);
}
