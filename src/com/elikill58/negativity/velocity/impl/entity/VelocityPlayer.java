package com.elikill58.negativity.velocity.impl.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.velocity.VelocityNegativity;

import net.kyori.text.TextComponent;

public class VelocityPlayer extends Player {

	private final com.velocitypowered.api.proxy.Player pp;
	
	public VelocityPlayer(com.velocitypowered.api.proxy.Player pp) {
		this.pp = pp;
	}

	@Override
	public UUID getUniqueId() {
		return pp.getUniqueId();
	}
	
	@Override
	public String getIP() {
		return pp.getRemoteAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return pp.isActive();
	}

	@Override
	public Version getPlayerVersion() {
		return Version.getVersionByProtocolID(pp.getProtocolVersion().getProtocol());
	}

	@Override
	public boolean hasPlayedBefore() {
		return true;
	}

	@Override
	public boolean isOp() {
		return pp.hasPermission("*");
	}

	@Override
	public void sendMessage(String msg) {
		pp.sendMessage(TextComponent.of(msg));
	}

	@Override
	public String getName() {
		return pp.getUsername();
	}

	@Override
	public Object getDefault() {
		return pp;
	}

	@Override
	public boolean hasPermission(String perm) {
		return pp.hasPermission(perm);
	}
	
	@Override
	public void kick(String reason) {
		pp.disconnect(TextComponent.of(reason));
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		// TODO implement channelID for all channel and not only negativity's one
		pp.sendPluginMessage(VelocityNegativity.NEGATIVITY_CHANNEL_ID, writeMessage);
	}

	@Override
	public int getPing() {
		return (int) pp.getPing();
	}

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
	public GameMode getGameMode() {
		return null;
	}

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
		return null;
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
	public void showPlayer(Player p) {}

	@Override
	public void hidePlayer(Player p) {}

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
}
