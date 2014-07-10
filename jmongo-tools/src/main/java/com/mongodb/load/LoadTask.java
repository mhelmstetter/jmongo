package com.mongodb.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;
import com.mongodb.util.Monitor;

public class LoadTask implements Callable {
    
    private File file;
    private LoaderConfig config;
    private TimedEvent event;
    private Monitor monitor;
    
    protected static final Logger logger = LoggerFactory.getLogger(Loader.class);
    
    public LoadTask(File file, LoaderConfig config, Monitor monitor) {
        this.file = file;
        this.config = config;
        this.monitor = monitor;
    }

    @Override
    public Object call() throws Exception {
        
        event = new TimedEvent();
        
        BufferedReader in = new BufferedReader(new FileReader(file));
        logger.debug("reading file " + file);
        
        String currentLine = null;
        int count = 0;
        BulkWriteOperation bulkWrite = config.getCollection().initializeUnorderedBulkOperation();
        while ((currentLine = in.readLine()) != null) {
            if (currentLine.length() == 0) {
                continue;
            }
            event.incrementCount();
            bulkWrite.insert((DBObject)JSON.parse(currentLine));
            if (++count % config.getBatchSize() == 0) {
                BulkWriteResult result = bulkWrite.execute(WriteConcern.UNACKNOWLEDGED);
                bulkWrite = config.getCollection().initializeUnorderedBulkOperation();
            }
            
        }
        bulkWrite.execute();
        in.close();
        
        monitor.add(event);
        return null;
    }

    private void insert(String currentLine) {
        DBObject doc = (DBObject)JSON.parse(currentLine);
        config.getCollection().insert(doc);
        
    }

}
