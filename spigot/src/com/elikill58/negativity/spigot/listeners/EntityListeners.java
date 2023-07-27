package com.elikill58.negativity.spigot.listeners;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EntityListeners implements Listener {

    @EventHandler
    public void onEntityShoot(EntityShootBowEvent e) {
        EventManager.callEvent(new com.elikill58.negativity.api.events.entity.EntityShootBowEvent(SpigotEntityManager.getEntity(e.getEntity())));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        EventManager.callEvent(new com.elikill58.negativity.api.events.entity.ProjectileHitEvent(SpigotEntityManager.getEntity(e.getEntity())));
    }
}
