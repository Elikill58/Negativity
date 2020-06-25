package com.elikill58.negativity.sponge;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class FakePlayer {

	private Entity fakePlayer;
	private Location<World> loc;
	private String name;

	public FakePlayer(Location<World> loc, String name) {
		this.loc = loc;
		this.name = name;
		fakePlayer = loc.getExtent().createEntity(EntityTypes.HUMAN, loc.getPosition());
		fakePlayer.offer(Keys.DISPLAY_NAME, Text.of(name));
		//fakePlayer.offer(Keys.INVISIBLE, true);
		fakePlayer.getOrCreate(InvisibilityData.class).ifPresent((data) -> {
			Value<Boolean> vanish = data.vanish().set(true);
			data.set(vanish);
			fakePlayer.offer(vanish);
		});
	}

	public FakePlayer show(Player p) {
		p.getWorld().spawnEntity(fakePlayer);
		Task.builder().execute(() -> {
			hide(p, false);
		}).delayTicks(20).submit(SpongeNegativity.getInstance());
		return this;
	}

	public void hide(Player p, boolean detected) {
		if(!fakePlayer.isRemoved())
			fakePlayer.remove();
		SpongeNegativityPlayer.getNegativityPlayer(p).removeFakePlayer(this, detected);
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
