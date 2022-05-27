package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerRegainHealthEvent;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;

public class EntityListeners {
	
	@Listener
	public void onDamageByEntity(DamageEntityEvent e, @First ServerPlayer attacker, @Getter("entity") ServerPlayer attacked) {
		EventManager.callEvent(new PlayerDamageEntityEvent(SpongeEntityManager.getPlayer(attacker), SpongeEntityManager.getEntity(attacked), true));
	}
	
	@Listener
	public void onRegainHealth(ChangeDataHolderEvent.ValueChange e, @First ServerPlayer p) {
		DataTransactionResult changes = e.endResult();
		for (Value.Immutable<?> replaced : changes.replacedData()) {
			if (replaced.key().equals(Keys.HEALTH)) {
				PlayerRegainHealthEvent event = new PlayerRegainHealthEvent(SpongeEntityManager.getPlayer(p));
				EventManager.callEvent(event);
				e.setCancelled(event.isCancelled()); // TODO do not cancel, set result instead
				return;
			}
		}
	}
}
