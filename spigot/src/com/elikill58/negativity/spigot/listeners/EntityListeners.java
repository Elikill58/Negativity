package com.elikill58.negativity.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.impl.location.SpigotWorld;

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
	public void onSpawn(EntitySpawnEvent e) {
		Entity et = SpigotEntityManager.getEntity(e.getEntity());
		((SpigotWorld) et.getWorld()).add(et);
	}
	
	@EventHandler
	public void onRemove(EntityDeathEvent e) {
		Entity et = SpigotEntityManager.getEntity(e.getEntity());
		((SpigotWorld) et.getWorld()).remove(et);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Entity et = SpigotEntityManager.getEntity(e.getPlayer());
		((SpigotWorld) et.getWorld()).add(et);
	}
}
