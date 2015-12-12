package com.stock.crawler.utils;

import com.stock.crawler.Stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


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
}

