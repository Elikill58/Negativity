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
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.support.ViaVersionSupport;

import net.kyori.adventure.text.Component;

public class SpongePlayer extends Player {
	
	private final ServerPlayer p;
	private Version playerVersion;
	
	public SpongePlayer(ServerPlayer p) {
		this.p = p;
		this.playerVersion = loadVersion();
	}
	
	private Version loadVersion() {
		return Negativity.viaVersionSupport ? ViaVersionSupport.getPlayerVersion(this) : Version.getVersion();
	}
	
	@Override
	public UUID getUniqueId() {
		return p.getUniqueId();
	}
	
	@Override
	public void sendMessage(String msg) {
		p.sendMessage(Component.text(msg));
	}
	
	@Override
	public boolean isOnGround() {
		return p.onGround().get();
	}
	
	@Override
	public boolean isOp() {
		return p.hasPermission("*");
	}
	
	@Override
	public boolean hasElytra() {
		return p.getOrElse(Keys.IS_ELYTRA_FLYING, false);
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return LocationUtils.hasLineOfSight(p, (ServerLocation) entity.getLocation().getDefault());
	}
	
	@Override
	public float getWalkSpeed() {
		return p.require(Keys.WALKING_SPEED).floatValue();
	}
	
	@Override
	public double getHealth() {
		return p.require(Keys.HEALTH);
	}
	
	@Override
	public float getFallDistance() {
		return p.require(Keys.FALL_DISTANCE).floatValue();
	}
	
	@Override
	public GameMode getGameMode() {
		return GameMode.get(p.require(Keys.GAME_MODE).getKey().value().toUpperCase(Locale.ROOT));
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		switch (gameMode) {
		case ADVENTURE:
			p.offer(Keys.GAME_MODE, GameModes.ADVENTURE.get());
			break;
		case CREATIVE:
			p.offer(Keys.GAME_MODE, GameModes.CREATIVE.get());
			break;
		case CUSTOM:
			p.offer(Keys.GAME_MODE, GameModes.NOT_SET.get());
			break;
		case SPECTATOR:
			p.offer(Keys.GAME_MODE, GameModes.SPECTATOR.get());
			break;
		case SURVIVAL:
			p.offer(Keys.GAME_MODE, GameModes.SURVIVAL.get());
			break;
		}
	}
	
	@Override
	public void damage(double amount) {
		p.damage(amount, DamageSource.builder().type(DamageTypes.CUSTOM).build());
	}
	
	@Override
	public Location getLocation() {
		return new SpongeLocation(p.getServerLocation());
	}
	
	@Override
	public int getPing() {
		return p.getConnection().getLatency();
	}
	
	@Override
	public World getWorld() {
		return new SpongeWorld(p.getWorld());
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
		return playerVersion == Version.HIGHER ? (playerVersion = loadVersion()) : playerVersion;
	}
	
	@Override
	public void kick(String reason) {
		p.kick(Component.text(reason));
	}
	
	@Override
	public int getLevel() {
		return p.require(Keys.EXPERIENCE_LEVEL);
	}
	
	@Override
	public double getFoodLevel() {
		return p.require(Keys.FOOD_LEVEL);
	}
	
	@Override
	public boolean getAllowFlight() {
		return p.getOrElse(Keys.CAN_FLY, false);
	}
	
	@Override
	public Entity getVehicle() {
		return SpongeEntityManager.getEntity(p.getOrNull(Keys.VEHICLE));
	}
	
	@Override
	public ItemStack getItemInHand() {
		org.spongepowered.api.item.inventory.ItemStack item = p.getItemInHand(HandTypes.MAIN_HAND);
		return item.isEmpty() ? null : new SpongeItemStack(item);
	}
	
	@Override
	public boolean isFlying() {
		return p.require(Keys.IS_FLYING);
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
			((RawDataChannel) channel).play().sendTo(p, buffer -> buffer.writeByteArray(writeMessage));
		} else {
			Adapter.getAdapter().getLogger().warn("Channel " + channelId + " is not a RawDataChannel");
		}
	}
	
	@Override
	public boolean isSleeping() {
		return p.require(Keys.IS_SLEEPING);
	}
	
	@Override
	public boolean isSneaking() {
		return p.require(Keys.IS_SNEAKING);
	}
	
	@Override
	public double getEyeHeight() {
		return Utils.getPlayerHeadHeight(p);
	}
	
	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		List<org.spongepowered.api.effect.potion.PotionEffect> potionEffects = p.getOrNull(Keys.POTION_EFFECTS);
		if (potionEffects == null) {
			return false;
		}
		for (org.spongepowered.api.effect.potion.PotionEffect effect : potionEffects) {
			if (effect.getType().key().asString().equalsIgnoreCase(type.getId())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<org.spongepowered.api.effect.potion.PotionEffect> effects = p.getOrNull(Keys.POTION_EFFECTS);
		if (effects == null) {
			return Collections.emptyList();
		}
		return effects.stream()
			.map(this::createPotionEffect)
			.collect(Collectors.toList());
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		return p.get(Keys.POTION_EFFECTS).flatMap(effects -> {
			for (org.spongepowered.api.effect.potion.PotionEffect effect : effects) {
				if (effect.getType().key().asString().equalsIgnoreCase(type.getId())) {
					return Optional.of(createPotionEffect(effect));
				}
			}
			return Optional.empty();
		});
	}
	
	private PotionEffect createPotionEffect(org.spongepowered.api.effect.potion.PotionEffect effect) {
		return new PotionEffect(PotionEffectType.forId(effect.getType().key().asString()), effect.getDuration(), effect.getAmplifier());
	}
	
	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		p.transform(Keys.POTION_EFFECTS, effects -> {
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
		p.transform(Keys.POTION_EFFECTS, effects -> {
			if (effects != null) {
				effects.removeIf(effect -> effect.getType().key().asString().equals(type.getId()));
				return effects;
			}
			return Collections.emptyList();
		});
	}
	
	@Override
	public String getIP() {
		return p.getConnection().getAddress().getAddress().getHostAddress();
	}
	
	@Override
	public boolean isOnline() {
		return p.isOnline();
	}
	
	@Override
	public void setSneaking(boolean b) {
		p.offer(Keys.IS_SNEAKING, b);
	}
	
	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
	
	@Override
	public boolean isSprinting() {
		return p.require(Keys.IS_SPRINTING);
	}
	
	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}
	
	@Override
	public void teleport(Location loc) {
		p.setLocation((ServerLocation) loc.getDefault());
	}
	
	@Override
	public boolean isInsideVehicle() {
		return p.get(Keys.VEHICLE).isPresent();
	}
	
	@Override
	public float getFlySpeed() {
		return p.require(Keys.FLYING_SPEED).floatValue();
	}
	
	@Override
	public void setSprinting(boolean b) {
		p.offer(Keys.IS_SPRINTING, b);
	}
	
	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		List<Entity> list = new ArrayList<>();
		p.getNearbyEntities(x).forEach((entity) -> list.add(SpongeEntityManager.getEntity(entity)));
		return list;
	}
	
	@Override
	public boolean isSwimming() {
		if (!isSprinting())
			return false;
		Location loc = getLocation().clone();
		if (loc.getBlock().getType().getId().contains("WATER"))
			return true;
		if (loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER"))
			return true;
		return false;
	}
	
	@Override
	public ItemStack getItemInOffHand() {
		org.spongepowered.api.item.inventory.ItemStack item = p.getItemInHand(HandTypes.OFF_HAND);
		return item.isEmpty() ? null : new SpongeItemStack(item);
	}
	
	@Override
	public boolean isDead() {
		return getHealth() <= 0;
	}
	
	@Override
	public Vector getVelocity() {
		Vector3d vel = p.require(Keys.VELOCITY);
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}
	
	@Override
	public PlayerInventory getInventory() {
		return new SpongePlayerInventory(p);
	}
	
	@Override
	public boolean hasOpenInventory() {
		return p.getOpenInventory().isPresent();
	}
	
	@Override
	public Inventory getOpenInventory() {
		return p.getOpenInventory().map(SpongeInventory::new).orElse(null);
	}
	
	@Override
	public void openInventory(Inventory inv) {
		p.openInventory((org.spongepowered.api.item.inventory.Inventory) inv.getDefault());
	}
	
	@Override
	public void closeInventory() {
		Sponge.getServer().getScheduler().submit(
			Task.builder()
				.plugin(SpongeNegativity.container())
				.execute(p::closeInventory)
				.build()
		);
	}
	
	@Override
	public void updateInventory() {
		
	}
	
	@Override
	public void setAllowFlight(boolean b) {
		p.offer(Keys.CAN_FLY, b);
	}
	
	@Override
	public void setVanished(boolean vanished) {
		p.offer(Keys.VANISH, vanished);
		if (vanished) {
			p.offer(Keys.VANISH_IGNORES_COLLISION, true);
			p.offer(Keys.VANISH_PREVENTS_TARGETING, true);
		}
	}
	
	@Override
	public void setVelocity(Vector vel) {
		p.offer(Keys.VELOCITY, new Vector3d(vel.getX(), vel.getY(), vel.getZ()));
	}
	
	@Override
	public Object getDefault() {
		return p;
	}
	
	@Override
	public Location getEyeLocation() {
		Vector3d pos = p.require(Keys.EYE_POSITION);
		return new SpongeLocation(new SpongeWorld(p.getWorld()), pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public Vector getRotation() {
		Vector3d vec = p.getRotation();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public int getEntityId() {
		return 0;
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return p.getConnection().getVirtualHost();
	}
}
