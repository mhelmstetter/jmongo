package com.mongodb.oplog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang.StringUtils;
import org.bson.types.BSONTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoCursorNotFoundException;
import com.mongodb.util.Monitor;

public class OplogTailThread extends Thread {

    protected static final Logger logger = LoggerFactory.getLogger(OplogTailThread.class);
    
    private DBCollection oplog;
    private Object lastTimestamp;
    protected long reportInterval = 1000;
    private boolean killMe;

    protected List<String> inclusions = Collections.EMPTY_LIST;
    protected List<String> exclusions =   Collections.EMPTY_LIST;
    protected List<OplogRecordProcessor> processors = new ArrayList<OplogRecordProcessor>();
    
    ThreadPoolExecutor pool;

    public OplogTailThread(Config config, ThreadPoolExecutor pool, Monitor monitor) {
        oplog = config.getMongoClient().getDB("local").getCollection("oplog.rs");
        // TODO inclusions & exclusions
        processors.add(new DefaultProcessor(pool, config));
    }

    @Override
    public void run() {

        long lastWrite = 0;
        long startTime = System.currentTimeMillis();
        long lastOutput = System.currentTimeMillis();

        try {

            while (true) {
                try {
                    DBCursor cursor = null;
                    if (lastTimestamp != null) {
                        cursor = oplog.find(new BasicDBObject("ts", new BasicDBObject("$gt", lastTimestamp)));
                        cursor.addOption(Bytes.QUERYOPTION_OPLOGREPLAY);
                    } else {
                        cursor = oplog.find();
                    }
                    cursor.addOption(Bytes.QUERYOPTION_TAILABLE);
                    cursor.addOption(Bytes.QUERYOPTION_AWAITDATA);
                    long count = 0;
                    long skips = 0;

                    while (!killMe && cursor.hasNext()) {
                        DBObject x = cursor.next();
                        if (!killMe) {
                            lastTimestamp = (BSONTimestamp) x.get("ts");
                            count++;
//                            if (shouldWrite(x)) {
//                                for (OplogRecordProcessor processor : processors)
//                                    processor.processRecord((BasicDBObject) x);
//                                count++;
//                            } else {
//                                skips++;
//                            }
                            if (System.currentTimeMillis() - lastWrite > 1000) {
                                // writeLastTimestamp(lastTimestamp);
                                lastWrite = System.currentTimeMillis();
                            }
                            long duration = System.currentTimeMillis() - lastOutput;
                            if (duration > reportInterval) {
                                report(this.getName(), count, skips, System.currentTimeMillis() - startTime);
                                lastOutput = System.currentTimeMillis();
                            }
                        }
                    }
                } catch (MongoCursorNotFoundException ex) {
                    // writeLastTimestamp(lastTimestamp);
                    System.out.println("Cursor not found, waiting");
                    Thread.sleep(2000);
                } catch (com.mongodb.MongoInternalException ex) {
                    System.out.println("Cursor not found, waiting");
                    // writeLastTimestamp(lastTimestamp);
                    ex.printStackTrace();
                } catch (com.mongodb.MongoException ex) {
                    // writeLastTimestamp(lastTimestamp);
                    System.out.println("Internal exception, waiting");
                    Thread.sleep(2000);
                } catch (Exception ex) {
                    killMe = true;
                    // writeLastTimestamp(lastTimestamp);
                    ex.printStackTrace();
                    break;
                }
                Thread.yield();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean shouldWrite(DBObject obj) {
        String ns = (String) obj.get("ns");
        String dbName = StringUtils.substringBefore(ns, ".");
        String collName = StringUtils.substringAfter(ns, ".");
        
        if (collName.equals("lastModified")) {
            return false;
        }

        if (ns == null || "".equals(ns)) {
            return false;
        }
        if (exclusions.size() == 0 && inclusions.size() == 0) {
            return true;
        }
        if (exclusions.contains(ns)) {
            return false;
        }
        if (inclusions.contains(ns) || inclusions.contains("*")) {
            return true;
        }
        // check database-level inclusion
        if (ns.indexOf('.') > 0 && inclusions.contains(ns.substring(0, ns.indexOf('.')))) {
            return true;
        }

        return false;
    }

    void report(String collectionName, long count, long skips, long duration) {
        double brate = (double) count / ((duration) / 1000.0);
        logger.debug(collectionName + ": " + count + " records, " + brate + " req/sec, " + skips + " skips, " + pool + " tasks");
    }

}
