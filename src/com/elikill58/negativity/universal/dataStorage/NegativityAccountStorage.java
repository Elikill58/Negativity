package com.elikill58.negativity.universal.dataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.dataStorage.database.DatabaseNegativityAccountStorage;

public abstract class NegativityAccountStorage {

	private static final Map<String, NegativityAccountStorage> storages = new HashMap<>();
	private static String storageId;

	@Nullable
	public abstract NegativityAccount loadAccount(UUID playerId);

	public abstract void saveAccount(NegativityAccount account);

	public NegativityAccount getOrCreateAccount(UUID playerId) {
		NegativityAccount existingAccount = loadAccount(playerId);
		if (existingAccount != null) {
			return existingAccount;
		}

		NegativityAccount createdAccount = new NegativityAccount(playerId);
		saveAccount(createdAccount);
		return createdAccount;
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

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		storageId = adapter.getConfig().getString("accounts.storage.id");
		if (Database.hasCustom) {
			NegativityAccountStorage.register("database", new DatabaseNegativityAccountStorage());
		}
	}
}
