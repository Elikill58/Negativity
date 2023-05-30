package com.elikill58.negativity.universal.bedrock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.packets.BedrockClientData;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.bedrock.checker.BedrockPlayerChecker;
import com.elikill58.negativity.universal.bedrock.checker.BedrockPlayerCheckerProvider;
import com.elikill58.negativity.universal.bedrock.data.BedrockClientDataGetter;
import com.elikill58.negativity.universal.bedrock.data.BedrockClientDataProvider;

public class BedrockPlayerManager {
	
	private static final List<BedrockPlayerChecker> CHECKERS = new ArrayList<>();
	private static final List<BedrockClientDataGetter> DATA_GETTERS = new ArrayList<>();
	
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
		CHECKERS.add((uuid) -> {
			NegativityPlayer np = NegativityPlayer.getCached(uuid);
			return np != null && np.getClientName() != null && np.getClientName().contains("Geyser"); // on velocity it's "Geyser (Velocity)" and not only "Geyser"
		});
		
		DATA_GETTERS.clear();
		Negativity.loadExtensions(BedrockClientDataProvider.class, provider -> {
			BedrockClientDataGetter checker = provider.create(Adapter.getAdapter());
			if (checker != null) {
				DATA_GETTERS.add(checker);
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
	
	/**
	 * Get bedrock client data if there is. Else, return null.
	 * 
	 * @param uuid the player UUID
	 * @return the client data, or null if offline/not bedrock
	 */
	public static @Nullable BedrockClientData getBedrockClientData(UUID uuid) {
		for (BedrockClientDataGetter getter : DATA_GETTERS) {
			BedrockClientData data = getter.getClientData(uuid);
			if(data != null)
				return data;
		}
		return null;
	}
}
