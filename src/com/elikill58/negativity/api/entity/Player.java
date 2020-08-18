package com.elikill58.negativity.api.entity;

import java.util.List;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Version;

public abstract class Player extends OfflinePlayer {

	public abstract String getIP();
	
	public abstract boolean isDead();
	public abstract boolean isSleeping();
	public abstract boolean isSwimming();
	public abstract boolean hasElytra();
	public abstract boolean hasPermission(String perm);
	public abstract boolean getAllowFlight();
	public abstract boolean hasLineOfSight(Entity entity);
	
	public abstract boolean isFlying();
	public abstract void setAllowFlight(boolean b);

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
	public abstract void teleport(Entity et);

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
	public abstract void removePotionEffect(PotionEffectType type);
	
	public abstract void sendPluginMessage(String channelId, byte[] writeMessage);
	
	public abstract List<Entity> getNearbyEntities(double x, double y, double z);

	public abstract PlayerInventory getInventory();
	public abstract Inventory getOpenInventory();
	public abstract boolean hasOpenInventory();
	public abstract void openInventory(Inventory inv);
	public abstract void closeInventory();
	public abstract void updateInventory();

	public abstract void showPlayer(Player p);
	public abstract void hidePlayer(Player p);
	public abstract Vector getVelocity();
	public abstract void setVelocity(Vector vel);
	
	@Override
	public boolean hasPlayedBefore() {
		return true;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return this.getUniqueId().equals(((Player) obj).getUniqueId());
	}

}
