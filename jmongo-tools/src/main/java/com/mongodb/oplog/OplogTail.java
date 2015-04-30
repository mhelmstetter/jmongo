package com.mongodb.oplog;

import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.util.Monitor;

public class OplogTail {

    protected static final Logger logger = LoggerFactory.getLogger(OplogTail.class);

    private ThreadPoolExecutor pool = null;

    private Config config;

    private Monitor monitor;

    public OplogTail(Config config) {
        this.config = config;
    }

    private void run() throws UnknownHostException {

        
        
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(config.getQueueSize());
        pool = new ThreadPoolExecutor(config.getThreads(), config.getThreads(), 30, TimeUnit.SECONDS, workQueue);
        pool.prestartAllCoreThreads();

        monitor = new Monitor(Thread.currentThread());
        config.setMonitor(monitor);
        monitor.setPool(pool);
        monitor.start();

        queueDocuments();

        //pool.shutdown();

        while (!pool.isTerminated()) {
            Thread.yield();
            try {
                Thread.sleep(config.getThreads() * Config.SLEEP_TIME);
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
        OplogTailThread scanner = new OplogTailThread(config, pool, monitor);
        scanner.start();

    }
    
    @SuppressWarnings("static-access")
    private static Config initializeAndParseCommandLineOptions(String[] args) throws ConfigurationException, UnknownHostException {
        Options options = new Options();
        options.addOption(OptionBuilder.withArgName("connection uri")
                .hasArgs()
                .isRequired()
                .withDescription(  "mongodb connection string uri" )
                .withLongOpt("uri")
                .create( "u" ));
        
        options.addOption(OptionBuilder.withArgName("# threads")
                .hasArg()
                .withDescription(  "number of threads" )
                .withLongOpt("threads")
                .withType(Number.class)
                .create( "t" ));
        options.addOption(OptionBuilder.withArgName("batch size (default 100)")
                .hasArg()
                .withDescription(  "number of documents per batch (default 100)" )
                .withLongOpt("batchSize")
                .withType(Number.class)
                .create( "b" ));

        CommandLineParser parser = new GnuParser();
        CommandLine line = null;
        Config config = null;
        try {
            line = parser.parse( options, args );
            config = new Config(line);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            printHelpAndExit(options);
        } catch (Exception e) {
            e.printStackTrace();
            printHelpAndExit(options);
        }
        return config;
        
    }
    
    private static void printHelpAndExit(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "loader", options );
        System.exit(-1);
    }

    public static void main(String[] args) throws ConfigurationException, UnknownHostException {
        
        Config config = initializeAndParseCommandLineOptions(args);
        OplogTail loader = new OplogTail(config);

        try {
            loader.run();
        } catch (Exception e) {
            logger.error("Error loading data", e);
        } finally {
            loader.halt();
        }
    }

}
