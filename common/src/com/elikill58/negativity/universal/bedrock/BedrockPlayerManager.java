package com.elikill58.negativity.universal.bedrock;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PluginDependentExtension;

public class BedrockPlayerManager {
	private static final List<BedrockPlayerChecker> CHECKERS = new ArrayList<>();
	
	public static void init() {
		CHECKERS.clear();
		Negativity.loadExtensions(BedrockPlayerCheckerProvider.class, provider -> {
			BedrockPlayerChecker checker = provider.create(Adapter.getAdapter());
			if (checker != null) {
				Negativity.integratedPlugins.add(((PluginDependentExtension) provider).getPluginId());
				CHECKERS.add(checker);
				return true;
			}
			return false;
		});
	}
	
	public static boolean isBedrockPlayer(Player player) {
		for (BedrockPlayerChecker checker : CHECKERS) {
			if (checker.isBedrockPlayer(player)) {
				return true;
			}
		}
		return false;
	}
}
