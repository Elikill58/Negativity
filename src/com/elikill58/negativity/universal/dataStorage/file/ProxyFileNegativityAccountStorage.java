package com.elikill58.negativity.universal.dataStorage.file;

import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;

public class ProxyFileNegativityAccountStorage extends NegativityAccountStorage {

	@Nullable
	@Override
	public NegativityAccount loadAccount(UUID playerId) {
		return new NegativityAccount(playerId, TranslatedMessages.getDefaultLang());
	}

	@Override
	public void saveAccount(NegativityAccount account) {
	}

	@Override
	public void init() {
	}

	@Override
	public void close() {
	}
}
