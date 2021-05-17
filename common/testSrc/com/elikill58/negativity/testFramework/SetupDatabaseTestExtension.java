package com.elikill58.negativity.testFramework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Database;

public class SetupDatabaseTestExtension implements BeforeEachCallback, AfterEachCallback {
	
	private final String databaseName;
	
	public SetupDatabaseTestExtension(String databaseName) {
		this.databaseName = databaseName;
	}
	
	private @NonNull String getParameter(ExtensionContext context, String key) {
		Optional<String> url = context.getConfigurationParameter("negativity.db." + databaseName + "." + key);
		Assumptions.assumeTrue(url.isPresent(), "Missing " + key + " of database '" + databaseName + "'");
		return url.get();
	}
	
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		Adapter adapter = Adapter.getAdapter();
		Configuration config = adapter.getConfig();
		config.set("Database.isActive", true);
		config.set("Database.url", getParameter(context, "url"));
		config.set("Database.user", getParameter(context, "user"));
		config.set("Database.password", getParameter(context, "password"));
		config.set("Database.type", getParameter(context, "type"));
		adapter.reload();
		Assumptions.assumeTrue(Database.hasCustom, "Could not connect to database, skipping tests.");
		Connection connection = Database.getConnection();
		if (connection != null) {
			// Clearing the tables before each test guarantees proper test isolation on the database end
			clearDatabaseTables(connection);
		}
	}
	
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Connection connection = Database.getConnection();
		if (connection != null) {
			// Clearing the tables after each test guarantees proper test isolation on the database end
			clearDatabaseTables(connection);
		}
		Database.close();
	}
	
	private static void clearDatabaseTables(Connection connection) throws SQLException {
		try (PreparedStatement listTablesStm = connection.prepareStatement("SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'negativity_tests'")) {
			ResultSet listTablesResult = listTablesStm.executeQuery();
			connection.setAutoCommit(false);
			while (listTablesResult.next()) {
				String tableName = listTablesResult.getString("TABLE_NAME");
				if (!tableName.startsWith("negativity_")) {
					continue;
				}
				
				try (PreparedStatement dropTableStm = connection.prepareStatement("DROP TABLE " + tableName)) {
					dropTableStm.executeUpdate();
				}
			}
			connection.commit();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			if (!connection.getAutoCommit()) {
				connection.rollback();
			}
		} finally {
			connection.setAutoCommit(true);
		}
	}
}
