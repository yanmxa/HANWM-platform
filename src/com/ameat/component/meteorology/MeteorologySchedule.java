package com.ameat.component.meteorology;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ameat.component.comunications.ComunicationInterface;
import com.ameat.simulation.TimeController;

public class MeteorologySchedule {

	private Logger logger = Logger.getLogger(this.getClass());
	private Map<String, ComunicationInterface> connections;
	private TimeController timeSchedule;
	
	private Map<String, Location> locationInfos;

	
	public MeteorologySchedule(TimeController timeController, Map<String, ComunicationInterface> comunications) {
		
		this.connections = comunications;
		this.timeSchedule = timeController;
		
		this.locationInfos = new HashMap<String, Location>();
		
		this.locationInfos.put("LuanPing", new Location(timeController, "LuanPing"));
		this.locationInfos.put("ChiCheng", new Location(timeController, "ChiCheng"));
		this.locationInfos.put("FengNing", new Location(timeController, "FengNing"));
		
	}
	
	
	protected void loadToCompute() {
		
		Set<String> locations = locationInfos.keySet();
		for(String location : locations) {
			locationInfos.get(location).loadMeteInfo();
		}
		
		
	}
	
	protected Location locatedToComunicate(String locationStr) {
		return locationInfos.get(locationStr);
	}
	
	
	protected void loadToAnchorCompute() {
		
		
		
	}
	
	
	protected void loadToFinish() {
		
		
		
	}
	
	
}
