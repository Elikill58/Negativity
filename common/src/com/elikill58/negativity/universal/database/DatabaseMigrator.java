package com.elikill58.negativity.universal.database;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DatabaseMigrator {

	// Matches an int from the start of the string
	private static final Pattern FILE_VERSION_PATTERN = Pattern.compile("^(\\d*)");
	// Flexible pattern for the in-house statement separator comment "-- ;"
	// The MULTILINE flag is used to easily match an entire line, especially to include the optional comment text after the semicolon
	private static final Pattern STATEMENT_SEPARATOR_PATTERN = Pattern.compile("-- \\s*;.*?$", Pattern.MULTILINE);
	// Matches a blank string
	private static final Pattern BLANK_PATTERN = Pattern.compile("\\s*");

	public static MigrationResult executeRemainingMigrations(Connection connection, String subsystem) throws SQLException {
		try {
			return CompletableFuture.supplyAsync(() -> {
				try (PreparedStatement createMigrationsStm = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS negativity_migrations_history (subsystem VARCHAR(32), version INT, update_time TIMESTAMP DEFAULT NOW())");
					 // Gets the latest version of this database
					 PreparedStatement getCurrentVersion = connection.prepareStatement("SELECT version FROM negativity_migrations_history WHERE subsystem = ? ORDER BY version DESC LIMIT 1")) {
					createMigrationsStm.executeUpdate();

					getCurrentVersion.setString(1, subsystem);
					ResultSet result = getCurrentVersion.executeQuery();
					int previousVersion = -1;
					if (result.next()) {
						previousVersion = result.getInt(1);
					}

					int newVersion = doExecuteRemainingMigrations(connection, previousVersion, subsystem);
					if (newVersion >= 0) {
						// At least one migration was executed
						try (PreparedStatement insertRecordStm = connection.prepareStatement("INSERT INTO negativity_migrations_history (version, subsystem) VALUES (?, ?)")) {
							insertRecordStm.setInt(1, newVersion);
							insertRecordStm.setString(2, subsystem);
							insertRecordStm.executeUpdate();
						}
					}
					return new MigrationResult(previousVersion, newVersion);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static int doExecuteRemainingMigrations(Connection connection, int currentVersion, String subsystem) throws SQLException {
		RemainingMigrations migrationsToDo = getMigrationsToExecute(currentVersion, subsystem);
		if (migrationsToDo == null) {
			return -1;
		}
		try (Statement migrationStm = connection.createStatement()) {
			for (MigrationScript migrationScript : migrationsToDo.migrationScripts) {
				for (String statement : migrationScript.statements) {
					if (!BLANK_PATTERN.matcher(statement).matches()) {
						// Empty statements are rejected by the MySQL driver
						migrationStm.addBatch(statement);
					}
				}
				migrationStm.executeBatch();
			}
		}
		return migrationsToDo.highestMigrationVersion;
	}

	@Nullable
	private static RemainingMigrations getMigrationsToExecute(int currentVersion, String subsystem) {
		try {
			URI migrationsDirUri = DatabaseMigrator.class.getResource("/databaseMigrations").toURI();
			if (migrationsDirUri.getScheme().equals("jar")) {
				try (FileSystem jarFs = FileSystems.newFileSystem(migrationsDirUri, Collections.emptyMap())) {
					return getMigrationsToExecute(jarFs.getPath("/databaseMigrations", subsystem), currentVersion);
				}
			}
			return getMigrationsToExecute(Paths.get(migrationsDirUri).resolve(subsystem), currentVersion);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static RemainingMigrations getMigrationsToExecute(Path migrationsDir, int currentVersion) throws IOException {
		int highestMigrationVersion = -1;
		List<MigrationScript> migrationScripts = new ArrayList<>();
		try (Stream<Path> migrationFiles = Files.list(migrationsDir)) {
			for (Path path : migrationFiles.collect(Collectors.toList())) {
				if (!Files.isRegularFile(path)) {
					continue;
				}
				String fileName = path.getFileName().toString();
				Matcher fileVersionMatcher = FILE_VERSION_PATTERN.matcher(fileName);
				if (!fileVersionMatcher.find()) {
					continue;
				}
				try {
					int migrationVersion = Integer.parseInt(fileVersionMatcher.group());
					if (migrationVersion > currentVersion) {
						String rawScript = new String(Files.readAllBytes(path));
						migrationScripts.add(new MigrationScript(STATEMENT_SEPARATOR_PATTERN.split(rawScript), migrationVersion));
						if (migrationVersion > highestMigrationVersion) {
							highestMigrationVersion = migrationVersion;
						}
					}
				} catch (NumberFormatException ignore) {
				}
			}
		}
		migrationScripts.sort(Comparator.comparingInt(script -> script.version));
		return new RemainingMigrations(migrationScripts, highestMigrationVersion);
	}

	private static class RemainingMigrations {

		public final List<MigrationScript> migrationScripts;
		public final int highestMigrationVersion;

		private RemainingMigrations(List<MigrationScript> migrationScripts, int highestMigrationVersion) {
			this.migrationScripts = migrationScripts;
			this.highestMigrationVersion = highestMigrationVersion;
		}
	}

	private static class MigrationScript {

		public final String[] statements;
		public final int version;

		private MigrationScript(String[] statements, int version) {
			this.statements = statements;
			this.version = version;
		}
	}

	public static class MigrationResult {

		private final int previousVersion;
		private final int newVersion;

		public MigrationResult(int previousVersion, int newVersion) {
			this.previousVersion = previousVersion;
			this.newVersion = newVersion;
		}

		public int getPreviousVersion() {
			return previousVersion;
		}

		public int getNewVersion() {
			return newVersion;
		}

		public boolean hasUpdated() {
			return newVersion > previousVersion;
		}
	}
}
