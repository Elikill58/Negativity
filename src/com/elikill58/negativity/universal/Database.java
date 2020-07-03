package com.elikill58.negativity.universal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.elikill58.negativity.universal.adapter.Adapter;

public class Database {

	private static Connection connection;
	private static String url, username, password;
	public static boolean hasCustom = false;
	private static long lastValidityCheck = 0;

	public static void connect(String url, String username, String password) {
		Database.url = url;
		Database.username = username;
		Database.password = password;
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				Adapter.getAdapter().getLogger().warn("Cannot find driver for MySQL.");
			}
			connection = DriverManager.getConnection("jdbc:mysql://" + url, username, password);
			Adapter.getAdapter().getLogger().info("Connection to database " + url + " done !");
			Database.hasCustom = true;
		} catch (SQLException e) {
			Adapter.getAdapter().getLogger().error("[Negativity] Error while connection to the database.");
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
		if(!hasCustom) {
			new IllegalStateException("You are trying to use database without active it.").printStackTrace();
			return null;
		}
		if (connection == null || connection.isClosed() || !isConnectionValid())
			connect(url, username, password);
		return connection;
	}

	private static boolean isConnectionValid() throws SQLException {
		long now = System.currentTimeMillis();
		// The connection may die if not used for some time, here we check each 15 minutes
		if (now - lastValidityCheck > 900_000) {
			lastValidityCheck = now;
			return connection.isValid(1);
		}
		return true;
	}

	public static void close() {
		if(connection == null)
			return;
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		Adapter store = Adapter.getAdapter();
		if (hasCustom = store.getConfig().getBoolean("Database.isActive")) {
			Database.connect(store.getConfig().getString("Database.url"),
					store.getConfig().getString("Database.user"),
					store.getConfig().getString("Database.password"));
		}
	}
}
