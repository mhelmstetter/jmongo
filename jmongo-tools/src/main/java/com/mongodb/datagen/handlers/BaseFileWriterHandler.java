package com.mongodb.datagen.handlers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.datagen.DataElement;

public abstract class BaseFileWriterHandler {

	protected File outputDir;

	protected boolean cleanOutputDir;

	protected static final Logger logger = LoggerFactory.getLogger(BaseFileWriterHandler.class);

	public BaseFileWriterHandler() {
		super();
	}

	public boolean handleMessageEvent(DataElement message) {
		if (outputDir != null) {
			try {
				writeFile(message);
			} catch (IOException e) {
				logger.error("Error writing output message xml file", e);
			}
		}

		return true;
	}

	protected abstract void writeFile(DataElement message)
			throws IOException;

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public void init() throws IOException {
		if (outputDir != null) {
			if (!outputDir.exists()) {
				FileUtils.forceMkdir(outputDir);
			}
			if (cleanOutputDir) {
				 //TODO bug here if this is empty?
				FileUtils.cleanDirectory(outputDir);
			}
		}
	}
	
	public void close() throws IOException {
		
	}

	public void setCleanOutputDir(boolean cleanOutputDir) {
		this.cleanOutputDir = cleanOutputDir;
	}

}