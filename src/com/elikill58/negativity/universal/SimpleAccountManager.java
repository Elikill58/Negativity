package com.elikill58.negativity.universal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;

public class SimpleAccountManager extends NegativityAccountManager {

	private final Map<UUID, NegativityAccount> accounts = Collections.synchronizedMap(new HashMap<>());
	private final Map<UUID, CompletableFuture<NegativityAccount>> pendingRequests = Collections.synchronizedMap(new HashMap<>());
	private final boolean persistent;

	public SimpleAccountManager(boolean persistent) {
		this.persistent = persistent;
	}

	@Override
	public CompletableFuture<NegativityAccount> get(UUID accountId) {
		CompletableFuture<NegativityAccount> pendingRequest = pendingRequests.get(accountId);
		if (pendingRequest != null) {
			return pendingRequest;
		}

		NegativityAccount existingAccount = accounts.get(accountId);
		if (existingAccount != null) {
			return CompletableFuture.completedFuture(existingAccount);
		}

		CompletableFuture<NegativityAccount> loadFuture = NegativityAccountStorage.getStorage().getOrCreateAccount(accountId);
		pendingRequests.put(accountId, loadFuture);
		loadFuture.whenComplete((account, throwable) -> {
			pendingRequests.remove(accountId);
			if (throwable != null && !(throwable instanceof CancellationException)) {
				Adapter.getAdapter().error("Account loading completed exceptionally: " + throwable.getMessage());
				throwable.printStackTrace();
				return;
			}

			UUID playerId = account.getPlayerId();
			accounts.put(playerId, account);
		});
		return loadFuture;
	}

	@Override
	public CompletableFuture<Void> save(UUID accountId) {
		if (persistent) {
			NegativityAccount existingAccount = accounts.get(accountId);
			if (existingAccount != null) {
				return NegativityAccountStorage.getStorage().saveAccount(existingAccount);
			}
		}
		return CompletableFuture.completedFuture(null);
	}

	@Nullable
	@Override
	public NegativityAccount dispose(UUID accountId) {
		return accounts.remove(accountId);
	}
}
