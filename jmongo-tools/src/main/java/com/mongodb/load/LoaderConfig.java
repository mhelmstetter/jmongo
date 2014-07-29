package com.mongodb.load;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.RoundRobin;

public class LoaderConfig {
    
    private static final int DEFAULT_QUEUE_SIZE = 1000000;

    public static final String DEFAULT_INPUT_PATTERN = "^.+\\.[jJ][sS][oO][nN]$";
    
    public static final int DISPLAY_MILLIS = 15000;
    public static final int SLEEP_TIME = 500;
    public static final int DEFAULT_BATCH_SIZE = 100;
    public static final int DEFAULT_THREADS = 8;
    
//    private String host;
//    private Integer port;
    private String databaseName;
    private String collectionName;
    private String connectionUri;
    
    private String inputPath;
    private File inputPathFile;
    private String inputPattern;
    
    private Iterator<DBCollection> collectionRoundRobin;
    
    //private MongoClient mongoClient;
    //private DB db;
    //private DBCollection collection;
    private boolean dropCollection;
    private Integer batchSize = DEFAULT_BATCH_SIZE;
    
    private int queueSize;
    private int threads = DEFAULT_THREADS;
    
//    public LoaderConfig(String propsPath) throws ConfigurationException, UnknownHostException {
//        this.loadProperties(propsPath);
//    }
    
    public LoaderConfig(CommandLine line) throws ParseException {
       
        loadProperties(line);
        
    }
    
//    private void loadProperties(String propsPath) throws ConfigurationException, UnknownHostException {
//        Configuration props = new PropertiesConfiguration(propsPath);
//        loadProperties(props);
//    }
    
    private void loadProperties(CommandLine line) throws ParseException {
//        host = props.getString("host", "localhost");
//        port = props.getInt("port", 27017);
//        databaseName = props.getString("database");
//        collectionName = props.getString("collection");
        
        
        String[] connectionUris = line.getOptionValues("u");
        List<DBCollection> collections = new ArrayList<DBCollection>(connectionUris.length);
        for (String connectionUri : connectionUris) {
            MongoClientURI uri = new MongoClientURI(connectionUri);
            MongoClient mongoClient = null;
            try {
                mongoClient = new MongoClient(uri);
            } catch (UnknownHostException e) {
                throw new ParseException("Invalid MongoClient uri " + e.getMessage());
            }
            
            databaseName = uri.getDatabase();
            collectionName = uri.getCollection();
            if (databaseName == null || collectionName == null) {
                throw new ParseException("Database and collection must be specified with connection uri");
            }
            
            DB db = mongoClient.getDB(databaseName);
            DBCollection collection = db.getCollection(collectionName);
            collections.add(collection);
        }
        RoundRobin<DBCollection> roundRobin = new RoundRobin<DBCollection>(collections);
        collectionRoundRobin = roundRobin.iterator();
        
        inputPath = line.getOptionValue("i");
        inputPathFile = new File(inputPath);
        // TODO verify path is readable, etc.
        
        // TODO read this from command line
        inputPattern = DEFAULT_INPUT_PATTERN;
        
        
        if (inputPath == null || connectionUri == null) {
            throw new ParseException("Missing configuration: inputPath, and uri are required");
        }
        
        
        
        //dropCollection = props.getBoolean("dropCollection", false);
        batchSize = ((Long)line.getParsedOptionValue("b")).intValue();
        //line.getParsedOptionValue(opt)
        
        setQueueSize(DEFAULT_QUEUE_SIZE);
        
        if (line.hasOption("t")) {
            threads = ((Long)line.getParsedOptionValue("t")).intValue();
        }
        
    }

   

    public DBCollection getCollection() {
        return collectionRoundRobin.next();
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

    

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getInputPattern() {
        return inputPattern;
    }

    public void setInputPattern(String inputPattern) {
        this.inputPattern = inputPattern;
    }

    public File getInputPathFile() {
        return inputPathFile;
    }

    public void setInputPathFile(File inputPathFile) {
        this.inputPathFile = inputPathFile;
    }

    public boolean isDropCollection() {
        return dropCollection;
    }

    public void setDropCollection(boolean dropCollection) {
        this.dropCollection = dropCollection;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

}
