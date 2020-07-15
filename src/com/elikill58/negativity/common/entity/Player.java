package com.elikill58.negativity.common.entity;

import java.util.List;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.item.ItemStack;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.location.Vector;
import com.elikill58.negativity.common.location.World;
import com.elikill58.negativity.common.potion.PotionEffect;
import com.elikill58.negativity.common.potion.PotionEffectType;
import com.elikill58.negativity.universal.Version;

public abstract class Player extends Entity {

	public abstract UUID getUniqueId();
	
	public abstract String getName();
	public abstract String getIP();
	
	public abstract void sendMessage(String msg);

	public abstract boolean isOnline();
	public abstract boolean isDead();
	public abstract boolean isFlying();
	public abstract boolean isSleeping();
	public abstract boolean isSwimming();
	public abstract boolean hasElytra();
	public abstract boolean hasPermission(String perm);
	public abstract boolean getAllowFlight();

	public abstract int getPing();
	public abstract int getLevel();
	
	public abstract float getFlySpeed();
	public abstract float getWalkSpeed();
	public abstract float getFallDistance();
	
	public abstract double getHealth();
	
	public abstract GameMode getGameMode();

	public abstract void damage(double amount);
	public abstract void kick(String reason);
	public abstract void teleport(Location loc);

	public abstract boolean isSneaking();
	public abstract void setSneaking(boolean b);
	
	public abstract boolean isSprinting();
	public abstract void setSprinting(boolean b);

	public abstract World getWorld();
	
	public abstract Version getPlayerVersion();

	public abstract Entity getVehicle();
	public abstract boolean isInsideVehicle();

	public abstract ItemStack getItemInHand();
	public abstract ItemStack getItemInOffHand();
	
	public abstract boolean hasPotionEffect(PotionEffectType type);
	public abstract List<PotionEffect> getActivePotionEffect();
	public abstract void addPotionEffect(PotionEffectType type, int duration, int amplifier);
	
	public abstract Object getDefaultPlayer();

	public abstract void sendPluginMessage(JavaPlugin instance, String channelId, byte[] writeMessage);
	
	public abstract List<Entity> getNearbyEntities(double x, double y, double z);
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return this.getUniqueId().equals(((Player) obj).getUniqueId());
	}

	public abstract Vector getVelocity();
}
