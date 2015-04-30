package com.mongodb.oplog;

import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.load.TimedEvent;
import com.mongodb.util.Monitor;

public class OplogTask implements Callable {
    
    private DBObject oplogEntry;
    private Config config;
    private TimedEvent event;
    private Monitor monitor;

    public OplogTask(DBObject oplogEntry, Config config) {
        this.oplogEntry = oplogEntry;
        this.config = config;
        this.monitor = config.getMonitor();
    }

    @Override
    public Object call() throws Exception {
        event = new TimedEvent();
        try {
            String ns = (String) oplogEntry.get("ns");
            String op = (String)oplogEntry.get("op");
            DBObject dbo = new BasicDBObject();
            if (op.equals("u")) {
                dbo.put("_id", oplogEntry.get("o2._id"));
            } else {
                dbo.put("_id", oplogEntry.get("o._id"));
            }
            String dbName = StringUtils.substringBefore(ns, ".");
            DB db = config.getMongoClient().getDB(dbName);
            DBCollection coll = db.getCollection("lastModified");
            
            dbo.put("ts", oplogEntry.get("ts"));
            
            coll.insert(dbo);
            event.incrementCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        monitor.add(event);
        return null;
    }

}
