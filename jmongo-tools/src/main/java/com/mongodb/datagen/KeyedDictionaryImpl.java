package com.mongodb.datagen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyedDictionaryImpl implements KeyedDictionary {
	
    protected static final Logger logger = LoggerFactory.getLogger(KeyedDictionaryImpl.class);
    
	protected TreeMap<String, String> entries;
	
	public KeyedDictionaryImpl() {
		entries = new TreeMap<String,String>();
	}
	
	
	protected File sourceFile;
	protected List<File> sourceFiles;
	

	public void setSourceFile(File sourceFile) throws IOException {
		this.sourceFile = sourceFile;
		if (sourceFile != null) {
			parse(sourceFile);
		}
	}
	
	protected void parse(File file) throws IOException {
		logger.debug("parse(): " + file);
		BufferedReader in = new BufferedReader(new FileReader(file));
		String currentLine = null;
        while ((currentLine = in.readLine()) != null) {
            if (currentLine.length() > 0) {
            	String[] tuple = currentLine.split(",|\t");
            	entries.put(tuple[0], tuple[1]);
            }
        }
        in.close();
	}

	public void setSourceFiles(List<File> sourceFiles) throws IOException {
		this.sourceFiles = sourceFiles;
		if (sourceFiles != null) {
			for (File f : sourceFiles) {
				parse(f);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Loaded " + entries.size() + " dictionary entries.");
		}
		
	}

	public String get(String key) {
		return entries.get(key);
	}



}
