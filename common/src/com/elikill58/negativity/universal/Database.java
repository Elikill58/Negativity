package com.elikill58.negativity.universal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

import com.elikill58.negativity.api.yaml.config.Configuration;

public class Database {

	private static Connection connection;
	private static String url, username, password;
	public static boolean hasCustom = false;
	private static long lastValidityCheck = 0;
	private static DatabaseType databaseType;

	/**
	 * Create a connection to the database
	 * 
	 * @param url the database URL such as 127.0.0.1/mySchema
	 * @param username the database username
	 * @param password the user password
	 */
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

	/**
	 * Get the database connection
	 * If null or closed, it will re-connect.
	 * 
	 * @return the database connection
	 * 
	 * @throws SQLException if the connection cannot be re-open
	 * @throws IllegalStateException if your are trying to use DB without active it
	 */
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

	/**
	 * Close the connection if not null
	 */
	public static void close() {
		if(connection == null)
			return;
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load database
	 * Don't try to connect if has custom is on false
	 */
	public static void init() {
		Configuration store = Adapter.getAdapter().getConfig();
		if (hasCustom = store.getBoolean("Database.isActive")) {
			databaseType = DatabaseType.valueOf(store.getString("Database.type", "mysql").toUpperCase(Locale.ROOT));
			Database.connect(store.getString("Database.url"),
					store.getString("Database.user"),
					store.getString("Database.password"));
		}
	}
	
	public static DatabaseType getType() {
		return databaseType;
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
