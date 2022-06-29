package com.elikill58.negativity.universal.storage.proof;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Proof;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.storage.proof.database.DatabaseNegativityProofStorage;
import com.elikill58.negativity.universal.storage.proof.file.FileNegativityProofStorage;

public abstract class NegativityProofStorage {

	private static final Map<String, NegativityProofStorage> storages = new HashMap<>();
	public static Map<String, NegativityProofStorage> getStorages() {
		return storages;
	}
	private static String storageId;
	
	public void enable() {}

	public abstract CompletableFuture<List<Proof>> getProof(UUID playerId);

	public abstract CompletableFuture<List<Proof>> getProofForCheat(UUID playerId, CheatKeys key);

	public abstract void saveProof(Proof proof);

	public void saveProof(List<Proof> proof) {
		proof.forEach(this::saveProof);
	}

	public static NegativityProofStorage getStorage() {
		return storages.getOrDefault(storageId, VoidProofStorage.INSTANCE);
	}

	public static void register(String id, NegativityProofStorage storage) {
		storages.put(id, storage);
	}

	public static String getStorageId() {
		return storageId;
	}

	public static void setStorageId(String storageId) {
		NegativityProofStorage.storageId = storageId;
		getStorage().enable();
	}

	public static void setDefaultStorage(String storageId) {
		NegativityProofStorage storage = storages.get(storageId);
		if (storage != null) {
			register("default", storage);
		}
	}

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		storageId = adapter.getConfig().getString("proofs.storage.id", "file");
		
		NegativityProofStorage.register("file", new FileNegativityProofStorage(adapter.getDataFolder().toPath().resolve("user").resolve("proof").toFile()));
		if (Database.hasCustom) {
			NegativityProofStorage.register("database", new DatabaseNegativityProofStorage());
		}
		getStorage().enable();
	}
}
