package com.stock.crawler.utils;

import com.stock.crawler.Quote;
import com.stock.crawler.Stock;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class DBUtils {
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
		String sql = "select closePrice from quote where stockId=? and volume > 0 and tradeDate<='2016-06-30' order by tradeDate limit 100000000";
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


	public static List<JSONObject> getQuoteByStockId(String stockId, int count, Connection con) {

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

	public static JSONObject getLatestQuoteByStockId(String stockId, int count, Connection con) {

		String sql = "select * from (select * from quote where stockId='" + stockId + "') a order by tradeDate desc limit " + count;
		PreparedStatement ps = null;
		JSONObject jsonObject = null;
		try {
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
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
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}


	public static void updateMACD(Map<String, Double> macdMap, String stockId, String dateStr, Connection con){
		String sql = "update quote set dif=" + macdMap.get("DIF") + ", dea=" + macdMap.get("DEA") + ", macd=" + macdMap.get("MACD") + ", ema12=" + macdMap.get("EMA12") + ", ema26=" + macdMap.get("EMA26") + " where stockId='" + stockId + "' and tradeDate='" + dateStr + "'";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateKDJ(List<JSONObject> jsonObjects, Connection con) {
		try {
			if(jsonObjects.size() == 0) return;
			Statement st = con.createStatement();
			long start = System.currentTimeMillis();
			String sql = null;

//			for (JSONObject jsonObject : jsonObjects) {
//				sql = "update quote set k=" + jsonObject.getDouble("KDJ_K") + ", d=" + jsonObject.getDouble("KDJ_D") + ", j=" + jsonObject.getDouble("KDJ_J") + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
//				st.addBatch(sql);
//			}
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

	public static void updateENE(List<JSONObject> jsonObjects, Connection con) {
		if(jsonObjects.size() == 0) return;
		try {
			Statement st = con.createStatement();
			String sql = null;
//			for (JSONObject jsonObject : jsonObjects) {
//				sql = "update quote set upper=" + jsonObject.getDouble("UPPER") + ", lower=" + jsonObject.getDouble("LOWER") + ", ene=" + jsonObject.getDouble("ENE") + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
//				st.addBatch(sql);
//			}
			JSONObject jsonObject = jsonObjects.get(jsonObjects.size() - 1);
			sql = "update quote set upper=" + jsonObject.getDouble("UPPER") + ", lower=" + jsonObject.getDouble("LOWER") + ", ene=" + jsonObject.getDouble("ENE") + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
			st.addBatch(sql);
			st.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateMA(List<JSONObject> jsonObjects, Connection con) {
		if(jsonObjects.size() == 0) return;
		try {
			Statement st = con.createStatement();
			String sql = null;
//			for (JSONObject jsonObject : jsonObjects) {
//				sql = "update quote set ma5=" + jsonObject.getDouble("MA_5") + ", ma10=" + jsonObject.getDouble("MA_10") + ", ma20=" + jsonObject.getDouble("MA_20")  + ", m30=" + jsonObject.getDouble("MA_30")  + ", ma60=" + jsonObject.getDouble("MA_60") + ", ma120=" + jsonObject.getDouble("120")  + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
//				st.addBatch(sql);
//			}
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
//			for (JSONObject jsonObject : jsonObjects) {
//				sql = "update quote set cci=" + jsonObject.getDouble("cci") + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
//				st.addBatch(sql);
//			}
//			int c = 0;
//			for (int i = jsonObjects.size() -1; i> 0; i--) {
//				if(c>10) break;
//				JSONObject jsonObject = jsonObjects.get(i);
//				if(!jsonObject.has("cci")) continue;
//				sql = "update quote set cci=" + jsonObject.getDouble("cci") + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
//				st.addBatch(sql);
//				c++;
//			}
			JSONObject jsonObject = jsonObjects.get(jsonObjects.size() - 1);
			if(!jsonObject.has("cci")) jsonObject.put("cci", -1000);
			sql = "update quote set cci=" + jsonObject.getDouble("cci") + " where stockId='" + jsonObject.getString("stockId") + "' and tradeDate='" + jsonObject.getString("tradeDate") + "'";
			st.addBatch(sql);
			st.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

