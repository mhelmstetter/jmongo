package com.mongodb.datagen;

import java.util.HashMap;
import java.util.Map;

public class GeoTrack {
    
    private Map<String, Object> metadata = new HashMap<String, Object>();
    
    private GeoPoint startPoint;
    
    private GeoPoint lastPoint;

    public GeoPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(GeoPoint startPoint) {
        this.startPoint = startPoint;
    }

    public GeoPoint getLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(GeoPoint lastPoint) {
        this.lastPoint = lastPoint;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }

}
