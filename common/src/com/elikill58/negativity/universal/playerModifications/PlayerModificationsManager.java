package com.elikill58.negativity.universal.playerModifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;

public class PlayerModificationsManager {
	
	private static final List<PlayerModifications> MODIFICATIONS = new ArrayList<>();
	
	public static void init() {
		MODIFICATIONS.clear();
		Adapter adapter = Adapter.getAdapter();
		Negativity.loadExtensions(PlayerModificationsProvider.class, provider -> {
			PlayerModifications modifications = provider.create(adapter);
			if (modifications != null) {
				MODIFICATIONS.add(modifications);
				return true;
			}
			return false;
		});
		if(Version.getVersion().isNewerOrEquals(Version.V1_13)) {
			MODIFICATIONS.add(new PlayerModifications() {
				@Override
				public boolean shouldIgnoreMovementChecks(Player player) {
					return player.isUsingRiptide();
				}
			});
		}
	}
	
	public static boolean isProtected(Player player, Entity damager) {
		for (PlayerModifications protection : MODIFICATIONS) {
			if (protection.isProtected(player, damager)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean canFly(Player player) {
		for (PlayerModifications protection : MODIFICATIONS) {
			if (protection.canFly(player)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isSpeedUnlocked(Player player) {
		for (PlayerModifications protection : MODIFICATIONS) {
			if (protection.isSpeedUnlocked(player)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean shouldIgnoreMovementChecks(Player player) {
		for (PlayerModifications protection : MODIFICATIONS) {
			if (protection.shouldIgnoreMovementChecks(player)) {
				return true;
			}
		}
		return false;
	}
	
	public static Collection<PlayerModifications> getModifications() {
		return Collections.unmodifiableCollection(MODIFICATIONS);
	}
}
