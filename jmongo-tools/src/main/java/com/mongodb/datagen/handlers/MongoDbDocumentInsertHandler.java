package com.mongodb.datagen.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.datagen.DataElement;
import com.mongodb.util.JSON;

public class MongoDbDocumentInsertHandler implements DataHandler {

    protected static final Logger logger = LoggerFactory.getLogger(MongoDbDocumentInsertHandler.class);
	
	private MongoClient mongoClient;
    private DB db;
    private DBCollection collection;
    
    private String databaseName;
    private String collectionName;
    
    private boolean dropCollection;

    
    public void init() {
        db = mongoClient.getDB(databaseName);
        collection = db.getCollection(collectionName);
        if (dropCollection) {
            logger.debug(String.format("Dropping collection %s", collectionName));
            collection.drop();
        }
        
    }
 	
	@Override
	public boolean handleMessageEvent(DataElement message) throws Exception {
	    DBObject doc = (DBObject)JSON.parse(message.getData());
		collection.insert(doc);
        return true;
	}

	@Override
	public void close() {
	}

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setDropCollection(boolean dropCollection) {
        this.dropCollection = dropCollection;
    }

}
