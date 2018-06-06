package com.ameat.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ameat.tables.Table;

public class ImportNoaaData {
	private final static String stationPath = "/Users/zmeat/gsod/isd-china.txt";
	private final static String dataPath = "/Users/zmeat/aa/";
	private final static DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");

	public static void main(String[] args) {
		importStation();
		importMeteorologies();
	}

	public static void importMeteorologies() {
		if(new Table("Meteorology").count() <= 0) {
			ArrayList<String> stationids = new ArrayList<String>();
			ArrayList<String> wbanids = new ArrayList<String>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(stationPath));
				String str;
				while((str = br.readLine()) != null){
					stationids.add(str.substring(6, 12));
					wbanids.add(str.substring(13, 18));
			    }

				for(int i=0; i<stationids.size(); i++) {
					for (int year = 2005; year<=2018; year++) {
						File file = new File(dataPath+stationids.get(i)+"-"+wbanids.get(i)+"-"+year+".op");
						if(!file.exists()) {
							continue;
						}else {
							System.out.println(dataPath+stationids.get(i)+"-"+wbanids.get(i)+"-"+year+".op");
							BufferedReader brmete = new BufferedReader(
								new FileReader(dataPath+stationids.get(i)+"-"+wbanids.get(i)+"-"+year+".op")
							);
							String str2;
							Table meteorology = new Table("Meteorology");
							brmete.readLine();//jump first row
							while((str2 = brmete.readLine()) != null){
								Map<String, Object> record = new HashMap<String, Object>();
								int station_id = Integer.valueOf(stationids.get(i));
								LocalDate startyearDateTime = format.parseLocalDate(str2.substring(14, 23).trim());
								String date = startyearDateTime.toString("yyyy-MM-dd");
								Double avg_temp = Double.valueOf(str2.substring(25, 30).trim()) > 9999
										? 0.0 : (Double.valueOf(str2.substring(25, 30).trim())-32)*5/9;
								Double max_temp = Double.valueOf(str2.substring(103, 108).trim()) > 9999
										? 0.0 : (Double.valueOf(str2.substring(103, 108).trim())-32)*5/9;
								Double min_temp = Double.valueOf(str2.substring(111, 116).trim()) > 9999
										? 0.0 : (Double.valueOf(str2.substring(111, 116).trim())-32)*5/9;
								Double slp = Double.valueOf(str2.substring(45, 52).trim());
								Double stp = Double.valueOf(str2.substring(56, 63).trim());
								Double precipition = Double.valueOf(str2.substring(119, 123).trim()) > 99
										? 0.0 : Double.valueOf(str2.substring(119, 123).trim())*10*2.54;
								Double sndp = Double.valueOf(str2.substring(125, 130).trim())  > 999
										? 0.0 : Double.valueOf(str2.substring(125, 130).trim())*2.54;

								record.put("station_id", station_id);
								record.put("date", date);
								record.put("avg_temp", avg_temp);
								record.put("min_temp", min_temp);
								record.put("max_temp", max_temp);
								record.put("slp", slp);
								record.put("stp", stp);
								record.put("precipition", precipition);
								record.put("sndp", sndp);

								meteorology.insertOne(record);
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void importStation() {
		if(new Table("Station").count()  <= 0) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(stationPath));
				Table station = new Table("Station");
				String str;
			    while((str = br.readLine()) != null){
				    	int station_id = Integer.valueOf(str.substring(6, 12));
				    	String station_name = str.substring(19, 49).trim();
				    	double lat = str.substring(64, 70).trim().length() == 0 ? 0 : Float.valueOf(str.substring(64, 70));
				    	double lon = str.substring(64, 70).trim().length() == 0 ? 0 : Float.valueOf(str.substring(72, 80));
					LocalDate startyearDateTime = format.parseLocalDate(str.substring(88, 96));
					LocalDate endyearDateTime = format.parseLocalDate(str.substring(97, 105));
					String startyear = startyearDateTime.toString("yyyy-MM-dd");
					String endyear = endyearDateTime.toString("yyyy-MM-dd");
					Map<String, Object> record = new HashMap<String, Object>();
					String[] poi = getLocation(lat, lon);
					record.put("station_id", station_id);
					record.put("station_name", station_name);
					record.put("lat", lat);
					record.put("lon", lon);
					record.put("province", poi.length > 0 ? poi[0] : null);
					record.put("city", poi.length > 1 ? poi[1] : null);
					record.put("district", poi.length > 2 ? poi[2] : null);
					record.put("startyear", startyear);
					record.put("endyear", endyear);
					station.insertOne(record);
			    }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private static String[] getLocation(double lat, double lon) {
		String urlString = "http://gc.ditu.aliyun.com/regeocoding?l="+lat+","+lon+"&type=010";
		String res = "";
	    try {
			URL url = new URL(urlString);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(),"UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				res += line+"\n";
				
			}
			in.close();
	    } catch (Exception e) {
	    		System.out.println("error in wapaction,and e is " + e.getMessage());
	    }

	    String arr[] = new String[0];
	    try {
	    		JSONObject jsonObject = new JSONObject(res);
	    		JSONArray jsonArray = new JSONArray(jsonObject.get("addrList").toString());
	    		JSONObject j_2 = new JSONObject(jsonArray.get(0).toString());
	    		String allAdd = j_2.getString("admName");
	    		arr = allAdd.split(",");
	    } catch (Exception e) {
	    		System.out.println(res);
	    }

		return arr;
	}

}
