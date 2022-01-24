package com.elikill58.negativity.universal.ban;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ban.storage.ActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.BanLogsStorage;
import com.elikill58.negativity.universal.ban.storage.FileActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.FileBanLogsStorage;

public class BansMigration {

	public static void migrateBans(Path activeBanStorageDir, Path loggedBanStorageDir) {
		Adapter adapter = Adapter.getAdapter();
		Path oldBanDir = adapter.getDataFolder().toPath().resolve(adapter.getConfig().getString("ban.file.dir", "xxxxxxxxxx"));
		// the "xxxxxxxxxx" field is to prevent empty path, and so always founded because it's the actual folder
		if (Files.notExists(oldBanDir))
			return;

		Path bansBackupDir = oldBanDir.resolveSibling(oldBanDir.getFileName()).resolveSibling("ban-migrations");
		if (Files.exists(bansBackupDir))
			return;

		adapter.getLogger().info("Started migration of bans saved as files");

		boolean didMigration = false;
		boolean migrationFailed = false;
		BanLogsStorage banLogsStorage = new MigrationFileBanLogsStorage(oldBanDir, loggedBanStorageDir);
		ActiveBanStorage activeBanStorage = new FileActiveBanStorage(activeBanStorageDir);
		try (Stream<Path> dirStream = Files.list(oldBanDir)) {
			List<Path> files = dirStream.filter(Files::isRegularFile).collect(Collectors.toList());
			if (files.isEmpty()) {
				return;
			}

			Files.createDirectories(bansBackupDir);

			for (Path file : files) {
				String filename = file.getFileName().toString();
				String filenameWithoutExt = filename.replace(".txt", "");
				UUID uuid;
				try {
					uuid = UUID.fromString(filenameWithoutExt);
				} catch (IllegalArgumentException e) {
					continue;
				}

				try {
					List<Ban> loadedBans = banLogsStorage.load(uuid);
					Ban extractedActiveBan = getActiveBanFromLoggedBans(loadedBans);

					List<Ban> loggedBansToSave = loadedBans;
					if (extractedActiveBan != null) {
						// We remove the active ban and all the following ones from the list of bans to keep in logs
						loggedBansToSave = loadedBans.subList(0, loadedBans.indexOf(extractedActiveBan));

						Ban activeBan = Ban.activeFrom(extractedActiveBan);
						activeBanStorage.save(activeBan);
					}

					Files.move(file, bansBackupDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
					loggedBansToSave.forEach(banLogsStorage::save);

					didMigration = true;
				} catch (Exception e) {
					adapter.getLogger().error("Could not migrate ban file " + filename + ". Another migration attempt for this file will be made the next startup or reload.");
					e.printStackTrace();
					migrationFailed = true;
				}
			}
		} catch (IOException e) {
			adapter.getLogger().error("Unable to migrate bans.");
			e.printStackTrace();
		}

		if (didMigration && !migrationFailed) {
			adapter.getLogger().info("Bans migration ended successfully");
		}
	}

	@Nullable
	private static Ban getActiveBanFromLoggedBans(List<Ban> loggedBans) {
		if (loggedBans.isEmpty())
			return null;

		final long now = System.currentTimeMillis();
		for (Ban loggedBan : loggedBans) {
			if (loggedBan.getStatus() != BanStatus.REVOKED && (loggedBan.isDefinitive() || loggedBan.getExpirationTime() > now)) {
				return loggedBan;
			}
		}

		return null;
	}

	private static class MigrationFileBanLogsStorage extends FileBanLogsStorage {

		private final Path oldLogsDir;

		public MigrationFileBanLogsStorage(Path oldLogsDir, Path banLogsStorage) {
			super(banLogsStorage);
			this.oldLogsDir = oldLogsDir;
		}

		@Override
		protected Path getLoadBanDir() {
			return oldLogsDir;
		}
	}
}
