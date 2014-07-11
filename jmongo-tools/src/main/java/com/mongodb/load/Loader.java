package com.mongodb.load;

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

import com.mongodb.DBCollection;
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
    
    /*
     * host=localhost
port=27017
database=loader
collection=test
dropCollection=false

inputPath=/data/serial
threads=32
batchSize=100
     */
    
    @SuppressWarnings("static-access")
    private static LoaderConfig initializeAndParseCommandLineOptions(String[] args) throws ConfigurationException, UnknownHostException {
        Options options = new Options();
        options.addOption(OptionBuilder.withArgName("hostname")
                .hasArg().isRequired()
                .withDescription(  "mongod or mongos host (default localhost)" )
                .withLongOpt("host")
                .create( "h" ));
        options.addOption(OptionBuilder.withArgName("port number")
                .hasArg()
                .withDescription(  "mongod or mongos port (default 27017)" )
                .withLongOpt("port")
                .create( "p" ));
        options.addOption(OptionBuilder.withArgName("database name")
                .hasArg()
                .isRequired()
                .withDescription(  "database to use" )
                .create( "db" ));
        options.addOption(OptionBuilder.withArgName("collection name")
                .hasArg()
                .isRequired()
                .withDescription(  "collection to use" )
                .withLongOpt("collection")
                .create( "c" ));
        options.addOption(OptionBuilder.withArgName("input path")
                .hasArg()
                .isRequired()
                .withDescription(  "directory path to load files from" )
                .withLongOpt("inputPath")
                .create( "i" ));
        options.addOption(OptionBuilder.withArgName("# threads")
                .hasArg()
                .withDescription(  "number of threads" )
                .withLongOpt("threads")
                .create( "t" ));
        options.addOption(OptionBuilder.withArgName("batch size (default 8)")
                .hasArg()
                .withDescription(  "number of documents per batch (default 100)" )
                .withLongOpt("batchSize")
                .create( "b" ));

        CommandLineParser parser = new GnuParser();
        CommandLine line = null;
        LoaderConfig config = null;
        try {
            line = parser.parse( options, args );
            config = new LoaderConfig(line);
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
        
        LoaderConfig config = initializeAndParseCommandLineOptions(args);
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
