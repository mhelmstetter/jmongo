package com.mongodb.query.analyzer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


public class ProfileQueryAnalyzerTest {

//    @Test
//    public void testLocalAnalyze() throws JsonParseException, IOException {
//        List<ServerAddress> mongoHosts = new ArrayList<ServerAddress>();
//        mongoHosts.add(new ServerAddress("localhost", 27017));
//        ProfileQueryAnalyzer p = new ProfileQueryAnalyzer("hmda", "hmda_lar", "test", "profile", mongoHosts);
//        p.analyze();
//    }
    
    @Test
    public void testFoo() throws JsonParseException, IOException {
        
        File file = new ClassPathResource("count1.json").getFile();
        String query = FileUtils.readFileToString(file);
        
        ProfileQueryAnalyzerConfig config = new ProfileQueryAnalyzerConfig();
        
        ProfileQueryAnalyzer p = new ProfileQueryAnalyzer(config);
        DBObject profile = (DBObject)JSON.parse(query);
        p.processCommand(profile);
        
    }
    
}
