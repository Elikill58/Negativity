package com.elikill58.negativity.universal.ban;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BaseNegativityBanProcessor;
import com.elikill58.negativity.universal.ban.processor.NegativityBanProcessor;
import com.elikill58.negativity.universal.ban.storage.BanStorageManager;

public class BanManager {

	public static boolean banActive;

	private static BanProcessor banProcessor = new NegativityBanProcessor();

	public static List<LoggedBan> getLoggedBans(UUID playerId) {
		if (!banActive)
			return Collections.emptyList();

		return banProcessor.getLoggedBans(playerId);
	}

	public static boolean isBanned(UUID playerId) {
		if (!banActive)
			return false;

		return banProcessor.isBanned(playerId);
	}

	@Nullable
	public static ActiveBan getActiveBan(UUID playerId) {
		if (!banActive)
			return null;

		return banProcessor.getActiveBan(playerId);
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

		return banProcessor.banPlayer(playerId, reason, bannedBy, isDefinitive, banType, expirationTime, cheatName);
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

		return banProcessor.revokeBan(playerId);
	}

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		banActive = adapter.getBooleanInConfig("ban.active");
		if (!banActive)
			return;

		loadStorages("ban.storage", BanStorageManager.getAvailableBanStorageIds(), BaseNegativityBanProcessor::setBanStorageId);

		BaseNegativityBanProcessor.setLogBans(adapter.getBooleanInConfig("ban.log_bans"));
	}

	private static void loadStorages(String propertyName, Collection<String> availableStorages, Consumer<String> storageSetter) {
		Adapter adapter = Adapter.getAdapter();
		String banStorage = adapter.getStringInConfig(propertyName);
		if (banStorage == null) {
			adapter.log("The property " + propertyName + " is missing from the configuration file. Please add it and restart the server.");
			return;
		}

		if (banStorage.equalsIgnoreCase("db"))
			banStorage = "database";

		if (!availableStorages.contains(banStorage)) {
			adapter.error("Error while loading ban system. '" + banStorage + "' is an unknown storage type.");
			adapter.error("Please set a valid storage type, then restart you server.");
			return;
		}

		storageSetter.accept(banStorage);
	}
}
