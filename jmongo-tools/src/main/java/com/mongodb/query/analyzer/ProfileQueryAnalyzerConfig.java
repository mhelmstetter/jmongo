package com.mongodb.query.analyzer;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClientURI;

public class ProfileQueryAnalyzerConfig {
    
    protected static final Logger logger = LoggerFactory.getLogger(ProfileQueryAnalyzerConfig.class);
    
    private String profiledDatabaseName;
    private String profiledCollectionName;
    private String databaseName;
    private String collectionName;
    private List<MongoClientURI> mongoUris = new ArrayList<MongoClientURI>();
    private File csvOutputFile;

    public ProfileQueryAnalyzerConfig(String propsPath) throws ConfigurationException, UnknownHostException {
        this.loadProperties(propsPath);
    }
    
    private void loadProperties(String propsPath) throws ConfigurationException, UnknownHostException {
        logger.debug(String.format("Reading properties from %s", propsPath));
        Configuration props = new PropertiesConfiguration(propsPath);
        //logger.debug(String.format("Config properties: %s", props));
        
        profiledDatabaseName = props.getString("mongo.database");
        profiledCollectionName = props.getString("mongo.collection");
        
        databaseName = props.getString("mongo.profile.database", profiledDatabaseName);
        collectionName = props.getString("mongo.profile.collection", "system.profile");
        
        String[] uris = props.getStringArray("mongo.uri");
        for (String uri : uris) {
            mongoUris.add(new MongoClientURI(uri));
        }
        
        this.csvOutputFile = new File(props.getString("csvOutputFile", "profileAnalyzer.csv"));
        
    }

    public String getProfiledDatabaseName() {
        return profiledDatabaseName;
    }

    public void setProfiledDatabaseName(String profiledDatabaseName) {
        this.profiledDatabaseName = profiledDatabaseName;
    }

    public String getProfiledCollectionName() {
        return profiledCollectionName;
    }

    public void setProfiledCollectionName(String profiledCollectionName) {
        this.profiledCollectionName = profiledCollectionName;
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

    public List<MongoClientURI> getMongoUris() {
        return mongoUris;
    }

    public void setMongoUris(List<MongoClientURI> mongoUris) {
        this.mongoUris = mongoUris;
    }

    public File getCsvOutputFile() {
        return csvOutputFile;
    }

    public void setCsvOutputFile(File csvOutputFile) {
        this.csvOutputFile = csvOutputFile;
    }


}