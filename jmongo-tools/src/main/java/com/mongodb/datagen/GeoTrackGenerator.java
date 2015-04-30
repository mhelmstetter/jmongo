package com.mongodb.datagen;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.util.RandomUtils;

public class GeoTrackGenerator {
    
    protected final static ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
            "datagen/spring.xml");
    
    protected static final Logger logger = LoggerFactory.getLogger(GeoTrackGenerator.class);
    
    private Map<String, GeoTrack> tracks = new HashMap<String, GeoTrack>();
    
    private void initializeTracks(int numTracks) {
        for (int t = 0; t < numTracks; t++) {
            GeoTrack track = new GeoTrack();
            String tailNumber = RandomUtils.getRandomString(5);
        }
    }
    
    private void generate(long iterations, int numTracks) {
        logger.debug("Starting generate for " + iterations + " iterations.");
        initializeTracks(numTracks);
        long recordCount = 0;
        for (int i = 0; i < iterations; i++) {
        
            if (i % 1000 == 1) {
                logger.debug("Generated " + recordCount + " records, iteration " + i);
            }
            
            
            
            
        }
    }
    
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        GeoTrackGenerator gen = applicationContext.getBean(GeoTrackGenerator.class);
        long iterations = 100000;
        if (args.length > 0) {
            iterations = Long.parseLong(args[0]);
        }
        
        int numTracks = 100;
        if (args.length >= 2) {
            numTracks = Integer.parseInt(args[1]);
        }
        
        gen.generate(iterations, numTracks);
    }
}
