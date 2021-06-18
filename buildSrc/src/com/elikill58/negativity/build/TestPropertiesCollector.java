package com.elikill58.negativity.build;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.gradle.api.tasks.testing.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPropertiesCollector {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestPropertiesCollector.class);
	
	// Matches NEGATIVITY_DB_MYSQL_URL
	// Where MYSQL is a database config name
	// and URL is a config key (URL, USER, PASSWORD or TYPE)
	private static final Pattern DB_CONFIG_PATTERN = Pattern.compile("^NEGATIVITY_DB_(?<name>\\p{Upper}+)_(?<key>\\p{Upper}+)$");
	
	/**
	 * Collects environment variables to be used as system properties of a test task.
	 * <br/>
	 * The following properties are supported:
	 * <ul>
	 * <li>
	 *   {@code NEGATIVITY_DB_<name>_<key>} -> {@code negativity.db.<name>.<key>}. {@code key} can be one of the following:
	 *   <ul>
	 *     <li>{@code URL}: the URL used to connect to the database</li>
	 *     <li>{@code USER}: the user to connect as</li>
	 *     <li>{@code PASSWORD}: the password to use when opening a connection</li>
	 *     <li>{@code TYPE}: the type of database, see {@code Database.DatabaseType} for all supported values</li>
	 *   </ul>
	 * </li>
	 * <li>{@code NEGATIVITY_DATABASES}: comma-separated string of all database configuration names</li>
	 * </ul>
	 *
	 * @return system properties to pass to a test task
	 *
	 * @see Test#systemProperties
	 * @see #applyTestProperties(Test)
	 */
	public static Map<String, String> collectTestProperties() {
		Map<String, DatabaseConfig> databaseConfigs = new HashMap<>();
		System.getenv().forEach((envKey, value) -> {
			Matcher dbConfigMatcher = DB_CONFIG_PATTERN.matcher(envKey);
			if (dbConfigMatcher.matches()) {
				String configName = dbConfigMatcher.group("name");
				String configKey = dbConfigMatcher.group("key");
				DatabaseConfig databaseConfig = databaseConfigs.computeIfAbsent(configName, name -> new DatabaseConfig());
				switch (configKey) {
				case "URL":
					databaseConfig.url = value;
					break;
				case "USER":
					databaseConfig.user = value;
					break;
				case "PASSWORD":
					databaseConfig.password = value;
					break;
				case "TYPE":
					databaseConfig.type = value;
					break;
				default:
					LOGGER.warn("Unknown database configuration key '{}'", configKey);
				}
			}
		});
		
		Map<String, String> systemProperties = new HashMap<>();
		
		Set<String> databaseConfigNames = new HashSet<>();
		for (Map.Entry<String, DatabaseConfig> entry : databaseConfigs.entrySet()) {
			String key = entry.getKey();
			DatabaseConfig config = entry.getValue();
			if (config.url == null) {
				LOGGER.error("Missing url of database configuration '{}'", key);
				continue;
			} else if (config.user == null) {
				LOGGER.error("Missing user of database configuration '{}'", key);
				continue;
			} else if (config.password == null) {
				LOGGER.error("Missing password of database configuration '{}'", key);
				continue;
			} else if (config.type == null) {
				LOGGER.error("Missing password of database configuration '{}'", key);
				continue;
			}
			
			databaseConfigNames.add(key);
			systemProperties.put("negativity.db." + key + ".url", config.url);
			systemProperties.put("negativity.db." + key + ".user", config.user);
			systemProperties.put("negativity.db." + key + ".password", config.password);
			systemProperties.put("negativity.db." + key + ".type", config.type);
		}
		
		systemProperties.put("negativity.databases", String.join(",", databaseConfigNames));
		return systemProperties;
	}
	
	public static void applyTestProperties(Test testTask) {
		Map<String, String> testProperties = collectTestProperties();
		testTask.getInputs().properties(testProperties);
		testTask.systemProperties(testProperties);
	}
	
	private static class DatabaseConfig {
		
		public @Nullable String url;
		public @Nullable String user;
		public @Nullable String password;
		public @Nullable String type;
	}
}
