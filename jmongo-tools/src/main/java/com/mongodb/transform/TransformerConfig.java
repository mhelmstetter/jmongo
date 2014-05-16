package com.mongodb.transform;

import java.net.UnknownHostException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class TransformerConfig {

    private String sourceHost;
    private Integer sourcePort;
    private String sourceDatabaseName;
    private String sourceCollectionName;
    private DBObject sourceQuery;
    private DBObject sourceProjection;
    private MongoClient sourceMongoClient;
    private DB sourceDb;
    private DBCollection sourceCollection;
    

    private String destinationHost;
    private Integer destinationPort;
    private String destinationDatabaseName;
    private String destinationCollectionName;
    private MongoClient destinationMongoClient;
    private DB destinationDb;
    private DBCollection destinationCollection;
    
    private int queueSize;
    private int threads;
    
    private final static String DEFAULT_PROJECTION = "{_id:1}";
    
    public TransformerConfig(String propsPath) throws ConfigurationException, UnknownHostException {
        this.loadProperties(propsPath);
    }
    
    private void loadProperties(String propsPath) throws ConfigurationException, UnknownHostException {
        Configuration props = new PropertiesConfiguration(propsPath);
        
        sourceHost = props.getString("sourceHost");
        sourcePort = props.getInt("sourcePort");
        sourceDatabaseName = props.getString("sourceDatabase");
        sourceCollectionName = props.getString("sourceCollection");
        if (sourceHost == null || sourcePort == null || sourceDatabaseName == null || sourceCollectionName == null) {
            throw new ConfigurationException("Missing configuration: sourceHost, sourcePort, sourceDatabase, and sourceCollection are required");
        }
        sourceMongoClient = new MongoClient(sourceHost, sourcePort);
        sourceDb = sourceMongoClient.getDB(sourceDatabaseName);
        sourceCollection = sourceDb.getCollection(sourceCollectionName);
        
        destinationHost = props.getString("destinationHost", sourceHost);
        destinationPort = props.getInt("destinationPort", sourcePort);
        destinationDatabaseName = props.getString("destinationDatabase", sourceDatabaseName);
        destinationCollectionName = props.getString("destinationCollection", sourceCollectionName);
        
        destinationMongoClient = new MongoClient(destinationHost, destinationPort);
        destinationDb = sourceMongoClient.getDB(destinationDatabaseName);
        destinationCollection = sourceDb.getCollection(destinationCollectionName);

        
        if (props.getString("sourceQuery") != null) {
            sourceQuery = (DBObject) JSON.parse(props.getString("sourceQuery"));
        }
        //if (props.getString("sourceProjection") != null) {
            sourceProjection = (DBObject) JSON.parse(props.getString("sourceProjection", DEFAULT_PROJECTION));
        //}
        
        setQueueSize(props.getInt("queueSize", 1000000));
        setThreads(props.getInt("threads", 8));
        
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

    public Integer getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(Integer sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getSourceDatabaseName() {
        return sourceDatabaseName;
    }

    public void setSourceDatabaseName(String sourceDatabase) {
        this.sourceDatabaseName = sourceDatabase;
    }

    public String getDestinationHost() {
        return destinationHost;
    }

    public void setDestinationHost(String destinationHost) {
        this.destinationHost = destinationHost;
    }

    public Integer getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(Integer destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getDestinationDatabaseName() {
        return destinationDatabaseName;
    }

    public void setDestinationDatabaseName(String destinationDatabase) {
        this.destinationDatabaseName = destinationDatabase;
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

    public String getSourceCollectionName() {
        return sourceCollectionName;
    }

    public void setSourceCollectionName(String sourceCollection) {
        this.sourceCollectionName = sourceCollection;
    }

    public String getDestinationCollectionName() {
        return destinationCollectionName;
    }

    public void setDestinationCollectionName(String destinationCollection) {
        this.destinationCollectionName = destinationCollection;
    }

    public DBObject getSourceQuery() {
        return sourceQuery;
    }

    public void setSourceQuery(DBObject sourceQuery) {
        this.sourceQuery = sourceQuery;
    }

    public DBObject getSourceProjection() {
        return sourceProjection;
    }

    public void setSourceProjection(DBObject sourceProjection) {
        this.sourceProjection = sourceProjection;
    }

    public MongoClient getSourceMongoClient() {
        return sourceMongoClient;
    }

    public void setSourceMongoClient(MongoClient sourceMongoClient) {
        this.sourceMongoClient = sourceMongoClient;
    }

    public DB getSourceDb() {
        return sourceDb;
    }

    public void setSourceDb(DB sourceDb) {
        this.sourceDb = sourceDb;
    }

    public DBCollection getSourceCollection() {
        return sourceCollection;
    }

    public void setSourceCollection(DBCollection sourceCollection) {
        this.sourceCollection = sourceCollection;
    }

    public MongoClient getDestinationMongoClient() {
        return destinationMongoClient;
    }

    public void setDestinationMongoClient(MongoClient destinationMongoClient) {
        this.destinationMongoClient = destinationMongoClient;
    }

    public DB getDestinationDb() {
        return destinationDb;
    }

    public void setDestinationDb(DB destinationDb) {
        this.destinationDb = destinationDb;
    }

    public DBCollection getDestinationCollection() {
        return destinationCollection;
    }

    public void setDestinationCollection(DBCollection destinationCollection) {
        this.destinationCollection = destinationCollection;
    }

}
