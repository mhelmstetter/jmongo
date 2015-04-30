package com.mongodb.oplog;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import com.mongodb.BasicDBObject;

public class DefaultProcessor implements OplogRecordProcessor {
    
    ThreadPoolExecutor pool;
    Config config;
    
    public DefaultProcessor(ThreadPoolExecutor pool, Config config) {
        this.pool = pool;
        this.config = config;
    }

    @Override
    public void processRecord(BasicDBObject x) throws Exception {
        OplogTask t = new OplogTask(x, config);
        pool.submit(t);
        
    }

    @Override
    public void close(String string) throws IOException {
        // TODO Auto-generated method stub
        
    }

}
