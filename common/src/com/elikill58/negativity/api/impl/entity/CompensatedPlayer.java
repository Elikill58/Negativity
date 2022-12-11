package com.elikill58.negativity.api.impl.entity;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.AbstractPlayer;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.impl.CompensatedWorld;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Adapter;

public class CompensatedPlayer extends AbstractPlayer {

	private final int entityId;
	private final UUID uuid;
	private float eyeHeight = 1.8F * 0.85f;
	private CompensatedWorld world;
	private boolean sleeping = false, swimming = false, sneaking = false, flying = false, sprinting = false;

	public CompensatedPlayer(int entityId, UUID uuid, CompensatedWorld world) {
		this.entityId = entityId;
		this.uuid = uuid;
		this.world = world;
	}

	@Override
	public double getEyeHeight() {
		return eyeHeight;
	}

	@Override
	public CompensatedWorld getWorld() {
		return world;
	}
	
	public void setWorld(CompensatedWorld world) {
		this.world = world;
	}

	@Override
	public int getEntityId() {
		return entityId;
	}

	@Override
	public @Nullable String getIP() {
		return null;
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public boolean isSleeping() {
		return sleeping;
	}

	@Override
	public boolean isSwimming() {
		return swimming;
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
	public boolean hasPermission(String perm) {
		return false;
	}

	@Override
	public boolean hasLineOfSight(Entity entity) {
		return false;
	}

	@Override
	public boolean isFlying() {
		return flying;
	}

	@Override
	public void setFlying(boolean b) {
		this.flying = b;
	}

	@Override
	public boolean getAllowFlight() {
		return flying;
	}

	@Override
	public void setAllowFlight(boolean b) {

	}

	@Override
	public int getPing() {
		return 0;
	}

	@Override
	public int getLevel() {
		return 0;
	}

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
	public void setHealth(double health) {

	}

	@Override
	public int getFoodLevel() {
		return 0;
	}

	@Override
	public void setFoodLevel(int foodlevel) {

	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void setGameMode(GameMode gameMode) {

	}

	@Override
	public void damage(double amount) {

	}

	@Override
	public void kick(String reason) {

	}

	@Override
	public void teleport(Location loc) {

	}

	@Override
	public void teleport(Entity et) {

	}

	@Override
	public boolean isSneaking() {
		return sneaking;
	}

	@Override
	public void setSneaking(boolean b) {
		this.sneaking = b;
	}

	@Override
	public boolean isSprinting() {
		return sprinting;
	}

	@Override
	public void setSprinting(boolean b) {
		this.sprinting = b;
	}

	@Override
	public Vector getTheoricVelocity() {
		return velocity;
	}

	@Override
	public void setVelocity(Vector vel) {
		this.velocity = vel;
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
		return Collections.emptyList();
	}

	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		return Optional.empty();
	}

	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {

	}

	@Override
	public void removePotionEffect(PotionEffectType type) {

	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {

	}

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
	public void openInventory(Inventory inv) {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public void updateInventory() {

	}

	@Override
	public void setVanished(boolean vanished) {

	}

	@Override
	public InetSocketAddress getAddress() {
		return null;
	}

	@Override
	public void sendToServer(String serverName) {
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public boolean isOnline() {
		return true;
	}

	@Override
	public boolean isOp() {
		return false;
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		Player cible = Adapter.getAdapter().getPlayer(uuid);
		return cible == null ? new BoundingBox(location.getX() - 0.25, location.getY(), location.getZ() - 0.25, location.getX() + 0.25, location.getY() + 1, location.getZ() + 0.25) : cible.getBoundingBox();
	}

	@Override
	public String getName() {
		return Adapter.getAdapter().getOfflinePlayer(uuid).getName();
	}

	@Override
	public Object getDefault() {
		return this;
	}

	@Override
	public String toString() {
		return "CompensatedPlayer{id=" + getEntityId() + ",uuid=" + getUniqueId() + ",world=" + (world == null ? null : world.getName()) + "}";
	}
}
