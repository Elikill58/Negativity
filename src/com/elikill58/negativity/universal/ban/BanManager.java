package com.elikill58.negativity.universal.ban;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.CommandBanProcessor;
import com.elikill58.negativity.universal.ban.processor.NegativityBanProcessor;
import com.elikill58.negativity.universal.ban.storage.DatabaseActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.DatabaseBanLogsStorage;
import com.elikill58.negativity.universal.ban.storage.FileActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.FileBanLogsStorage;

public class BanManager {

	public static boolean banActive;

	private static String processorId;
	private static Map<String, BanProcessor> processors = new HashMap<>();

	public static List<LoggedBan> getLoggedBans(UUID playerId) {
		if (!banActive)
			return Collections.emptyList();

		return getProcessor().getLoggedBans(playerId);
	}

	public static boolean isBanned(UUID playerId) {
		if (!banActive)
			return false;

		return getProcessor().isBanned(playerId);
	}

	@Nullable
	public static ActiveBan getActiveBan(UUID playerId) {
		if (!banActive)
			return null;

		return getProcessor().getActiveBan(playerId);
	}

	/**
	 * Executes the given ban. The executed ban may contain different information than the one you provided.
	 * Therefore, it is advised to use the returned {@link ActiveBan} data instead of what you gave in this method parameters.
	 * <p>
	 * The ban may not be executed if bans are disabled, or for any {@link BanProcessor}-specific reason, like if the player bypassed the ban.
	 *
	 * @return the ban that has been executed, or {@code null} if the ban has not been executed.
	 */
	@Nullable
	public static ActiveBan banPlayer(UUID playerId, String reason, String bannedBy, boolean isDefinitive, BanType banType, long expirationTime, @Nullable String cheatName) {
		if (!banActive)
			return null;

		return getProcessor().banPlayer(playerId, reason, bannedBy, isDefinitive, banType, expirationTime, cheatName);
	}

	/**
	 * Revokes the active ban of the player identified by the given UUID.
	 * <p>
	 * The revocation may fail if the player is not banned or bans are disabled.
	 * <p>
	 * If ban logging is disabled, a LoggedBan will still be returned even though it will not be saved.
	 *
	 * @param playerId the UUID of the player to unban
	 *
	 * @return the logged revoked ban or {@code null} if the revocation failed.
	 */
	@Nullable
	public static LoggedBan revokeBan(UUID playerId) {
		if (!banActive)
			return null;

		return getProcessor().revokeBan(playerId);
	}

	public static String getProcessorId() {
		return processorId;
	}

	public static BanProcessor getProcessor() {
		return processors.get(processorId);
	}

	public static void registerProcessor(String id, BanProcessor processor) {
		processors.put(id, processor);
	}

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		banActive = adapter.getBooleanInConfig("ban.active");
		if (!banActive)
			return;

		processorId = adapter.getStringInConfig("ban.type");

		boolean logBans = adapter.getBooleanInConfig("ban.log_bans");

		Path dataDir = adapter.getDataFolder().toPath();
		Path banDir = dataDir.resolve(adapter.getStringInConfig("ban.file.dir"));
		Path banLogsDir = dataDir.resolve(adapter.getStringInConfig("ban.file.logs_dir"));
		registerProcessor("file", new NegativityBanProcessor(new FileActiveBanStorage(banDir), logBans ? new FileBanLogsStorage(banLogsDir) : null));

		registerProcessor("database", new NegativityBanProcessor(new DatabaseActiveBanStorage(), logBans ? new DatabaseBanLogsStorage() : null));

		List<String> banCommands = adapter.getStringListInConfig("ban.command.ban");
		List<String> unbanCommands = adapter.getStringListInConfig("ban.command.unban");
		registerProcessor("commands", new CommandBanProcessor(banCommands, unbanCommands));

		BansMigration.migrateBans(banDir, banLogsDir);
	}
}
