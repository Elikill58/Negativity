package com.elikill58.negativity.universal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.elikill58.negativity.universal.adapter.Adapter;

public class Database {

	private static Connection connection;
	private static String url, username, password;
	public static boolean hasCustom = false, saveInCache = false;
	public static String column_perm, column_uuid, table_perm, table_lang;

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
			Adapter.getAdapter().error("You are trying to use database without active it.");
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
		saveInCache = store.getBooleanInConfig("Database.saveInCache");

		if (hasCustom = store.getBooleanInConfig("Database.isActive")) {
			column_perm = store.getStringInConfig("Database.column_perm");
			column_uuid = store.getStringInConfig("Database.column_find_row");
			table_perm = store.getStringInConfig("Database.table_perm");
			table_lang = store.getStringInConfig("Database.table_lang");
			Database.connect(store.getStringInConfig("Database.url"),
					store.getStringInConfig("Database.user"),
					store.getStringInConfig("Database.password"));
		}
	}
}
