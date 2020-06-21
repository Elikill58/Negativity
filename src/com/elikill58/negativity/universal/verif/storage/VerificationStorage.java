package com.elikill58.negativity.universal.verif.storage;


import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.verif.Verificator;
import com.elikill58.negativity.universal.verif.storage.database.DatabaseVerificationStorage;
import com.elikill58.negativity.universal.verif.storage.file.FileVerificationStorage;

public abstract class VerificationStorage {

	private static final Map<String, VerificationStorage> storages = new HashMap<>();
	private static String storageId;

	public abstract CompletableFuture<List<Verificator>> loadAllVerifications(UUID playerId);

	public abstract CompletableFuture<Void> saveVerification(Verificator account);

	public static VerificationStorage getStorage() {
		return storages.getOrDefault(storageId, VoidVerificationStorage.INSTANCE);
	}

	public static void register(String id, VerificationStorage storage) {
		storages.put(id, storage);
	}

	public static String getStorageId() {
		return storageId;
	}
	
	public static String getNewFileName() {
		return new Timestamp(System.currentTimeMillis()).toString().split("\\.")[0].replaceAll(" ", "_").replaceAll(":", "_") + ".json";
	}

	public static void setStorageId(String storageId) {
		VerificationStorage.storageId = storageId;
	}

	public static void setDefaultStorage(String storageId) {
		VerificationStorage storage = storages.get(storageId);
		if (storage != null) {
			register("default", storage);
		}
	}

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		storageId = adapter.getConfig().getString("verif.storage.id");
		register("file", new FileVerificationStorage(new File(adapter.getDataFolder(), "verif")));
		
		if (Database.hasCustom) {
			register("database", new DatabaseVerificationStorage());
		}
		setDefaultStorage("file");
	}
}
