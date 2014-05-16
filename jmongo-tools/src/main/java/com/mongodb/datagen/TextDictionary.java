package com.mongodb.datagen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TextDictionary extends AbstractTextDictionary implements IndexedDictionary {
	
	private List<String> entries;
	
	public TextDictionary() {
		entries = new ArrayList<String>();
	}
	
	public TextDictionary(File file) throws IOException {
		this();
		this.setSourceFile(file);
	}

	@Override
	public String get(int index) {
		return entries.get(index);
	}

	@Override
	public Collection<String> getEntries() {
		return entries;
	}

	@Override
	public void add(String obj) {
		entries.add(obj);
	}

	@Override
	public int getSize() {
		return entries.size();
	}

}
