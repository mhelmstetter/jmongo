package com.mongodb.datagen;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class DataGenerator {
	
	protected final static ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
    "datagen/spring.xml");
	
	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		DataService d = (DataService)applicationContext.getBean("dataService");
		long iterations = 100000;
		if (args.length > 0) {
			iterations = Long.parseLong(args[0]);
		}
		d.generate(iterations);
	}

}
