package com.mongodb.oplog;

import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.Monitor;

public class Config {
    
    private static final int DEFAULT_QUEUE_SIZE = 1000000;

    
    public static final int DISPLAY_MILLIS = 15000;
    public static final int SLEEP_TIME = 500;
    public static final int DEFAULT_BATCH_SIZE = 100;
    public static final int DEFAULT_THREADS = 8;
    
    
    
    private Integer batchSize = DEFAULT_BATCH_SIZE;
    
    private int queueSize;
    private int threads = DEFAULT_THREADS;
    private MongoClient mongoClient;


    private Monitor monitor;
    
    
    public Config(CommandLine line) throws ParseException {
       
        loadProperties(line);
        
    }
    

    
    private void loadProperties(CommandLine line) throws ParseException {

        String connectionUri = line.getOptionValue("u");
        
        MongoClientURI uri = new MongoClientURI(connectionUri);
        try {
            mongoClient = new MongoClient(uri);
        } catch (UnknownHostException e) {
            throw new ParseException("Invalid MongoClient uri " + e.getMessage());
        }
      
        if (line.hasOption("b")) {
            batchSize = ((Long)line.getParsedOptionValue("b")).intValue();
        }
        
        
        setQueueSize(DEFAULT_QUEUE_SIZE);
        
        if (line.hasOption("t")) {
            threads = ((Long)line.getParsedOptionValue("t")).intValue();
        }
        
    }
    

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    





    

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }



    public MongoClient getMongoClient() {
        return mongoClient;
    }



    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
        
    }



    public Monitor getMonitor() {
        return monitor;
    }

}
