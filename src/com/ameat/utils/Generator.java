package com.ameat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
	/**
	 * 返回一个服从正太分布的随机数
	 * @param mean：期望
	 * @param sigma：方差(标准差的平方)
	 * @param number:个数
	 * @return
	 */
	public static List<Double> N(double mean, double sigma2, int number) {
		Random random = new Random(100);
		List<Double> result = new ArrayList<Double>();
		for(int i=0; i<number; i++) {
			result.add(random.nextGaussian()*Math.sqrt(sigma2)+mean);
		}
		return result;
	}
	/**
	 * 返回一组服从正太分布的数
	 * @param mean ： 期望
	 * @param cv ： 变异系数
	 * @param number：个数
	 * @return
	 */
	public static List<Double> Normal(double mean, double cv, int number){
		Random random = new Random(100);
		List<Double> result = new ArrayList<Double>();
		for(int i=0; i<number; i++) {
			result.add(cv * mean * random.nextGaussian() + mean);
		}
		return result;
	}
	
	/**
	 * 返回一个服从N(mean, (cv*mean)^2)的随机数
	 * @param mean
	 * @param cv
	 * @return
	 */
	public static double Normal(double mean, double cv) {
		Random random = new Random();
		return cv*mean*random.nextGaussian() + mean;
	}

}
