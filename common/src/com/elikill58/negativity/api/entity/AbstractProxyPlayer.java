package com.elikill58.negativity.api.entity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

public abstract class AbstractProxyPlayer extends AbstractEntity implements Player {
	
	@Override
	public boolean isDead() {
		return false;
	}
	
	@Override
	public boolean isSleeping() {
		return false;
	}
	
	@Override
	public boolean isSwimming() {
		return false;
	}
	
	@Override
	public boolean isUsingRiptide() {
		return false;
	}
	
	@Override
	public boolean hasElytra() {
		return false;
	}
	
	@Override
	public boolean getAllowFlight() {
		return false;
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return false;
	}
	
	@Override
	public boolean isFlying() {
		return false;
	}
	
	@Override
	public void setAllowFlight(boolean b) {}
	
	@Override
	public int getLevel() {
		return 0;
	}
	
	@Override
	public int getFoodLevel() {
		return 0;
	}
	
	@Override
	public void setFoodLevel(int foodlevel) {}
	
	@Override
	public float getFlySpeed() {
		return 0;
	}
	
	@Override
	public float getWalkSpeed() {
		return 0;
	}
	
	@Override
	public float getFallDistance() {
		return 0;
	}
	
	@Override
	public double getHealth() {
		return 0;
	}
	
	@Override
	public double getMaxHealth() {
		return 0;
	}
	
	@Override
	public void setHealth(double health) {}
	
	@Override
	public GameMode getGameMode() {
		return GameMode.CUSTOM;
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {}
	
	@Override
	public void damage(double amount) {}
	
	@Override
	public void teleport(Location loc) {}
	
	@Override
	public void teleport(Entity et) {}
	
	@Override
	public boolean isSneaking() {
		return false;
	}
	
	@Override
	public void setSneaking(boolean b) {}
	
	@Override
	public boolean isSprinting() {
		return false;
	}
	
	@Override
	public void setSprinting(boolean b) {}
	
	@Override
	public World getWorld() {
		return null;
	}
	
	@Override
	public Entity getVehicle() {
		return null;
	}
	
	@Override
	public boolean isInsideVehicle() {
		return false;
	}
	
	@Override
	public ItemStack getItemInHand() {
		return null;
	}
	
	@Override
	public ItemStack getItemInOffHand() {
		return null;
	}
	
	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return false;
	}
	
	@Override
	public List<PotionEffect> getActivePotionEffect() {
		return null;
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		return Optional.empty();
	}
	
	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {}
	
	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		return Collections.emptyList();
	}
	
	@Override
	public PlayerInventory getInventory() {
		return null;
	}
	
	@Override
	public Inventory getOpenInventory() {
		return null;
	}
	
	@Override
	public boolean hasOpenInventory() {
		return false;
	}
	
	@Override
	public void openInventory(Inventory inv) {}
	
	@Override
	public void closeInventory() {}
	
	@Override
	public void updateInventory() {}
	
	@Override
	public void setVanished(boolean vanished) {}
	
	@Override
	public Vector getVelocity() { return null; }
	
	@Override
	public void setVelocity(Vector vel) {}
	
	@Override
	public double getEyeHeight() {
		return 0;
	}
	
	@Override
	public Location getEyeLocation() {
		return null;
	}
	
	@Override
	public Vector getRotation() {
		return null;
	}

	@Override
	public String getEntityId() {
		return null;
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		return null;
	}
	
	@Override
	public Vector getTheoricVelocity() {
		return null;
	}
	
	@Override
	public void setPlayerVersion(Version version) {
		
	}
	
	@Override
	public void setProtocolVersion(int protocolVersion) {
		// don't need it on bungee
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
