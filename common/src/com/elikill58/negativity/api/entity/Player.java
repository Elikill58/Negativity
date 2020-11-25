package com.elikill58.negativity.api.entity;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

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

	/**
	 * Get the player IP
	 * 
	 * @return player IP
	 */
	@Nullable
	public abstract String getIP();
	
	/**
	 * Know if the player is dead
	 * 
	 * @return true if the player is dead
	 */
	public abstract boolean isDead();
	
	/**
	 * Know if the player is sleeping
	 * 
	 * @return true is the player is sleeping
	 */
	public abstract boolean isSleeping();
	/**
	 * Know if the player is swimming
	 * (compatible with 1.8 and lower)
	 * 
	 * @return true if it's swimming
	 */
	public abstract boolean isSwimming();
	/**
	 * Check if the player is using elytra (flying with it)
	 * (compatible with 1.8 and lower)
	 * 
	 * @return true if is elytra flying
	 */
	public abstract boolean hasElytra();
	/**
	 * Check if the player has the specified permission
	 * 
	 * @param perm the needed permission
	 * @return true if the player has permission
	 */
	public abstract boolean hasPermission(String perm);
	/**
	 * Check if player can see the specified entity
	 * 
	 * @param entity the entity to see
	 * @return true if the player can see it
	 */
	public abstract boolean hasLineOfSight(Entity entity);
	
	/**
	 * Check if the player is flying
	 * 
	 * @return true is the player fly
	 */
	public abstract boolean isFlying();
	/**
	 * Check if the player is authorized to fly
	 * 
	 * @return true if the player can fly
	 */
	public abstract boolean getAllowFlight();
	/**
	 * Edit the authorization to fly
	 * 
	 * @param b true if the player is allowed to fly
	 */
	public abstract void setAllowFlight(boolean b);

	/**
	 * Get current player latency
	 * 
	 * @return the player ping
	 */
	public abstract int getPing();
	/**
	 * Get player XP level
	 * 
	 * @return the player level
	 */
	public abstract int getLevel();
	
	/**
	 * Get player fly speed
	 * 
	 * @return the speed when player fly
	 */
	public abstract float getFlySpeed();
	/**
	 * Get player walk speed
	 * 
	 * @return the speed when player walk
	 */
	public abstract float getWalkSpeed();
	/**
	 * Get the player fall distance when player fall
	 * 
	 * @return the player fall distance
	 */
	public abstract float getFallDistance();
	
	/**
	 * Get the player health
	 * 
	 * @return the health
	 */
	public abstract double getHealth();
	
	/**
	 * Get the current player food level
	 * 
	 * @return the food level
	 */
	public abstract double getFoodLevel();
	
	/**
	 * Get player gamemode
	 * 
	 * @return the Gamemode
	 */
	public abstract GameMode getGameMode();
	
	/**
	 * Set the player gamemode
	 * Warn: support only default gamemode. Not modded server.
	 * 
	 * @param gameMode the new player gamemode
	 */
	public abstract void setGameMode(GameMode gameMode);

	/**
	 * Damage player according to damage amount
	 * 
	 * @param amount the quantity of damage
	 */
	public abstract void damage(double amount);
	/**
	 * Kick player with the specified reason
	 * 
	 * @param reason the reason of kick
	 */
	public abstract void kick(String reason);
	/**
	 * Teleport player to specified location
	 * 
	 * @param loc location destination
	 */
	public abstract void teleport(Location loc);
	/**
	 * Teleport player to specified entity
	 * 
	 * @param et entity destination
	 */
	public abstract void teleport(Entity et);

	public abstract boolean isSneaking();
	public abstract void setSneaking(boolean b);
	
	public abstract boolean isSprinting();
	public abstract void setSprinting(boolean b);

	/**
	 * Get player world
	 * 
	 * @return the world where the player is
	 */
	public abstract World getWorld();
	
	/**
	 * Get player version
	 * (Compatible with ViaVersion and ProtocolSupport for multiple client version)
	 * 
	 * @return the version which player use on server
	 */
	public abstract Version getPlayerVersion();

	/**
	 * Get the entity which is used as vehicle.
	 * It can be a wagon or a zombie.
	 * 
	 * @return the vehicle entity
	 */
	public abstract Entity getVehicle();
	
	/**
	 * Check if player is in a vehicle.
	 * 
	 * @return true if it is in vehicle
	 */
	public abstract boolean isInsideVehicle();

	/**
	 * Get the item in main hand
	 * Return null if there is not any item in hand
	 * 
	 * @return the item in hand
	 */
	public abstract ItemStack getItemInHand();
	
	/**
	 * Get the item in second hand
	 * Compatible with 1.8 and lower.
	 * Return null if there is any item in second hand or if the server is on 1.8 or lower
	 * 
	 * @return the item in off hand
	 */
	public abstract ItemStack getItemInOffHand();
	
	public abstract boolean hasPotionEffect(PotionEffectType type);
	public abstract List<PotionEffect> getActivePotionEffect();
	public abstract Optional<PotionEffect> getPotionEffect(PotionEffectType type);
	public void addPotionEffect(PotionEffect pe) {
		addPotionEffect(pe.getType(), pe.getDuration(), pe.getAmplifier());
	}
	public abstract void addPotionEffect(PotionEffectType type, int duration, int amplifier);
	public abstract void removePotionEffect(PotionEffectType type);
	
	/**
	 * Send plugin message :
	 * bungee > spigot
	 * OR
	 * spigot > bungee
	 * On the specified channel
	 * 
	 * @param channelId the channel ID
	 * @param writeMessage the message to sent
	 */
	public abstract void sendPluginMessage(String channelId, byte[] writeMessage);
	
	public abstract List<Entity> getNearbyEntities(double x, double y, double z);

	public abstract PlayerInventory getInventory();
	public abstract Inventory getOpenInventory();
	public abstract boolean hasOpenInventory();
	public abstract void openInventory(Inventory inv);
	public abstract void closeInventory();
	public abstract void updateInventory();

	/**
	 * Sets whether the play should be visible to other players.
	 *
	 * @param vanished true is the player should NOT be visible
	 */
	public abstract void setVanished(boolean vanished);
	
	/**
	 * Get current player velocity
	 * 
	 * @return the player velocity
	 */
	public abstract Vector getVelocity();
	/**
	 * Edit the player velocity
	 * 
	 * @param vel the new velocity
	 */
	public abstract void setVelocity(Vector vel);

	/**
	 * Get the player address
	 * 
	 * @return the player inet address
	 */
	public abstract InetSocketAddress getAddress();
	
	/**
	 * Check if it's a new player
	 * 
	 * @return true if the player has already played
	 */
	@Override
	public boolean hasPlayedBefore() {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return this.getUniqueId().equals(((Player) obj).getUniqueId());
	}

}
