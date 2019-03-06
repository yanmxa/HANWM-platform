package com.ameat.application;

import com.ameat.utils.ConfigurationLoader;
import org.javalite.activejdbc.DB;

import com.ameat.simulation.Simulation;
import com.ameat.simulation.TimeController;
import com.ameat.tables.Table;
import com.ameat.utils.StructuralProperties;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 程序主入口：
 * 			1. start() 模拟逻辑入口
 * 			2. clearTable() 清除数据库数据
 * 			3. exportTable() 将数据库中的数据导出
 *
 * created by yanmeng 2017/11
 */
public class Bootstrap {

	static volatile int taskCount = 0;

	public static void main(String[] args) {
//		start();
//		new Table("ResidentResult").export();
//		new Table("ResidentSimulation").export();
//		clearTable();
		exportTable();
//		new Table("RiverFlow").export();
		DB.closeAllConnections();
	}


	public static void start() {
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

		Properties application = ConfigurationLoader.loadConfProperties("application.properties");
		String simulationFile = application.getProperty("simulationpath");
		List<Map<String, String>> parameters = StructuralProperties.struct(simulationFile);

		for(Map<String, String> params : parameters) {
			Runnable myRunnable = new Runnable(){
			   public void run(){
				    taskCount++;
				    Thread.currentThread().setName(""+taskCount);
				    System.out.println(" >> Thread : " + Thread.currentThread().getName() + " is Running !");
				    System.out.println("    > params : " + params);
					TimeController timeController = new TimeController(params);
					Simulation simulation = new Simulation(timeController, params);
					simulation.run();
				    System.out.println(" >> Thread : " + Thread.currentThread().getName() + " is over !");
			   }
			};
			singleThreadExecutor.execute(myRunnable);
		}
	}
	
	
	
	public static void clearTable(){
		new Table("FarmerTrace").delete();
		new Table("FarmerAnchor").delete();
		new Table("FarmerInit").delete();
		new Table("Simulation").delete();
		new Table("CountryTrace").delete();
	}

	public static void exportTable() {
		new Table("FarmerTrace").export();
		new Table("FarmerAnchor").export();
		new Table("FarmerInit").export();
		new Table("Simulation").export();
		new Table("CountryTrace").export();
	}

}
