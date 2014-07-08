package com.mongodb.load;

import java.io.File;
import java.net.UnknownHostException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class LoaderConfig {
    
    private static final int DEFAULT_QUEUE_SIZE = 1000000;

    public static final String DEFAULT_INPUT_PATTERN = "^.+\\.[jJ][sS][oO][nN]$";
    
    public static final int DISPLAY_MILLIS = 15000;
    public static final int SLEEP_TIME = 500;
    public static final int DEFAULT_BATCH_SIZE = 100;
    
    private String host;
    private Integer port;
    private String databaseName;
    private String collectionName;
    
    private String inputPath;
    private File inputPathFile;
    private String inputPattern;
    
    private MongoClient mongoClient;
    private DB db;
    private DBCollection collection;
    private boolean dropCollection;
    private Integer batchSize;
    
    private int queueSize;
    private int threads;
    
    public LoaderConfig(String propsPath) throws ConfigurationException, UnknownHostException {
        this.loadProperties(propsPath);
    }
    
    private void loadProperties(String propsPath) throws ConfigurationException, UnknownHostException {
        Configuration props = new PropertiesConfiguration(propsPath);
        
        host = props.getString("host");
        port = props.getInt("port");
        databaseName = props.getString("database");
        collectionName = props.getString("collection");
        inputPath = props.getString("inputPath");
        inputPathFile = new File(inputPath);
        // TODO verify path is readable, etc.
        inputPattern = props.getString("inputPattern", DEFAULT_INPUT_PATTERN);
        if (inputPath == null || collectionName == null || port == null || databaseName == null || collectionName == null) {
            throw new ConfigurationException("Missing configuration: inputPath, host, port, databaseName, and collectionName are required");
        }
        mongoClient = new MongoClient(host, port);
        db = mongoClient.getDB(databaseName);
        collection = db.getCollection(collectionName);
        dropCollection = props.getBoolean("dropCollection");
        batchSize = props.getInt("batchSize", DEFAULT_BATCH_SIZE);
        
        setQueueSize(props.getInt("queueSize", DEFAULT_QUEUE_SIZE));
        setThreads(props.getInt("threads", 8));
        
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public DB getDb() {
        return db;
    }

    public void setDb(DB db) {
        this.db = db;
    }

    public DBCollection getCollection() {
        return collection;
    }

    public void setCollection(DBCollection collection) {
        this.collection = collection;
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

    public MongoClient getMongoClient() {
        return mongoClient;
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
