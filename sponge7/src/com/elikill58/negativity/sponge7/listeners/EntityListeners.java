package com.elikill58.negativity.sponge7.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.HealEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.PlayerDamagedByEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerRegainHealthEvent;
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntityManager;

public class EntityListeners {

	@Listener
	public void onDamageByEntity(DamageEntityEvent e,
			   @First DamageSource damageSource,
			   @Getter("getTargetEntity") Player p) {
		EventManager.callEvent(new PlayerDamagedByEntityEvent(SpongeEntityManager.getPlayer(p), SpongeEntityManager.getEntity(e.getTargetEntity())));
	}

	@Listener
	public void onRegainHealth(HealEntityEvent e, @First Player p) {
		PlayerRegainHealthEvent event = new PlayerRegainHealthEvent(SpongeEntityManager.getPlayer(p));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
}
