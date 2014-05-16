package com.mongodb.datagen.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.mongodb.datagen.DataElement;

public class XmlFileWriterHandler extends BaseFileWriterHandler implements DataHandler {

    protected void writeFile(DataElement message)
            throws IOException {
    	//String docUri = URLEncoder.encode(message.getKey(), "UTF-8");
    	String docUri = message.getKey();
        File xmlFile = new File(outputDir, docUri);
        FileUtils.forceMkdir(xmlFile.getParentFile());
        BufferedWriter out = new BufferedWriter(new FileWriter(xmlFile));
        out.write(message.getData());
        out.close();
    }

}
