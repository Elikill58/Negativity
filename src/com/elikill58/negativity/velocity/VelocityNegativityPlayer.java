package com.elikill58.negativity.velocity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.velocitypowered.api.proxy.Player;

public class VelocityNegativityPlayer extends NegativityPlayer {

	private static final Map<UUID, VelocityNegativityPlayer> players = new HashMap<>();
	private Player p;

	public VelocityNegativityPlayer(Player p) {
		super(null);
		this.p = p;
	}

	public static VelocityNegativityPlayer getNegativityPlayer(Player p) {
		return players.computeIfAbsent(p.getUniqueId(), id -> new VelocityNegativityPlayer(p));
	}

	public static void removeFromCache(UUID playerId) {
		players.remove(playerId);
	}

	@Override
	public String getName() {
		return p.getUsername();
	}

	@Override
	public void kickPlayer(String reason, String time, String by, boolean def) {
		p.disconnect(VelocityMessages.getMessage(p, "ban.kick_" + (def ? "def" : "time"), "%reason%", reason, "%time%", String.valueOf(time), "%by%", by));
	}

	@Override
	public void startAllAnalyze() {
	}
	
	@Override
	public void startAnalyze(Cheat c) {

	}

	@Override
	public void stopAnalyze(Cheat c) {
		
	}

	@Override
	public String getReason(Cheat c) {
		return null;
	}
}
