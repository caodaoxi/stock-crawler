package com.stock.crawler.config;

import java.util.ResourceBundle;

public class Configuration {
	public static String JDBCURL = "jdbc:mysql://192.168.1.83:3307/trade2?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
	public static String JDBCUSERNAME = "root";
	public static String JDBCPASSWORD = "DBroot83";
	
	public static void config() {
		ResourceBundle resource = ResourceBundle.getBundle("config");
		JDBCURL = resource.getString("jdbc.url");
		JDBCUSERNAME = resource.getString("jdbc.user");
		JDBCPASSWORD = resource.getString("jdbc.password");
	}
}
