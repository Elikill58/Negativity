package com.elikill58.negativity.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.elikill58.negativity.api.NegativityPlayer;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeNegativityPlayer extends NegativityPlayer {

	private static final Map<UUID, BungeeNegativityPlayer> players = new HashMap<>();
	private ProxiedPlayer p;

	public BungeeNegativityPlayer(ProxiedPlayer p) {
		super(null);
		//super(p.getUniqueId(), p.getName());
		this.p = p;
	}

	public static BungeeNegativityPlayer getNegativityPlayer(ProxiedPlayer p) {
		return players.computeIfAbsent(p.getUniqueId(), id -> new BungeeNegativityPlayer(p));
	}
	
	public static void removeFromCache(UUID playerId) {
		players.remove(playerId);
	}
	
	public void kickPlayer(String reason, String time, String by, boolean def) {
		p.disconnect(new ComponentBuilder(BungeeMessages.getMessage(p, "ban.kick_" + (def ? "def" : "time"), "%reason%", reason, "%time%", String.valueOf(time), "%by%", by)).create());
	}
	
	public String getIP() {
		return p.getAddress().getHostName();
	}
}
