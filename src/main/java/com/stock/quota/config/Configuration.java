package com.stock.quota.config;

import java.util.ResourceBundle;

public class Configuration {
	public static String JDBCURL = "jdbc:mysql://127.0.0.1:3306/crawler?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
	public static String JDBCUSERNAME = "root";
	public static String JDBCPASSWORD = "caodaoxi123";
	public static String SMTPHOST = "mail.jzsec.com";
	public static String MAILPORT = "25";
	public static String MAILSENDERUSERNAME = "caodaoxi@jzsec.com";
	public static String MAILSENDERPASSWORD = "Abcd1234";
	public static String MAILPASSWORD = "Abcd1234";

	
	public static void config() {
		ResourceBundle resource = ResourceBundle.getBundle("config");
		JDBCURL = resource.getString("jdbc.url");
		JDBCUSERNAME = resource.getString("jdbc.user");
		JDBCPASSWORD = resource.getString("jdbc.password");
	}
}
