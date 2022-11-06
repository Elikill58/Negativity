package com.elikill58.negativity.minestom.listeners;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.entity.EntityShootBowEvent;
import com.elikill58.negativity.api.events.entity.ProjectileHitEvent;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithBlockEvent;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithEntityEvent;

public class EntityListeners {

	public EntityListeners(EventNode<Event> e) {
		e.addListener(EntityShootEvent.class, this::onEntityShoot);
		e.addListener(ProjectileCollideWithBlockEvent.class, this::onProjectileHit);
		e.addListener(ProjectileCollideWithEntityEvent.class, this::onProjectileHit);
	}
	
	public void onEntityShoot(EntityShootEvent e) {
		EventManager.callEvent(new EntityShootBowEvent(MinestomEntityManager.getEntity(e.getEntity())));
	}

	public void onProjectileHit(ProjectileCollideWithBlockEvent e) {
		EventManager.callEvent(new ProjectileHitEvent(MinestomEntityManager.getEntity(e.getEntity())));
	}

	public void onProjectileHit(ProjectileCollideWithEntityEvent e) {
		EventManager.callEvent(new ProjectileHitEvent(MinestomEntityManager.getEntity(e.getEntity())));
	}
}
