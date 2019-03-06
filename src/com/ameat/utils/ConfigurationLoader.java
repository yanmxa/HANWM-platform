package com.ameat.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationLoader {
	private static String configurationDir = "configuration/";
	private static String tail = ".properties";
	private static String charset = "UTF-8";
	
	private static Properties loadProperties(String pathName){
		Properties properties = null;
		pathName = UnifyFileName.convert(pathName);
		InputStream configStream;
		try {
			configStream = new FileInputStream(pathName);
			properties = new Properties();
			properties.load(configStream);
			configStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("load config file: " + pathName + " failed");
		}
		return properties;
	}
	
	public static Properties loadConfProperties(String fileName) {
		return loadProperties(configurationDir+fileName);
	}
	
	/**
	 * get one config property from a file  eg: config("filename.property.property.property");
	 * @param str
	 * @return String
	 */
	public static String config(String str) {
		String property = "";
		try {
			int index = str.indexOf('.');
			String fileName = str;
			String propertyName = "";
			
			if(index > 0) {
				fileName = str.substring(0, index);
				propertyName = str.substring(index+1);
			}
			
			Properties properties = loadProperties(configurationDir+fileName+tail);
			property = properties.getProperty(propertyName);
			property = new String(property.getBytes("ISO-8859-1"), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return property;
	}
	
	
	/**
	 * get all of the configs under a namespace eg: configs("database.development"), 
	 * this will return all the database configs under development namespace without the "development" prefix;
	 * @param str
	 * @return Map<String, String>
	 */
	public static Map<String, String> configs(String str) {
		Map<String, String> confs = new HashMap<String, String>();
		
		int index = str.lastIndexOf('.');
		String fileName = str;
		String propertyName = null;
		
		if(index > 0) {
			propertyName = str.substring(str.lastIndexOf('.') + 1);
		}
		
		Properties properties = loadProperties(configurationDir + fileName);
		Enumeration<Object> keys = properties.keys();

		if(propertyName != null) {
			while(keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				
				if(key.startsWith(propertyName)) {
					int sindex = str.indexOf('.');
					String sKey = "";
					
					if(sindex > 0) {
						sKey = key.substring(key.indexOf('.')+1);
						try {
							confs.put(sKey, new String(properties.getProperty(key).getBytes("ISO-8859-1"), charset));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						confs.put(key, new String(properties.getProperty(key).getBytes("ISO-8859-1"), charset));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}else {
			while(keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				try {
					confs.put(key, new String(properties.getProperty(key).getBytes("ISO-8859-1"), charset));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return confs;
	}

	/**
	 * get all of the configs under a namespace eg: configs("database.properties" ,"development"),
	 * this will return all the database configs under development namespace without the "development" prefix;
	 * @return Map<String, String>
	 */
	public static Map<String, String> configs(String filePath, String prefix) {
		Map<String, String> confs = new HashMap<String, String>();

		String fileName = filePath;

		Properties properties = loadProperties(configurationDir + fileName);
		Enumeration<Object> keys = properties.keys();

		if(prefix != null) {
			while(keys.hasMoreElements()) {
				String key = keys.nextElement().toString();

				if(key.startsWith(prefix)) {
					int sindex = key.indexOf('.');
					String sKey = "";
					if(sindex > 0) {
						sKey = key.substring(key.indexOf('.')+1);
						try {
							confs.put(sKey, new String(properties.getProperty(key).getBytes("ISO-8859-1"), charset));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						confs.put(key, new String(properties.getProperty(key).getBytes("ISO-8859-1"), charset));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}else {
			while(keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				try {
					confs.put(key, new String(properties.getProperty(key).getBytes("ISO-8859-1"), charset));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return confs;
	}
}
