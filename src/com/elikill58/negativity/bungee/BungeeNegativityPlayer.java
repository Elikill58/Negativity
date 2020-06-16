package com.elikill58.negativity.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.Version;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeNegativityPlayer extends NegativityPlayer {

	private static final Map<UUID, BungeeNegativityPlayer> players = new HashMap<>();
	private ProxiedPlayer p;

	public BungeeNegativityPlayer(ProxiedPlayer p) {
		super(p.getUniqueId(), p.getName());
		this.p = p;
	}

	public static BungeeNegativityPlayer getNegativityPlayer(ProxiedPlayer p) {
		return players.computeIfAbsent(p.getUniqueId(), id -> new BungeeNegativityPlayer(p));
	}
	
	public static void removeFromCache(UUID playerId) {
		players.remove(playerId);
	}

	@Override
	public Object getPlayer() {
		return p;
	}

	@Override
	public Version getPlayerVersion() {
		return null;
	}

	@Override
	public boolean hasDefaultPermission(String s) {
		return p.hasPermission(s);
	}

	@Override
	public double getLife() {
		return -1;
	}

	@Override
	public String getName() {
		return p.getName();
	}

	@Override
	public String getGameMode() {
		return "unknow";
	}

	@Override
	public float getWalkSpeed() {
		return -1;
	}

	@Override
	public int getLevel() {
		return -1;
	}

	@Override
	public void kickPlayer(String reason, String time, String by, boolean def) {
		p.disconnect(new ComponentBuilder(BungeeMessages.getMessage(p, "ban.kick_" + (def ? "def" : "time"), "%reason%", reason, "%time%", String.valueOf(time), "%by%", by)).create());
	}

	@Override
	public void banEffect() {
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
	public boolean isOp() {
		return false;
	}
	
	@Override
	public String getIP() {
		return p.getAddress().getHostName();
	}

	@Override
	public String getReason(Cheat c) {
		return null;
	}
}
