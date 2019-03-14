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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

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
		engine(4);
//		new Table("ResidentResult").export();
//		new Table("ResidentSimulation").export();
//		clearTable();
//		exportTable();
//		new Table("RiverFlow").export();
		DB.closeAllConnections();
	}


	public static void engine(int engineNumber) {
		ExecutorService executorService = EngineFactory.fixedThreadPool(engineNumber);

		Properties application = ConfigurationLoader.loadConfProperties("application.properties");
		String simulationFile = application.getProperty("simulationpath");
		List<Map<String, String>> parameters = StructuralProperties.struct(simulationFile);
		final CountDownLatch latch = new CountDownLatch(parameters.size());
		for(final Map<String, String> params : parameters) {
			Runnable myRunnable = new Runnable(){
			   public void run(){
			        try {
                        taskCount++;
                        Thread.currentThread().setName(""+taskCount);
                        System.out.println(" >> thread task : " + Thread.currentThread().getName() + " is Running !");
                        System.out.println("    > params : " + params);
                        TimeController timeController = new TimeController(params);
                        Simulation simulation = new Simulation(timeController, params);
                        int simulationId = simulation.run();
                        System.out.println(" >> thread task : " + Thread.currentThread().getName() + " - simulationId "+ simulationId +" is over !");
				    } catch (Exception e) {
                        System.out.println(" ======>>>>>> thread task : " + Thread.currentThread().getName() + " exception !");
                        System.out.println(" ======>>>>>> params : " + params);
                        e.printStackTrace();
                    } finally {
			            latch.countDown();
                    }
			   }
			};
			executorService.execute(myRunnable);
		}
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	
	
	public static void clearTable(){
		System.out.print(" clear tables.");
		new Table("FarmerTrace").delete();
		new Table("FarmerAnchor").delete();
		new Table("FarmerInit").delete();
		new Table("Simulation").delete();
		new Table("CountryTrace").delete();
	}

	public static void exportTable() {
		System.out.print(" export data to excel.");
		new Table("FarmerTrace").export();
		new Table("FarmerAnchor").export();
		new Table("FarmerInit").export();
		new Table("Simulation").export();
		new Table("CountryTrace").export();
	}

}
