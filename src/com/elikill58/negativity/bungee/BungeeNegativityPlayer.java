package com.elikill58.negativity.bungee;

import java.util.HashMap;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeNegativityPlayer extends NegativityPlayer {

	private static HashMap<ProxiedPlayer, BungeeNegativityPlayer> players = new HashMap<>();
	private ProxiedPlayer p;

	public BungeeNegativityPlayer(ProxiedPlayer p) {
		super(p.getUniqueId());
		this.p = p;
		players.put(p, this);
	}

	public static BungeeNegativityPlayer getNegativityPlayer(ProxiedPlayer p) {
		return players.containsKey(p) ? players.get(p) : new BungeeNegativityPlayer(p);
	}

	@Override
	public Object getPlayer() {
		return p;
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
	public int getWarn(Cheat c) {
		return 0;
	}

	@Override
	public void startAnalyze(Cheat c) {

	}
}
