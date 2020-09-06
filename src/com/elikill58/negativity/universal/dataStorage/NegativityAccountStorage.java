package com.elikill58.negativity.universal.dataStorage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.dataStorage.database.DatabaseNegativityAccountStorage;
import com.elikill58.negativity.universal.dataStorage.file.FileNegativityAccountStorage;

public abstract class NegativityAccountStorage {

	private static final Map<String, NegativityAccountStorage> storages = new HashMap<>();
	private static String storageId;

	public abstract CompletableFuture<@Nullable NegativityAccount> loadAccount(UUID playerId);

	public abstract CompletableFuture<Void> saveAccount(NegativityAccount account);

	public CompletableFuture<NegativityAccount> getOrCreateAccount(UUID playerId) {
		return loadAccount(playerId).thenApply(existingAccount -> existingAccount == null ? new NegativityAccount(playerId) : existingAccount);
	}

	public static NegativityAccountStorage getStorage() {
		return storages.getOrDefault(storageId, VoidAccountStorage.INSTANCE);
	}

	public static void register(String id, NegativityAccountStorage storage) {
		storages.put(id, storage);
	}

	public static String getStorageId() {
		return storageId;
	}

	public static void setStorageId(String storageId) {
		NegativityAccountStorage.storageId = storageId;
	}

	public static void setDefaultStorage(String storageId) {
		NegativityAccountStorage storage = storages.get(storageId);
		if (storage != null) {
			register("default", storage);
		}
	}

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		storageId = adapter.getConfig().getString("accounts.storage.id");
		
		NegativityAccountStorage.register("file", new FileNegativityAccountStorage(new File(adapter.getDataFolder(), "user")));
		if (Database.hasCustom) {
			NegativityAccountStorage.register("database", new DatabaseNegativityAccountStorage());
		}
	}
}
