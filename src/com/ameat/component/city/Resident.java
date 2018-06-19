package com.ameat.component.city;

import java.util.HashMap;
import java.util.Map;

import com.ameat.utils.Generator;


public class Resident {
	
	static PersonInfo minPersonInfo = new PersonInfo.Builder()
			.withAge(100).withEducation(100).withDeficitCognition(100).withIncome(100).withSavingEquipment(100).withPolicyCognition(100)
			.build();
	static PersonInfo maxPersonInfo = new PersonInfo.Builder()
			.withAge(-1).withEducation(-1).withDeficitCognition(-1).withIncome(-1).withSavingEquipment(-1).withPolicyCognition(-1)
			.build();;
	
	PersonInfo personInfo;
	
	// 信息半径
	private int radius;
	// 惰性系数
	private float alpha;
	// 学习能力
	private float beta;
	
	// 朋友圈
	private Resident[] friends;
	
	// 节水设备情况
	private DevicesManager devicesManger;
	
	// 节水占比
	private Map<String, Double> waterSavingAmount;


	public Resident(Map<String, Object> record, int radius, float alpha, float beta, float cv) {
		this.personInfo = new PersonInfo.Builder()
				.withId(Integer.parseInt(record.get("id").toString()))
				.withNum(Integer.parseInt(record.get("num").toString()))
				.withAge(Integer.parseInt(record.get("age").toString()))
				.withEducation(Integer.parseInt(record.get("education").toString()))
				.withDeficitCognition(Integer.parseInt(record.get("deficit_cognition").toString()))
				.withIncome(Integer.parseInt(record.get("income").toString()))
				.withSavingEquipment(Integer.parseInt(record.get("saving_equipment").toString()))
				.withPolicyCognition(Integer.parseInt(record.get("policy_cognition").toString()))
				.build();
		
		updateMaxPersonInfo();
		updateMinPersonInfo();
		
		// 获取当前线程的初始化参数
		do {
			this.radius = (int) Generator.Normal(radius, cv);
		} while(this.radius <= 0);
		this.alpha = (float) Generator.Normal(alpha, cv);
		this.beta = (float) Generator.Normal(beta, cv);
		
		this.friends = new Resident[this.radius];
		
		// 生成初始器具
		this.devicesManger = new DevicesManager();
		
		this.waterSavingAmount = new HashMap<>();
	}
	
	private void updateMinPersonInfo() {
		minPersonInfo.age = this.personInfo.age < minPersonInfo.age ? this.personInfo.age : minPersonInfo.age;
		minPersonInfo.education = this.personInfo.education < minPersonInfo.education ? 
				this.personInfo.education : minPersonInfo.education;
		minPersonInfo.deficitCognition = this.personInfo.deficitCognition < minPersonInfo.deficitCognition ? 
				this.personInfo.deficitCognition : minPersonInfo.deficitCognition;
		minPersonInfo.income = this.personInfo.income < minPersonInfo.income ? this.personInfo.income : minPersonInfo.income;
		minPersonInfo.savingEquipment = this.personInfo.savingEquipment < minPersonInfo.savingEquipment ? 
				this.personInfo.savingEquipment : minPersonInfo.savingEquipment;
		minPersonInfo.policyCognition = this.personInfo.policyCognition < minPersonInfo.policyCognition ? 
				this.personInfo.policyCognition : minPersonInfo.policyCognition;
	}

	private void updateMaxPersonInfo() {
		maxPersonInfo.age = this.personInfo.age > maxPersonInfo.age ? this.personInfo.age : maxPersonInfo.age;
		maxPersonInfo.education = this.personInfo.education > maxPersonInfo.education ? 
				this.personInfo.education : maxPersonInfo.education;
		maxPersonInfo.deficitCognition = this.personInfo.deficitCognition > maxPersonInfo.deficitCognition ? 
				this.personInfo.deficitCognition : maxPersonInfo.deficitCognition;
		maxPersonInfo.income = this.personInfo.income > maxPersonInfo.income ? this.personInfo.income : maxPersonInfo.income;
		maxPersonInfo.savingEquipment = this.personInfo.savingEquipment > maxPersonInfo.savingEquipment ? 
				this.personInfo.savingEquipment : maxPersonInfo.savingEquipment;
		maxPersonInfo.policyCognition = this.personInfo.policyCognition > maxPersonInfo.policyCognition ? 
				this.personInfo.policyCognition : maxPersonInfo.policyCognition;
	}

	double distance(Resident resident) {
		double result = 0;
		result +=( (Math.abs(this.personInfo.age - resident.personInfo.age)) / (maxPersonInfo.age - minPersonInfo.age) );
		result +=( (Math.abs(this.personInfo.education - resident.personInfo.education)) / (maxPersonInfo.education - minPersonInfo.education) );
		result +=( (Math.abs(this.personInfo.deficitCognition - resident.personInfo.deficitCognition)) / (maxPersonInfo.deficitCognition - minPersonInfo.deficitCognition) );
		result +=( (Math.abs(this.personInfo.income - resident.personInfo.income)) / (maxPersonInfo.income - minPersonInfo.income) );
		result +=( (Math.abs(this.personInfo.savingEquipment - resident.personInfo.savingEquipment)) / (maxPersonInfo.savingEquipment - minPersonInfo.savingEquipment) );
		result +=( (Math.abs(this.personInfo.policyCognition - resident.personInfo.policyCognition)) / (maxPersonInfo.policyCognition - minPersonInfo.policyCognition) );
		return result;
	}

	
	public void loadFriends(Resident[] residents, Map<String, Double> distances) {
		int index = this.personInfo.num - 1;
		for(int i = 0; i < residents.length; i++) {
			
			if(index == i) continue;
			double distance = index < i ? distances.get(index + "To" + i) : distances.get(i + "To" + index);
			
			// 将没有的位置填上
			boolean isNull = false;
			for(int j = 0; j < friends.length; j++) {
				if(friends[j] == null) {
					friends[j] = residents[i];
					isNull = (j == friends.length - 1 ? false : true);
					break;
				} 
			}
			
			// 将最长距离的friend放在数组最后
			double maxDistance = 0;
			int maxIndex = 0;
			if(!isNull) {
				for(int k = 0; k < friends.length; k++) {
					double tempDistance = (index < friends[k].personInfo.num - 1) ? 
							distances.get(index + "To" + (friends[k].personInfo.num - 1)) : 
								distances.get((friends[k].personInfo.num - 1) + "To" + index);
					if(maxDistance < tempDistance) {
						maxDistance = tempDistance;
						maxIndex = k;
					}
				}
				if(maxIndex != friends.length - 1) {
					Resident temp = friends[maxIndex];
					friends[maxIndex] = friends[friends.length - 1];
					friends[friends.length - 1] = temp;
				}
			} else {
				continue;
			}
			
			// 若当期那距离比最后的距离近，则进行替换
			if(distance < maxDistance) {
				friends[friends.length - 1] = residents[i];
			}
		}
	}

	public Map<String, Double> getWaterSavingAmount() {
		return waterSavingAmount;
	}

	public void setWaterSavingAmount(Map<String, Double> waterSavingAmount) {
		this.waterSavingAmount = waterSavingAmount;
	}

	public DevicesManager getDevicesManger() {
		return devicesManger;
	}

	public Resident[] getFriends() {
		return this.friends;
	}
	
	public float getAlpha() {
		return this.alpha;
	}
	
	public float getBeta() {
		return this.beta;
	}
}

class PersonInfo {

	public static class Builder {
		private int id;
		private int num;
		private int age;
		private int education;
		private int deficitCognition;
		private int income;
		private int savingEquipment;
		private int policyCognition;
		
		public Builder withId(int id) {
			this.id = id;
			return this;
		}
		public Builder withNum(int num) {
			this.num = num;
			return this;
		}
		public Builder withAge(int age) {
			this.age = age;
			return this;
		}
		public Builder withEducation(int edu) {
			this.education = edu;
			return this;
		}
		public Builder withDeficitCognition(int deficitCognition) {
			this.deficitCognition = deficitCognition;
			return this;
		}
		public Builder withIncome(int income) {
			this.income = income;
			return this;
		}
		public Builder withSavingEquipment(int savingEquipment) {
			this.savingEquipment = savingEquipment;
			return this;
		}
		public Builder withPolicyCognition(int policyCognition) {
			this.policyCognition = policyCognition;
			return this;
		}
		
		public PersonInfo build() {
			return new PersonInfo(this);
		}
	}

	int id;
	int num;
	int age;
	int education;
	int deficitCognition;
	int income;
	int savingEquipment;
	int policyCognition;
	
	private PersonInfo(Builder build) {
		this.id = build.id;
		this.num = build.num;
		this.age = build.age;
		this.education = build.education;
		this.deficitCognition = build.deficitCognition;
		this.income = build.income;
		this.savingEquipment = build.savingEquipment;
		this.policyCognition = build.policyCognition;
	}
		
}
