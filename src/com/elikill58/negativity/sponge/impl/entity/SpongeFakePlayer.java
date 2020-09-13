package com.elikill58.negativity.sponge.impl.entity;

import java.util.UUID;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.api.entity.FakePlayer;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge.impl.location.SpongeWorld;
import com.flowpowered.math.vector.Vector3d;

@SuppressWarnings("unchecked")
public class SpongeFakePlayer extends FakePlayer {

	private Entity fakePlayer;
	private Location loc;
	private String name;

	public SpongeFakePlayer(Location loc, String name) {
		this.loc = loc;
		this.name = name;
		org.spongepowered.api.world.Location<World> spongeLoc = (org.spongepowered.api.world.Location<World>) loc.getDefault();
		fakePlayer = spongeLoc.getExtent().createEntity(EntityTypes.HUMAN, spongeLoc.getPosition());
		fakePlayer.offer(Keys.DISPLAY_NAME, Text.of(name));
		fakePlayer.getOrCreate(InvisibilityData.class).ifPresent((data) -> {
			Value<Boolean> vanish = data.vanish().set(true);
			data.set(vanish);
			fakePlayer.offer(vanish);
		});
	}

	public void show(com.elikill58.negativity.api.entity.Player pl) {
		Player p = (Player) pl.getDefault();
		p.getWorld().spawnEntity(fakePlayer);
		Task.builder().execute(() -> {
			hide(pl);
		}).delayTicks(20).submit(SpongeNegativity.getInstance());
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
		return fakePlayer.isOnGround();
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
		Vector3d vec = fakePlayer.getProperty(EyeLocationProperty.class).map(EyeLocationProperty::getValue).orElse(fakePlayer.getRotation());
		return new SpongeLocation(new SpongeWorld(fakePlayer.getWorld()), vec.getX(), vec.getY(), vec.getZ());
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
