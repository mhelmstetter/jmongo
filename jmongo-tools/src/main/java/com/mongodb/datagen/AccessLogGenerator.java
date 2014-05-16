package com.mongodb.datagen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.util.RandomUtils;


public class AccessLogGenerator {

	protected final static ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
			"spring.xml");

	protected static final Logger logger = LoggerFactory.getLogger(AccessLogGenerator.class);

	private RandomServiceImpl randomService;
	private TextDictionary senders;
	private File sendersFile;
	private File followersFile;
	private TextDictionary followers;

	public void setRandomService(RandomServiceImpl randomService) {
		this.randomService = randomService;
	}

	public String writerLogLine() {

		StringBuilder sb = new StringBuilder("\"POST /");
		String sender = randomService.dict(senders);
		sb.append(sender);
		sb.append("?message=");

		int maxLength = RandomUtils.getRandomInt(95, 128);
		sb.append(getTweetTextEncoded(maxLength));
		sb.append(" HTTP/1.1\"");
		return sb.toString();
	}
	
	public String readerLogLine() {

		StringBuilder sb = new StringBuilder("\"GET /");
		String follower = randomService.dict(followers);
		sb.append(follower);
		sb.append(" HTTP/1.1\"");
		return sb.toString();
	}

	private String getTweetTextEncoded(int maxLength) {
		StringBuilder sb = new StringBuilder();
		String first = randomService.dict("eng");
		sb.append(StringUtils.capitalize(first));
		while (true) {
			String next = randomService.dict("eng");
			if (sb.length() + next.length() < maxLength) {
				sb.append(" ");
				sb.append(next);
			} else {
				break;
			}
		}
		if (sb.length() >= maxLength) {
			sb.delete(maxLength - 1, sb.length() - 1);
		}
		try {
			return URLEncoder.encode(sb.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("file.encoding", "UTF-8");

		AccessLogGenerator dg = (AccessLogGenerator) applicationContext
				.getBean("accessLogGenerator");
		if (args.length == 0) {
			System.out.println("Usage: java " + dg.getClass().getName() + "<# to generate> <sendersFile> <followersFile>");
			System.exit(-1);
		}
		Integer iterations = Integer.parseInt(args[0]);
		
		

		dg.sendersFile = new File(args[1]);
		dg.senders = new TextDictionary(dg.sendersFile);
		
		dg.followersFile = new File(args[2]);
		dg.followers = new TextDictionary(dg.followersFile);
		
		dg.generate(iterations);

	}

	private void generate(Integer iterations) {
		BufferedWriter writerWriter = null;
		BufferedWriter readerWriter = null;
		try {
			File outputDir = sendersFile.getParentFile();
			File writerOutputFile = new File(outputDir, "WriterAccessLog.txt");
			writerWriter = new BufferedWriter(new FileWriter(writerOutputFile));
			
			File readerOutputFile = new File(outputDir, "ReaderAccessLog.txt");
			readerWriter = new BufferedWriter(new FileWriter(readerOutputFile));
			
			for (int i = 0; i < iterations; i++) {
				String message = writerLogLine();
				writerWriter.write(message);
				writerWriter.newLine();
				readerWriter.write(readerLogLine());
				readerWriter.newLine();
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} finally {
			try {
				writerWriter.close();
				readerWriter.close();
			} catch (IOException e) {
			}
		}


	}

}
