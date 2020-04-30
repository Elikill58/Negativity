package com.elikill58.negativity.universal;

import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.annotation.Nullable;

public abstract class NegativityAccountManager {

	/**
	 * Gets the account associated to the given UUID wrapped in a CompletableFuture.
	 * <p>
	 * The account may be returned directly (if cached for example), or loaded via
	 * potential (slow) IO operations.
	 * <p>
	 * Ideally, cancelling the returned CompletableFuture will cancel the underlying
	 * (potentially slow) operations to avoid wasting resources.
	 *
	 * @param accountId the ID of the account to get
	 *
	 * @return the requested account wrapped in a CompletableFuture
	 *
	 * @see #getNow
	 */
	public abstract CompletableFuture<NegativityAccount> get(UUID accountId);

	/**
	 * Gets the account associated to the given UUID in a blocking manner.
	 * <p>
	 * This is equivalent to calling {@code get(accountId).join()}
	 * <p>
	 * This method may throw {@link CompletionException} according to {@link CompletableFuture#join()},
	 * and is unlikely (but still may) throw {@link CancellationException}.
	 *
	 * @param accountId the ID of the account to get
	 *
	 * @return the requested account
	 */
	public NegativityAccount getNow(UUID accountId) {
		return get(accountId).join();
	}

	/**
	 * Saves the account associated to the given UUID.
	 * The returned CompletableFuture will be completed once the save has been done,
	 * and may complete exceptionally if it failed in some way.
	 *
	 * @param accountId the ID of the account to save
	 *
	 * @return a CompletableFuture that will complete once the save has been done
	 */
	public abstract CompletableFuture<Void> save(UUID accountId);

	/**
	 * Indicates that the account for the given UUID can be forgotten by this manager.
	 * <p>
	 * What this method does is completely up to the implementing class, it may do nothing,
	 * or remove the account from a cache for example.
	 * <p>
	 * If an implementation caches accounts, the returned account would be the one removed from its cache.
	 *
	 * @param accountId the ID of the account to dispose
	 *
	 * @return the disposed account, if available
	 */
	@Nullable
	public abstract NegativityAccount dispose(UUID accountId);
}
