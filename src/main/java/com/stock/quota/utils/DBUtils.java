package com.stock.quota.utils;

import com.jzsec.bean.Epl;
import com.jzsec.bean.Schema;
import com.stock.quota.Quote;
import com.stock.quota.Stock;
import com.stock.quota.config.Configuration;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;


public class DBUtils {

	public static Connection con = null;
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Configuration.config();
			String url = Configuration.JDBCURL;
			String userName = Configuration.JDBCUSERNAME;
			String password = Configuration.JDBCPASSWORD;
			con = DriverManager.getConnection(url, userName, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
//	public static Connection getConnection() {
//		Connection con = null;
//		try {
//			if(con != null) return con;
//			Configuration.config();
//			DBPoolContext.getInstance().init(Configuration.JDBCURL, Configuration.JDBCPASSWORD, Configuration.JDBCUSERNAME);
//			con = DBPoolContext.getInstance().open();
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return con;
//	}


	public static Connection getConnection() {
		Connection con = null;
		try {
			if(con != null) return con;

			Class.forName("com.mysql.jdbc.Driver");
			Configuration.config();
			String url = Configuration.JDBCURL;
			String userName = Configuration.JDBCUSERNAME;
			String password = Configuration.JDBCPASSWORD;
			con = DriverManager.getConnection(url, userName, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return con;
	}

	public static void close(Connection con, Statement st) {
		try {
			if (st != null) {
				st.close();
			}
			if (con != null)
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
			if(rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(ResultSet rs) {
		try {
			if(rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(Connection con) {
		try {
			if(con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void saveStockList(List<Stock> stockList, Connection con){
		String sql = "insert into stock (stock_id, stock_name, stock_url) values(?, ?, ?)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			for(Stock stock : stockList) {
				ps.setString(1, stock.getStockId());
				ps.setString(2, stock.getStockName());
				ps.setString(3, stock.getStockUrl());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void saveQuoteList(List<Quote> quoteList, Connection con){
		String sql = "insert into quote (tradeDate, stockId, stockName, openPrice, " +
				"closePrice, addPrice, addRate, minPrice, maxPrice, volume, turnover, turnoverRate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			for(Quote quote : quoteList) {
				ps.setString(1, quote.getTradeDate());
				ps.setString(2, quote.getStockId());
				ps.setString(3, quote.getStockName());
				ps.setDouble(4, quote.getOpenPrice());
				ps.setDouble(5, quote.getClosePrice());
				ps.setDouble(6, quote.getAddPrice());
				ps.setDouble(7, quote.getAddRate());
				ps.setDouble(8, quote.getMinPrice());
				ps.setDouble(9, quote.getMaxPrice());
				ps.setDouble(10, quote.getVolume());
				ps.setDouble(11, quote.getTurnover());
				ps.setDouble(12, quote.getTurnoverRate());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Double> getClosePrice(String stockId, int count, Connection con) {
		String sql = "select closePrice from quote where stockId=? and volume > 0 order by tradeDate limit 100000000";
		PreparedStatement ps = null;
		ArrayList<Double> prices = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, stockId);
			prices = new ArrayList<Double>();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				double price = rs.getDouble(1);
				prices.add(price);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		Collections.reverse(prices);
		return prices;
	}


	public static List<JSONObject> getQuoteByStockId(String stockId, int count) {

//		String sql = "select * from quote where stockId=? and volume > 0 order by tradeDate desc limit " + count;
		String sql = "select * from quote where stockId=? and volume > 0 and tradeDate >= '2015-03-20' order by tradeDate";
		PreparedStatement ps = null;
		List<JSONObject> quotes = new ArrayList<JSONObject>();
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, stockId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("tradeDate", rs.getString("tradeDate"));
				jsonObject.put("stockId", rs.getString("stockId"));
				jsonObject.put("stockName", rs.getString("stockName"));
				jsonObject.put("openPrice", rs.getDouble("openPrice"));
				jsonObject.put("closePrice", rs.getDouble("closePrice"));
				jsonObject.put("addPrice", rs.getDouble("addPrice"));
				jsonObject.put("addRate", rs.getDouble("addRate"));
				jsonObject.put("minPrice", rs.getDouble("minPrice"));
				jsonObject.put("maxPrice", rs.getDouble("maxPrice"));

				jsonObject.put("volume", rs.getDouble("volume"));
				jsonObject.put("turnover", rs.getDouble("turnover"));
				jsonObject.put("turnoverRate", rs.getDouble("turnoverRate"));
				quotes.add(jsonObject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return quotes;
	}

	public static List<JSONObject> getLatestQuoteByStockId(String stockId, int count, String yesterday) {

		String sql = "select * from (select * from quote where stockId='" + stockId + "' and tradeDate<='" + yesterday + "') a order by tradeDate desc limit " + count;
		PreparedStatement ps = null;
		JSONObject jsonObject = null;
		List<JSONObject> quotes = new ArrayList<JSONObject>();
		try {
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				jsonObject = new JSONObject();
				jsonObject.put("tradeDate", rs.getString("tradeDate"));
				jsonObject.put("stockId", rs.getString("stockId"));
				jsonObject.put("stockName", rs.getString("stockName"));
				jsonObject.put("openPrice", rs.getDouble("openPrice"));

				jsonObject.put("closePrice", rs.getDouble("closePrice"));
				jsonObject.put("addPrice", rs.getDouble("addPrice"));
				jsonObject.put("addRate", rs.getDouble("addRate"));
				jsonObject.put("minPrice", rs.getDouble("minPrice"));
				jsonObject.put("maxPrice", rs.getDouble("maxPrice"));
				jsonObject.put("volume", rs.getDouble("volume"));
				jsonObject.put("turnover", rs.getDouble("turnover"));
				jsonObject.put("turnoverRate", rs.getDouble("turnoverRate"));

				jsonObject.put("dea", rs.getDouble("dea"));
				jsonObject.put("macd", rs.getDouble("macd"));
				jsonObject.put("dif", rs.getDouble("dif"));
				jsonObject.put("k", rs.getDouble("k"));
				jsonObject.put("d", rs.getDouble("d"));
				jsonObject.put("j", rs.getDouble("j"));

				jsonObject.put("ma5", rs.getDouble("ma5"));
				jsonObject.put("ma10", rs.getDouble("ma10"));
				jsonObject.put("ma20", rs.getDouble("ma20"));
				jsonObject.put("ma30", rs.getDouble("ma30"));
				jsonObject.put("ma60", rs.getDouble("ma60"));
				jsonObject.put("ma120", rs.getDouble("ma120"));

				jsonObject.put("upper", rs.getDouble("upper"));
				jsonObject.put("lower", rs.getDouble("lower"));
				jsonObject.put("ene", rs.getDouble("ene"));
				jsonObject.put("cci", rs.getDouble("cci"));
				jsonObject.put("ema12", rs.getDouble("ema12"));
				jsonObject.put("ema26", rs.getDouble("ema26"));
				quotes.add(jsonObject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return quotes;
	}


	public static void updateMACD(Map<String, Double> macdMap, String stockId, String dateStr){
		String sql = "update quote set dif=" + macdMap.get("DIF") + ", dea=" + macdMap.get("DEA") + ", macd=" + macdMap.get("MACD") + ", ema12=" + macdMap.get("EMA12") + ", ema26=" + macdMap.get("EMA26") + " where stockId='" + stockId + "' and tradeDate='" + dateStr + "'";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateKDJ(List<JSONObject> jsonObjects) {
		try {

			if(jsonObjects.size() == 0) return;
			Statement st = con.createStatement();
			long start = System.currentTimeMillis();
			String sql = null;

			JSONObject jsonObject = jsonObjects.get(jsonObjects.size() - 1);
			if(!jsonObject.has("KDJ_K")) return;
			sql = "update quote set k=" + jsonObject.getDouble("KDJ_K") + ", d=" + jsonObject.getDouble("KDJ_D") + ", j=" + jsonObject.getDouble("KDJ_J") + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
			st.addBatch(sql);
			st.executeBatch();
			System.out.println((System.currentTimeMillis() - start)/1000.000);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateENE(List<JSONObject> jsonObjects) {
		if(jsonObjects.size() == 0) return;
		try {
			Statement st = con.createStatement();
			String sql = null;

			JSONObject jsonObject = jsonObjects.get(jsonObjects.size() - 1);
			sql = "update quote set upper=" + jsonObject.getDouble("UPPER") + ", lower=" + jsonObject.getDouble("LOWER") + ", ene=" + jsonObject.getDouble("ENE") + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
			st.addBatch(sql);
			st.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateMA(List<JSONObject> jsonObjects) {
		if(jsonObjects.size() == 0) return;
		try {
			Statement st = con.createStatement();
			String sql = null;
			JSONObject jsonObject = jsonObjects.get(jsonObjects.size() - 1);
			sql = "update quote set ma5=" + jsonObject.getDouble("MA_5") + ", ma10=" + jsonObject.getDouble("MA_10") + ", ma20=" + jsonObject.getDouble("MA_20")  + ", ma30=" + jsonObject.getDouble("MA_30")  + ", ma60=" + jsonObject.getDouble("MA_60") + ", ma120=" + jsonObject.getDouble("MA_120")  + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
			st.addBatch(sql);
			st.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static void updateCCI(List<JSONObject> jsonObjects, Connection con) {
		if(jsonObjects.size() == 0) return;
		try {
			Statement st = con.createStatement();
			String sql = null;
			JSONObject jsonObject = jsonObjects.get(jsonObjects.size() - 1);
			if(!jsonObject.has("cci")) jsonObject.put("cci", -1000);
			sql = "update quote set cci=" + jsonObject.getDouble("cci") + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
			st.addBatch(sql);
			st.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, List<Schema>> qetAllSchema() {
		String sql = "select id,schema_code,schema_name,schema_sql,is_alert from alert_schema";
		Map<String, List<Schema>> schemeMap = new HashMap<String, List<Schema>>();
		if (con == null) return schemeMap;
		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);
			List<Schema> schemes = null;
			Schema scheme = null;
			while (rs.next()) {
				int id = rs.getInt("id");
				String schemaCode = rs.getString("schema_code");
				String schemaName = rs.getString("schema_name");
				String schemaSql = rs.getString("schema_sql");
				int isAlert = rs.getInt("is_alert");
				scheme = new Schema(id, schemaCode, schemaName, schemaSql, isAlert);
				if(schemeMap.containsKey(schemaCode)) {
					schemeMap.get(schemaCode).add(scheme);
				} else {
					schemes = new ArrayList<Schema>();
					schemes.add(scheme);
					schemeMap.put(schemaCode, schemes);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return schemeMap;
	}

	public static Map<Integer, Epl> qetAllEpls() {
		String sql = "select e.id,e.epl_name,e.buy_sql,e.sale_sql,e.alert_type,g.phone,g.email,e.alert_template\n" +
				"from alert_epl e  \n" +
				"left outer join\n" +
				"(\n" +
				"\tselect g.id, GROUP_CONCAT(phone) phone,GROUP_CONCAT(email) email\n" +
				"\tfrom alert_group g\n" +
				"\tleft outer join alert_group_user gu\n" +
				"\ton(g.id=gu.group_id)\n" +
				"\tleft outer join alert_user u\n" +
				"\ton(gu.user_id=u.id)\n" +
				") g\n" +
				"on(e.alert_group_id=g.id)";
		Map<Integer, Epl> eplMap = new HashMap<Integer, Epl>();
		if (con == null) return eplMap;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);
			Epl epl = null;
			while (rs.next()) {
				int id = rs.getInt("id");
				String eplName = rs.getString("epl_name");
				String statisticSql = rs.getString("buy_sql");
				String alertSql = rs.getString("sale_sql");
				int alertType = rs.getInt("alert_type");
				String phone = rs.getString("phone");
				String email = rs.getString("email");
				String alertTemplate = rs.getString("alert_template");
				epl = new Epl(id, eplName, statisticSql, alertSql, alertType, phone, email,  alertTemplate);
				eplMap.put(id, epl);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eplMap;
	}

	private static void close(Connection con, Statement st, ResultSet rs) {
		try {
			if(rs != null) rs.close();
			if(st != null) st.close();
			if(con != null) con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static void recordTrade(Map<String, Object> eventMap, int userId, int isBuy) {
		String sql = "insert into trade(stockId,userId,price,num,jsonString,isBuy,tradeTime) values(?,?,?,?,?,?,?)";
		try {
			JSONObject jsonObject = mapToJson(eventMap);
			PreparedStatement pps = con.prepareStatement(sql);
			pps.setString(1, eventMap.get("stockId").toString());
			pps.setInt(2, userId);
			pps.setDouble(3, Double.parseDouble(eventMap.get("closePrice").toString()));
			pps.setDouble(4, 1000);
			pps.setString(5, jsonObject.toString());
			pps.setInt(6, isBuy);
			pps.setString(7, eventMap.get("tradeDate").toString());
			pps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject mapToJson(Map<String, Object> map) {
		JSONObject jsonObject = new JSONObject();
		Set<String> keys = map.keySet();
		for(String key : keys) {
			jsonObject.put(key, map.get(key));
		}
		return jsonObject;
	}
}

