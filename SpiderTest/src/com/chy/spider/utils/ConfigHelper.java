package com.chy.spider.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigHelper {

//	private static Log logger = LogFactory.getLog(ConfigHelper.class);
	private static Logger logger=LoggerFactory.getLogger(ConfigHelper.class);
	
	private static Properties properties = new Properties();
	private static String filePath;
	
	static{
		properties=getProperties();
	}
	
	private static Properties getProperties(){
		
		String baseHome=System.getProperty("user.dir");
		filePath=baseHome+File.separator+"conf"+File.separator+"config.properties";
		if(logger.isInfoEnabled()){
			logger.info("load config file: "+filePath);
		}
		
		Properties props=new Properties();
		try {
			props.load(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		Properties props=System.getProperties();
//		@SuppressWarnings("unchecked")
//		Enumeration<String> names=(Enumeration<String>)props.propertyNames();
//		while(names.hasMoreElements()){
//			String name=names.nextElement();
//			System.out.println(name+" = "+ System.getProperty(name));
//		}
		return props;
	}
	
	public static String getParameter(String paramName){
		return properties.getProperty(paramName);
	}
	
	public static void main(String[] args) {
//		properties=getProperties();
		Enumeration<String> names=(Enumeration<String>)properties.propertyNames();
		while(names.hasMoreElements()){
			String name=names.nextElement();
			System.out.println(name+" = "+ properties.getProperty(name));
		}
	}

}
