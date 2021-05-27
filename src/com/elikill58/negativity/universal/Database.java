package com.elikill58.negativity.universal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;

public class Database {

	private static Connection connection;
	private static String url, username, password;
	public static boolean hasCustom = false;
	private static long lastValidityCheck = 0;
	private static DatabaseType databaseType;

	public static void connect(String url, String username, String password) {
		Database.url = url;
		Database.username = username;
		Database.password = password;
		try {
			try { // load drivers
				Class.forName(databaseType.getDriver());
			} catch (ClassNotFoundException e) {
				Adapter.getAdapter().getLogger().warn("Cannot find driver for " + databaseType.getName() + ".");
			}
			connection = DriverManager.getConnection("jdbc:" + databaseType.getType() + "://" + url, username, password);
			Adapter.getAdapter().getLogger().info("Connection to database " + url + " (with " + databaseType.getName() + ") done !");
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
		ConfigAdapter config = Adapter.getAdapter().getConfig();
		if (hasCustom = config.getBoolean("Database.isActive")) {
			databaseType = DatabaseType.valueOf(config.getString("Database.type").toUpperCase(Locale.ROOT));
			Database.connect(config.getString("Database.url"),
					config.getString("Database.user"),
					config.getString("Database.password"));
		}
	}
	
	public static enum DatabaseType {
		MARIA("mariadb", "MariaDB", "org.mariadb.jdbc.Driver"),
		MYSQL("mysql", "MySQL", "com.mysql.jdbc.Driver");
		
		private final String type, name, driver;
		
		private DatabaseType(String type, String name, String driver) {
			this.type = type;
			this.name = name;
			this.driver = driver;
		}
		
		public String getName() {
			return name;
		}
		
		public String getType() {
			return type;
		}
		
		public String getDriver() {
			return driver;
		}
	}
}
