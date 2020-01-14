package com.elikill58.negativity.universal.ban.storage;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BansMigration;

public class BanStorageManager {

	private static final Map<String, BanLogsStorage> LOG_STORAGES = new HashMap<>();
	private static final Map<String, ActiveBanStorage> BAN_STORAGES = new HashMap<>();

	/**
	 * Tries to register the given {@link ActiveBanStorage}. Registration may fail if a storage with the given ID is already registered.
	 *
	 * @param id the ID used to identify the storage to register
	 * @param storage the storage to register
	 *
	 * @return {@code true} if the storage has been successfully registered, {@code false} otherwise.
	 */
	public static boolean registerStorage(String id, ActiveBanStorage storage) {
		return registerStorage(id, storage, false);
	}

	/**
	 * Tries to register the given {@link ActiveBanStorage}. Registration may fail if a storage with the given ID is already registered.
	 *
	 * @param id the ID used to identify the storage to register
	 * @param storage the storage to register
	 * @param replace {@code true} if the existing storage should be replaced
	 *
	 * @return {@code true} if the storage has been successfully registered, {@code false} otherwise.
	 */
	public static boolean registerStorage(String id, ActiveBanStorage storage, boolean replace) {
		if (!replace && BAN_STORAGES.containsKey(id)) {
			return false;
		}

		BAN_STORAGES.put(id, storage);
		return true;
	}

	/**
	 * Tries to register the given {@link BanLogsStorage}. Registration may fail if a storage with the given ID is already registered.
	 *
	 * @param id the ID used to identify the storage to register
	 * @param storage the storage to register
	 *
	 * @return {@code true} if the storage has been successfully registered, {@code false} otherwise.
	 */
	public static boolean registerStorage(String id, BanLogsStorage storage) {
		return registerStorage(id, storage, false);
	}

	/**
	 * Tries to register the given {@link BanLogsStorage}. Registration may fail if a storage with the given ID is already registered.
	 *
	 * @param id the ID used to identify the storage to register
	 * @param storage the storage to register
	 * @param replace {@code true} if the existing storage should be replaced
	 *
	 * @return {@code true} if the storage has been successfully registered, {@code false} otherwise.
	 */
	public static boolean registerStorage(String id, BanLogsStorage storage, boolean replace) {
		if (!replace && LOG_STORAGES.containsKey(id)) {
			return false;
		}

		LOG_STORAGES.put(id, storage);
		return true;
	}

	public static ActiveBanStorage getActiveBanStorage(String id) {
		return BAN_STORAGES.get(id);
	}

	public static BanLogsStorage getBanLogsStorage(String id) {
		return LOG_STORAGES.get(id);
	}

	public static Collection<String> getAvailableBanStorageIds() {
		return BAN_STORAGES.keySet();
	}

	public static Collection<String> getAvailableLogStorageIds() {
		return LOG_STORAGES.keySet();
	}

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		Path dataDir = adapter.getDataFolder().toPath();

		Path banDir = dataDir.resolve(adapter.getStringInConfig("ban.file.dir"));
		registerStorage("file", new FileActiveBanStorage(banDir), true);
		registerStorage("database", new DatabaseActiveBanStorage(), true);

		Path banLogsDir = dataDir.resolve(adapter.getStringInConfig("ban.file.logs_dir"));
		registerStorage("file", new FileBanLogsStorage(banLogsDir), true);
		registerStorage("database", new DatabaseBanLogsStorage(), true);

		BansMigration.migrateBans(banDir, banLogsDir);
	}
}
