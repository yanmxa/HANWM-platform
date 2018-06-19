package com.ameat.application;

import org.javalite.activejdbc.DB;

import com.ameat.simulation.Simulation;
import com.ameat.simulation.TimeController;
import com.ameat.tables.Table;
import com.ameat.utils.StructuralProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bootstrap {

	static volatile int taskCount = 0;
	
	public static void main(String[] args) {

//		start();
		new Table("ResidentResult").export();
		new Table("ResidentSimulation").export();
//			clearTable();
//			exportTable(); 
//			new Table("RiverFlow").export();
		DB.closeAllConnections();
	}


	public static void start() {
		List<Map<String, String>> parameters = StructuralProperties.struct("simulation.resident.properties");
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		
		for(Map<String, String> params : parameters) {
			Runnable myRunnable = new Runnable(){
			   public void run(){
				    taskCount++;
				    Thread.currentThread().setName(""+taskCount);
				    System.out.println(" Thread : " + Thread.currentThread().getName() + " is Running !");
					TimeController timeController = new TimeController(params);
					Simulation simulation = new Simulation(timeController, params);
					simulation.run();
				    System.out.println(" Thread : " + Thread.currentThread().getName() + " is runned over !");
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
