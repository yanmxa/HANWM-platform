package com.ameat.component.meteorology;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ameat.component.comunications.ComunicationInterface;
import com.ameat.component.comunications.MeteorologyToCountry;
import com.ameat.component.interfaces.CompInterface;
import com.ameat.simulation.TimeController;

public class MeteorologyImpl implements CompInterface, MeteorologyToCountry{

	private Logger logger = Logger.getLogger(this.getClass());
	private MeteorologySchedule meteorologySchedule;
	private TimeController tc;

	@Override
	public void init(TimeController timeController, Map<String, ComunicationInterface> comunications) {
		this.tc = timeController;
		this.meteorologySchedule = new MeteorologySchedule(timeController, comunications);
		
		logger.info("Init time : "+this.tc.getCurrentTime() + "component = Meteorology is inited !");
	}

	@Override
	public void compute() {
		
		this.meteorologySchedule.loadToCompute();
		
		logger.info(" Time : "+this.tc.getCurrentTime() + "component = Meteorology is computed !");
	}

	@Override
	public void anchorCompute() {
		
		this.meteorologySchedule.loadToAnchorCompute();
		
		logger.info("Anchor time : "+this.tc.getCurrentTime() + "component = Meteorology is anchor computed !");
	}

	@Override
	public void finished() {
		
		this.meteorologySchedule.loadToFinish();
		
		logger.info("Deadline : "+this.tc.getCurrentTime() + "component = Meteorology is finished !");
	}

	
	
	@Override
	public Location getMeteInfo(String locationStr) {
		return this.meteorologySchedule.locatedToComunicate(locationStr);
	}


}
