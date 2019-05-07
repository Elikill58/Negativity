package com.elikill58.negativity.universal;

import java.util.UUID;

import com.elikill58.negativity.universal.adapter.Adapter;

public abstract class NegativityPlayer {

	private final UUID playerId;

	public NegativityPlayer(UUID playerId) {
		this.playerId = playerId;
	}

	public NegativityAccount getAccount() {
		return Adapter.getAdapter().getNegativityAccount(playerId);
	}

	public abstract Object getPlayer();
	public abstract boolean hasDefaultPermission(String s);
	public abstract int getWarn(Cheat c);
	public abstract double getLife();
	public abstract String getName();
	public abstract String getGameMode();
	public abstract float getWalkSpeed();
	public abstract int getLevel();
	public abstract void kickPlayer(String reason, String time, String by, boolean def);
	public abstract void banEffect();
	public abstract void startAnalyze(Cheat c);
	public abstract void startAllAnalyze();
	public abstract void updateMinerateInFile();
}
