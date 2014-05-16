package com.mongodb.datagen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractTextDictionary implements IndexedDictionary {
	
    protected static final Logger logger = LoggerFactory.getLogger(AbstractTextDictionary.class);
	
	
	
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
            	add(currentLine);
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
			logger.debug("Loaded " + getSize() + " dictionary entries.");
		}
		
	}
}
