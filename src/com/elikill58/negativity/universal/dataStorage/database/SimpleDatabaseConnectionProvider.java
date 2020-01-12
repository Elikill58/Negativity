package com.elikill58.negativity.universal.dataStorage.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.adapter.Adapter;

public class SimpleDatabaseConnectionProvider implements DatabaseConnectionProvider {

	private final String url;
	private final String username;
	private final String password;

	private Connection connection;

	public SimpleDatabaseConnectionProvider(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	@Override
	public Connection get() throws SQLException {
		if (connection == null || connection.isClosed()) {
			connect(url, username, password);
		}
		return connection;
	}

	private void connect(String url, String username, String password) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			Adapter.getAdapter().warn("Cannot find driver for MySQL.");
		}
		connection = DriverManager.getConnection("jdbc:mysql://" + url, username, password);
		Database.hasCustom = true;
	}

	@Override
	public void close() {
		if (connection == null) {
			return;
		}
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
