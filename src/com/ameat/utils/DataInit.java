package com.ameat.utils;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.javalite.activejdbc.DB;

/**
 * 将模拟需要的降雨、潮白河流量以、气象监测点位置信息、气象水文信息数据迁移至数据库
 * created by zhangjunwei 2017/11
 */
public class DataInit {
	private final static String dataPath = "resource/data/";

	public static void main(String[] args) {
		DB db = new DB("DataInit");
		db.open();
		try {
			File dir = new File(UnifyFileName.convert(dataPath));
			if(dir.exists()) {
				File[] files = dir.listFiles();
				for (File file : files) {
					if(file.isFile() && file.getName().endsWith(".sql")) {
						System.out.println(file);
						@SuppressWarnings("resource")
						Scanner s = new Scanner(file).useDelimiter(";");
						StringBuffer statement = new StringBuffer();
						while(s.hasNextLine()) {
							String line = s.nextLine().trim();
							if(!line.matches("^.?[#*/-].*")) {
								statement.append(line);
								if(line.endsWith(";")) {
									db.exec(statement.toString());
									statement = new StringBuffer();
								}
							}
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}
}
