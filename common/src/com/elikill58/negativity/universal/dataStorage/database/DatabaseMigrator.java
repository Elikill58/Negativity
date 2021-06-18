package com.elikill58.negativity.universal.dataStorage.database;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DatabaseMigrator {
	
	// Matches the full migration file name, example: 000-postgresql-Create-Initial-Table.sql
	// 000 -> int; the version
	// postgresql -> optional string; group named 'dbvariant'
	// Create-Initial-Table -> string; migration name
	// .sql -> literal; file extension
	private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^(\\d*)(-(?<dbvariant>\\p{Lower}*))?-(.*)\\.sql$");
	// Flexible pattern for the in-house statement separator comment "-- ;"
	// The MULTILINE flag is used to easily match an entire line, especially to include the optional comment text after the semicolon
	private static final Pattern STATEMENT_SEPARATOR_PATTERN = Pattern.compile("-- \\s*;.*?$", Pattern.MULTILINE);
	// Matches a blank string
	private static final Pattern BLANK_PATTERN = Pattern.compile("\\s*");
	
	public static MigrationResult executeRemainingMigrations(Connection connection, String databaseType, String subsystem) throws SQLException {
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
					
					int newVersion = doExecuteRemainingMigrations(connection, databaseType, previousVersion, subsystem);
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
	
	private static int doExecuteRemainingMigrations(Connection connection, String databaseType, int currentVersion, String subsystem) throws SQLException {
		RemainingMigrations migrationsToDo = getMigrationsToExecute(databaseType, currentVersion, subsystem);
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
	private static RemainingMigrations getMigrationsToExecute(String databaseType, int currentVersion, String subsystem) {
		try {
			URI migrationsDirUri = DatabaseMigrator.class.getResource("/databaseMigrations").toURI();
			if (migrationsDirUri.getScheme().equals("jar")) {
				try (FileSystem jarFs = FileSystems.newFileSystem(migrationsDirUri, Collections.emptyMap())) {
					return getMigrationsToExecute(databaseType, jarFs.getPath("/databaseMigrations", subsystem), currentVersion);
				}
			}
			return getMigrationsToExecute(databaseType, Paths.get(migrationsDirUri).resolve(subsystem), currentVersion);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static RemainingMigrations getMigrationsToExecute(String databaseType, Path migrationsDir, int currentVersion) throws IOException {
		int highestMigrationVersion = -1;
		Set<Integer> scriptsWithVariant = new HashSet<>();
		List<MigrationScript> migrationScripts = new ArrayList<>();
		try (Stream<Path> migrationFiles = Files.list(migrationsDir)) {
			for (Path path : migrationFiles.collect(Collectors.toList())) {
				if (!Files.isRegularFile(path)) {
					continue;
				}
				
				String fileName = path.getFileName().toString();
				Matcher fileNameMatcher = FILE_NAME_PATTERN.matcher(fileName);
				if (!fileNameMatcher.matches()) {
					continue;
				}
				
				@Nullable String databaseVariant = fileNameMatcher.group("dbvariant");
				if (databaseVariant != null && !databaseType.equals(databaseVariant)) {
					continue;
				}
				
				String migrationName = fileNameMatcher.group(fileNameMatcher.groupCount());
				
				String rawMigrationVersion = fileNameMatcher.group(1);
				try {
					int migrationVersion = Integer.parseInt(rawMigrationVersion);
					if (migrationVersion > currentVersion) {
						String rawScript = new String(Files.readAllBytes(path));
						String[] statements = STATEMENT_SEPARATOR_PATTERN.split(rawScript);
						migrationScripts.add(new MigrationScript(statements, migrationVersion, databaseVariant, migrationName));
						
						if (databaseVariant != null) {
							scriptsWithVariant.add(migrationVersion);
						}
						
						if (migrationVersion > highestMigrationVersion) {
							highestMigrationVersion = migrationVersion;
						}
					}
				} catch (NumberFormatException ignore) {
					throw new IllegalStateException("Migration file name does not have a valid version number: '" + rawMigrationVersion + "'");
				}
			}
		}
		
		migrationScripts.removeIf(script -> scriptsWithVariant.contains(script.version) && script.variant == null);
		migrationScripts.sort(Comparator.comparingInt(script -> script.version));
		
		Set<Integer> versions = new HashSet<>();
		for (MigrationScript script : migrationScripts) {
			if (!versions.add(script.version)) {
				throw new IllegalStateException("Found scripts with duplicate version " + script);
			}
		}
		
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
		public final @Nullable String variant;
		public final String name;
		
		private MigrationScript(String[] statements, int version, @Nullable String variant, String name) {
			this.statements = statements;
			this.version = version;
			this.variant = variant;
			this.name = name;
		}
		
		@Override
		public String toString() {
			return "MigrationScript{" +
				"statements=" + Arrays.toString(statements) +
				", version=" + version +
				", variant='" + variant + '\'' +
				", name='" + name + '\'' +
				'}';
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
