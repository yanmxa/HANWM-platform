package com.ameat.component.city;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ameat.component.comunications.ComunicationInterface;
import com.ameat.simulation.TimeController;
import com.ameat.tables.Table;

public class CitySchedule {
	
	private TimeController timeController;
	private Resident[] residents;
	private static final String RESIDENT_TABLE = "Resident";
	private static final String RESIDENT_SIMULATION_TABLE = "ResidentSimulation";
	private static final float CV = (float) 0.2;
	private Map<String, Double> distances;

	public CitySchedule(TimeController timeController, Map<String, ComunicationInterface> comunications) {
		this.timeController = timeController;
		Table residentTable = new Table(RESIDENT_TABLE);
		Table residentSimulation = new Table(RESIDENT_SIMULATION_TABLE);
		Map<String, Object> Simulationrecord = residentSimulation.getOne("thread_name desc", "thread_name = "+Thread.currentThread().getName());
		float temp = Float.valueOf(Simulationrecord.get("radius").toString());
		int radius = (int) temp;
		float alpha = Float.valueOf(Simulationrecord.get("alpha").toString());
		float beta = Float.valueOf(Simulationrecord.get("beta").toString());
		
		residents = new Resident[(int) residentTable.count()];
		// 装在resident个人信息
		for(int i = 0; i < residentTable.count(); i++) {
			residents[i] = (new Resident(residentTable.getOne("id desc", "num = " + (i+1)), radius, alpha, beta, CV));
		}
		
		distances = new HashMap<>();
		// 计算距离
		for(int i = 0; i < residents.length - 1; i++) {
			for(int j = i+1; j < residents.length; j++) {
				String key = i + "To" + j;
				distances.put(key, residents[i].distance(residents[j]));
			}
		}
		
		// 找朋友
		for(int i = 0; i < residents.length; i++) {
			residents[i].loadFriends(residents, distances);
		}
		
	}

	
	
	public void loadToAchorTime() {
		
		// # step1. 计算每个居民户的用水情况,并存入数据库
		double amount = new OriginalWaterConsume().getAmount(timeController.getCurrentTime());
		double totalOriginalWater = amount * residents.length;
		double totalSavingWater = 0;
		double totalConsumeWater = 0;
		for(int i = 0; i < residents.length; i++) {
			Map<String, Float> waterProportion = residents[i].getDevicesManger().getWaterProportion();
			Map<String, Float> currentRatio = residents[i].getDevicesManger().getCurrentRatio();
			Map<String, Double> waterSavingAmount = residents[i].getWaterSavingAmount();
			
			Set<String> keySet = waterProportion.keySet();
			for (String key : keySet) {
				double consumeWater = amount * waterProportion.get(key) * currentRatio.get(key);
				double savingWater = amount - consumeWater;
				totalConsumeWater += consumeWater;
				totalSavingWater += savingWater;
				waterSavingAmount.put(key, savingWater);
			}
		}
		
		Table residentResult = new Table("ResidentResult");
		Map<String, Object> record = new HashMap<>();
		DecimalFormat df = new DecimalFormat("######0.0000");
		record.put("thread_name", Thread.currentThread().getName());
		record.put("total_original_water", df.format(totalOriginalWater));
		record.put("total_saving_water", df.format(totalSavingWater));
		record.put("total_consumer_water", df.format(totalConsumeWater));
		record.put("year", this.timeController.getCurrentTime().getYear());
		residentResult.insertOne(record);
		
		// # step2. 更新每个居民户的currentDeviceRatio和previousDeviceRatio
		Resident[] cloneResidents = residents.clone();
		Set<String> names = DevicesManager.getNames();
		for(int i = 0; i < residents.length; i++) {
			// 1.记录每一项中的优势朋友,以及高于自己的朋友数量
			Map<String, Integer> priority = new HashMap<>();
			Map<String, Integer> priorityNum = new HashMap<>();
			Resident[] friends = cloneResidents[i].getFriends();  // 使用的是副本的朋友，不会现有resident造成影响 
			for (int j = 0; j < friends.length; j++) {
				for(String key : names) {
					Double friendSaving = friends[j].getWaterSavingAmount().get(key);
					Double currentSaving = residents[i].getWaterSavingAmount().get(key);
					// 选择优势朋友位置
					int friendIndex = (friendSaving > friends[priority.getOrDefault(key, 0)].getWaterSavingAmount().get(key)) ? 
							j : priority.getOrDefault(key, 0);
					priority.put(key, friendIndex);
					// 更新优势朋友数量
					if(friendSaving > currentSaving) {
						priorityNum.put(key, priorityNum.getOrDefault(key, 0) + 1);
					}
				}
			}
			// 2.保存currentDevice 到 previousDevice，向居民学习更新currentDevice，
			float alpha = residents[i].getAlpha();   // 获取当前resident的惰性系数
			float beta = residents[i].getBeta();
			int friendsNum = friends.length;
			for(String key : names) {
				// 更新previousRatio数值
				residents[i].getDevicesManger().setPreviousRatio(key, 
						cloneResidents[i].getDevicesManger().getCurrentRatio().get(key));
				// 更新currentRatio数值：如果优势群体占比大于惰性系数，学习最优个体；否则进行则我进化
				float newRatio;
				
				if( priorityNum.get(key) != null && (priorityNum.get(key) / friendsNum) > alpha) {
					newRatio = cloneResidents[i].getDevicesManger().getCurrentRatio().get(key) + 
							(beta * (friends[priority.get(key)].getDevicesManger().getCurrentRatio().get(key) - 
									cloneResidents[i].getDevicesManger().getCurrentRatio().get(key))
									);
					
				} else {
					newRatio = cloneResidents[i].getDevicesManger().getCurrentRatio().get(key) +
							(beta * (cloneResidents[i].getDevicesManger().getCurrentRatio().get(key) - 
									cloneResidents[i].getDevicesManger().getPreviousRatio().get(key))
									);
				}
				residents[i].getDevicesManger().setCurrentRatio(key, newRatio);
			}
		}
		
	}



	public void loadToinit() {
		Table residentResult = new Table("ResidentResult");
		Map<String, Object> record = new HashMap<>();
		record.put("thread_name", Thread.currentThread().getName());
		double initConsume = new OriginalWaterConsume().getAmount(this.timeController.getCurrentTime());
		record.put("total_original_water", initConsume);
		record.put("total_saving_water", 0);
		record.put("total_consumer_water", initConsume);
		record.put("year", this.timeController.getCurrentTime().getYear());
		residentResult.insertOne(record);
	}

}
