package com.ameat.simulation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import static com.ameat.utils.ConfigurationLoader.config;

import java.util.Map;


public class TimeController {

	private DateTime startTime;
	private DateTime endTime;
	private DateTime currentTime;
	private int stepValue;
	private String stepUnit;
	private DateTime anchorTime;
	private int simulationId;

	public TimeController(Map<String, String> params) {
		String startStr = params.get("starttime");
		String endStr = params.get("endtime");
		String anchorStr = params.get("anchortime");
		String[] stepStr = params.get("timestep").split("-");
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");  
		this.startTime = DateTime.parse(startStr, formatter);  
		this.endTime = DateTime.parse(endStr, formatter);  
		this.stepValue = Integer.parseInt(stepStr[0]);
		this.stepUnit = stepStr[1];
		this.currentTime = this.startTime;
		this.anchorTime = DateTime.parse(anchorStr, formatter);
	}

	/**
	 * @return DateTime : Current Time plus a Time Step. The time step could be modified
	 */
	public void nextStepTime() {
		if (this.stepUnit.equals("day"))
			this.currentTime = this.currentTime.plusDays(this.stepValue);
		if (this.stepUnit.equals("year"))
			this.currentTime = this.currentTime.plusYears(this.stepValue);
//		if (this.stepUnit.equals("month")) this.currentTime = this.currentTime.plusMonths(this.stepValue);
//		if (this.stepUnit.equals("hour")) this.currentTime = this.currentTime.plusHours(this.stepValue);
//		return this.currentTime;
	}
	
	public void nextYearStart() {
		while(this.startTime.getYear() <= this.currentTime.getYear()) this.startTime = this.startTime.plusYears(1);
		this.currentTime = this.startTime;
	}
	
	/**
	 * @return DateTime : AnchorTime plus a Year. Generally the anchor time should plus T(a cycle)
	 */
	public DateTime nextAnchorTime() {
		this.anchorTime = this.anchorTime.plusYears(1);
		return this.anchorTime;
	}
	
	public DateTime getEndTime() {
		return this.endTime;
	}
	
	public DateTime getCurrentTime() {
		return this.currentTime;
	}
	
	public DateTime getAnchorTime() {
		return this.anchorTime;
	}

	public int getSimulationId() {
		return simulationId;
	}

	public void setSimulationId(int simulationId) {
		this.simulationId = simulationId;
	}
}
