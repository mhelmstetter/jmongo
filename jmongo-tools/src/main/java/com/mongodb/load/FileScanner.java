package com.mongodb.load;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.util.Monitor;

public class FileScanner {

    private LoaderConfig config;

    private FileFilter fileFilter;
    
    ThreadPoolExecutor pool;
    private Monitor monitor;
    
    protected static final Logger logger = LoggerFactory.getLogger(FileScanner.class);

    public FileScanner(LoaderConfig loaderConfig, ThreadPoolExecutor pool, Monitor monitor) {
        this.config = loaderConfig;
        fileFilter = new FileFilter() {
            public boolean accept(File f) {
                String inputPattern = config.getInputPattern();
                String name = f.getName();
                return f.isDirectory() || (f.isFile() && (name.matches(inputPattern)));
            }
        };
        this.pool = pool;
        this.monitor = monitor;
        
    }

    public void scan() {
        File inputPathFile = config.getInputPathFile();
        scan(inputPathFile);
    }
    
    private void scan(File file) {
        logger.debug("scaning directory: " + file);
        if (file.isDirectory()) {
            File[] dirList = file.listFiles(fileFilter);
            for (File dirFile : dirList) {
                if (dirFile.isDirectory()) {
                    scan(dirFile);
                } else {
                    queueFile(dirFile);
                }
            }
        } else {
            queueFile(file);
        }
        
    }
    
    private void queueFile(File file) {
        logger.debug("queueing file: " + file);
        pool.submit(new LoadTask(file, config, monitor));
    }

}
