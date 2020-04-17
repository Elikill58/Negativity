package com.elikill58.negativity.universal.dataStorage;

import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.NegativityAccount;

public final class VoidAccountStorage extends NegativityAccountStorage {

	public static final VoidAccountStorage INSTANCE = new VoidAccountStorage();

	@Nullable
	@Override
	public NegativityAccount loadAccount(UUID playerId) {
		return null;
	}

	@Override
	public void saveAccount(NegativityAccount account) {
	}
}
