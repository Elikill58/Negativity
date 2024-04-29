package com.elikill58.negativity.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;

public class EntityListeners implements Listener {

	@EventHandler
	public void onEntityShoot(EntityShootBowEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.entity.EntityShootBowEvent(SpigotEntityManager.getEntity(e.getEntity())));
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.entity.ProjectileHitEvent(SpigotEntityManager.getEntity(e.getEntity())));
	}

	@EventHandler
	public void onEntityDismount(EntityDismountEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.entity.EntityDismountEvent(SpigotEntityManager.getEntity(e.getEntity()), SpigotEntityManager.getEntity(e.getDismounted())));
	}
}
