package com.elikill58.negativity.universal.ban;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.CommandBanProcessor;
import com.elikill58.negativity.universal.ban.processor.NegativityBanProcessor;
import com.elikill58.negativity.universal.ban.storage.DatabaseActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.DatabaseBanLogsStorage;
import com.elikill58.negativity.universal.ban.storage.FileActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.FileBanLogsStorage;

public class BanManager {

	public static boolean banActive, autoBan = false;

	private static String processorId;
	private static Map<String, BanProcessor> processors = new HashMap<>();

	public static List<Ban> getLoggedBans(UUID playerId) {
		BanProcessor processor = getProcessor();
		if (processor == null)
			return Collections.emptyList();

		return processor.getLoggedBans(playerId);
	}

	public static boolean isBanned(UUID playerId) {
		BanProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find ban processor while trying check if players banned for " + playerId.toString());
			return false;
		}

		return processor.isBanned(playerId);
	}

	@Nullable
	public static Ban getActiveBan(UUID playerId) {
		BanProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find ban processor while trying to get active ban from " + playerId.toString());
			return null;
		}

		return processor.getActiveBan(playerId);
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
	public static Ban executeBan(Ban ban) {
		BanProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find ban processor while trying to execute ban from " + ban.getPlayerId().toString());
			return null;
		}

		Adapter.getAdapter().debug("Running ban for player UUID " + ban.getPlayerId().toString());
		return processor.executeBan(ban);
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
	public static Ban revokeBan(UUID playerId) {
		BanProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find ban processor while trying to revoke ban from " + playerId.toString());
			return null;
		}

		return processor.revokeBan(playerId);
	}
	
	/**
	 * Indicates whether Negativity should kick banned players
	 */
	public static boolean shouldNegativityHandleBans() {
		BanProcessor processor = getProcessor();
		return processor != null && processor.isHandledByNegativity();
	}

	public static String getProcessorId() {
		return processorId;
	}

	@Nullable
	public static BanProcessor getProcessor() {
		if (!banActive)
			return null;

		return processors.get(processorId);
	}

	public static void registerProcessor(String id, BanProcessor processor) {
		processors.put(id, processor);
	}

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		banActive = adapter.getConfig().getBoolean("ban.active");
		if (!banActive)
			return;

		processorId = adapter.getConfig().getString("ban.processor");

		Path dataDir = adapter.getDataFolder().toPath();
		Path banDir = dataDir.resolve("bans");
		Path banLogsDir = banDir.resolve("logs");
		boolean fileLogBans = adapter.getConfig().getBoolean("ban.file.log_bans");
		registerProcessor("file", new NegativityBanProcessor(new FileActiveBanStorage(banDir), fileLogBans ? new FileBanLogsStorage(banLogsDir) : null));

		if (Database.hasCustom) {
			boolean dbLogBans = adapter.getConfig().getBoolean("ban.database.log_bans");
			registerProcessor("database", new NegativityBanProcessor(new DatabaseActiveBanStorage(), dbLogBans ? new DatabaseBanLogsStorage() : null));
		}
		autoBan = adapter.getConfig().getBoolean("ban.auto");

		List<String> banCommands = adapter.getConfig().getStringList("ban.command.ban");
		List<String> unbanCommands = adapter.getConfig().getStringList("ban.command.unban");
		registerProcessor("command", new CommandBanProcessor(banCommands, unbanCommands));

		BansMigration.migrateBans(banDir, banLogsDir);
	}
}
