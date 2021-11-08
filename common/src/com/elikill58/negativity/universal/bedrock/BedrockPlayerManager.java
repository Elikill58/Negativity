package com.elikill58.negativity.universal.bedrock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;

public class BedrockPlayerManager {
	private static final List<BedrockPlayerChecker> CHECKERS = new ArrayList<>();
	
	public static void init() {
		CHECKERS.clear();
		Negativity.loadExtensions(BedrockPlayerCheckerProvider.class, provider -> {
			BedrockPlayerChecker checker = provider.create(Adapter.getAdapter());
			if (checker != null) {
				CHECKERS.add(checker);
				return true;
			}
			return false;
		});
	}
	
	public static boolean isBedrockPlayer(UUID uuid) {
		for (BedrockPlayerChecker checker : CHECKERS) {
			if (checker.isBedrockPlayer(uuid)) {
				return true;
			}
		}
		return false;
	}
}
