package com.elikill58.negativity.universal.ban.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.LoggedBan;

public class FileBanLogsStorage implements BanLogsStorage {

	private Path storageDir;

	public FileBanLogsStorage(Path storageDir) {
		this.storageDir = storageDir;
	}

	@Override
	public List<LoggedBan> load(UUID playerId) {
		List<LoggedBan> loadedBans = new ArrayList<>();

		Path banFile = getLoadBanDir().resolve(playerId + ".txt");
		if (Files.notExists(banFile)) {
			return loadedBans;
		}

		try {
			for (String line : Files.readAllLines(banFile)) {
				LoggedBan ban = fromString(playerId, line);
				if (ban != null) {
					loadedBans.add(ban);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return loadedBans;
	}

	@Override
	public void save(LoggedBan ban) {
		try {
			Path file = storageDir.resolve(ban.getPlayerId() + ".txt");
			Files.createDirectories(file.getParent());

			Files.write(file, (toString(ban) + "\n").getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Used for migrations
	protected Path getLoadBanDir() {
		return storageDir;
	}

	private static String toString(LoggedBan ban) {
		return ban.getExpirationTime()
				+ ":reason=" + ban.getReason().replaceAll(":", "")
				+ ":bantype=" + ban.getBanType().name()
				+ (ban.getCheatName() != null ? ":ac=" + ban.getCheatName() : "")
				+ ":by=" + ban.getBannedBy()
				+ ":revoked=" + ban.isRevoked();
	}

	@Nullable
	private static LoggedBan fromString(UUID playerId, String line) {
		String[] content = line.split(":");
		if (content.length == 1)
			return null;

		long expirationTime;
		try {
			expirationTime = Long.parseLong(content[0]);
		} catch (NumberFormatException e) {
			// This line is invalid
			return null;
		}

		String reason = "";
		String by = "Negativity";
		boolean isRevoked = false;
		BanType banType = BanType.UNKNOW;
		String ac = null;
		for (String s : content) {
			String[] part = s.split("=", 2);
			if (part.length != 2)
				continue;
			String type = part[0], value = part[1];
			switch (type) {
				case "bantype":
					banType = BanType.valueOf(value.toUpperCase());
					break;
				case "def":
					// Here for compatibility with files generated from an older version
					// of the plugin, where the expiration value may not be negative
					expirationTime = -1;
					break;
				case "reason":
					reason = value;
					break;
				case "ac":
					ac = value;
					break;
				case "by":
					by = value;
					break;
				case "unban":
				case "revoked":
					isRevoked = Boolean.parseBoolean(value);
					break;
				default:
					Adapter.getAdapter().warn("Type " + type + " unknow. Value: " + value);
					break;
			}
		}

		return new LoggedBan(playerId, reason, by, banType, expirationTime, ac, isRevoked);
	}
}
