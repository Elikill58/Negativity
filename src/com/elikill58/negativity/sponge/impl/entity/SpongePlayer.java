package com.elikill58.negativity.sponge.impl.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.SleepingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SprintData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.impl.SpongePotionEffectType;
import com.elikill58.negativity.sponge.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge.impl.inventory.SpongePlayerInventory;
import com.elikill58.negativity.sponge.impl.item.SpongeItemStack;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge.impl.location.SpongeWorld;
import com.elikill58.negativity.sponge.utils.LocationUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Version;
import com.flowpowered.math.vector.Vector3d;

public class SpongePlayer extends Player {

	private final org.spongepowered.api.entity.living.player.Player p;

	public SpongePlayer(org.spongepowered.api.entity.living.player.Player p) {
		this.p = p;
	}

	@Override
	public UUID getUniqueId() {
		return p.getUniqueId();
	}

	@Override
	public void sendMessage(String msg) {
		p.sendMessage(Text.of(msg));
	}

	@Override
	public boolean isOnGround() {
		return p.isOnGround();
	}

	@Override
	public boolean isOp() {
		return p.hasPermission("*");
	}

	@Override
	public boolean hasElytra() {
		return p.get(Keys.IS_ELYTRA_FLYING).orElse(false);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return LocationUtils.hasLineOfSight(p, (org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) entity.getLocation().getDefault());
	}

	@Override
	public float getWalkSpeed() {
		return (float) (double) p.get(Keys.WALKING_SPEED).get();
	}

	@Override
	public double getHealth() {
		return p.get(HealthData.class).get().health().get();
	}

	@Override
	public float getFallDistance() {
		return p.getOrCreate(FallDistanceData.class).get().fallDistance().get();
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(p.gameMode().get().getName());
	}

	@Override
	public void damage(double amount) {
		p.damage(amount, DamageSource.builder().type(DamageTypes.CUSTOM).build());
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(p.getLocation());
	}

	@Override
	public int getPing() {
		return p.getConnection().getLatency();
	}

	@Override
	public World getWorld() {
		return new SpongeWorld(p.getWorld());
	}

	@Override
	public String getName() {
		return p.getName();
	}

	@Override
	public boolean hasPermission(String perm) {
		return p.hasPermission(perm);
	}

	@Override
	public Version getPlayerVersion() {
		return Version.getVersion();
	}

	@Override
	public void kick(String reason) {
		p.kick(Text.of(reason));
	}

	@Override
	public int getLevel() {
		return p.get(Keys.EXPERIENCE_LEVEL).get();
	}

	@Override
	public boolean getAllowFlight() {
		return p.get(Keys.CAN_FLY).orElse(false);
	}

	@Override
	public Entity getVehicle() {
		return SpongeEntityManager.getEntity(p.getVehicle().orElse(null));
	}
	
	@Override
	public ItemStack getItemInHand() {
		Optional<org.spongepowered.api.item.inventory.ItemStack> opt = p.getItemInHand(HandTypes.MAIN_HAND);
		return opt.isPresent() ? new SpongeItemStack(opt.get()) : null;
	}

	@Override
	public boolean isFlying() {
		return p.getOrCreate(FlyingData.class).get().flying().get();
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		(channelId.equalsIgnoreCase("fml") ? SpongeNegativity.fmlChannel : SpongeNegativity.channel).sendTo(p, (chan) -> chan.writeByteArray(writeMessage));
	}

	@Override
	public boolean isSleeping() {
		return p.getOrCreate(SleepingData.class).get().sleeping().get();
	}

	@Override
	public boolean isSneaking() {
		return p.getOrCreate(SneakingData.class).get().sneaking().get();
	}

	@Override
	public double getEyeHeight() {
		return Utils.getPlayerHeadHeight(p);
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return p.getOrCreate(PotionEffectData.class).get().asList().stream().filter((pe) -> pe.getType().getName().equalsIgnoreCase(type.name()))
				.findAny().isPresent();
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<PotionEffect> list = new ArrayList<PotionEffect>();
		p.getOrCreate(PotionEffectData.class).get().asList().forEach((pe) -> list.add(new PotionEffect(PotionEffectType.fromName(pe.getType().getName()))));
		return list;
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		Utils.removePotionEffect(p.getOrCreate(PotionEffectData.class).get(), SpongePotionEffectType.getEffect(type));
	}

	@Override
	public String getIP() {
		return p.getConnection().getAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return p.isOnline();
	}

	@Override
	public void setSneaking(boolean b) {
		p.getOrCreate(SneakingData.class).get().sneaking().set(b);
	}

	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		PotionEffectData potionEffects = p.getOrCreate(PotionEffectData.class).orElse(null);
		potionEffects.addElement(org.spongepowered.api.effect.potion.PotionEffect.builder().potionType(SpongePotionEffectType.getEffect(type))
					.amplifier(amplifier).duration(duration).build());
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public boolean isSprinting() {
		return p.getOrCreate(SprintData.class).get().sprinting().get();
	}

	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void teleport(Location loc) {
		p.setLocation((org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) loc.getDefault());
	}

	@Override
	public boolean isInsideVehicle() {
		return p.getVehicle().isPresent();
	}

	@Override
	public float getFlySpeed() {
		return (float) (double) p.get(Keys.FLYING_SPEED).get();
	}

	@Override
	public void setSprinting(boolean b) {
		p.getOrCreate(SprintData.class).get().sprinting().set(b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		List<Entity> list = new ArrayList<>();
		p.getNearbyEntities(x).forEach((entity) -> list.add(SpongeEntityManager.getEntity(entity)));
		return list;
	}

	@Override
	public boolean isSwimming() {
		if (!isSprinting())
			return false;
		Location loc = getLocation().clone();
		if (loc.getBlock().getType().getId().contains("WATER"))
			return true;
		if (loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER"))
			return true;
		return false;
	}

	@Override
	public ItemStack getItemInOffHand() {
		Optional<org.spongepowered.api.item.inventory.ItemStack> opt = p.getItemInHand(HandTypes.OFF_HAND);
		return opt.isPresent() ? new SpongeItemStack(opt.get()) : null;
	}

	@Override
	public boolean isDead() {
		return getHealth() <= 0;
	}

	@Override
	public Vector getVelocity() {
		Vector3d vel = p.getVelocity();
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}

	@Override
	public PlayerInventory getInventory() {
		return new SpongePlayerInventory(p, p.getInventory());
	}
	
	@Override
	public boolean hasOpenInventory() {
		return p.getOpenInventory().isPresent() && p.getOpenInventory().get().getArchetype().equals(InventoryArchetypes.CHEST);
	}

	@Override
	public Inventory getOpenInventory() {
		return p.getOpenInventory().isPresent() ? null : new SpongeInventory(p.getOpenInventory().get());
	}

	@Override
	public void openInventory(Inventory inv) {
		p.openInventory((org.spongepowered.api.item.inventory.Inventory) inv.getDefault());
	}

	@Override
	public void closeInventory() {
		Task.builder().execute(() -> p.closeInventory()).submit(SpongeNegativity.getInstance());
	}

	@Override
	public void updateInventory() {
		
	}

	@Override
	public void setAllowFlight(boolean b) {
		p.offer(Keys.CAN_FLY, b);
	}

	@Override
	public void showPlayer(Player p) {
		// TODO implement showPlayer
	}
	
	@Override
	public void hidePlayer(Player p) {
		// TODO implement hidePlayer
	}

	@Override
	public void setVelocity(Vector vel) {
		p.setVelocity(new Vector3d(vel.getX(), vel.getY(), vel.getZ()));
	}

	@Override
	public Object getDefault() {
		return p;
	}
}
