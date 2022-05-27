package com.elikill58.negativity.sponge9.impl.entity;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.VanishState;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import com.elikill58.negativity.api.entity.FakePlayer;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge9.SpongeNegativity;
import com.elikill58.negativity.sponge9.utils.LocationUtils;

import net.kyori.adventure.text.Component;

public class SpongeFakePlayer extends SpongeEntity<Human> implements FakePlayer {

	public SpongeFakePlayer(Location loc, String name) {
		super(LocationUtils.createEntityAt(loc, EntityTypes.HUMAN.get()));
		entity.offer(Keys.DISPLAY_NAME, Component.text(name));
		entity.offer(Keys.VANISH_STATE, VanishState.vanished());
	}

	@Override
	public void show(com.elikill58.negativity.api.entity.Player pl) {
		Player p = (Player) pl.getDefault();
		p.world().spawnEntity(entity);
		Sponge.server().scheduler().submit(
			Task.builder()
				.plugin(SpongeNegativity.container())
				.delay(Ticks.of(20))
				.execute(() -> hide(pl))
				.build()
		);
	}

	@Override
	public void hide(com.elikill58.negativity.api.entity.Player p) {
		if(!entity.isRemoved())
			entity.remove();
		//NegativityPlayer.getCached(p.getUniqueId()).removeFakePlayer(this, detected);
	}
	
	@Override
	public UUID getUUID() {
		return entity.uniqueId();
	}
}
