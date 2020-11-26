package com.elikill58.negativity.sponge8.impl.entity;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import com.elikill58.negativity.api.entity.FakePlayer;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.sponge8.SpongeNegativity;
import com.elikill58.negativity.sponge8.impl.location.SpongeLocation;

import net.kyori.adventure.text.Component;

@SuppressWarnings("unchecked")
public class SpongeFakePlayer extends FakePlayer {

	private Entity fakePlayer;
	private Location loc;
	private String name;

	public SpongeFakePlayer(Location loc, String name) {
		this.loc = loc;
		this.name = name;
		ServerLocation spongeLoc = (ServerLocation) loc.getDefault();
		fakePlayer = spongeLoc.createEntity(EntityTypes.HUMAN.get());
		fakePlayer.offer(Keys.DISPLAY_NAME, Component.text(name));
		fakePlayer.offer(Keys.VANISH, true);
	}

	public void show(com.elikill58.negativity.api.entity.Player pl) {
		Player p = (Player) pl.getDefault();
		p.getWorld().spawnEntity(fakePlayer);
		Sponge.getServer().getScheduler().submit(
			Task.builder()
				.plugin(SpongeNegativity.container())
				.delay(Ticks.of(20))
				.execute(() -> hide(pl))
				.build()
		);
	}

	public Location getLocation() {
		return loc;
	}

	public String getName() {
		return name;
	}

	@Override
	public void hide(com.elikill58.negativity.api.entity.Player p) {
		if(!fakePlayer.isRemoved())
			fakePlayer.remove();
		//NegativityPlayer.getCached(p.getUniqueId()).removeFakePlayer(this, detected);
	}
	
	@Override
	public UUID getUUID() {
		return fakePlayer.getUniqueId();
	}

	@Override
	public boolean isOnGround() {
		return fakePlayer.require(Keys.ON_GROUND);
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	@Deprecated
	public int getEntityId() {
		return fakePlayer.getUniqueId().clockSequence();
	}
	
	@Override
	public double getEyeHeight() {
		return 0;
	}

	@Override
	public Location getEyeLocation() {
		Vector3d vec = fakePlayer.require(Keys.EYE_POSITION);
		return new SpongeLocation((ServerWorld) fakePlayer.getWorld(), vec);
	}

	@Override
	public Vector getRotation() {
		Vector3d vec = fakePlayer.getRotation();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}

	@Override
	public Object getDefault() {
		return fakePlayer;
	}
}
