package com.ameat.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Configuration;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.ModelDelegate;

import com.ameat.utils.CamelCaseHelper;
import com.ameat.utils.ConfigurationLoader;
import com.ameat.utils.Jexcel;

/* this class is write as a model constructor, do not use other tables directly */
public class Table{
	private String tableClassPrefix = "com.ameat.tables.";

	private String tableName;

	private Model model;

	private Class<? extends Model> claz;

	public Table(String tableName){
		this.tableName = tableName;
		this.checkConection();
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Model> claz = (Class<? extends Model>) Class.forName(tableClassPrefix+tableName);
			this.claz = claz;
			this.model = (Model) claz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void resetModel() {
		// to refresh the model in order to avoid dirty data
		this.model.reset();
	}

	private Connection checkConection() {
		Connection conn = null;
		if(!DB.connections().containsKey("default")) {
			DB db = new DB("default");
			db.open();
			conn = db.getConnection();
		}else {
			conn = DB.connections().get("default");
		}
		
		return conn;
	}


	/**
	 * Insert a piece of data and return id
	 * @param record
	 * @return id
	 */
	public int insertReturnKey(Map<String, Object> record) {
		this.model.fromMap(record);
		this.model.insert();
		int id = Integer.valueOf(this.model.getId().toString());
		this.resetModel();
		return id;
	}

	/**
	 * Insert a piece of data
	 * @param record
	 */
	public boolean insertOne(Map<String, Object> record) {
		this.model.fromMap(record);
		boolean sign = this.model.insert();
		this.resetModel();

		return  sign;
	}

	/**
	 * if the record exists, update it, if the record does not exit, insert one new record
	 * @param record
	 * @return
	 */
	public boolean save(Map<String, Object> record) {
		Object id = record.get("id");
		boolean signal = true;

		if(id != null) {
			Model dbRecord = ModelDelegate.findById(this.claz, id);
			if(dbRecord.exists()) {
				signal = this.updateById(Integer.valueOf(id.toString()), record) >= 1;
			}else {
				signal = this.insertOne(record);
			}
		} else {
			signal = this.insertOne(record);
		}
		this.resetModel();

		return signal;
	}

	/**
	 * update one record by id
	 * @param id
	 * @param updateParams
	 * @return updateCount
	 */
	public int updateById(int id, Map<String, Object> updateParams) {
		return this.update(updateParams, "id = "+String.valueOf(id));
	}

	/**
	 * update all the records which is conform to the conditions
	 * @param updateParams
	 * @param conditions
	 * @return updateCount
	 */
	public int update(Map<String, Object> updateParams, String ... conditions) {
		String query = this.buildQuery(conditions);
		String params = this.buildParams(updateParams);

		return ModelDelegate.update(this.claz, params, query, new Object[0]);
	}

	/**
	 * get the first record which is conform to the conditions
	 * @param orderBy
	 * @param conditions
	 * @return
	 */
	public Map<String, Object> getOne(String orderBy, String ... conditions) {
		String query = this.buildQuery(conditions);
		LazyList<? extends Model> result = ModelDelegate
				.where(this.claz, query, new Object[0])
				.limit(1)
				.offset(0)
				.orderBy(orderBy);

		return result.size() > 0 ? result.get(0).toMap() : new HashMap<String, Object>();
	}

	/**
	 * get the first record which is conform to the conditions
	 * @param orderBy
	 * @param conditions
	 * @return the instance of the model
	 */
	public Model getOneM(String orderBy, String ... conditions) {
		String query = this.buildQuery(conditions);
		LazyList<? extends Model> result = ModelDelegate
				.where(this.claz, query, new Object[0])
				.limit(1)
				.offset(0)
				.orderBy(orderBy);

		return result.size() > 0 ? result.get(0) : null;
	}

	/**
	 * get records which is conform to the parameters
	 * @param perpage
	 * @param page
	 * @param orderBy
	 * @param conditions
	 * @return
	 */
	public List<Map<String, Object>> gets(long perpage, long page, String orderBy, String ... conditions) {
		String query = this.buildQuery(conditions);
		LazyList<? extends Model> result = ModelDelegate
				.where(this.claz, query, new Object[0])
				.limit(perpage)
				.offset(perpage*(page-1))
				.orderBy(orderBy);

		return result.toMaps();
	}

	/**
	 * get all the records which is conform to the parameters
	 * @param conditions
	 * @return
	 */
	public List<Map<String, Object>> gets(String ... conditions) {
		String query = this.buildQuery(conditions);
		LazyList<? extends Model> result = ModelDelegate
				.where(this.claz, query, new Object[0]);

		return result.toMaps();
	}

	/**
	 * get all the records which is conform to the parameters
	 * @param conditions
	 * @return the instance list of models
	 */
	public List<Model> getsM(String ... conditions) {
		String query = this.buildQuery(conditions);
		LazyList<? extends Model> datas = ModelDelegate
				.where(this.claz, query, new Object[0]);
		List<Model> results = new ArrayList<Model>();
		for(Model item : datas) {
			results.add(item);
		}

		return results;
	}

	/**
	 * delete the record which is conform to id
	 * @param id
	 * @return deletedCount
	 */
	public int deleteById(int id) {
		return this.delete("id = "+String.valueOf(id));
	}

	/**
	 * delete all the records which is conform to the conditions
	 * @param conditions
	 * @return deletedCount
	 */
	public int delete(String ... conditions) {
		String query = this.buildQuery(conditions);
		return ModelDelegate.delete(this.claz, query, new Object[0]);
	}

	/**
	 * delete all the records in the table
	 * @return
	 */
	public int delete() {
		return ModelDelegate.deleteAll(this.claz);
	}

	/**
	 * count all records in the table
	 * @return
	 */
	public long count() {
		return ModelDelegate.count(this.claz);
	}

	/**
	 * count records which is conform to the conditions
	 * @param conditions
	 * @return
	 */
	public long count(String ... conditions) {
		String query = this.buildQuery(conditions);

		return ModelDelegate.count(this.claz, query, new Object[0]);
	}

	public void export() {
		String sheetName = this.tableName;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String fileName = this.tableName + "_export_" + df.format(new Date());
		this.export(sheetName, fileName);
	}

	/**
	 * export the whole data of the table to excel
	 * @param fileName
	 * @param sheetName
	 * @param conditions
	 */
	public void export(String fileName, String sheetName, String ... conditions) {
//		default_perpage = 500
//		default_page = 1
//		default_order_by = id asc
//		int perpage = Integer.parseInt(ConfigurationLoader.config("application.default_perpage"));
//		int page = Integer.parseInt(ConfigurationLoader.config("application.default_page"));
//		String orderBy = ConfigurationLoader.config("application.default_order_by");
		int perpage = 500;
		int page = 1;
		String orderBy = "id asc";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("conditions", conditions);
		args.put("perpage", perpage);
		args.put("page", page);
		args.put("orderBy", orderBy);
		Map<String, Object> headers = this.getComments();
		Jexcel.writeExcel(sheetName, fileName, headers, this, args);
	}

	/**
	 *  Get table columns' comments  eg:
	 *  		{date=日期, updated_at=更新时间, county=县级区域, degree=温度值, created_at=创建时间, id=}
	 * @return
	 */
	public Map<String, Object> getComments() {
		DB db = new DB(Table.class.getSimpleName());
		String environment = Configuration.getEnv();
		String driver = ConfigurationLoader.config("database."+environment+".driver");
		String url = ConfigurationLoader.config("database."+environment+".url").
				substring(0, ConfigurationLoader.config("database."+environment+".url").
						lastIndexOf('/')+1)+"information_schema";
		String username = ConfigurationLoader.config("database."+environment+".username");
		String password = ConfigurationLoader.config("database."+environment+".password");
		db.open(driver, url, username, password);
		Map<String, Object> comments = new HashMap<String, Object>();
		String query = "select * from COLUMNS where TABLE_NAME = '" +CamelCaseHelper.camelToSnake(this.tableName)+ "s';";
		List<Map> lists = db.all(query);
		db.close();
		for(Map item : lists) {
			comments.put(item.get("COLUMN_NAME").toString(), item.get("COLUMN_COMMENT").toString());
		}
		return comments;
	}

	/**
	 * execute the original sql and return the results
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> exec(String sql) {
		Connection conn = this.checkConection();
		Statement res = null;
		ResultSet result = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			res = conn.createStatement();
			result = res.executeQuery(sql);
			ResultSetMetaData md = result.getMetaData();
			int columnCount = md.getColumnCount();
			while (result.next()) {
				Map<String, Object> rowData = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), result.getObject(i));
				}
				list.add(rowData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				result.close();
				res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}


	private String buildQuery(String[] conditions) {
		StringBuffer querySb = new StringBuffer();

		for (String condition : conditions) {
			querySb.append(condition + " and ");
		}

		String queryStr = querySb.toString();

		return conditions.length > 0 ? queryStr.substring(0, queryStr.lastIndexOf("and")) : "";
	}

	private String buildParams(Map<String, Object> params) {
		StringBuffer paramsSb = new StringBuffer();
		params.forEach((k, v) -> {
			if(k != "id") {
				paramsSb.append(k+" = '"+v.toString()+"', ");
			}
		});
		String paramsStr = paramsSb.toString();

		return params.size() > 0 ? paramsStr.substring(0, paramsStr.lastIndexOf(',')) : "";
	}
}
