package com.ameat.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ameat.tables.Table;

public class ImportExcelToDatabase {
	public static void main(String[] args) {
		List<String[]> readExcel = Jexcel.readExcel("/Users/yanmeng/Downloads/residents_to_classify.xls", 0);
		Table resident = new Table("Resident");
		for(int i = 1; i < readExcel.size(); i++) {
			Map<String, Object> record = new HashMap<>();
			record.put("num", readExcel.get(i)[0]);
			record.put("age", readExcel.get(i)[1]);
			record.put("education", readExcel.get(i)[2]);
			record.put("deficit_cognition", readExcel.get(i)[3]);
			record.put("income", readExcel.get(i)[4]);
			record.put("saving_equipment", readExcel.get(i)[5]);
			record.put("policy_cognition", readExcel.get(i)[6]);
			resident.insertOne(record);
		}
	}
}
