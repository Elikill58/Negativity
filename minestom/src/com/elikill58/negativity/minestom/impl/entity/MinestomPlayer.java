package com.elikill58.negativity.minestom.impl.entity;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.AbstractPlayer;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.minestom.impl.inventory.MinestomInventory;
import com.elikill58.negativity.minestom.impl.inventory.MinestomPlayerInventory;
import com.elikill58.negativity.minestom.impl.item.MinestomItemStack;
import com.elikill58.negativity.minestom.impl.location.MinestomLocation;

import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.TimedPotion;

public class MinestomPlayer extends AbstractPlayer implements Player {

	private net.minestom.server.entity.Player entity;
	
	public MinestomPlayer(net.minestom.server.entity.Player p) {
		this.entity = p;
		this.location = MinestomLocation.toCommon(p.getInstance(), p.getPosition());
		init();
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUuid();
	}

	@Override
	public void sendMessage(String msg) {
		entity.sendMessage(msg);
	}

	@Override
	public boolean isOp() {
		return entity.getPermissionLevel() >= 4;
	}

	@Override
	public boolean hasElytra() {
		return entity.isFlyingWithElytra();
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return LocationUtils.hasLineOfSight(this, entity.getLocation());
	}

	@Override
	public float getWalkSpeed() {
		return entity.getAttributeValue(Attribute.MOVEMENT_SPEED);
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
		entity.setHealth((float) health);
	}

	@Override
	public float getFallDistance() {
		return (float) entity.getAerodynamics().gravity();
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(entity.getGameMode().name());
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		entity.setGameMode(net.minestom.server.entity.GameMode.valueOf(gameMode.name()));
	}

	@Override
	public void damage(double amount) {
		entity.damage(DamageType.GENERIC, (float) amount);
	}

	@Override
	public int getPing() {
		return entity.getLatency();
	}

	@Override
	public String getName() {
		return entity.getUsername();
	}

	@Override
	public boolean hasPermission(String perm) {
		return entity.hasPermission(perm) || isOp();
	}

	@Override
	public void kick(String reason) {
		entity.kick(reason);
	}

	@Override
	public int getLevel() {
		return entity.getLevel();
	}
	
	@Override
	public int getFoodLevel() {
		return entity.getFood();
	}
	
	@Override
	public void setFoodLevel(int foodlevel) {
		entity.setFood(foodlevel);
	}

	@Override
	public boolean getAllowFlight() {
		return entity.isAllowFlying();
	}

	@Override
	public Entity getVehicle() {
		return MinestomEntityManager.getEntity(entity.getVehicle());
	}
	
	@Override
	public ItemStack getItemInHand() {
		return new MinestomItemStack(entity.getItemInMainHand());
	}

	@Override
	public boolean isFlying() {
		return entity.isFlying();
	}
	
	@Override
	public void setFlying(boolean b) {
		entity.setFlying(b);
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		entity.sendPluginMessage(channelId, writeMessage);
	}

	@Override
	public boolean isSleeping() {
		// TODO implement is sleeping for minestom
		return false;
	}

	@Override
	public boolean isSneaking() {
		return entity.isSneaking();
	}

	@Override
	public boolean isUsingRiptide() {
		// TODO implement riptide for sponge
		return false;
	}
	
	@Override
	public double getEyeHeight() {
		return entity.getEyeHeight();
	}
	
	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return entity.getActiveEffects().stream().filter(p -> p.potion().effect().key().asString().equalsIgnoreCase(type.getId())).count() > 0;
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		return entity.getActiveEffects().stream().map(effect -> new PotionEffect(PotionEffectType.forId(effect.potion().effect().key().asString()), effect.potion().duration(), effect.potion().amplifier())).collect(Collectors.toList());
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		for(TimedPotion effect : entity.getActiveEffects()) {
			if(effect.potion().effect().key().asString().equalsIgnoreCase(type.getId()))
				return Optional.of(new PotionEffect(PotionEffectType.forId(effect.potion().effect().key().asString()), effect.potion().duration(), effect.potion().amplifier()));
		}
		return Optional.empty();
	}
	
	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		entity.addEffect(new Potion(net.minestom.server.potion.PotionEffect.fromNamespaceId(type.getId()), (byte) amplifier, duration));
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {
		entity.removeEffect(net.minestom.server.potion.PotionEffect.fromNamespaceId(type.getId()));
	}

	@Override
	public String getIP() {
		return entity.getPlayerConnection().getRemoteAddress().toString();
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
		entity.teleport(new Pos(loc.getX(), loc.getY(), loc.getZ()));
	}

	@Override
	public boolean isInsideVehicle() {
		return entity.getVehicle() != null;
	}

	@Override
	public float getFlySpeed() {
		return entity.getFlyingSpeed();
	}

	@Override
	public void setSprinting(boolean b) {
		entity.setSprinting(b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		return entity.getInstance().getNearbyEntities(entity.getPosition(), z).stream().map(MinestomEntityManager::getEntity).collect(Collectors.toList());
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
		return new MinestomItemStack(entity.getItemInOffHand());
	}

	@Override
	public boolean isDead() {
		return entity.isDead();
	}

	@Override
	public PlayerInventory getInventory() {
		return new MinestomPlayerInventory(entity);
	}
	
	@Override
	public boolean hasOpenInventory() {
		return entity.getOpenInventory() != null;
	}

	@Override
	public Inventory getOpenInventory() {
		return new MinestomInventory(entity.getOpenInventory());
	}

	@Override
	public void openInventory(Inventory inv) {
		entity.openInventory((net.minestom.server.inventory.@NotNull Inventory) inv.getDefault());
	}

	@Override
	public void closeInventory() {
		entity.closeInventory();
	}

	@Override
	public void updateInventory() {
		
	}

	@Override
	public void setAllowFlight(boolean b) {
		entity.setAllowFlying(b);
	}

	@Override
	public void setVanished(boolean vanished) {
		/*entity.offer(Keys.VANISH, vanished);
		if (vanished) {
			entity.offer(Keys.VANISH_IGNORES_COLLISION, true);
			entity.offer(Keys.VANISH_PREVENTS_TARGETING, true);
		}*/
		// TODO add vanish feature
	}
	
	@Override
	public InetSocketAddress getAddress() {
		SocketAddress address = entity.getPlayerConnection().getRemoteAddress();
		if (address instanceof InetSocketAddress inetAddress) {
			return inetAddress;
		}
		return null;
	}
	
	@Override
	public void sendToServer(String serverName) {
		// TODO fix send to server
		//ServerPlayNetworking.send(entity, MinestomNegativity.bungeecordChannel, PacketByteBufs.create().writeString("String").writeString(serverName));
	}
	
	@Override
	public String getServerName() {
		return MinecraftServer.getBrandName();
	}

	@Override
	public Object getDefault() {
		return entity;
	}

	@Override
	public Vector getTheoricVelocity() {
		Vec vel = entity.getVelocity();
		return new Vector(vel.x(), vel.y(), vel.z());
	}

	@Override
	public void setVelocity(Vector vel) {
		entity.setVelocity(new Vec(vel.getX(), vel.getY(), vel.getZ()));
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		net.minestom.server.collision.BoundingBox box = entity.getBoundingBox();
		return new BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
	}
	
	@Override
	public int getEntityId() {
		return entity.getEntityId();
	}
}
