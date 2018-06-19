package com.ameat.component.city;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ameat.component.comunications.ComunicationInterface;
import com.ameat.component.interfaces.CompInterface;
import com.ameat.simulation.TimeController;

public class CityImpl implements CompInterface {
	
	private Logger logger = Logger.getLogger(this.getClass());
	private TimeController tc;
	private CitySchedule cs;
	
	@Override
	public void init(TimeController timeController, Map<String, ComunicationInterface> comunications) {
		this.tc = timeController;
		this.cs = new CitySchedule(timeController, comunications);
		this.cs.loadToinit();
		logger.info("Init time : "+this.tc.getCurrentTime() + "component = city is inited !");
	}

	@Override
	public void anchorCompute() {
		cs.loadToAchorTime();
		logger.info("Init time : "+this.tc.getCurrentTime() + "component = city is anchorComputed !");
	}

	@Override
	public void compute() {
		
//		logger.info("Init time : "+this.tc.getCurrentTime() + "component = city is computed !");
	}

	@Override
	public void finished() {
		
		logger.info("Init time : "+this.tc.getCurrentTime() + "component = city is finished !");
	}

}
