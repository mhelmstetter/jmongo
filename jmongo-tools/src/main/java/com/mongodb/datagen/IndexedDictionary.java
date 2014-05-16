package com.mongodb.datagen;

import java.util.Collection;

public interface IndexedDictionary extends Dictionary {

	public String get(int index);
	
	public int getSize();
	
	public void add(String obj);
	
	public Collection<String> getEntries();

}
