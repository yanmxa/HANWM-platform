package com.ameat.application;

import org.javalite.activejdbc.DB;

import com.ameat.simulation.Simulation;
import com.ameat.simulation.TimeController;
import com.ameat.tables.Table;
import com.ameat.utils.StructuralProperties;

import java.util.List;
import java.util.Map;

public class Bootstrap {

	public static void main(String[] args) {

		try {
//			List<Map<String, String>> parameters = StructuralProperties.struct("simulation");
//			for(Map<String, String> params : parameters) {
//				Runnable myRunnable = new Runnable(){
//				   public void run(){
//						TimeController timeController = new TimeController();
//						Simulation simulation = new Simulation(timeController, params);
//						simulation.run();
//				   }
//				};
//				Thread thread = new Thread(myRunnable);
//				thread.start();
//				System.out.println("Thread :" +thread.getId()+ " is running!!!");
//			}


//			clearTable();
			exportTable(); 
//			new Table("RiverFlow").export();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.closeAllConnections();
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
