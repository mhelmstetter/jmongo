package com.mongodb.datagen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WeightedTextDictionary extends TextDictionary {
	
	@Override
	protected void parse(File file) throws IOException {
		logger.debug("parse(): " + file);
		BufferedReader in = new BufferedReader(new FileReader(file));
		String currentLine = null;
        while ((currentLine = in.readLine()) != null) {
            if (currentLine.length() > 0) {
            	if (currentLine.contains("|")) {
            		String[] toks = currentLine.split("\\|");
            		String tok = toks[0];
            		Integer freq = Integer.parseInt(tok);
            		if (freq <= 1000) {
            			for (int i = 0; i < freq; i++) {
                			getEntries().add(toks[1]);
                		}
            		} else {
            			logger.warn("Skipping line frequency must be <= 1000: " + currentLine);
            		}
            		
            	} else {
            		getEntries().add(currentLine);
            	}
            }
        }
        in.close();
        logger.debug("parse() completed: " + file);
	}

}
