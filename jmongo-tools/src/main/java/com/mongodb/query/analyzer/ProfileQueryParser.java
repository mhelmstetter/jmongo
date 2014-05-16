package com.mongodb.query.analyzer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class ProfileQueryParser {
    
    protected static final Logger logger = LoggerFactory.getLogger(ProfileQueryParser.class);
    
    private final static String[] RANGE_QUERY_OPERATORS = { "$ne", "$gt", "$lt", "$gte", "$lte", "$in", "$nin", "$all",
    "$not" };
    
    JsonFactory f = new JsonFactory();
    JsonParser jp;
    JsonToken currentToken;
    
    private Set<String> ignoreKeySet = new HashSet<String>();
    
    boolean omitEmptyElements = true;

    private SortedSet<String> keySet = new TreeSet<String>();
    
    public ProfileQueryParser() {
        ignoreKeySet.addAll(Arrays.asList(RANGE_QUERY_OPERATORS));
    }
    
    protected String[] parse(String input) throws JsonParseException, IOException {
        //keySet.clear();
        keySet = new TreeSet<String>();
        jp = f.createParser(input);
        parseIternal();
        return keySet.toArray(new String[0]);
    }
    
    private void parseIternal() throws JsonParseException, IOException {
        currentToken = jp.nextToken();
        if (currentToken == JsonToken.START_OBJECT) {
            parseCurrent(jp);
        }
    }

    private void parseCurrent(JsonParser jp) throws JsonParseException, IOException {
        
        while (currentToken != null) {
            String key = jp.getCurrentName();
            //logger.debug(currentToken.toString() + " key:" + key);
            
            switch (currentToken) {
            case START_OBJECT:
                //logger.debug("START_OBJECT: " + key + " stack:" + keyStack);
                break;
            case START_ARRAY:
                break;
            case END_OBJECT:
                //logger.debug("END_OBJECT " + key + " " + objDepth + " recordCount: " + recordCount);
                break;
            case END_ARRAY:
                // logger.debug("End array " + key + " ]");
                //stack.pop();
                break;
            case FIELD_NAME:
                //logger.debug("FIELD_NAME " + key);
                break;
            case VALUE_STRING:
                
                if (jp.getText().length() == 0 && omitEmptyElements) {
                    break;
                }
                add(key, jp.getText());
                break;
            case VALUE_NUMBER_INT:
                add(key, jp.getLongValue());
                break;
            case VALUE_NUMBER_FLOAT:
                add(key, jp.getFloatValue());
                break;
            case VALUE_NULL:
                add(key, null);
                break;
            case VALUE_TRUE:
            case VALUE_FALSE:
                add(key, jp.getBooleanValue());
                break;
            default:
                logger.debug("Unhandled token: " + currentToken.name());
            }
            currentToken = jp.nextToken();
        }
    }
    

    @SuppressWarnings("unchecked")
    protected void add(String key, Object value) {
        //logger.debug(String.format("add() key: %s value: %s", key, value));
        if (! ignoreKeySet.contains(key)) {
            keySet.add(key); 
        }
        
    }

}
