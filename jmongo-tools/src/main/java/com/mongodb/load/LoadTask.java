package com.mongodb.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.Callable;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.Monitor;

public class LoadTask implements Callable {
    
    private File file;
    private LoaderConfig config;
    private TimedEvent event;
    private Monitor monitor;
    
    public LoadTask(File file, LoaderConfig config, Monitor monitor) {
        this.file = file;
        this.config = config;
        this.monitor = monitor;
    }

    @Override
    public Object call() throws Exception {
        
        BufferedReader in = new BufferedReader(new FileReader(file));
        
        String currentLine = null;
        while ((currentLine = in.readLine()) != null) {
            event = new TimedEvent();
            if (currentLine.length() > 0) {
                insert(currentLine);
            }
            monitor.add(event);
        }
        in.close();
        
        return null;
    }

    private void insert(String currentLine) {
        DBObject doc = (DBObject)JSON.parse(currentLine);
        config.getCollection().insert(doc);
        
    }

}
