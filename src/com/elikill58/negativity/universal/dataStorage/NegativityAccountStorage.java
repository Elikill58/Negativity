package com.elikill58.negativity.universal.dataStorage;

import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.TranslatedMessages;

public abstract class NegativityAccountStorage {

	private static NegativityAccountStorage storage;

	@Nullable
	public abstract NegativityAccount loadAccount(UUID playerId);

	public abstract void saveAccount(NegativityAccount account);

	public NegativityAccount getOrCreateAccount(UUID playerId) {
		NegativityAccount existingAccount = loadAccount(playerId);
		if (existingAccount != null) {
			return existingAccount;
		}

		NegativityAccount createdAccount = new NegativityAccount(playerId, TranslatedMessages.getDefaultLang());
		saveAccount(createdAccount);
		return createdAccount;
	}

	public abstract void init();

	public abstract void close();

	public static NegativityAccountStorage getStorage() {
		return storage;
	}

	public static void setStorage(NegativityAccountStorage storage) {
		NegativityAccountStorage.storage = storage;
	}
}
