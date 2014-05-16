package com.mongodb.transform;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TransformImpl implements Runnable {

    private DBObject document;
    private TransformerConfig config;

    public TransformImpl(DBObject document, TransformerConfig config) {
        this.document = document;
        this.config = config;
    }

    public void run() {
        DBObject update = new BasicDBObject("$set", new BasicDBObject("foo", "blah").append("last-update", new Date()));

        
        document.keySet().size();

        config.getDestinationCollection().update(document, update);
        
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
