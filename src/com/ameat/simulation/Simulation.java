package com.ameat.simulation;

import com.ameat.component.comunications.ComunicationInterface;
import com.ameat.component.interfaces.CompInterface;
import com.ameat.tables.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class Simulation {
	
	private TimeController timeController;
	private Map<String, String> parameters;
	private Logger logger = Logger.getLogger(this.getClass());
	private Map<String, CompInterface> components = new HashMap<String, CompInterface>();;
	private Map<String, ComunicationInterface> comunications = new HashMap<String, ComunicationInterface>();;
	private ArrayList<Integer> sequence = new ArrayList<Integer>();
	
	public Simulation(TimeController timeController, Map<String, String> parameters) {
		this.timeController = timeController;
		this.parameters = parameters;
		this.register();
	}
	
	public int run() {
		// record this time simulation record to database
		// todo 目前农业灌溉用调用的是recordSimulation(), 城市居民用水调用的是recordResidentSimulation(), 可以考虑将其改为配置项
		int simId = this.recordSimulation();
		this.timeController.setSimulationId(simId);
//		this.recordResidentSimulation();

		// simulation start, components init;
		this.simulationStart();
		 
		// simulation cycle
		while( this.timeController.getCurrentTime().isBefore(this.timeController.getEndTime()) ) {
			
			for(Integer i : this.sequence) {
				CompInterface component = this.components.get(i+"");
				component.compute();
			}
			
			if(this.timeController.getAnchorTime().isEqual(this.timeController.getCurrentTime())) {
				for(Integer i : this.sequence) {
					CompInterface component = this.components.get(i+"");
					component.anchorCompute();
				}
				this.timeController.nextAnchorTime();
				this.timeController.nextYearStart();
				logger.info(this.timeController.getCurrentTime()+"  computions starting !");
				continue;
			}
			this.timeController.nextStepTime();
		}
		
		// simulation end, components finished
		this.simulationEnd();

		return simId;
	}
	

	private void simulationEnd() {
		for(Integer i : this.sequence) {
			CompInterface component = this.components.get(""+i);
			component.finished();
		}
		logger.info("simulation end, components finished !");
	}

	private void simulationStart() {
		for(Integer i : this.sequence) {
			CompInterface component = this.components.get(""+i);
			component.init(this.timeController, this.comunications);
		}
		logger.info("simulation start, components are initialized !");
	}

	
	/**
	 * register component , register comunications, register sequence
	 */
	private void register() {
		Collection<String> values = new ArrayList<String>();
		this.parameters.forEach((key, value) -> {
			if(key.startsWith("component")) {
				values.add(value);
			}
		});
		for(String value: values) {
			try {
				Class<?> claz = Class.forName(value.split(":")[0]);
				CompInterface component = (CompInterface) claz.newInstance();
				this.components.put(value.split(":")[1].toString(), component);
				this.sequence.add(Integer.valueOf(value.split(":")[1].toString()));
				String ClazName = value.substring(value.lastIndexOf(".")+1, value.indexOf(":"));
				String subKey = ClazName.substring(0, ClazName.indexOf("Impl"));
				File subFile = new File("src/com/ameat/component/comunications");
				File[] subListFiles = subFile.listFiles();
				for(File f : subListFiles) {
					if(f.getName().startsWith(subKey+"To")) {
						comunications.put(subKey.toLowerCase(), component);
					}
				}
				logger.info(component + "has been registered !");
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		Collections.sort(this.sequence);
	}
	
	private int recordSimulation() {
		Table sim = new Table("Simulation");
		Map<String, Object> record = new HashMap<String, Object>();
		StringBuffer comps = new StringBuffer();
		StringBuffer crop_areas = new StringBuffer();
		StringBuffer farmer_numbers = new StringBuffer();
		
		record.put("thread_id", Thread.currentThread().getId());
		record.put("start_time", this.parameters.get("starttime"));
		record.put("end_time", this.parameters.get("endtime"));
		record.put("time_step", this.parameters.get("timestep"));
		record.put("anchor_time", this.parameters.get("anchortime"));
		record.put("mu", this.parameters.get("mu"));
		record.put("learn", this.parameters.get("learn"));
		record.put("radius", this.parameters.get("radius"));
		record.put("sense", this.parameters.get("sense"));
		record.put("cv", this.parameters.get("cv"));
		this.components.forEach((k, v) -> {
			comps.append(v.toString().split("@")[0]+":"+k+";");
		});
		this.parameters.forEach((k, v) -> {
			if(k.contains("crop_area")) {
				crop_areas.append(k.split("\\.")[1]+":"+v+";");
			}
			if(k.startsWith("farmer_number")) {
				farmer_numbers.append(k.split("\\.")[1]+":"+v+";");
			}
		});
		record.put("components", comps.toString());
		record.put("crop_area", crop_areas.toString());
		record.put("farmer_number", farmer_numbers.toString());
		record.put("water_limit", this.parameters.get("water_limit"));
		
		return sim.insertReturnKey(record);

	}
	
	private void recordResidentSimulation() {
		Table sim = new Table("ResidentSimulation");
		Map<String, Object> record = new HashMap<String, Object>();
		StringBuffer comps = new StringBuffer();
		
		record.put("thread_name", Thread.currentThread().getName());
		record.put("start_time", this.parameters.get("starttime"));
		record.put("end_time", this.parameters.get("endtime"));
		record.put("time_step", this.parameters.get("timestep"));
		record.put("anchor_time", this.parameters.get("anchortime"));
		record.put("radius", this.parameters.get("radius"));
		record.put("alpha", this.parameters.get("alpha"));
		record.put("beta", this.parameters.get("beta"));
		this.components.forEach((k, v) -> {
			comps.append(v.toString().split("@")[0]+":"+k+";");
		});
		record.put("components", comps.toString());
	
		record.put("faucet", this.parameters.get("device.faucet"));
		record.put("toilet", this.parameters.get("device.toilet"));
		record.put("shower", this.parameters.get("device.shower"));
		record.put("laundry", this.parameters.get("device.laundry"));
		record.put("resident_number", this.parameters.get("resident_number"));
		record.put("consume", this.parameters.get("consume"));
		
		sim.insertReturnKey(record);
	}
}
