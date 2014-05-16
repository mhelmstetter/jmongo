package com.mongodb.datagen.handlers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.mongodb.datagen.DataElement;

public class ZipFileWriterHandler extends BaseFileWriterHandler implements DataHandler {

	protected String outputFileName;
	protected ZipOutputStream zipOutputStream;
	
	public final static String CHARSET_UTF8 = "UTF-8";
	Charset charset = Charset.forName(CHARSET_UTF8);
	
	static final int BUFFER = 2048;
	
	public void init() throws IOException {
		super.init();
		File outputFile = new File(this.outputDir, "datagen.zip");
		FileOutputStream dest = new FileOutputStream(outputFile);
		zipOutputStream = new ZipOutputStream(new BufferedOutputStream(dest));
	}
	
	public void close() throws IOException {
		zipOutputStream.close();
	}
	
    protected void writeFile(DataElement message)
            throws IOException {
    	//String docUri = URLEncoder.encode(message.getKey(), "UTF-8");
    	String docUri = message.getKey();
        
        ZipEntry entry = new ZipEntry(docUri);
        zipOutputStream.putNextEntry(entry);
        
        byte[] data = message.getData().getBytes(charset);
        zipOutputStream.write(data, 0, data.length);
        zipOutputStream.closeEntry();
    }


}
