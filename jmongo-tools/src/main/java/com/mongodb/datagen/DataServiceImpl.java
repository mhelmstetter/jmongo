package com.mongodb.datagen;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.datagen.handlers.DataHandler;
import com.mongodb.util.RandomUtils;
import com.mongodb.util.VelocityServiceImpl;

public class DataServiceImpl implements DataService {
	
    protected static final Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);
	
	private VelocityServiceImpl velocityService;
	//private RandomServiceImpl randomService;
	private IndexedDictionary templateDictionary;
	
	private List<DataHandler> dataHandlers;
	
	Map model = new HashMap();
	
	public DataServiceImpl() {
	}
	
	public DataElement generateData(String template) {
		//StringBuilder sb = new StringBuilder("http://");
		StringBuilder sb = new StringBuilder();
		//http://${rand.dict("eng")}/${rand.dict("eng")}/${randomUtils.getRandomString(10)}
//		sb.append(randomService.dict("eng"));
//		sb.append("/");
//		sb.append(randomService.dict("eng"));
//		sb.append("/");
		sb.append(RandomUtils.getRandomString(10));
		sb.append(".xml");
		String key = sb.toString();
		model.put("key", key);
		String result = velocityService.mergeTemplateIntoString(template, model);
		return new DataElement(key, result);
	}
	
	public void generate(long iterations) {
		logger.debug("Starting generate for " + iterations + " iterations.");
		for (int i = 0; i < iterations; i++) {
		
			if (i % 1000 == 1) {
				logger.debug("Generated " + i + " records");
			}
		
			for (String template : templateDictionary.getEntries()) {
				DataElement d = generateData(template);
				logger.debug(d.getData());
				for (DataHandler dh : dataHandlers) {
					try {
						dh.handleMessageEvent(d);
					} catch (Exception e) {
						logger.error("handleMessageEvent() error:", e);
					}
				}
			}
		}
		
		for (DataHandler dh : dataHandlers) {
			try {
				dh.close();
			} catch (IOException e) {
				logger.error("Error closing", e);
			}
		}
		
	}


	public void setVelocityService(VelocityServiceImpl velocityService) {
		this.velocityService = velocityService;
	}


//	public void setTemplates(List<String> templates) {
//		this.templates = templates;
//	}

	public void setDataHandlers(List<DataHandler> dataHandlers) {
		this.dataHandlers = dataHandlers;
	}



	public void setTemplateDictionary(IndexedDictionary templateDictionary) {
		this.templateDictionary = templateDictionary;
	}

}
