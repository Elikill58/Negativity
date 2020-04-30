package com.elikill58.negativity.universal.dataStorage.file;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;

public class ProxyFileNegativityAccountStorage extends NegativityAccountStorage {

	@Override
	public CompletableFuture<@Nullable NegativityAccount> loadAccount(UUID playerId) {
		return CompletableFuture.completedFuture(new NegativityAccount(playerId));
	}

	@Override
	public CompletableFuture<Void> saveAccount(NegativityAccount account) {
		return CompletableFuture.completedFuture(null);
	}
}
