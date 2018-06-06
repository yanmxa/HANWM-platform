package com.ameat.simulation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import static com.ameat.utils.ConfigurationLoader.config;


public class TimeController {

	private DateTime startTime;
	private DateTime endTime;
	private DateTime currentTime;
	private int stepValue;
	private String stepUnit;
	private DateTime anchorTime;
	
	public TimeController() {
		String startStr = config("simulation.starttime");
		String endStr = config("simulation.endtime");
		String anchorStr = config("simulation.anchortime");
		String[] stepStr = config("simulation.timestep").split("-");
		
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
//		if (this.stepUnit.equals("day"))
			this.currentTime = this.currentTime.plusDays(this.stepValue);
//		if (this.stepUnit.equals("month")) this.currentTime = this.currentTime.plusMonths(this.stepValue);
//		if (this.stepUnit.equals("hour")) this.currentTime = this.currentTime.plusHours(this.stepValue);
//		return this.currentTime;
	}
	
	public void nextYearSart() {
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
	
}
