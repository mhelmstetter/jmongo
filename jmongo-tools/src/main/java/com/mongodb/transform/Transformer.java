package com.mongodb.transform;

import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Transformer {

    protected static final Logger logger = LoggerFactory.getLogger(Transformer.class);

    private TransformerConfig config;

    private ThreadPoolExecutor pool = null;

    public Transformer(TransformerConfig config) {
        this.config = config;
    }

    private void transform() throws UnknownHostException {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(config.getQueueSize());
        pool = new ThreadPoolExecutor(config.getThreads(), config.getThreads(), 30, TimeUnit.SECONDS, workQueue);
        pool.prestartAllCoreThreads();

//        Monitor monitor = new Monitor(pool, 1);
//        Thread monitorThread = new Thread(monitor);
//        monitorThread.start();

        Monitor monitor = new Monitor(pool, 1);
        Thread t = new Thread(monitor);
        t.setDaemon(true);
        t.start();
        

        
        
        queueDocuments();
        
      //Thread.sleep(30000);
        // shut down the pool
        pool.shutdown();
        // shut down the monitor thread
        //Thread.sleep(5000);
        monitor.shutdown();

    }

    private void queueDocuments() throws UnknownHostException {

        logger.debug("queueDocuments()");
        DBCollection collection = config.getSourceCollection();

        DBCursor cursor = collection.find(config.getSourceQuery(), config.getSourceProjection());
        int resultCount = cursor.count();
        logger.debug("Result count: " + resultCount);
        while (cursor.hasNext()) {
            DBObject currentDoc = cursor.next();

            pool.submit(new TransformImpl(currentDoc, config));
        }
        logger.debug(String.format("Queued %s documents", resultCount));
        //pool.shutdown();
    }

    public static void main(String[] args) throws UnknownHostException {
        if (args.length == 0) {
            System.out.println("Usage: java " + Transformer.class.getName() + " <transformPropertiesFile>");
            System.exit(-1);
        }

        TransformerConfig config = null;
        try {
            config = new TransformerConfig(args[0]);
        } catch (Exception e) {
            logger.error("Error loading properties file", e);
            System.exit(-1);
        }
        Transformer loader = new Transformer(config);

        try {
            loader.transform();
        } catch (Exception e) {
            logger.error("Error loading data", e);
        }

    }

}
