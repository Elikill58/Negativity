package com.elikill58.negativity.universal;

import java.util.UUID;

import com.elikill58.negativity.universal.adapter.Adapter;

public abstract class NegativityPlayer {

	private final UUID playerId;
	private boolean isBanned = false, isMcLeaks = false;

	public NegativityPlayer(UUID playerId) {
		this.playerId = playerId;
		this.isMcLeaks = Adapter.getAdapter().isUsingMcLeaks(playerId);
	}

	public NegativityAccount getAccount() {
		return Adapter.getAdapter().getNegativityAccount(playerId);
	}
	
	public UUID getUUID() {
		return playerId;
	}
	
	public boolean isMcLeaks() {
		return isMcLeaks;
	}
	
	public boolean isBanned() {
		return isBanned;
	}
	
	public void setBanned(boolean b) {
		isBanned = b;
	}

	public abstract Object getPlayer();
	public abstract boolean hasDefaultPermission(String s);
	public abstract int getWarn(Cheat c);
	public abstract int getAllWarn(Cheat c);
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
	public abstract boolean isOp();
	//public abstract void setLang(String newLang);
	public abstract String getIP();
	public abstract String getReason(Cheat c);
}
