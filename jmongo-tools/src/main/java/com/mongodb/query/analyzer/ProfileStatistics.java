package com.mongodb.query.analyzer;

import java.util.HashMap;
import java.util.Map;

public class ProfileStatistics implements Comparable<ProfileStatistics> {

    public Integer totalCount = 0;

    public Integer queryCount = 0;

    public Integer commandCount = 0;

    public Map<String, Integer> commandCounts = new HashMap<String, Integer>();

    public Integer avgMillis = 0;

    public Integer minMillis = 0;

    public Integer maxMillis = 0;
    
    public final static String[] headers = new String[] {"totalCount", "queryCount", "commandCount", "countCount", "mapReduceCount"};

    public void incrementQuery() {
        queryCount++;
        totalCount++;
    }

    public int compareTo(ProfileStatistics o) {
        return this.totalCount.compareTo(o.totalCount);
    }
    
    public static String csvHeaders() {
        StringBuilder result = new StringBuilder();
        for(String string : headers) {
            result.append(string);
            result.append(",");
        }
        return result.toString();
    }
    
    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append(totalCount);
        sb.append(",");
        sb.append(queryCount);
        sb.append(",");
        sb.append(commandCount);
        sb.append(",");
        Integer count = commandCounts.get("count");
        sb.append(count == null ? 0 : count);
        sb.append(",");
        Integer mrCount = commandCounts.get("mapreduce");
        sb.append(mrCount == null ? 0 : mrCount);
        return sb.toString();
    }

    public String toString() {
        return String.format("totalCount: %s, queryCount: %s, commandCount: %s, countCount: %s, mapReduceCount: %s", totalCount,
                queryCount, commandCount, commandCounts.get("count"), commandCounts.get("mapreduce"));
    }

    public void incrementCommand(String commandName) {
        commandCount++;
        totalCount++;

        Integer count = commandCounts.get(commandName);
        if (count == null) {
            commandCounts.put(commandName, 1);
        } else {
            commandCounts.put(commandName, ++count);
        }

    }

}
