package com.elikill58.negativity.universal.account;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.pluginMessages.AccountUpdateMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;

public class SimpleAccountManager extends NegativityAccountManager {

	protected final Map<UUID, NegativityAccount> accounts = Collections.synchronizedMap(new HashMap<>());
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
				Adapter.getAdapter().getLogger().error("Account loading completed exceptionally: " + throwable.getMessage());
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

	@Override
	public void update(NegativityAccount account) {
		accounts.put(account.getPlayerId(), account);
	}

	@Nullable
	@Override
	public NegativityAccount dispose(UUID accountId) {
		return accounts.remove(accountId);
	}

	public static class Proxy extends SimpleAccountManager {

		public Proxy() {
			super(false);
		}
	}

	public static class Server extends SimpleAccountManager {

		private final Consumer<byte[]> updateMessageSender;

		public Server(Consumer<byte[]> updateMessageSender) {
			super(true);
			this.updateMessageSender = updateMessageSender;
		}

		public void sendAccountToProxy(UUID accountId) throws IOException {
			NegativityAccount account = accounts.get(accountId);
			if (account != null) {
				sendAccountToProxy(account);
			}
		}

		public void sendAccountToProxy(NegativityAccount account) throws IOException {
			byte[] rawMessage = NegativityMessagesManager.writeMessage(new AccountUpdateMessage(account));
			updateMessageSender.accept(rawMessage);
		}
	}
}
