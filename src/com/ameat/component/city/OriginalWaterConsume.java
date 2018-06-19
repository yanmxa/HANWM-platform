package com.ameat.component.city;

import org.joda.time.DateTime;

public class OriginalWaterConsume {
	private double amount;
	public double getAmount(DateTime time) {
		// 计算根据时间计算原始用水里那个amount 
		int year = time.getYear();
		this.amount = 0.6779 * Math.pow(year, 2) - (2726.9 * year) + 3000000;
		return this.amount;
	}
	public void reset() {
		this.amount = 0;
	}
}
