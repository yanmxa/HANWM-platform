package com.ameat.component.country;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ameat.component.comunications.ComunicationInterface;
import com.ameat.component.comunications.MeteorologyToCountry;
import com.ameat.component.meteorology.Location;
import com.ameat.simulation.TimeController;
import com.ameat.tables.Table;

public class CountrySchedule{
	private TimeController timeController;
	private MeteorologyToCountry MeteInfo;
	private Map<String, Crop> cropInfos;
	private Map<String, List<Farmer>> farmers;
	private boolean isWaterLimited = false;
	
	public CountrySchedule(TimeController timeController, Map<String, ComunicationInterface> comunications) {
		this.timeController = timeController;
		this.cropInfos = new HashMap<String, Crop>();
		this.cropInfos.put("rice", new Rice(timeController));
		this.cropInfos.put("maize", new Maize(timeController));
		// 根据farmer_init表生成农民对象，注：内部用到成员变量timeController，cropInfos
		this.farmers = constructFarmers();
		this.MeteInfo = (MeteorologyToCountry) comunications.get("meteorology");
	}

	protected void loadToCompute() {
		
		Set<String> locations = this.farmers.keySet();
		for(String location : locations) {
			List<Farmer> farmers = this.farmers.get(location);
			Location meteInfo = this.MeteInfo.getMeteInfo(location);
			for(Farmer farmer : farmers) {
				// 更新数据
				farmer.dayByDay(meteInfo, this.isWaterLimited);
				// 插入数据库
//				farmer.recordToFarmerTrace(meteInfo);
			}
		}
		
	}
	
	protected void loadToAnchorCompute() {
		
		Table table = new Table("CountryTrace");
		Map<String, Object> record = new HashMap<String, Object>();
		
		int sim_id = 0 ;
		double precipitation=0;
		double cropIrrigation=0;
		double riceIrrigation=0;
		double maizeIrrigation=0;
		double cropIncome=0;
		double riceIncome=0;
		double maizeIncome=0;
		

		Set<String> locations = this.farmers.keySet();
		for(String location : locations) {
			List<Farmer> farmers = this.farmers.get(location);
			for(Farmer farmer : farmers) {
				// 年终总结
				farmer.summaryYearEnd();
			}
			for(Farmer farmer : farmers) {
				// 写入数据库
				
				sim_id = farmer.getSimId();
				precipitation = farmer.getPrecipitation();
				riceIrrigation += farmer.getRiceCousumeWater();
				cropIrrigation += farmer.getCousumeWater();
				maizeIrrigation += farmer.getMaizeConsumeWater();
				cropIncome += farmer.getCropIncome();
				riceIncome += farmer.getRiceIncome();
				maizeIncome += farmer.getMaizeIncome();
				
				farmer.recordToFarmerAnchor();
				
			}
			for(Farmer farmer : farmers) {
			    // 更新策略(需要根据所有农民的状况来评估自己是否更新策略)
				farmer.updateStrategy(this.farmers);
			}
			
		}
		
		record.put("sim_id", sim_id);
		record.put("time", this.timeController.getCurrentTime().toString("yyyy-MM-dd"));
		record.put("precipitation", precipitation);
		record.put("crop_irrigation", cropIrrigation);
		record.put("rice_irrigation", riceIrrigation);
		record.put("maize_irrigation", maizeIrrigation);
		record.put("crop_income", cropIncome);
		record.put("rice_income", riceIncome);
		record.put("maize_income", maizeIncome);
		
		table.insertOne(record);
	}
	
	protected void loadToFinish() {
		
	}
	
	
	/**
	 * 根据farmer_init表生成农民对象,并且确定是否有用水限制
	 * @return  Map<String, List<Farmer>>     location : List<Farmer> 
	 */
	private Map<String, List<Farmer>> constructFarmers() {
		Map<String, List<Farmer>> farmers = new HashMap<String, List<Farmer>>();
		
		Table farmerInit = new Table("FarmerInit");
		Table simulation = new Table("Simulation");
		long threadId = Thread.currentThread().getId();
		Map<String, Object> record = simulation.getOne("id desc", "thread_id = "+ threadId);
		int simulationId = Integer.parseInt(record.get("id").toString());
		double waterLimit = Double.parseDouble(record.get("water_limit").toString());
		if(waterLimit > 0.0) this.isWaterLimited = true;
		
		List<Map<String, Object>> records = farmerInit.gets("location = 'ChiCheng'", "sim_id="+simulationId);
		farmers.put("ChiCheng", this.convertRecordsToFarmer(records));
		List<Map<String, Object>> records1 = farmerInit.gets("location = 'FengNing'", "sim_id="+simulationId);
		farmers.put("FengNing", this.convertRecordsToFarmer(records1));
		List<Map<String, Object>> records2 = farmerInit.gets("location = 'LuanPing'", "sim_id="+simulationId);
		farmers.put("LuanPing", this.convertRecordsToFarmer(records2));
		
		return farmers;
	}
	
	private List<Farmer> convertRecordsToFarmer(List<Map<String, Object>> records){
		List<Farmer> farmers = new ArrayList<Farmer>();
		for(Map<String, Object> record : records) {
			farmers.add(new Farmer(Integer.valueOf(record.get("id").toString()), this.timeController, this.cropInfos, Integer.valueOf(record.get("sim_id").toString()), 
					Integer.valueOf(record.get("farmer_no").toString()), record.get("location").toString(),
					Integer.parseInt(record.get("farmer_number").toString()), Double.valueOf(record.get("crop_area").toString()), 
					Double.valueOf(record.get("mu").toString()), Double.valueOf(record.get("learn").toString()), 
					Double.valueOf(record.get("radius").toString()), Double.valueOf(record.get("sense").toString()), 
					Double.valueOf(record.get("water_permit").toString())));
		}
		return farmers;
	}
	
}
