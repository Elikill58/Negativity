package com.elikill58.negativity.spigot.impl.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.messaging.ChannelNotRegisteredException;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.inventory.SpigotInventory;
import com.elikill58.negativity.spigot.impl.inventory.SpigotPlayerInventory;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.spigot.impl.location.SpigotWorld;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;
import com.elikill58.negativity.universal.permissions.Perm;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

@SuppressWarnings("deprecation")
public class SpigotPlayer extends SpigotEntity<org.bukkit.entity.Player> implements Player {

	private int protocolVersion = 0;
	private Version playerVersion;

	public SpigotPlayer(org.bukkit.entity.Player p) {
		super(p);
		this.protocolVersion = PlayerVersionManager.getPlayerProtocolVersion(this);
		this.playerVersion = Version.getVersionByProtocolID(getProtocolVersion());
	}

	@Override
	public Version getPlayerVersion() {
		return isVersionSet() ? playerVersion : (playerVersion = Version.getVersionByProtocolID(getProtocolVersion()));
	}
	
	@Override
	public void setPlayerVersion(Version version) {
		playerVersion = version;
		protocolVersion = version.getFirstProtocolNumber();
	}

	private boolean isVersionSet() {
		return playerVersion != null && !playerVersion.equals(Version.HIGHER);
	}

	@Override
	public int getProtocolVersion() {
		if(protocolVersion <= 0)
			this.protocolVersion = PlayerVersionManager.getPlayerProtocolVersion(this);
		return protocolVersion;
	}

	@Override
	public void setProtocolVersion(int protocolVersion) {
		if (this.protocolVersion == 0
				|| Version.getVersion().getFirstProtocolNumber() == PlayerVersionManager.getPlayerProtocolVersion(this)
				|| !isVersionSet()) { // if his using default values
			this.playerVersion = Version.getVersionByProtocolID(protocolVersion);
			Adapter.getAdapter().debug("Setting ProtocolVersion: " + protocolVersion + ", founded: "
					+ playerVersion.name() + " (previous: " + this.protocolVersion + ")");
			if (protocolVersion == 0 && this.protocolVersion != 0)
				return;// prevent losing good value
			this.protocolVersion = protocolVersion;
		}
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUniqueId();
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
	}

	@Override
	public boolean isOp() {
		return entity.isOp();
	}

	@Override
	public boolean hasElytra() {
		return Version.getVersion().isNewerOrEquals(Version.V1_9) && entity.isGliding();
	}

	@Override
	public boolean hasLineOfSight(Entity entity) {
		return SpigotNegativity.isCraftBukkit || ((org.bukkit.entity.Entity) entity.getDefault()).hasMetadata("NPC")
				|| this.entity.hasLineOfSight((org.bukkit.entity.Entity) entity.getDefault());
	}

	@Override
	public float getWalkSpeed() {
		return entity.getWalkSpeed();
	}

	@Override
	public double getHealth() {
		return entity.getHealth();
	}

	@Override
	public double getMaxHealth() {
		return entity.getMaxHealth();
	}

	@Override
	public void setHealth(double health) {
		entity.setHealth(health);
	}

	@Override
	public float getFallDistance() {
		return entity.getFallDistance();
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(entity.getGameMode().name());
	}

	@Override
	public void setGameMode(GameMode gameMode) {
		entity.setGameMode(org.bukkit.GameMode.valueOf(gameMode.name()));
	}

	@Override
	public void damage(double amount) {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> entity.damage(amount));
	}

	@Override
	public Location getLocation() {
		return SpigotLocation.toCommon(entity.getLocation().clone());
	}

	@Override
	public int getPing() {
		return SpigotVersionAdapter.getVersionAdapter().getPlayerPing(entity);
	}

	@Override
	public World getWorld() {
		return new SpigotWorld(entity.getWorld());
	}

	@Override
	public boolean hasPermission(String perm) {
		return entity.hasPermission(perm);
	}

	@Override
	public void kick(String reason) {
		if(Bukkit.isPrimaryThread())
			entity.kickPlayer(reason);
		else {
			Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> entity.kickPlayer(reason));
		}
	}

	@Override
	public int getLevel() {
		return entity.getLevel();
	}

	@Override
	public int getFoodLevel() {
		return entity.getFoodLevel();
	}

	@Override
	public void setFoodLevel(int foodlevel) {
		entity.setFoodLevel(foodlevel);
	}

	@Override
	public boolean getAllowFlight() {
		return entity.getAllowFlight();
	}

	@Override
	public Entity getVehicle() {
		return entity.isInsideVehicle() ? SpigotEntityManager.getEntity(entity.getVehicle()) : null;
	}

	@Override
	public ItemStack getItemInHand() {
		return new SpigotItemStack(entity.getItemInHand());
	}

	@Override
	public boolean isFlying() {
		return entity.isFlying();
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		SpigotNegativity pl = SpigotNegativity.getInstance();
		try {
			entity.sendPluginMessage(pl, channelId, writeMessage);
		} catch (ChannelNotRegisteredException e) {
			Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(pl, channelId);
			entity.sendPluginMessage(pl, channelId, writeMessage);
		}
	}

	@Override
	public boolean isSleeping() {
		return entity.isSleeping();
	}

	@Override
	public boolean isSneaking() {
		return entity.isSneaking();
	}

	@Override
	public boolean isUsingRiptide() {
		return Version.getVersion().isNewerOrEquals(Version.V1_13) && entity.isRiptiding();
	}

	@Override
	public double getEyeHeight() {
		return entity.getEyeHeight();
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return entity.getActivePotionEffects().stream()
				.filter((pe) -> pe.getType().getName().equalsIgnoreCase(type.name())).findAny().isPresent();
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<PotionEffect> list = new ArrayList<>();
		entity.getActivePotionEffects()
				.forEach((pe) -> list.add(new PotionEffect(PotionEffectType.fromName(pe.getType().getName()),
						pe.getDuration(), pe.getAmplifier())));
		return list;
	}

	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		for (PotionEffect pe : getActivePotionEffect())
			if (pe.getType().equals(type))
				return Optional.of(pe);
		return Optional.empty();
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		entity.removePotionEffect(org.bukkit.potion.PotionEffectType.getByName(type.name()));
	}

	@Override
	public String getIP() {
		try {
			return entity.getAddress().getAddress().getHostAddress();
		} catch (NullPointerException e) {
			return "127.0.0.1";
		}
	}

	@Override
	public boolean isOnline() {
		return entity.isOnline();
	}

	@Override
	public void setSneaking(boolean b) {
		entity.setSneaking(b);
	}

	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		entity.addPotionEffect(new org.bukkit.potion.PotionEffect(
				org.bukkit.potion.PotionEffectType.getByName(type.name()), duration, amplifier));
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public boolean isSprinting() {
		return entity.isSprinting();
	}

	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}

	@Override
	public void teleport(Location loc) {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(),
				() -> entity.teleport(SpigotLocation.fromCommon(loc)));
	}

	@Override
	public boolean isInsideVehicle() {
		return entity.isInsideVehicle();
	}

	@Override
	public float getFlySpeed() {
		return entity.getFlySpeed();
	}

	@Override
	public void setSprinting(boolean b) {
		entity.setSprinting(b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		List<Entity> list = new ArrayList<>();
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> entity.getNearbyEntities(x, y, z)
				.forEach((entity) -> list.add(SpigotEntityManager.getEntity(entity))));
		return list;
	}

	@Override
	public boolean isSwimming() {
		if (Version.getVersion().isNewerOrEquals(Version.V1_13))
			return entity.isSwimming() || entity.hasPotionEffect(org.bukkit.potion.PotionEffectType.DOLPHINS_GRACE);
		else {
			if (!entity.isSprinting())
				return false;
			Location loc = getLocation().clone();
			if (loc.getBlock().getType().getId().contains("WATER"))
				return true;
			return loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
		}
	}

	@Override
	public ItemStack getItemInOffHand() {
		return Version.getVersion().isNewerOrEquals(Version.V1_9) && entity.getInventory().getItemInOffHand() != null
				? new SpigotItemStack(entity.getInventory().getItemInOffHand())
				: null;
	}

	@Override
	public boolean isDead() {
		return entity.getHealth() <= 0;
	}

	@Override
	public PlayerInventory getInventory() {
		return new SpigotPlayerInventory(entity.getInventory());
	}

	@Override
	public boolean hasOpenInventory() {
		return entity.getOpenInventory() != null && entity.getOpenInventory().getTopInventory() != null
				&& entity.getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST);
	}

	@Override
	public Inventory getOpenInventory() {
		return entity.getOpenInventory() == null || entity.getOpenInventory().getTopInventory() == null ? null
				: new SpigotInventory(entity.getOpenInventory().getTopInventory());
	}

	@Override
	public void openInventory(Inventory inv) {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(),
				() -> entity.openInventory((org.bukkit.inventory.Inventory) inv.getDefault()));
	}

	@Override
	public void closeInventory() {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), entity::closeInventory);
	}

	@Override
	public void updateInventory() {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), entity::updateInventory);
	}

	@Override
	public void setAllowFlight(boolean b) {
		entity.setAllowFlight(b);
	}

	@Override
	public void setVanished(boolean vanished) {
		if (vanished) {
			for (Player other : Adapter.getAdapter().getOnlinePlayers()) {
				if(Perm.hasPerm(other, Perm.ADMIN))
					continue;
				if(Version.getVersion().isNewerOrEquals(Version.V1_13))
					((org.bukkit.entity.Player) other.getDefault()).hidePlayer(SpigotNegativity.getInstance(), entity);
				else
					((org.bukkit.entity.Player) other.getDefault()).hidePlayer(entity);
			}
		} else {
			for (Player other : Adapter.getAdapter().getOnlinePlayers()) {
				if(Version.getVersion().isNewerOrEquals(Version.V1_13))
					((org.bukkit.entity.Player) other.getDefault()).showPlayer(SpigotNegativity.getInstance(), entity);
				else
					((org.bukkit.entity.Player) other.getDefault()).showPlayer(entity);
			}
		}
	}

	@Override
	public InetSocketAddress getAddress() {
		return entity.getAddress();
	}

	@Override
	public void sendToServer(String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		entity.sendPluginMessage(SpigotNegativity.getInstance(), "BungeeCord", out.toByteArray());
	}
	
	@Override
	public String getServerName() {
		return Utils.getBukkitServerName();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
