package com.elikill58.negativity.spigot.impl.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Damageable;
import org.bukkit.plugin.java.JavaPlugin;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.entity.Entity;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.item.ItemStack;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.location.World;
import com.elikill58.negativity.common.potion.PotionEffect;
import com.elikill58.negativity.common.potion.PotionEffectType;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.spigot.impl.location.SpigotWorld;
import com.elikill58.negativity.spigot.item.SpigotItemStack;
import com.elikill58.negativity.spigot.utils.Utils;
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
		for (org.bukkit.inventory.ItemStack item : p.getInventory().getArmorContents())
			if (item != null && item.getType().name().contains("ELYTRA"))
				return true;
		return false;
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
		return GameMode.valueOf(p.getGameMode().name());
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
		return Utils.getPing(p);
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
		return p.isInsideVehicle() ? new SpigotEntity(p.getVehicle()) : null;
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
	public void sendPluginMessage(JavaPlugin instance, String channelId, byte[] writeMessage) {
		p.sendPluginMessage(instance, channelId, writeMessage);
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
	public boolean hasPotionEffect(PotionEffectType type) {
		return p.getActivePotionEffects().stream().filter((pe) -> pe.getType().getName().equalsIgnoreCase(type.name())).findAny().isPresent();
	}
	
	@Override
	public double getEyeHeight() {
		return p.getEyeHeight();
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<PotionEffect> list = new ArrayList<PotionEffect>();
		p.getActivePotionEffects().forEach((pe) -> list.add(new PotionEffect(PotionEffectType.fromName(pe.getType().getName()))));
		return list;
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
		p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(type.name()), duration, amplifier));
	}
	
	
}
