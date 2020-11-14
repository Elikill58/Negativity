package com.elikill58.negativity.spigot.impl.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.event.inventory.InventoryType;

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
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.inventory.SpigotInventory;
import com.elikill58.negativity.spigot.impl.inventory.SpigotPlayerInventory;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.spigot.impl.location.SpigotWorld;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.support.ProtocolSupportSupport;
import com.elikill58.negativity.universal.support.ViaVersionSupport;

public class SpigotPlayer extends Player {

	private final org.bukkit.entity.Player p;
	private Version playerVersion;
	
	public SpigotPlayer(org.bukkit.entity.Player p) {
		this.p = p;
		this.playerVersion = loadVersion();
	}
	
	private Version loadVersion() {
		return (Negativity.viaVersionSupport ? ViaVersionSupport.getPlayerVersion(this) : (Negativity.protocolSupportSupport ? ProtocolSupportSupport.getPlayerVersion(this) : Version.getVersion()));
	}

	@Override
	public UUID getUniqueId() {
		return p.getUniqueId();
	}

	@Override
	public void sendMessage(String msg) {
		p.sendMessage(msg);
	}

	@Override
	public boolean isOnGround() {
		return ((org.bukkit.entity.Player) p).isOnGround();
	}

	@Override
	public boolean isOp() {
		return p.isOp();
	}

	@Override
	public boolean hasElytra() {
		return Version.getVersion().isNewerOrEquals(Version.V1_9) && p.isGliding();
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return SpigotNegativity.isCraftBukkit ? true : p.hasLineOfSight((org.bukkit.entity.Entity) entity.getDefault());
	}

	@Override
	public float getWalkSpeed() {
		return p.getWalkSpeed();
	}

	@Override
	public double getHealth() {
		return ((Damageable) p).getHealth();
	}

	@Override
	public float getFallDistance() {
		return p.getFallDistance();
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(p.getGameMode().name());
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		p.setGameMode(org.bukkit.GameMode.valueOf(gameMode.name()));
	}

	@Override
	public void damage(double amount) {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> p.damage(amount));
	}

	@Override
	public Location getLocation() {
		return new SpigotLocation(p.getLocation());
	}

	@Override
	public int getPing() {
		try {
			Object entityPlayer = PacketUtils.getEntityPlayer(p);
			return entityPlayer.getClass().getField("ping").getInt(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public World getWorld() {
		return new SpigotWorld(p.getWorld());
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
		return playerVersion.equals(Version.HIGHER) ? (playerVersion = loadVersion()) : playerVersion;
	}

	@Override
	public void kick(String reason) {
		p.kickPlayer(reason);
	}

	@Override
	public int getLevel() {
		return p.getLevel();
	}
	
	@Override
	public double getFoodLevel() {
		return p.getFoodLevel();
	}

	@Override
	public boolean getAllowFlight() {
		return p.getAllowFlight();
	}

	@Override
	public Entity getVehicle() {
		return p.isInsideVehicle() ? SpigotEntityManager.getEntity(p.getVehicle()) : null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItemInHand() {
		return new SpigotItemStack(p.getItemInHand());
	}

	@Override
	public boolean isFlying() {
		return p.isFlying();
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		p.sendPluginMessage(SpigotNegativity.getInstance(), channelId, writeMessage);
	}

	@Override
	public boolean isSleeping() {
		return p.isSleeping();
	}

	@Override
	public boolean isSneaking() {
		return p.isSneaking();
	}

	@Override
	public double getEyeHeight() {
		return p.getEyeHeight();
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return p.getActivePotionEffects().stream().filter((pe) -> pe.getType().getName().equalsIgnoreCase(type.name()))
				.findAny().isPresent();
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<PotionEffect> list = new ArrayList<>();
		p.getActivePotionEffects()
				.forEach((pe) -> list.add(new PotionEffect(PotionEffectType.fromName(pe.getType().getName()), pe.getDuration(), pe.getAmplifier())));
		return list;
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		for(PotionEffect pe : getActivePotionEffect())
			if(pe.getType().equals(type))
				return Optional.of(pe);
		return Optional.empty();
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		p.removePotionEffect(org.bukkit.potion.PotionEffectType.getByName(type.name()));
	}

	@Override
	public String getIP() {
		return p.getAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return p.isOnline();
	}

	@Override
	public void setSneaking(boolean b) {
		p.setSneaking(b);
	}

	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(type.name()),
				duration, amplifier));
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public boolean isSprinting() {
		return p.isSprinting();
	}

	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}

	@Override
	public void teleport(Location loc) {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> p.teleport((org.bukkit.Location) loc.getDefault()));
	}

	@Override
	public boolean isInsideVehicle() {
		return p.isInsideVehicle();
	}

	@Override
	public float getFlySpeed() {
		return p.getFlySpeed();
	}

	@Override
	public void setSprinting(boolean b) {
		p.setSprinting(b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		List<Entity> list = new ArrayList<>();
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> p.getNearbyEntities(x, y, z).forEach((entity) -> list.add(SpigotEntityManager.getEntity(entity))));
		return list;
	}

	@Override
	public boolean isSwimming() {
		if (Version.getVersion().isNewerOrEquals(Version.V1_13))
			return p.isSwimming() || p.hasPotionEffect(org.bukkit.potion.PotionEffectType.DOLPHINS_GRACE);
		else {
			if (!p.isSprinting())
				return false;
			Location loc = getLocation().clone();
			if (loc.getBlock().getType().getId().contains("WATER"))
				return true;
			if (loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER"))
				return true;
			return false;
		}
	}

	@Override
	public ItemStack getItemInOffHand() {
		return Version.getVersion().isNewerOrEquals(Version.V1_9) && p.getInventory().getItemInOffHand() != null ? new SpigotItemStack(p.getInventory().getItemInOffHand()) : null;
	}

	@Override
	public boolean isDead() {
		return p.getHealth() <= 0;
	}

	@Override
	public Vector getVelocity() {
		org.bukkit.util.Vector vel = p.getVelocity();
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}

	@Override
	public PlayerInventory getInventory() {
		return new SpigotPlayerInventory(p.getInventory());
	}
	
	@Override
	public boolean hasOpenInventory() {
		return p.getOpenInventory() != null && p.getOpenInventory().getTopInventory() != null && p.getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST);
	}

	@Override
	public Inventory getOpenInventory() {
		return p.getOpenInventory() == null || p.getOpenInventory().getTopInventory() == null ? null
				: new SpigotInventory(p.getOpenInventory().getTopInventory());
	}

	@Override
	public void openInventory(Inventory inv) {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> p.openInventory((org.bukkit.inventory.Inventory) inv.getDefault()));
	}

	@Override
	public void closeInventory() {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> p.closeInventory());
	}

	@Override
	public void updateInventory() {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> p.updateInventory());
	}

	@Override
	public void setAllowFlight(boolean b) {
		p.setAllowFlight(b);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void showPlayer(Player p) {
		this.p.showPlayer((org.bukkit.entity.Player) p.getDefault());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void hidePlayer(Player p) {
		this.p.hidePlayer((org.bukkit.entity.Player) p.getDefault());
	}

	@Override
	public void setVelocity(Vector vel) {
		p.setVelocity(new org.bukkit.util.Vector(vel.getX(), vel.getY(), vel.getZ()));
	}

	@Override
	public Object getDefault() {
		return p;
	}
	
	@Override
	public Location getEyeLocation() {
		org.bukkit.Location eye = p.getEyeLocation();
		return new SpigotLocation(new SpigotWorld(eye.getWorld()), eye.getX(), eye.getY(), eye.getZ());
	}
	
	@Override
	public Vector getRotation() {
		org.bukkit.util.Vector vec = p.getLocation().getDirection();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public int getEntityId() {
		return p.getEntityId();
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return p.getAddress();
	}
}
