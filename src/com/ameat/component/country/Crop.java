package com.ameat.component.country;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.ameat.component.meteorology.Location;
import com.ameat.simulation.TimeController;

public abstract class Crop {
	protected Map<String, Integer> L;
	protected Map<String, Double> K;
	protected int totalDay;
	protected TimeController tc;
	protected DateTime sowingTime;
	protected DateTime harvestTime;
	// 土壤水消耗比:随ETc变化
	protected double p;
	// 根系层深度
	protected double Zrmin;
	protected double Zrmax;
	// 最大产量 kg/mu
	protected double yieldMax;
	// 生产期内的Ky累加
	protected double KyAccu;
	
	
	/**
	 * 获取每天的实际作物产量
	 * @param location
	 * @param ETo
	 * @param Dr
	 * @return
	 */
	protected double getYieldActually(Location location, double ETo, double Dr) {
		double ETc = this.getKc()*ETo;
		double ETcadj = this.getKs(location, ETo, Dr)*ETc;
		double Ky = this.getKy();
		double temp = (1 - (ETcadj/ETc))*Ky;
		return (1-temp)*this.getYeildMaxOfDay();
	}
	
	protected double getETcadj(Location location, double ETo, double Dr) {
		double ETc = this.getKc()*ETo;
		double ETcadj = this.getKs(location, ETo, Dr)*ETc;
		return ETcadj;
	}
	
	/**
	 * 获取水分胁迫系数Ks,不在生长期返回-1.0
	 * @param location:通过Location获取土壤的田间持水量和田间枯萎量
	 * @param ETo: 参考作物蒸发蒸腾量
	 * @param Dr : 根系层中的消耗水量(mm)
	 * @return
	 */
	public double getKs(Location location, double ETo, double Dr) {
		if(this.getZr() < 0.0) return -1.0;
		double Ks = 1.0;
		double TotalAvailableWater = 1000.0*(location.getFC() - location.getWP())*this.getZr();
		double ReadliyAvailableWater = this.getP(ETo)*TotalAvailableWater;
		if(Dr > ReadliyAvailableWater && Dr < TotalAvailableWater) 
			Ks = (TotalAvailableWater - Dr)/(TotalAvailableWater - ReadliyAvailableWater);
		if(Dr >= TotalAvailableWater) Ks = 0.0;
		return Ks;
	}
	
	/**
	 * 获取产量响应因子，不再生长期返回-1.0
	 * @return
	 */
	public double getKy() {
		int currentDay = this.tc.getCurrentTime().getDayOfYear() - this.getSowingTime().getDayOfYear();
		if(currentDay < 0 || currentDay > this.totalDay) return -1.0;
		else if( currentDay < this.L.get("Lini") ) return this.K.get("Kyini");
		else if( currentDay < this.L.get("Lini")+this.L.get("Ldev") ) return this.K.get("Kydev");
		else if( currentDay < this.L.get("Lini")+this.L.get("Ldev")+this.L.get("Lmid") ) return this.K.get("Kymid");
		else return this.K.get("Kylate");
	}
	
	/**
	 * 获取当天的最大产量，不在生长期返回-1.0
	 * @return
	 */
	public double getYeildMaxOfDay() {
		double KyOfDay = this.getKy();
		if(KyOfDay < 0.0) return -1.0;
		return this.yieldMax * KyOfDay / this.getKyAccu();
	}
	
	/**
	 * Readliy Available Water,不在生长期返回-1.0
	 * @param location : 地理位置
	 * @param ETo ： 参考作物蒸发蒸腾量
	 * @return
	 */
	public double getRAW(Location location, double ETo) {
		if(this.getZr() < 0.0) return -1.0;
		double TotalAvailableWater = 1000.0*(location.getFC() - location.getWP())*this.getZr();
		double ReadliyAvailableWater = this.getP(ETo)*TotalAvailableWater;
		return ReadliyAvailableWater;
	}
	
	/**
	 * 获取当前时间的作物系数，不在生长前期返回-1.0
	 * @return 
	 */
	public double getKc() {
		double Kc = -1.0;
		int currentDay = this.tc.getCurrentTime().getDayOfYear() - this.getSowingTime().getDayOfYear();
		if(currentDay < 0 || currentDay > this.totalDay) return Kc;
		
		if( currentDay <= this.L.get("Lini") ) Kc = this.K.get("Kcini");
		else if( currentDay <= (this.L.get("Lini")+this.L.get("Ldev")) ) 
			Kc = this.K.get("Kcini") + ( (currentDay-this.L.get("Lini")) * (this.K.get("Kcmid") - this.K.get("Kcini"))/this.L.get("Ldev") );
		else if( currentDay <= (this.L.get("Lini")+this.L.get("Lmid") +this.L.get("Ldev"))) Kc = this.K.get("Kcmid");
		else Kc = this.K.get("Kcmid") + (currentDay - (this.L.get("Lini")+this.L.get("Lmid")+this.L.get("Ldev")))
				* (this.K.get("Kcend")-this.K.get("Kcmid"))/this.L.get("Llate");
		return Kc;
	}
	
	protected int getTotalDay() {
		return this.totalDay;
	}
	
	/**
	 * 根据当前时间获取本年的收获时间
	 * @param currentTime
	 * @return
	 */
	protected DateTime getHarvestTime() {
		while(this.harvestTime.getYear() < this.tc.getCurrentTime().getYear()) this.harvestTime = this.harvestTime.plusYears(1);
		return this.harvestTime;
	}
	/**
	 * 根据当前时间获取本年度播种时间
	 * @param currentTime
	 * @return
	 */
	protected DateTime getSowingTime() {
		while(this.sowingTime.getYear() < this.tc.getCurrentTime().getYear()) this.sowingTime = this.sowingTime.plusYears(1);
		return this.sowingTime;
	}
	
	/**
	 * 获取根系层深度,不在生长期返回 -1.0
	 * @return  double： m 
	 */
	protected double getZr() {
		double Zr = -1.0;
		int currentDay = this.tc.getCurrentTime().getDayOfYear() - this.getSowingTime().getDayOfYear();
		if(currentDay < 0 || currentDay > this.totalDay) return Zr;
		
		if( currentDay < this.L.get("Lini")+this.L.get("Ldev") ) 
			Zr = this.Zrmin + (currentDay*(this.Zrmax-this.Zrmin)/(this.L.get("Lini")+this.L.get("Ldev")));
		else 
			Zr = this.Zrmax;
		return Zr;
	}
	
	/**
	 * 获取土壤水消耗比
	 * @param ETo，kc
	 * @return mm/day
	 */
	private double getP(double ETo) {
		return (5.0 - this.getKc()*ETo)*0.04+this.p;
	}
	private double getKyAccu() {
		double result = this.K.get("Kyini") * this.L.get("Lini") + this.K.get("Kydev") * this.L.get("Ldev")+
				this.K.get("Kymid") * this.L.get("Lmid") + this.K.get("Kylate") * this.L.get("Llate");
		return result;
	}
}

class Rice extends Crop{
	public Rice(TimeController tc) {
		this.tc = tc;
		this.L = new HashMap<String, Integer>();
		this.L.put("Lini", 30);
		this.L.put("Ldev", 30);
		this.L.put("Lmid", 60);
		this.L.put("Llate", 30);
		this.K = new HashMap<String, Double>();
		this.K.put("Kcini", 1.05);
		this.K.put("Kcmid", 1.20);
		this.K.put("Kcend", 0.75);
		this.K.put("Kyini", 1.00);
		this.K.put("Kydev", 1.09);
		this.K.put("Kymid", 1.32);
		this.K.put("Kylate", 0.5);
		this.yieldMax = 716.0;
		this.p = 0.20;
		this.Zrmax = 0.6;  
		this.Zrmin = 0.1;
		
		this.totalDay = 150;
		this.sowingTime = new DateTime(this.tc.getCurrentTime().getYear(),4,20,0,0);
		this.harvestTime = this.sowingTime.plusDays(this.totalDay);
	}
	
}

class Maize extends Crop{
	public Maize(TimeController tc) {
		this.tc = tc;
		this.L = new HashMap<String, Integer>();
		this.L.put("Lini", 30);
		this.L.put("Ldev", 40);
		this.L.put("Lmid", 50);
		this.L.put("Llate", 30);
		this.K = new HashMap<String, Double>();
		this.K.put("Kcini", 0.30);
		this.K.put("Kcmid", 1.20);
		this.K.put("Kcend", 0.35);
		this.K.put("Kyini", 0.3);
		this.K.put("Kydev", 1.15);
		this.K.put("Kymid", 0.5);
		this.K.put("Kylate", 0.2);
		this.yieldMax = 540;
		this.p = 0.55;
		this.Zrmax = 1.2;
		this.Zrmin = 0.05;
		
		this.totalDay = 150;
		this.sowingTime = new DateTime(this.tc.getCurrentTime().getYear(),4,20,0,0);
		this.harvestTime = this.sowingTime.plusDays(this.totalDay);
	}
	
}