package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import com.elikill58.negativity.api.NegativityPlayer;

public class ElytraListeners implements Listener {
	
	@EventHandler
	public void onGlide(EntityToggleGlideEvent e) {
		if(!e.isGliding() && e.getEntity() instanceof Player)
			NegativityPlayer.getCached(e.getEntity().getUniqueId()).addInvincibilityTicks(5, "Glide");
	}
}
