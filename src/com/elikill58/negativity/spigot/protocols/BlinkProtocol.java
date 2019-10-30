package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class BlinkProtocol extends Cheat implements Listener {
	
	public BlinkProtocol() {
		super("BLINK", true, Material.COAL_BLOCK, false, true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e){
		SpigotNegativityPlayer.getNegativityPlayer(e.getEntity()).bypassBlink = true;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e){
		SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer()).bypassBlink = false;
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
