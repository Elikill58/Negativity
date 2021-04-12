package com.elikill58.negativity.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;

public class ElytraEvents implements Listener {
	
	
	@EventHandler
	public void onGlide(EntityToggleGlideEvent e) {
		if(!e.isGliding() && e.getEntity() instanceof Player)
			SpigotNegativityPlayer.getNegativityPlayer((Player) e.getEntity()).TIME_INVINCIBILITY = System.currentTimeMillis() + 1000;
	}
}
