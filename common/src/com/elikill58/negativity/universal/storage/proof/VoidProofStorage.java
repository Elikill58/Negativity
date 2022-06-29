package com.elikill58.negativity.universal.storage.proof;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.universal.Proof;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;

public final class VoidProofStorage extends NegativityProofStorage {

	public static final VoidProofStorage INSTANCE = new VoidProofStorage();

	@Override
	public CompletableFuture<List<Proof>> getProof(UUID playerId) {
		return CompletableFuture.completedFuture(new ArrayList<>());
	}

	@Override
	public CompletableFuture<List<Proof>> getProofForCheat(UUID playerId, CheatKeys key) {
		return CompletableFuture.completedFuture(new ArrayList<>());
	}

	@Override
	public void saveProof(Proof proof) {}
}
