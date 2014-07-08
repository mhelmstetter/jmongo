package com.mongodb.query.analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;

public class ProfileQueryAnalyzer {

    protected static final Logger logger = LoggerFactory.getLogger(ProfileQueryAnalyzer.class);
    
    private Map<String, ProfileStatistics> keysHistogram = new HashMap<String, ProfileStatistics>();
    
    private ProfileQueryAnalyzerConfig config;
    
    ProfileQueryParser parser = new ProfileQueryParser();
    
    public ProfileQueryAnalyzer(ProfileQueryAnalyzerConfig config) {
        this.config = config;
    }

    public void analyze() throws JsonParseException, IOException {
        PrintWriter writer = new PrintWriter(config.getCsvOutputFile());
        for (MongoClientURI uri : config.getMongoUris()) {
            logger.debug(String.format("Analyizing from mongo uri: %s", uri));
            MongoClient mongo = new MongoClient(uri);
            analyze(mongo);
        }
        
        Map<String, ProfileStatistics> sortedMap = sortByValuesDescending(keysHistogram);
        
        
        writer.print("queryKeys,");
        writer.println(ProfileStatistics.csvHeaders());
        
        for (Map.Entry<String, ProfileStatistics> entry : sortedMap.entrySet()) {
            String keys = entry.getKey();
            ProfileStatistics count = entry.getValue();
            logger.debug(keys + " " + count);
            writer.print('"');
            writer.print(keys);
            writer.print('"');
            writer.print(",");
            writer.println(count.toCsv());
        }
        writer.close();
        logger.debug(String.format("Output written to %s", config.getCsvOutputFile()));
    }
    
    private void analyze(MongoClient mongo) throws JsonParseException, IOException {
        
        
        
        DB db = mongo.getDB(config.getDatabaseName());
        DBCollection profileCollection = db.getCollection(config.getCollectionName());
        
        BasicDBList opTypes = new BasicDBList();
        opTypes.add("query");
        
        //TODO need special logic for handling getmores. query is null, probably need to track cursor ids.
        //opTypes.add("getmore");
        
        DBObject queryQuery = new BasicDBObject("op", new BasicDBObject("$in", opTypes)).append("ns", config.getProfiledDatabaseName() + "." + config.getProfiledCollectionName());
        //DBObject queryProjection = new BasicDBObject("query", true).append("_id", false);
        DBCursor cursor = profileCollection.find(queryQuery, null);
        logger.debug(String.format("Query count: %s", cursor.count()));;
        while (cursor.hasNext()) {
            DBObject profile = cursor.next();
            processQuery(profile);
        }
        
        DBObject commandQuery = new BasicDBObject("op", "command").append("ns", config.getProfiledDatabaseName() + ".$cmd");
        //logger.debug(commandQuery.toString());
        //DBObject commandProjection = new BasicDBObject("command", true).append("_id", false);
        cursor = profileCollection.find(commandQuery, null);
        while (cursor.hasNext()) {
            DBObject profile = cursor.next();
            processCommand(profile);
        }
    }
    
    public void processCommand(DBObject profile) throws JsonParseException, IOException {
        DBObject command = (DBObject)profile.get("command");
        DBObject profileQuery = (DBObject)command.get("query");
        
        String profileJson = JSON.serialize(profileQuery);
        String[] keys = parser.parse(profileJson);
        String keysString = StringUtils.join(keys, ",");
        ProfileStatistics existing = keysHistogram.get(keysString);
        if (existing == null) {
            existing = new ProfileStatistics();
            keysHistogram.put(keysString, existing);
        }
        
        String commandStr = processCommand(command, existing);
    }
    
    public void processQuery(DBObject profile) throws JsonParseException, IOException {
        String opType = (String)profile.get("op");
        DBObject outer = (DBObject)profile.get("query");
        logger.debug(profile.toString());
        DBObject profileQuery = (DBObject)outer.get("$query");
        
        
        String profileJson = JSON.serialize(profileQuery);
        String[] keys = parser.parse(profileJson);
        String keysString = StringUtils.join(keys, ",");
        
        
        ProfileStatistics existing = keysHistogram.get(keysString);
        if (existing == null) {
            existing = new ProfileStatistics();
            existing.incrementQuery();
            keysHistogram.put(keysString, existing);
        } else {
            existing.incrementQuery();
        }
    }
    
    public void processAggregate(DBObject profile) {
        
        String collection = (String)profile.get("aggregate");
        if (collection.equals(config.getProfiledCollectionName())) {
            logger.debug(profile.toString());
            DBObject pipeline = (DBObject)profile.get("pipeline");
            logger.debug(pipeline.toString());
        }
        
        
    }
    
    private String processCommand(DBObject command, ProfileStatistics stats) {
        if (command.containsField("count")) {
            return filterCommandByCollection(command, "count", stats);
        } else if (command.containsField("mapreduce")) {
            return filterCommandByCollection(command, "mapreduce", stats);
        } else if (command.containsField("aggregate")) {
            //TODO handle aggregate
            logger.warn("\"aggregate\" command analysis not currently implemented");
            processAggregate(command);
        } else {
            //logger.warn("Unrecognized command: " + command);
        }
        return null;
    }
    
    private String filterCommandByCollection(DBObject command, String commandName, ProfileStatistics stats) {
        String collection = (String)command.get(commandName);
        if (collection.equals(config.getProfiledCollectionName())) {
            stats.incrementCommand(commandName);
            return commandName;
        } else {
            return null;
        }
    }
    
    private static <K extends Comparable,V extends Comparable> Map<K,V> sortByValuesDescending(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
      
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
      
        return sortedMap;
    }


    
    
    public static void main(String[] args) throws UnknownHostException {
        if (args.length == 0) {
            System.out.println("Usage: java " + ProfileQueryAnalyzer.class.getName() + " <analyzerPropertiesFile>");
            System.exit(-1);
        }

        ProfileQueryAnalyzerConfig config = null;
        try {
            config = new ProfileQueryAnalyzerConfig(args[0]);
        } catch (Exception e) {
            logger.error("Error loading properties file", e);
            System.exit(-1);
        }
        ProfileQueryAnalyzer analyzer = new ProfileQueryAnalyzer(config);

        try {
            analyzer.analyze();
        } catch (Exception e) {
            logger.error("Analyzer error", e);
        }

    }

}
