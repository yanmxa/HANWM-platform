package com.ameat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructuralProperties {
	/**
	 * StructuralProperties for the project
	 * @param str eg: "application.properties"
	 * @return
	 */
	public static List<Map<String, String>> struct(String str) {
		List<Map<String, String>> stucturalPropers = new ArrayList<Map<String, String>>();
		Map<String, String> properties = ConfigurationLoader.configs(str);
		properties.forEach((key, value) -> {
			List<Map<String, String>> tempStucturalPropers = new ArrayList<Map<String, String>>();
			for(int i=0; i<value.split(",").length; i++) {
				if(stucturalPropers.size() == 0) {
					Map<String, String> tempPro = ConfigurationLoader.configs(str);
					tempPro.put(key, value.split(",")[i]);
					stucturalPropers.add(tempPro);
				} else {
					if(i == 0) {
						for(int j=0; j<stucturalPropers.size(); j++) {
							stucturalPropers.get(j).put(key, value.split(",")[i]);
						}
					} else {
						for(Map<String, String> item : stucturalPropers) {
							Map<String, String> tempPro2 = ConfigurationLoader.configs(str);
							item.forEach((k, v) -> {
								tempPro2.put(k, v);
							});
							tempPro2.put(key, value.split(",")[i]);
							tempStucturalPropers.add(tempPro2);
						}
					}
				}
			}
			stucturalPropers.addAll(tempStucturalPropers);
		});
		
		return stucturalPropers;
	}
}
