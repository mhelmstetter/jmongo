package com.mongodb.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BulkWriteException;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
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
        DBCollection collection = config.getCollection();
        BulkWriteOperation bulkWrite = collection.initializeUnorderedBulkOperation();
        while ((currentLine = in.readLine()) != null) {
            if (currentLine.length() == 0) {
                continue;
            }
            //event.incrementCount();
            bulkWrite.insert((DBObject)JSON.parse(currentLine));
            if (++count % config.getBatchSize() == 0) {
                execute(bulkWrite, event, count);
                bulkWrite = collection.initializeUnorderedBulkOperation();
                count = 0;
            }
            
        }
        execute(bulkWrite, event, count);
        in.close();
        
        monitor.add(event);
        return null;
    }
    
    private static void execute(BulkWriteOperation bulkWrite, TimedEvent event, int count) {
        BulkWriteResult result = null;
        try {
            result = bulkWrite.execute();
            event.incrementCount(result.getInsertedCount());
        } catch (BulkWriteException bulkWriteException) {
            logger.warn("BulkWriteException: " + bulkWriteException.getWriteErrors().size() + "/" + count + " records failed.");
            event.incrementError(bulkWriteException.getWriteErrors().size());
            result = bulkWriteException.getWriteResult();
            event.incrementCount(result.getInsertedCount());
        } catch (Exception e) {
            // TODO - need to figure out how to account for these errors that aren't BulkWriteException
            // e.g. CommandFailureException
            //e.printStackTrace();
            event.incrementError(count);
        }
        
        
        
        
    }
    

    private void insert(String currentLine) {
        DBObject doc = (DBObject)JSON.parse(currentLine);
        config.getCollection().insert(doc);
        
    }

}
