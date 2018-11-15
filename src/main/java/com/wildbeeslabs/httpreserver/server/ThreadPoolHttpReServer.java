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
package com.wildbeeslabs.httpreserver.server;

import com.wildbeeslabs.httpreserver.connection.HttpClientConnection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Thread pool HTTP server implementation
 *
 * @author alexander.rogalskiy
 * @version 1.0
 * @since 2017-12-12
 *
 */
public class ThreadPoolHttpReServer extends HttpReServer {

    /*
     * Default pool size
     */
    private static final int DEFAULT_POOL_SIZE = 100;
    /*
     * Default maximum pool size
     */
    private static final int DEFAULT_MAX_POOL_SIZE = 100;
    /*
     * Default alive timeout
     */
    private static final int DEFAULT_ALIVE_TIMEOUT = 50;
    /*
     * Default alive timeout
     */
    private static final int DEFAULT_QUEUE_SIZE = 1000;

    private final ThreadPoolExecutor executor;

    public ThreadPoolHttpReServer() {
        final BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);
        final RejectedExecutionHandler handler = (Runnable r, ThreadPoolExecutor tpe) -> {
            ((HttpClientConnection) r).serviceUnavailable();
        };
        this.executor = new ThreadPoolExecutor(DEFAULT_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, DEFAULT_ALIVE_TIMEOUT, TimeUnit.MILLISECONDS, workingQueue, handler);
    }

    public static void main(String[] args) throws Exception {
        new ThreadPoolHttpReServer().run(DEFAULT_PORT);
    }

    @Override
    protected void handle(HttpClientConnection clientConnection) {
        new Thread(clientConnection).start();
    }
}
