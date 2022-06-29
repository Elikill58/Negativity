package com.elikill58.negativity.universal.verif.storage;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.verif.Verificator;
import com.elikill58.negativity.universal.verif.storage.database.DatabaseVerificationStorage;
import com.elikill58.negativity.universal.verif.storage.file.FileVerificationStorage;

public abstract class VerificationStorage {

	private static final Map<String, VerificationStorage> storages = new HashMap<>();
	private static final DateTimeFormatter PATTERN_FILE_NAME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
	private static String storageId;

	/**
	 * Get all verification made previously
	 * 
	 * @param playerId the player UUID
	 * @return a futur which have to be executed async
	 */
	public abstract CompletableFuture<List<Verificator>> loadAllVerifications(UUID playerId);

	/**
	 * Save verification
	 * 
	 * @param account the verificator to save
	 * @return a futur which have to be executed async
	 */
	public abstract CompletableFuture<Void> saveVerification(Verificator account);

	/**
	 * Get the current verification storage system
	 * If the storage ID is not found, it will return the instance of {@link VoidVerificationStorage}
	 * 
	 * @return the verification storage manager
	 */
	public static VerificationStorage getStorage() {
		return storages.getOrDefault(storageId, VoidVerificationStorage.INSTANCE);
	}

	/**
	 * Register a new verification storage
	 * 
	 * @param id the storage ID
	 * @param storage the storage instance
	 */
	public static void register(String id, VerificationStorage storage) {
		storages.put(id, storage);
	}

	/**
	 * Get the storage ID currently used
	 * 
	 * @return the storage ID
	 */
	public static String getStorageId() {
		return storageId;
	}
	
	/**
	 * Get a new file name according to the {@link #PATTERN_FILE_NAME} format
	 * Don't return the file path, just the name (with json extension)
	 * 
	 * @return the file name
	 */
	public static String getNewFileName() {
		return PATTERN_FILE_NAME.format(LocalDateTime.now()) + ".json";
	}

	/**
	 * Edit the current storage ID
	 * 
	 * @param storageId the new storage ID
	 */
	public static void setStorageId(String storageId) {
		VerificationStorage.storageId = storageId;
	}

	/**
	 * Edit the default storage ID
	 * 
	 * @param storageId the default storage ID
	 */
	public static void setDefaultStorage(String storageId) {
		VerificationStorage storage = storages.get(storageId);
		if (storage != null) {
			register("default", storage);
		}
	}

	/**
	 * Load verification storage
	 */
	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		storageId = adapter.getConfig().getString("verif.storage.id");
		register("file", new FileVerificationStorage(adapter.getDataFolder().toPath().resolve("verif")));
		
		if (Database.hasCustom) {
			register("database", new DatabaseVerificationStorage());
		}
		setDefaultStorage("file");
	}
}
