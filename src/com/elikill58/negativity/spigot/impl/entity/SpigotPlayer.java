package com.elikill58.negativity.spigot.impl.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.elikill58.negativity.universal.Version;

public class SpigotPlayer extends Player {

	private final org.bukkit.entity.Player p;

	public SpigotPlayer(org.bukkit.entity.Player p) {
		this.p = p;
	}

	@Override
	public Object getDefaultPlayer() {
		return p;
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
		return p.isOnGround();
	}

	@Override
	public boolean isOp() {
		return p.isOp();
	}

	@Override
	public boolean hasElytra() {
		org.bukkit.inventory.ItemStack helmet = p.getInventory().getHelmet();
		return helmet != null && helmet.getType().name().contains("ELYTRA");
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return p.hasLineOfSight((org.bukkit.entity.Entity) entity.getDefaultEntity());
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
	public void damage(double amount) {
		p.damage(amount);
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
		return Version.getVersion();
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
		List<PotionEffect> list = new ArrayList<PotionEffect>();
		p.getActivePotionEffects()
				.forEach((pe) -> list.add(new PotionEffect(PotionEffectType.fromName(pe.getType().getName()))));
		return list;
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
		p.teleport((org.bukkit.Location) loc.getDefaultLocation());
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
		p.getNearbyEntities(x, y, z).forEach((entity) -> list.add(SpigotEntityManager.getEntity(entity)));
		return list;
	}

	@Override
	public boolean isSwimming() {
		if (Version.getVersion().isNewerOrEquals(Version.V1_13))
			return p.isSwimming();
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
		return null;
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public Vector getVelocity() {
		org.bukkit.util.Vector vel = p.getVelocity();
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}

	@Override
	public int getEntityId() {
		return p.getEntityId();
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
		p.openInventory((org.bukkit.inventory.Inventory) inv.getDefaultInventory());
	}

	@Override
	public void closeInventory() {
		p.closeInventory();
	}

	@Override
	public void updateInventory() {
		p.updateInventory();
	}

	@Override
	public void setAllowFlight(boolean b) {
		p.setAllowFlight(b);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void showPlayer(Player p) {
		this.p.showPlayer((org.bukkit.entity.Player) p.getDefaultPlayer());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void hidePlayer(Player p) {
		this.p.hidePlayer((org.bukkit.entity.Player) p.getDefaultPlayer());
	}

	@Override
	public void setVelocity(Vector vel) {
		p.setVelocity(new org.bukkit.util.Vector(vel.getX(), vel.getY(), vel.getZ()));
	}

	@Override
	public Object getDefaultEntity() {
		return p;
	}
}
