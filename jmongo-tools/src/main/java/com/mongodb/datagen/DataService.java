package com.mongodb.datagen;

public interface DataService {

	public DataElement generateData(String template);
	
	public void generate(long iterations);

}