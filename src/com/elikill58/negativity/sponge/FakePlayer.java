package com.elikill58.negativity.sponge;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.universal.adapter.Adapter;

public class FakePlayer {

	private Entity fakePlayer;
	private Location<World> loc;
	private String name;

	public FakePlayer(Location<World> loc, String name) {
		this.loc = loc;
		this.name = name;
		fakePlayer = loc.getExtent().createEntity(EntityTypes.HUMAN, loc.getPosition());
		fakePlayer.offer(Keys.DISPLAY_NAME, Text.of(name));
		fakePlayer.offer(Keys.INVISIBLE, true);
		//Adapter.getAdapter().warn("Cannot spawn a fake player now. Sponge doesn't allow us to do it right now.");
	}

	public FakePlayer show(Player p) {
		p.getWorld().spawnEntity(fakePlayer);
		return this;
	}

	public void hide(Player p) {
		if(fakePlayer.isRemoved())
			fakePlayer.remove();
		SpongeNegativityPlayer.getNegativityPlayer(p).removeFakePlayer(this);
	}

	public Location<World> getLocation() {
		return loc;
	}

	public String getName() {
		return name;
	}

	public Entity getEntity() {
		return fakePlayer;
	}
}
