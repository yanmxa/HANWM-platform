package com.ameat.component.country;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ameat.component.comunications.ComunicationInterface;
import com.ameat.component.interfaces.CompInterface;
import com.ameat.simulation.TimeController;
import com.ameat.tables.Table;
import com.ameat.utils.Generator;

public class CountryImpl implements CompInterface {
	
	private Logger logger = Logger.getLogger(this.getClass());
	private TimeController tc;
	private CountrySchedule countrySchedule;

	@Override
	public void init(TimeController timeController, Map<String, ComunicationInterface> comunications) {
		this.tc = timeController;
		this.generateFarmerInfos(this.tc.getSimulationId());
		this.countrySchedule = new CountrySchedule(timeController, comunications);
		logger.info("Init time : "+this.tc.getCurrentTime() + "component = Country is inited !");
	}
	
	@Override
	public void compute() {
		
		this.countrySchedule.loadToCompute();
		
		logger.info(" Time : "+this.tc.getCurrentTime() + "component = Country is computed !");
	}
	@Override
	public void anchorCompute() {
		
		this.countrySchedule.loadToAnchorCompute();
		
		logger.info("Anchor time : "+this.tc.getCurrentTime() + "component = Country is anchor computed !");
	}
	
	@Override
	public void finished() {
		
		this.countrySchedule.loadToFinish();
		
		logger.info("Deadline : "+this.tc.getCurrentTime() + "component = Country is finished !");
	}


	
	
	/**
	 * 根据simulation 表中的记录生成农民初始化市所需要的参数
	 */
	private static void generateFarmerInfos(int simulationId) {
		long threadId = Thread.currentThread().getId();
		Map<String, Object> simulation = new Table("Simulation").getOne("id desc", "id = "+ simulationId);
		Table FarmerInit = new Table("FarmerInit");
		Map<String, String> farmerNumbers = colonToMap(simulation.get("farmer_number").toString());
		farmerNumbers.forEach((k, v) -> {
			for(int i=1; i<=Integer.valueOf(v); i++) {
				Map<String, Object> record = new HashMap<String, Object>();
				Map<String, String> cropAreas = colonToMap(simulation.get("crop_area").toString());
				Double cv = Double.valueOf(simulation.get("cv").toString());
				Double mu = Generator.Normal(Double.valueOf(simulation.get("mu").toString()), cv);
				Double learn = Generator.Normal(Double.valueOf(simulation.get("learn").toString()), cv);
				Double radius = Generator.Normal(Double.valueOf(simulation.get("radius").toString()), cv);
				Double sense = Generator.Normal(Double.valueOf(simulation.get("sense").toString()), cv);
				Double waterPermit = Double.valueOf(simulation.get("water_limit").toString()) < 0.0
						? -1: getWaterPermit(simulation, Double.valueOf(cropAreas.get(k)));
					
				record.put("sim_id", simulationId);
				record.put("location", k);
				record.put("farmer_no", i);
				record.put("mu", mu);
				record.put("learn", learn);
				record.put("radius", radius);
				record.put("sense", sense);
				record.put("farmer_number", Integer.valueOf(v));
				record.put("crop_area", cropAreas.get(k));
				record.put("water_permit", waterPermit);
				FarmerInit.insertOne(record);
			}
		});
	}
	
	// 转换形如 k1:v1;k2:v2;k3:v3;  这样的字符串为map
	private static Map<String, String> colonToMap(String colon) {
		Map<String, String> result = new HashMap<String, String>();
		String[] items = colon.split(";");
		for(String item : items) {
			result.put(item.split(":")[0], item.split(":")[1]);
		}
		
		return result;
	}
	
	private static double getWaterPermit(Map<String, Object> simulation, double cropAreasPerFarmer) {
		Map<String, String> cropAreas = colonToMap(simulation.get("crop_area").toString());
		Map<String, String> farmerNumbers = colonToMap(simulation.get("farmer_number").toString());
		double areaCounts = 0.0;
		for(String key : farmerNumbers.keySet()) {
			areaCounts += Double.valueOf(cropAreas.get(key))*Double.valueOf(farmerNumbers.get(key));
		}
		return cropAreasPerFarmer*(Double.valueOf(simulation.get("water_limit").toString())/areaCounts);
	}

}
