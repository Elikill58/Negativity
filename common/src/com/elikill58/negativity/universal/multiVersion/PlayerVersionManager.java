package com.elikill58.negativity.universal.multiVersion;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;

public class PlayerVersionManager {
	
	private static final List<PlayerVersionFetcher> FETCHERS = new ArrayList<>();
	
	public static void init() {
		FETCHERS.clear();
		Adapter adapter = Adapter.getAdapter();
		Negativity.loadExtensions(PlayerVersionFetcherProvider.class, provider -> {
			PlayerVersionFetcher fetcher = provider.create(adapter);
			if (fetcher != null) {
				FETCHERS.add(fetcher);
				return true;
			}
			return false;
		});
	}
	
	public static Version getPlayerVersion(Player player) {
		for (PlayerVersionFetcher fetcher : FETCHERS) {
			Version playerVersion = fetcher.getPlayerVersion(player);
			if (playerVersion != null) {
				return playerVersion;
			}
		}
		return Version.HIGHER;
	}
	
	public static int getPlayerProtocolVersion(Player player) {
		for (PlayerVersionFetcher fetcher : FETCHERS) {
			Integer playerVersion = fetcher.getPlayerProtocolVersion(player);
			if (playerVersion != null) {
				return playerVersion;
			}
		}
		return Version.getVersion().getFirstProtocolNumber();
	}
}
