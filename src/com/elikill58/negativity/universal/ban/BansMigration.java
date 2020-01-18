package com.elikill58.negativity.universal.ban;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.storage.ActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.FileActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.FileBanLogsStorage;
import com.elikill58.negativity.universal.ban.storage.BanLogsStorage;

public class BansMigration {

	public static void migrateBans(Path activeBanStorageDir, Path loggedBanStorageDir) {
		Adapter adapter = Adapter.getAdapter();
		Path oldBanDir = adapter.getDataFolder().toPath().resolve(adapter.getStringInConfig("ban.file.dir"));
		if (Files.notExists(oldBanDir))
			return;

		Path bansBackupDir = oldBanDir.resolveSibling(oldBanDir.getFileName() + "_old");
		if (Files.exists(bansBackupDir))
			return;

		adapter.log("Started migration of bans saved as files");

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
					List<LoggedBan> loadedBans = banLogsStorage.load(uuid);
					LoggedBan extractedActiveBan = getActiveBanFromLoggedBans(loadedBans);

					List<LoggedBan> loggedBansToSave = loadedBans;
					if (extractedActiveBan != null) {
						// We remove the active ban and all the following ones from the list of bans to keep in logs
						loggedBansToSave = loadedBans.subList(0, loadedBans.indexOf(extractedActiveBan));

						ActiveBan activeBan = ActiveBan.from(extractedActiveBan);
						activeBanStorage.save(activeBan);
					}

					Files.move(file, bansBackupDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
					loggedBansToSave.forEach(banLogsStorage::save);

					didMigration = true;
				} catch (Exception e) {
					adapter.error("Could not migrate ban file " + filename + ". Another migration attempt for this file will be made the next startup or reload.");
					e.printStackTrace();
					migrationFailed = true;
				}
			}
		} catch (IOException e) {
			adapter.error("Unable to migrate bans.");
			e.printStackTrace();
		}

		if (didMigration && !migrationFailed) {
			adapter.log("Bans migration ended successfully");
		}
	}

	@Nullable
	private static LoggedBan getActiveBanFromLoggedBans(List<LoggedBan> loggedBans) {
		if (loggedBans.isEmpty())
			return null;

		final long now = System.currentTimeMillis();
		for (LoggedBan loggedBan : loggedBans) {
			if (!loggedBan.isRevoked() && (loggedBan.isDefinitive() || loggedBan.getExpirationTime() > now)) {
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
