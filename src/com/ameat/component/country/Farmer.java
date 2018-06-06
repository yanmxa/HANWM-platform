package com.ameat.component.country;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ameat.component.meteorology.Location;
import com.ameat.simulation.TimeController;
import com.ameat.tables.Table;
import com.ameat.utils.Generator;
/**
 * 每年做两件事儿：归零+调整策略
 * @author yanmeng
 *
 */
public class Farmer {
	private int farmer_id;
	private TimeController tc;
	private Map<String, Crop> cropInfos;
	
	private Table farmerTraceTable = new Table("FarmerTrace");
	private Map<String, Object> record = new HashMap<String, Object>();
	
	// 一次定型
	private String locationStr;
	private int simId;
	private int farmerNumber;             
	private int farmerNo;                //每个村按照0开始编码
	private double cropArea;             // 作物面积
	
	private double learn;         // 学习因子
	private double radius;        // 信息半径
	private double sense;         // 敏感因子
	
	// 限制用水，每天累加，年度更新
	private double waterLimit;           // 作物用水限制， -1(<0)为不限用水，(>=0)为限制用水 ,单位:立方米 一次定型
	private double daysWater;            // 立方米，每天不变
	private double waterRemaining;       // 立方米，每天更新，年底不变
	
	// 每天不变，年度更新 - 4
	private double currentMu;            // 旱地占总耕种面积的比例
	private double previousMu;
	private double riceArea;      
	private double maizeArea;     
	// 每天没有，年底结算 - 4
	private double cropIncome;    // 作物中收入，包含补贴
	private double riceIncome;    
	private double maizeIncome;    
	private double maizeConsumeWater; // 立方米
	private double riceConsumeWater;  // 立方米
	private double consumeWater;   // 立方米
	
	// 每天累加，年度清零 - 7
	private double precipitation;        // 年度累计降雨量  mm
	private double ricePrecipitation;      //水稻生长期间累计降雨量 mm
	private double maizePrecipitation;     // 玉米生长期间累计降雨量 mm
	private double riceYield;              // 水稻累计亩产量  kg/mu
	private double maizeYield;             // 玉米累计亩产量  kg/mu
	private double riceIrrigation;         // 水稻累计灌溉量  mm
	private double maizeIrrigation;        // 玉米累计灌溉量  mm

	// 每天更新，年度清零 - 5
	private double precip;           // 该地区每天的降雨情况
	private double riceIrri;         // 水稻灌溉  mm
	private double riceDr;           // 当天根系层水量消耗 mm
	private double maizeIrri;
	private double maizeDr; 
	
	/**
	 * 从数据库中读取生成参数，构造农民对象
	 * @param tc
	 * @param simId
	 * @param farmerId    每个村统一按0开始编排
	 * @param location    地点
	 * @param farmerNum   每个村的农民数量
	 * @param cropArea    生成面积
	 * @param mu          生成旱地占比
	 * @param learn       生成学习因子
	 * @param radius      生成学习半径
	 * @param senes       生成敏感度
	 * @param waterPermit 根据耕地面积，确定该户农民能提取得水量，单位:立方米 m^3   
	 */
	public Farmer(int farmer_id, TimeController tc, Map<String, Crop> cropInfos, int simId, int farmerId, String location, int farmerNum, 
			double cropArea, double mu, double learn, double radius, double senes, double waterPermit) {
		
		// 初始化表示参数
		this.tc =  tc;
		this.cropInfos = cropInfos;
		
		this.simId = simId;
		this.farmer_id = farmer_id;
		this.farmerNo = farmerId;
		this.locationStr = location;
		this.currentMu = mu;
		this.previousMu = Generator.Normal(mu, 0.4);
		while(this.previousMu < 0.0 || this.previousMu > 1.0) this.previousMu = Generator.Normal(mu, 0.4);
		this.learn = learn;
		this.radius = radius;
		this.sense = senes;
		this.cropArea = cropArea;
		
		this.farmerNumber = farmerNum;
		this.waterLimit = waterPermit;
		// 初始化每年的调整参数
		this.maizeArea = mu*cropArea;
		this.riceArea = this.cropArea - this.maizeArea;
		
		// 限制用水
		this.daysWater = waterPermit / 
				Math.abs(cropInfos.get("rice").getHarvestTime().getDayOfYear() - cropInfos.get("maize").getSowingTime().getDayOfYear());
		this.waterRemaining = 0;
		
		// 清零
		yearValueToZero();
	}
	
	
	/**
	 * 年尾总结：计算扣除投入以及结合当前情况下的 总收入
	 */
	protected void summaryYearEnd() {
		this.maizeIncome = (this.maizeYield * this.maizeArea * this.getMaizePrice()) - (this.maizeArea*this.getMaizeCost())
				+ (this.maizeArea*this.getMaizeSubsidy());
		this.riceIncome = (this.riceYield * this.riceArea * this.getRicePrice()) - (this.riceArea*this.getRiceCost());
		this.cropIncome = this.maizeIncome + this.riceIncome;
		this.riceConsumeWater = this.mmTom3(this.riceArea, this.riceIrrigation);
		this.maizeConsumeWater = this.mmTom3(this.maizeArea, this.maizeIrrigation);
		this.consumeWater = this.riceConsumeWater + this.maizeConsumeWater;
	}
	
	protected void recordToFarmerAnchor() {
		
		Table table = new Table("FarmerAnchor");
		Map<String, Object> record = new HashMap<String, Object>();
		
		record.put("time", this.tc.getCurrentTime().toString("yyyy-MM-dd"));
		record.put("farmer_id", this.farmer_id);
		record.put("previous_mu", this.previousMu);
		record.put("current_mu", this.currentMu);
		record.put("precipitation", this.precipitation);
		record.put("rice_precipitation", this.ricePrecipitation);
		record.put("maize_precipitation", this.maizePrecipitation);
		record.put("consumer_water", this.consumeWater);
		record.put("rice_consumer_water", this.riceConsumeWater);
		record.put("maize_consumer_water", this.maizeConsumeWater);
		record.put("rice_yield", this.riceYield);
		record.put("maize_yield", this.maizeYield);
		record.put("rice_area", this.riceArea);
		record.put("maize_area", this.maizeArea);
		record.put("crop_income", this.cropIncome);
		record.put("rice_income", this.riceIncome);
		record.put("maize_income", this.maizeIncome);
		record.put("water_limit", this.waterLimit);
		record.put("water_remaining", this.waterRemaining);
		
		table.insertOne(record);
	}
	
	/**
	 * 农民每年会根据年尾总结情况进行策略调整(包含下一年所有变量的初始化)
	 * @param farmers
	 */
	protected void updateStrategy(Map<String, List<Farmer>> totalFarmers) {
		double incomePerArea = this.cropIncome / this.cropArea; 
		List<Farmer> farmers = totalFarmers.get(this.locationStr);
		int r = (int) this.radius;
		
		// 对高出的差价敏感
		int count1 = 0;
		double muAccumu1 = 0.0;
		// 对达到对数量敏感
		int count2 = 0;
		double muAccumu2 = 0.0;
		
		int priority = 0;
		
		// 遍历邻居，进行学习
		for(int i=0; i<r; i++) {
			int index = (this.farmerNo + (i+1)) % farmers.size();
			double incomePerAreaI =  farmers.get(index).getCropIncome() / farmers.get(index).getCropArea();
			double muI = farmers.get(index).getMu();
			if(incomePerAreaI > incomePerArea*2*this.sense && incomePerAreaI > incomePerArea) {
				muAccumu1 += muI;
				count1++;
				priority=1;
			}
			
			if(incomePerAreaI > incomePerArea) {
				muAccumu2 += muI;
				count2++;
			}
		}
		
		double muTemp = this.currentMu;
		if(priority == 1) {
			this.currentMu = this.learn*((muAccumu1/count1) - this.currentMu) + this.previousMu;
		}else {
			if( (count2 / r) > (0.5*this.sense) ) this.currentMu = this.learn*((muAccumu2/count2) - this.currentMu) + this.previousMu;
			else this.currentMu = this.learn*(this.currentMu - this.previousMu) + this.previousMu;
		}
		this.previousMu = muTemp;
		
		this.maizeArea = this.cropArea * this.currentMu;	
		this.riceArea = this.cropArea - this.maizeArea;
		yearValueToZero();
	}
	
	
	/**
	 * 农民根据气象数据更新每天的数据
	 * @param ET
	 * @param isWaterLimited
	 */
	protected void dayByDay(Location meteInfo, boolean isWaterLimited) {
		if(isWaterLimited) daysWithWaterStress(meteInfo);
		else daysWithoutWaterStress(meteInfo);
	}

	/**
	 * 将该农民的信息插入到数据库中
	 */
	protected void recordToFarmerTrace(Location meteInfo) {
//		Map<String, Object> record = new HashMap<String, Object>();
		
		double ETo = meteInfo.getETo();
		record.put("farmer_id", this.farmer_id);
		record.put("time", this.tc.getCurrentTime().toString("yyyy-MM-dd"));
		record.put("remaining_water", this.waterRemaining);
		record.put("precip", this.precip);
		record.put("eto", ETo);
		
		record.put("zr_rice", this.cropInfos.get("rice").getZr());
		record.put("raw_rice", this.cropInfos.get("rice").getRAW(meteInfo, ETo));
		record.put("irri_rice", this.riceIrri);
		record.put("kc_rice", this.cropInfos.get("rice").getKc());
		record.put("etc_rice", this.cropInfos.get("rice").getKc()*ETo);
		record.put("ks_rice", this.cropInfos.get("rice").getKs(meteInfo, ETo, this.riceDr));
		record.put("etcadj_rice", ETo*this.cropInfos.get("rice").getKc()*this.cropInfos.get("rice").getKs(meteInfo, ETo, this.riceDr));
		record.put("dr_rice", this.riceDr);
		record.put("yield_rice", this.riceYield);
		
		record.put("zr_maize", this.cropInfos.get("maize").getZr());
		record.put("raw_maize", this.cropInfos.get("maize").getRAW(meteInfo, ETo));
		record.put("irri_maize", this.maizeIrri);
		record.put("kc_maize", this.cropInfos.get("maize").getKc());
		record.put("etc_maize", this.cropInfos.get("maize").getKc()*ETo);
		record.put("ks_maize", this.cropInfos.get("maize").getKs(meteInfo, ETo, this.maizeDr));
		record.put("etcadj_maize", ETo*this.cropInfos.get("maize").getKc()*this.cropInfos.get("maize").getKs(meteInfo, ETo, this.maizeDr));
		record.put("dr_maize", this.maizeDr);
		record.put("yield_maize", this.maizeYield);
		
		this.farmerTraceTable.insertOne(record);
	}
	
	protected void daysWithWaterStress(Location meteInfo) {
		//每天更新
		double ETo = meteInfo.getETo();
		this.waterRemaining += this.daysWater;
		
		// 每天更新      1
		this.precip = meteInfo.getPrecip();
		
		// 每天累加      1
		this.precipitation += this.precip;
		// ------------------------------------更新水稻灌溉 、 消耗------------------------------------//
		// 每天更新      2，3
		if(this.cropInfos.get("rice").getKc() < 0.0) {
			this.riceIrri = 0.0;
			this.riceDr = 0.0;
		}else {
			if(this.riceDr < this.cropInfos.get("rice").getRAW(meteInfo, ETo)) {
				this.riceIrri = 0.0;
			} else {
				this.riceIrri = this.riceDr - this.precip;
				if(this.riceIrri < 0.0) this.riceIrri = 0.0;
				// 现在持有的水量所能灌溉的深度
				double tempmm = this.muTomm(this.riceArea, this.waterRemaining);
				if(this.riceIrri < tempmm) {
					// 剩余水量为灌溉后所剩的水量
					this.waterRemaining = this.waterRemaining - this.mmTom3(this.riceArea, this.riceIrri);
				}else {
					// 所需灌溉深度大于现有水量
					this.riceIrri = tempmm;
					this.waterRemaining = 0.0;
				}
			}
			this.riceDr = this.riceDr - this.precip - this.riceIrri + this.cropInfos.get("rice").getETcadj(meteInfo, ETo, this.riceDr);
			if(this.riceDr < 0.0) this.riceDr = 0.0;
			// 每天累加 2,3,4
			this.ricePrecipitation += this.precip;
			this.riceIrrigation += this.riceIrri;
			this.riceYield += this.cropInfos.get("rice").getYieldActually(meteInfo, ETo, this.riceDr);
		}
		
		// ------------------------------------更新玉米灌溉 、 消耗------------------------------------//
		if(this.cropInfos.get("maize").getKc() < 0.0) {
			this.maizeIrri = 0.0;
			this.maizeDr = 0.0;
		}else {
			if(this.maizeDr < this.cropInfos.get("maize").getRAW(meteInfo, ETo)) {
				this.maizeIrri = 0.0;
			} else {
				this.maizeIrri = this.maizeDr - this.precip;
				if(this.maizeIrri < 0.0) this.maizeIrri = 0.0;
				// 现在持有的水量所能灌溉的深度
				double tempmm = this.muTomm(this.maizeArea, this.waterRemaining);
				if(this.maizeIrri < tempmm) {
					// 剩余水量为灌溉后所剩的水量
					this.waterRemaining = this.waterRemaining - this.mmTom3(this.maizeArea, this.maizeIrri);
				}else {
					// 所需灌溉深度大于现有水量
					this.maizeIrri = tempmm;
					this.waterRemaining = 0.0;
				}
			}
			this.maizeDr = this.maizeDr - this.precip - this.maizeIrri + this.cropInfos.get("maize").getETcadj(meteInfo, ETo, this.maizeDr);
			if(this.maizeDr < 0.0) this.maizeDr = 0.0;
			
			this.maizePrecipitation += this.precip;
			this.maizeIrrigation += this.maizeIrri;
			this.maizeYield += this.cropInfos.get("maize").getYieldActually(meteInfo, ETo, this.maizeDr);
		}
		
	}

/**
 * 没有水分胁迫的情况下，农民每天的策略
 * @param ET
 */
	protected void daysWithoutWaterStress(Location meteInfo) {
		
		double ETo = meteInfo.getETo();
		// 每天更新      1
		this.precip = meteInfo.getPrecip();
		// 每天累加      1
		this.precipitation += this.precip;
		// ------------------------------------更新水稻灌溉 、 消耗------------------------------------//
		// 每天更新      2，3
		
		if(this.cropInfos.get("rice").getKc() < 0.0) {
			this.riceIrri = 0.0;
			this.riceDr = 0.0;
		}else {
			if(this.riceDr < this.cropInfos.get("rice").getRAW(meteInfo, ETo)) {
				this.riceIrri = 0.0;
			} else {
				this.riceIrri = this.riceDr - this.precip;
				if(this.riceIrri < 0.0) this.riceIrri = 0.0;
			}
			this.riceDr = this.riceDr - this.precip - this.riceIrri + ETo*this.cropInfos.get("rice").getKc();
			if(this.riceDr < 0.0) this.riceDr = 0.0;
			// 每天累加 2,3,4
			this.ricePrecipitation += this.precip;
			this.riceIrrigation += this.riceIrri;
			this.riceYield += this.cropInfos.get("rice").getYeildMaxOfDay();
		}
		// ------------------------------------更新玉米灌溉 、 消耗------------------------------------//
		// 每天更新   4，5
		if(this.cropInfos.get("maize").getKc() < 0.0) {
			this.maizeIrri = 0.0;
			this.maizeDr = 0.0;
		}else {
			if(this.maizeDr < this.cropInfos.get("maize").getRAW(meteInfo, ETo)) {
				this.maizeIrri = 0.0;
			}else {
				this.maizeIrri = this.maizeDr - this.precip;
				if(this.maizeIrri < 0.0) this.maizeIrri = 0.0;
			}
			this.maizeDr = this.maizeDr - this.precip - this.maizeIrri + ETo*this.cropInfos.get("maize").getKc();
			if(this.maizeDr < 0.0) this.maizeDr = 0.0;
			
			// 每天累加 5，6，7
			this.maizePrecipitation += this.precip;
			this.maizeIrrigation += this.maizeIrri;
			this.maizeYield += this.cropInfos.get("maize").getYeildMaxOfDay();
		}
	
	}
	
	
	
	
	public double getMu() {
		return this.currentMu;
	}
	protected double getCropArea() {
		return this.cropArea;
	}
	
	private void yearValueToZero() {
		
		this.cropIncome = 0.0;
		this.riceIncome = 0.0;
		this.maizeIncome = 0.0;
		this.consumeWater = 0.0;
		
		this.precipitation = 0.0;
		this.maizePrecipitation = 0.0;
		this.ricePrecipitation = 0.0;
		this.riceYield = 0.0;
		this.riceIrrigation = 0.0;
		this.maizeYield = 0.0;
		this.maizeIrrigation = 0.0;
		
		this.precip = 0.0;
		this.riceIrri = 0.0;
		this.riceDr = 0.0;
		this.maizeIrri = 0.0;
		this.maizeDr = 0.0;
	}

	
	/**
	 * 获取 每一亩的 玉米补助
	 * @return double : 元/亩
	 */
	private double getMaizeSubsidy() {
		double subsidy = 0.0;
		if(this.tc.getCurrentTime().getYear()==2007) subsidy = 450.0;   // 6750 yuan/hm2 
		if(this.tc.getCurrentTime().getYear() > 2007) subsidy = 550.0;  // 8250 yuan/hm2
		return subsidy;
	}
	/**
	 * 获取玉米价格 
	 * @return double : 元/kg
	 */
	private double getMaizePrice() {
		return 2.1;
	}
	/**
	 * 获取水稻价格
	 * @return double : 元/kg
	 */
	private double getRicePrice() {
		return 5.0;
	}
	/**
	 * 水稻每亩的成本：种子、化肥、农药
	 * @return double : 元/亩
	 */
	private double getRiceCost() {
		return 10643.60/15;
	}
	/**
	 * 玉米每亩的成本：种子、化肥、农药
	 * @return double : 元/亩
	 */
	private double getMaizeCost() {
		return 5265.25/15;
	}
	/**
	 * volume(立方米)的水 灌溉到 面积为 area(亩) ,的灌溉高度 mm
	 * @param area
	 * @param volume
	 * @return 灌溉深度 mm
	 */
	private double muTomm(double area, double volume) {
		double d = volume/(area*666.6666667);
		return d*1000;
	}
	/**
	 * 深度为mm(毫米) ，面积为area(亩) 的灌溉量对应的体积(立方米)
	 * @param area 亩
	 * @param mm   毫米
	 * @return  体积 立方米
	 */
	private double mmTom3(double area, double mm) {
		return (mm/1000)*area*666.6666667;
	}
	
	
	/**
	 * 作物总输入
	 * @return 总收入 ： yuan
	 */
	protected double getCropIncome() {
		return this.cropIncome;
	}
	protected double getRiceIncome() {
		return this.riceIncome;
	}
	protected double getMaizeIncome() {
		return this.maizeIncome;
	}
	protected double getMaizeConsumeWater() {
		return this.maizeConsumeWater;
	}
	protected double getRiceCousumeWater() {
		return this.riceConsumeWater;
	}
	protected double getCousumeWater() {
		return this.consumeWater;
	}
	protected double getPrecipitation() {
		return this.precipitation;
	}
	protected int getSimId() {
		return this.simId;
	}
	
	
}
