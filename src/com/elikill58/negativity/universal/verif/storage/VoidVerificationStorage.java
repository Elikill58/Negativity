package com.elikill58.negativity.universal.verif.storage;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.universal.verif.Verificator;

public final class VoidVerificationStorage extends VerificationStorage {

	public static final VoidVerificationStorage INSTANCE = new VoidVerificationStorage();

	@Override
	public CompletableFuture<List<Verificator>> loadAllVerifications(UUID playerId) {
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> saveVerification(Verificator account) {
		return CompletableFuture.completedFuture(null);
	}
}
