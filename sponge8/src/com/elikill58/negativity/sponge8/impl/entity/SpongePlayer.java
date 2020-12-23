package com.elikill58.negativity.sponge8.impl.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.network.channel.Channel;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

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
import com.elikill58.negativity.sponge8.SpongeNegativity;
import com.elikill58.negativity.sponge8.impl.SpongePotionEffectType;
import com.elikill58.negativity.sponge8.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge8.impl.inventory.SpongePlayerInventory;
import com.elikill58.negativity.sponge8.impl.item.SpongeItemStack;
import com.elikill58.negativity.sponge8.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge8.impl.location.SpongeWorld;
import com.elikill58.negativity.sponge8.utils.LocationUtils;
import com.elikill58.negativity.sponge8.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;

import net.kyori.adventure.text.Component;

public class SpongePlayer extends SpongeEntity<ServerPlayer> implements Player {
	
	private Version playerVersion;
	
	public SpongePlayer(ServerPlayer p) {
		super(p);
		this.playerVersion = loadVersion();
	}
	
	private Version loadVersion() {
		return PlayerVersionManager.getPlayerVersion(this);
	}
	
	@Override
	public UUID getUniqueId() {
		return entity.getUniqueId();
	}
	
	@Override
	public void sendMessage(String msg) {
		entity.sendMessage(Component.text(msg));
	}
	
	@Override
	public boolean isOp() {
		return entity.hasPermission("*");
	}
	
	@Override
	public boolean hasElytra() {
		return entity.getOrElse(Keys.IS_ELYTRA_FLYING, false);
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return LocationUtils.hasLineOfSight(this.entity, (ServerLocation) entity.getLocation().getDefault());
	}
	
	@Override
	public float getWalkSpeed() {
		return entity.require(Keys.WALKING_SPEED).floatValue();
	}
	
	@Override
	public double getHealth() {
		return entity.require(Keys.HEALTH);
	}
	
	@Override
	public float getFallDistance() {
		return entity.require(Keys.FALL_DISTANCE).floatValue();
	}
	
	@Override
	public GameMode getGameMode() {
		ResourceKey key = Sponge.getGame().registries().registry(RegistryTypes.GAME_MODE).valueKey(entity.require(Keys.GAME_MODE));
		return GameMode.get(key.value().toUpperCase(Locale.ROOT));
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		switch (gameMode) {
		case ADVENTURE:
			entity.offer(Keys.GAME_MODE, GameModes.ADVENTURE.get());
			break;
		case CREATIVE:
			entity.offer(Keys.GAME_MODE, GameModes.CREATIVE.get());
			break;
		case CUSTOM:
			entity.offer(Keys.GAME_MODE, GameModes.NOT_SET.get());
			break;
		case SPECTATOR:
			entity.offer(Keys.GAME_MODE, GameModes.SPECTATOR.get());
			break;
		case SURVIVAL:
			entity.offer(Keys.GAME_MODE, GameModes.SURVIVAL.get());
			break;
		}
	}
	
	@Override
	public void damage(double amount) {
		entity.damage(amount, DamageSource.builder().type(DamageTypes.CUSTOM).build());
	}
	
	@Override
	public Location getLocation() {
		return new SpongeLocation(entity.getServerLocation());
	}
	
	@Override
	public int getPing() {
		return entity.getConnection().getLatency();
	}
	
	@Override
	public World getWorld() {
		return new SpongeWorld(entity.getWorld());
	}
	
	@Override
	public String getName() {
		return entity.getName();
	}
	
	@Override
	public boolean hasPermission(String perm) {
		return entity.hasPermission(perm);
	}
	
	@Override
	public Version getPlayerVersion() {
		return playerVersion == Version.HIGHER ? (playerVersion = loadVersion()) : playerVersion;
	}
	
	@Override
	public void kick(String reason) {
		entity.kick(Component.text(reason));
	}
	
	@Override
	public int getLevel() {
		return entity.require(Keys.EXPERIENCE_LEVEL);
	}
	
	@Override
	public double getFoodLevel() {
		return entity.require(Keys.FOOD_LEVEL);
	}
	
	@Override
	public boolean getAllowFlight() {
		return entity.getOrElse(Keys.CAN_FLY, false);
	}
	
	@Override
	public Entity getVehicle() {
		return SpongeEntityManager.getEntity(entity.getOrNull(Keys.VEHICLE));
	}
	
	@Override
	public ItemStack getItemInHand() {
		return new SpongeItemStack(entity.getItemInHand(HandTypes.MAIN_HAND));
	}
	
	@Override
	public boolean isFlying() {
		return entity.require(Keys.IS_FLYING);
	}
	
	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		Channel channel = Sponge.getChannelRegistry().get(ResourceKey.resolve(channelId)).orElse(null);
		if (channel == null) {
			Adapter.getAdapter().getLogger().warn("Channel " + channelId + " does not exist");
			Thread.dumpStack();
			return;
		}

		if (channel instanceof RawDataChannel) {
			((RawDataChannel) channel).play().sendTo(entity, buffer -> buffer.writeByteArray(writeMessage));
		} else {
			Adapter.getAdapter().getLogger().warn("Channel " + channelId + " is not a RawDataChannel");
		}
	}
	
	@Override
	public boolean isSleeping() {
		return entity.require(Keys.IS_SLEEPING);
	}
	
	@Override
	public boolean isSneaking() {
		return entity.require(Keys.IS_SNEAKING);
	}
	
	@Override
	public double getEyeHeight() {
		return Utils.getPlayerHeadHeight(entity);
	}
	
	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		List<org.spongepowered.api.effect.potion.PotionEffect> potionEffects = entity.getOrNull(Keys.POTION_EFFECTS);
		if (potionEffects == null) {
			return false;
		}
		for (org.spongepowered.api.effect.potion.PotionEffect effect : potionEffects) {
			if (Utils.getKey(effect.getType()).asString().equalsIgnoreCase(type.getId())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<org.spongepowered.api.effect.potion.PotionEffect> effects = entity.getOrNull(Keys.POTION_EFFECTS);
		if (effects == null) {
			return Collections.emptyList();
		}
		return effects.stream()
			.map(this::createPotionEffect)
			.collect(Collectors.toList());
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		return entity.get(Keys.POTION_EFFECTS).flatMap(effects -> {
			for (org.spongepowered.api.effect.potion.PotionEffect effect : effects) {
				if (Utils.getKey(effect.getType()).asString().equalsIgnoreCase(type.getId())) {
					return Optional.of(createPotionEffect(effect));
				}
			}
			return Optional.empty();
		});
	}
	
	private PotionEffect createPotionEffect(org.spongepowered.api.effect.potion.PotionEffect effect) {
		return new PotionEffect(PotionEffectType.forId(Utils.getKey(effect.getType()).asString()), effect.getDuration(), effect.getAmplifier());
	}
	
	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		entity.transform(Keys.POTION_EFFECTS, effects -> {
			org.spongepowered.api.effect.potion.PotionEffect effect =
				org.spongepowered.api.effect.potion.PotionEffect.of(SpongePotionEffectType.getEffect(type), amplifier, duration);
			if (effects == null) {
				return Collections.singletonList(effect);
			}
			effects.add(effect);
			return effects;
		});
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {
		entity.transform(Keys.POTION_EFFECTS, effects -> {
			if (effects != null) {
				effects.removeIf(effect -> Utils.getKey(effect.getType()).asString().equals(type.getId()));
				return effects;
			}
			return Collections.emptyList();
		});
	}
	
	@Override
	public String getIP() {
		return entity.getConnection().getAddress().getAddress().getHostAddress();
	}
	
	@Override
	public boolean isOnline() {
		return entity.isOnline();
	}
	
	@Override
	public void setSneaking(boolean b) {
		entity.offer(Keys.IS_SNEAKING, b);
	}
	
	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
	
	@Override
	public boolean isSprinting() {
		return entity.require(Keys.IS_SPRINTING);
	}
	
	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}
	
	@Override
	public void teleport(Location loc) {
		entity.setLocation((ServerLocation) loc.getDefault());
	}
	
	@Override
	public boolean isInsideVehicle() {
		return entity.get(Keys.VEHICLE).isPresent();
	}
	
	@Override
	public float getFlySpeed() {
		return entity.require(Keys.FLYING_SPEED).floatValue();
	}
	
	@Override
	public void setSprinting(boolean b) {
		entity.offer(Keys.IS_SPRINTING, b);
	}
	
	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		List<Entity> list = new ArrayList<>();
		entity.getNearbyEntities(x).forEach((entity) -> list.add(SpongeEntityManager.getEntity(entity)));
		return list;
	}
	
	@Override
	public boolean isSwimming() {
		if (!isSprinting())
			return false;
		Location loc = getLocation().clone();
		if (loc.getBlock().getType().getId().contains("WATER"))
			return true;
		return loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
	}
	
	@Override
	public ItemStack getItemInOffHand() {
		return new SpongeItemStack(entity.getItemInHand(HandTypes.OFF_HAND));
	}
	
	@Override
	public boolean isDead() {
		return getHealth() <= 0;
	}
	
	@Override
	public Vector getVelocity() {
		Vector3d vel = entity.require(Keys.VELOCITY);
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}
	
	@Override
	public PlayerInventory getInventory() {
		return new SpongePlayerInventory(entity);
	}
	
	@Override
	public boolean hasOpenInventory() {
		return entity.getOpenInventory().isPresent();
	}
	
	@Override
	public Inventory getOpenInventory() {
		return entity.getOpenInventory().map(SpongeInventory::new).orElse(null);
	}
	
	@Override
	public void openInventory(Inventory inv) {
		entity.openInventory((org.spongepowered.api.item.inventory.Inventory) inv.getDefault());
	}
	
	@Override
	public void closeInventory() {
		Sponge.getServer().getScheduler().submit(
			Task.builder()
				.plugin(SpongeNegativity.container())
				.execute(entity::closeInventory)
				.build()
		);
	}
	
	@Override
	public void updateInventory() {
		
	}
	
	@Override
	public void setAllowFlight(boolean b) {
		entity.offer(Keys.CAN_FLY, b);
	}
	
	@Override
	public void setVanished(boolean vanished) {
		entity.offer(Keys.VANISH, vanished);
		if (vanished) {
			entity.offer(Keys.VANISH_IGNORES_COLLISION, true);
			entity.offer(Keys.VANISH_PREVENTS_TARGETING, true);
		}
	}
	
	@Override
	public void setVelocity(Vector vel) {
		entity.offer(Keys.VELOCITY, new Vector3d(vel.getX(), vel.getY(), vel.getZ()));
	}
	
	@Override
	public Location getEyeLocation() {
		Vector3d pos = entity.require(Keys.EYE_POSITION);
		return new SpongeLocation(new SpongeWorld(entity.getWorld()), pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return entity.getConnection().getVirtualHost();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
