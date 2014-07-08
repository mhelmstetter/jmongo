package com.mongodb.load;

import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBCollection;
import com.mongodb.transform.Transformer;
import com.mongodb.util.Monitor;

public class Loader {

    protected static final Logger logger = LoggerFactory.getLogger(Loader.class);

    private ThreadPoolExecutor pool = null;

    private LoaderConfig config;

    private Monitor monitor;

    public Loader(LoaderConfig config) {
        this.config = config;
    }

    private void load() throws UnknownHostException {

        if (config.isDropCollection()) {
            DBCollection c = config.getCollection();
            if (logger.isDebugEnabled()) {
                logger.debug("Dropping collection: " + c);
            }
            c.drop();
        }
        
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(config.getQueueSize());
        pool = new ThreadPoolExecutor(config.getThreads(), config.getThreads(), 30, TimeUnit.SECONDS, workQueue);
        pool.prestartAllCoreThreads();

        monitor = new Monitor(config, Thread.currentThread());
        monitor.setPool(pool);
        monitor.start();

        queueDocuments();

        pool.shutdown();

        while (!pool.isTerminated()) {
            Thread.yield();
            try {
                Thread.sleep(config.getThreads() * LoaderConfig.SLEEP_TIME);
            } catch (InterruptedException e) {
                // reset interrupted status
                Thread.interrupted();
            }
        }

        try {
            pool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // reset interrupted status
            Thread.interrupted();
            if (null != monitor && monitor.isAlive()) {
                logger.error("interrupted error", e);
            }
            // harmless - this means the monitor wants to exit
            // if anything went wrong, the monitor will log it
            logger.warn("interrupted while waiting for pool termination");
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // reset interrupted status and ignore
            Thread.interrupted();
        }

    }

    private void halt() {
        if (null != pool) {
            pool.shutdownNow();
        }

        while (null != monitor && monitor.isAlive()) {
            try {
                monitor.halt();
                // wait for monitor to exit
                monitor.join();
            } catch (InterruptedException e) {
                // reset interrupted status and ignore
                Thread.interrupted();
            }
        }

        if (Thread.currentThread().isInterrupted()) {
            logger.debug("resetting thread status");
            Thread.interrupted();
        }
    }

    private void queueDocuments() {
        FileScanner scanner = new FileScanner(config, pool, monitor);
        scanner.scan();

    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java " + Loader.class.getName() + " <loaderPropertiesFile>");
            System.exit(-1);
        }

        LoaderConfig config = null;
        try {
            config = new LoaderConfig(args[0]);
        } catch (Exception e) {
            logger.error("Error loading properties file", e);
            System.exit(-1);
        }
        Loader loader = new Loader(config);

        try {
            loader.load();
        } catch (Exception e) {
            logger.error("Error loading data", e);
        } finally {
            loader.halt();
        }
    }

}
