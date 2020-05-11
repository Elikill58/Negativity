package com.elikill58.negativity.universal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.elikill58.negativity.universal.adapter.Adapter;

public class Database {

	private static Connection connection;
	private static String url, username, password;
	public static boolean hasCustom = false;
	public static String table_lang;

	public static void connect(String url, String username, String password) {
		Database.url = url;
		Database.username = username;
		Database.password = password;
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				Adapter.getAdapter().warn("Cannot find driver for MySQL.");
			}
			connection = DriverManager.getConnection("jdbc:mysql://" + url, username, password);
			Database.hasCustom = true;
		} catch (SQLException e) {
			Adapter.getAdapter().error("[Negativity] Error while connection to the database.");
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
		if(!hasCustom) {
			new IllegalStateException("You are trying to use database without active it.").printStackTrace();
			return null;
		}
		if (connection == null)
			connect(url, username, password);
		if(connection.isClosed())
			connect(url, username, password);
		return connection;
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
			table_lang = store.getConfig().getString("Database.table_lang");
			Database.connect(store.getConfig().getString("Database.url"),
					store.getConfig().getString("Database.user"),
					store.getConfig().getString("Database.password"));
		}
	}
}
