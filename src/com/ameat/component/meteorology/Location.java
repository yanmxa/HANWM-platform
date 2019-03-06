package com.ameat.component.meteorology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.joda.time.DateTime;

import com.ameat.simulation.TimeController;
import com.ameat.tables.Table;

import static com.ameat.utils.ConfigurationLoader.*;

public class Location {
	// σ 为Stefan-Boltzmann常数(4.903*10-9MJK-4m-2day-1),
	protected double sigma = 4.903*Math.pow(10, -9);
	// 2m/s
	protected double u2 = 2;
	// 调整系数:靠海会受气影响Krs=0.19，内陆为Krs=0.16
	protected double Krs = 0.16;
	// 土质类型：沙壤土确定田间持水量和凋萎点水量 m3 / m-3  (FAO-56)
	protected double FC = 0.24;
	protected double WP = 0.05;
	// m
	protected double H;
	// latitude
	protected double angle;
	protected double cent;
	protected boolean isNorth;

	// 湿度计常数Kpa/℃
	protected double gamma;
	// Ra
	protected double Ra;

	// 纬度
	protected String lat;

	// 经度
	protected String lng;

	protected String chineseName;
	protected String englishName;

	protected TimeController timeController;

	protected double Tmin;
	protected double Tmax;
	protected double Tmean;
	protected double precip;
	protected double ETo;


	public Location(TimeController tc, String englishName) {
		Map<String, String> area = configs("location.properties", englishName);
		final ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
		this.timeController = tc;
		this.englishName = englishName;
		this.chineseName = area.get("chinese_name");
		this.lat = area.get("lat");
		this.lng = area.get("lng");
		this.H = Double.valueOf(area.get("H"));
		try {
			String script = area.get("gamma").replace("{H}", String.valueOf(this.H));
			this.gamma = Double.valueOf(jse.eval(script).toString());
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		this.angle = Double.valueOf(area.get("angle"));
		this.cent = Double.valueOf(area.get("cent"));
		this.isNorth = Boolean.valueOf(area.get("is_north"));
	}

	/**
	 * 每次需要加载气象数据
	 */
	protected void loadMeteInfo() {
		Map<String, Object> meteorology = getMeteInfoByCoordinates();

		this.Tmax = meteorology.containsKey("max_temp") ? Double.valueOf(meteorology.get("max_temp").toString()) : -1.0;
		this.Tmin = meteorology.containsKey("min_temp") ? Double.valueOf(meteorology.get("min_temp").toString()) : -1.0;
		this.Tmean = meteorology.containsKey("avg_temp") ? Double.valueOf(meteorology.get("avg_temp").toString()) : -1.0;
		this.precip = meteorology.containsKey("precipition") ? Double.valueOf(meteorology.get("precipition").toString()) : -1.0;

		this.ETo = computeETo();

	}

	public double getPrecip() {
		return this.precip;
	}
	public double getETo() {
		return this.ETo;
	}


	/**
	 * 计算参考作物腾发量：缺少气象数据是ETo的计算，根据传入的位置从数据库中取出位置信息，从location对象中获取该位置的其他参数 mm/day
	 * @param locationStr
	 * @return
	 */
 	private double computeETo() {

		// KPa / C
		double p = 4098 * ( 0.6108*Math.exp((17.27*this.Tmean)/(this.Tmean+237.3)) );
		double t = (this.Tmean+237.3)*(this.Tmean+237.3);
		double delta = p/t;

		// 获取湿度数据估计
		double Tdew = this.Tmin;
		double ea = getE(Tdew);
		double eTmax = getE(this.Tmax);
		double eTmin = getE(this.Tmin);
		double es = (eTmax + eTmin) / 2.0;
		double RHmax = (100*ea) / eTmin; // %
		double RHmin = (100*ea) / eTmax; // %
		double RHmean = (RHmin + RHmax) / 2.0;

		// 获取辐射数据估计
		double Ra = getRa();//根据当前时间获取到
		double Rs =this.Krs * Math.sqrt(this.Tmax-this.Tmin) * Ra;
		double Rso = (0.75+(2*200)/100000)*Ra;
		double Rns = (1-0.23)*Rs;

		double Tmaxk = this.Tmax + 273.16;
		double Tmink = this.Tmin + 273.16;
		double Rnl = this.sigma * ((Math.pow(Tmaxk, 4)+Math.pow(Tmink, 4))/2)*
				(0.34-(0.14*Math.sqrt(ea))) * ((1.35*Rs/Rso)-0.35);
		double Rn = Rns - Rnl;
		double G = 0; //以1天或10天为单位

		double top = (0.408*delta*(Rn-G))+(900.0/(this.Tmean+273)*this.gamma*this.u2*(es-ea));
		double down = (delta+(gamma*(0.34*this.u2 + 1)));
		double ETo = top/down;
		return ETo;
	}


	/**
	 * @param T : ℃
	 * @return e : Kpa：saturation vapour pressure at the air temperature T [kPa]
	 */
	private double getE(double t) {
		double c = (17.27*t) / (t+237.3);
		double e = 0.6108*Math.exp(c);
		return e;
	}


	/**
	 * 获取该地区土壤类型的田间持水量 m3 / m-3
	 * @return
	 */
	public double getFC() {
		return this.FC;
	}
	
	/**
	 * 获取该地区土壤类型的枯萎点水量 m3 / m-3
	 * @return
	 */
	public double getWP() {
		return this.WP;
	}

	public double getH() {
		return this.H;
	}


	/**
	 * 根据时间获取点前位置天顶辐射
	 * @param time ： DateTime
	 * @return Ra : double MJ/m2/day
	 */
	private double getRa() {
		if(this.isNorth) return getNRa(this.angle, this.cent, this.timeController.getCurrentTime());
		else return getSRa(this.angle, this.cent, this.timeController.getCurrentTime());
	}

	/**
	 * 根据时间和地点(纬度)获取北半球天顶辐射
	 * @param angle
	 * @param cent
	 * @param time
	 * @return Ra : MJ/m2/day
	 */
	public static double getNRa(double angle, double cent, DateTime time) {
		double decimal = angle + (cent/60);
		double radian = decimal * (Math.PI/180);

		double Gsc = 0.0820; // MJ/m2/min
		double dr = 1 + (0.033*Math.cos(((2*Math.PI)/365.0)*time.getDayOfYear()));
		double delt = 0.409*Math.sin(((2*Math.PI)/365.0)*time.getDayOfYear() - 1.39);
		double ws = Math.acos(-(Math.tan(radian)*Math.tan(delt)));

		double Ra = (24*60/Math.PI) * Gsc*dr*( (ws*Math.sin(radian)*Math.sin(delt)) +
				(Math.cos(radian)*Math.cos(delt)*Math.sin(ws)) );

		return Ra;
	}
	
	
	public static double getSRa(double angle, double cent, DateTime time) {
		return getNRa(-angle, -cent, time);
	}
	
	// 根据经纬度获取最近的区域的水文信息
	protected Map<String, Object> getMeteInfoByCoordinates() {
		Table meteTable = new Table("Meteorology");
		String distanceStr = "select *,acos(sin((PI() / 180)*"+this.lat+") * sin((PI() / 180)*lat) + cos((PI() / 180)*"+this.lat+") * cos((PI() / 180)*lat) "+
				"* cos((PI() / 180)*lon - (PI() / 180)*"+this.lng+")) * 6371 as distance from stations where endyear>'2017-12-12' order by distance asc;";
		
		List<Map<String, Object>> stations = meteTable.exec(distanceStr);
		Map<String, Object> station = stations.size() > 0 ? stations.get(0) : new HashMap<String, Object>();
		String StationId = station.containsKey("station_id") ? station.get("station_id").toString() : "-1";
		Map<String, Object> meteorology = meteTable.getOne("id asc", "date='"+this.timeController.getCurrentTime().toString("yyyy-MM-dd")+"'", "station_id="+StationId);
		
		return meteorology;
	} 

}


