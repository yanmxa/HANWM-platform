package com.ameat.component.city;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.ameat.utils.ConfigurationLoader;
import com.ameat.utils.Generator;

public class DevicesManager {
	private static final float CV = (float) 0.2; 
	
	// 器具占比依次
	private static Map<String, Float> waterProportion;
	// 节水器具占的最小比例
	private static Map<String, Float> minRatio;
	
	// 节水器具所占比
	private Map<String, Float> previousRatio;

	private Map<String, Float> currentRatio;
	
	private static Set<String> names;
 	
	public DevicesManager() {
		
		waterProportion = new HashMap<>();
		minRatio = new HashMap<>();
		previousRatio = new HashMap<>();
		currentRatio = new HashMap<>();
		names = new HashSet<>();
		
		Properties properties = ConfigurationLoader.loadConfProperties("simulation.resident.properties");
		properties.forEach((key, value) -> {
			String keyStr = key.toString();
			String valueStr = value.toString();
			if(keyStr.startsWith("device")) {
				String tempKey = keyStr.substring(keyStr.indexOf(".")+1);
				names.add(tempKey);
				waterProportion.put(tempKey, (float) (Float.valueOf(valueStr.substring(0, valueStr.indexOf("%")))/100.0));
				minRatio.put(tempKey,  (float) (Float.valueOf(valueStr.substring(valueStr.indexOf("-")+1, valueStr.indexOf("]")))));
				previousRatio.put(tempKey, (float) Generator.Normal(1, CV));
				double temp = 1;
				do {
					temp = Generator.Normal(1, CV);
				} while(temp > 1.0);
				currentRatio.put(tempKey, (float) temp);
			}
		});
		
	}
	
	public static void main(String[] args) {
		new DevicesManager();
	}
	public Map<String, Float> getWaterProportion() {
		return waterProportion;
	}

	public Map<String, Float> getMinRatio() {
		return minRatio;
	}

	public Map<String, Float> getPreviousRatio() {
		return previousRatio;
	}
	
	public void setPreviousRatio(String key, float value) {
		this.previousRatio.put(key, value);
	}
	public Map<String, Float> getCurrentRatio() {
		return currentRatio;
	}
	
	public void setCurrentRatio(String key, float value) {
		this.currentRatio.put(key, value);
	}
	
	public static Set<String> getNames() {
		return names;
	}
}
