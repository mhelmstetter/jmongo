package com.mongodb.datagen.handlers;

import java.io.IOException;

import com.mongodb.datagen.DataElement;

/**
 * This interface is a slight variation of the
 * Chain of Responsibility pattern (GoF).
 *
 * Handler nodes return a boolean value indicating if
 * the chain should continue.
 *
 * @author mhelmstetter
 *
 */
public interface DataHandler {

    public boolean handleMessageEvent(DataElement message) throws Exception;
    
    public void close() throws IOException;

}
