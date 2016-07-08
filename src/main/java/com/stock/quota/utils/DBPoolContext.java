package com.stock.quota.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

public class DBPoolContext {
	private DataSource source;
	private static DBPoolContext instance = new DBPoolContext();
	private AtomicBoolean ok = new AtomicBoolean(false);

	public static DBPoolContext getInstance() {
		return instance;
	}

	public DBPoolContext init(String url, String pwd, String user) {
		if (ok.compareAndSet(false, true)) {
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(url);
			config.setUsername(user);
			config.setPassword(pwd);
			config.setAcquireIncrement(1);
			config.setIdleConnectionTestPeriodInSeconds(60);
			config.setMaxConnectionsPerPartition(30);
			config.setMinConnectionsPerPartition(1);
			config.setPartitionCount(10);
			config.setIdleMaxAgeInMinutes(60);
			source = new BoneCPDataSource(config);
		}
		return this;
	}

	public Connection open() throws SQLException {
		return source.getConnection();
	}

	public void close() {
		if (ok.compareAndSet(true, false)) {
			if (source != null) {
				((BoneCPDataSource) source).close();
			}
		}
	}
}
