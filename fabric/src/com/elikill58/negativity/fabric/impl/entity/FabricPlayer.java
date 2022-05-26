package com.elikill58.negativity.fabric.impl.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.elikill58.negativity.fabric.FabricNegativity;
import com.elikill58.negativity.fabric.impl.FabricPotionEffectType;
import com.elikill58.negativity.fabric.impl.inventory.FabricInventory;
import com.elikill58.negativity.fabric.impl.inventory.FabricPlayerInventory;
import com.elikill58.negativity.fabric.impl.inventory.NegativityScreenHandler;
import com.elikill58.negativity.fabric.impl.item.FabricItemStack;
import com.elikill58.negativity.fabric.impl.location.FabricLocation;
import com.elikill58.negativity.fabric.impl.location.FabricWorld;
import com.elikill58.negativity.fabric.utils.LocationUtils;
import com.elikill58.negativity.fabric.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FabricPlayer extends FabricEntity<ServerPlayerEntity> implements Player {

	private int protocolVersion = 0;
	private Version playerVersion;

	public FabricPlayer(ServerPlayerEntity p) {
		super(p);
		this.protocolVersion = PlayerVersionManager.getPlayerProtocolVersion(this);
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
		return protocolVersion;
	}
	
	@Override
	public void setProtocolVersion(int protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUuid();
	}

	@Override
	public void sendMessage(String msg) {
		entity.sendMessage(Text.of(msg), false);
	}

	@Override
	public boolean isOp() {
		return entity.hasPermissionLevel(4);
	}

	@Override
	public boolean hasElytra() {
		return false;
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return LocationUtils.hasLineOfSight(this.entity, ((net.minecraft.entity.Entity) entity.getDefault()).getPos());
	}

	@Override
	public float getWalkSpeed() {
		return entity.getAbilities().getWalkSpeed();
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
		return entity.fallDistance;
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(entity.interactionManager.getGameMode().name());
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		entity.changeGameMode(net.minecraft.world.GameMode.byName(gameMode.name()));
	}

	@Override
	public void damage(double amount) {
		entity.damage(DamageSource.MAGIC, (float) amount);
	}

	@Override
	public Location getLocation() {
		return FabricLocation.toCommon(entity.getWorld(), entity.getPos());
	}

	@Override
	public int getPing() {
		return entity.pingMilliseconds / 1000;
	}

	@Override
	public World getWorld() {
		return new FabricWorld(entity.getWorld());
	}

	@Override
	public String getName() {
		return entity.getName().asString();
	}

	@Override
	public boolean hasPermission(String perm) {
		return isOp();
	}

	@Override
	public void kick(String reason) {
		entity.networkHandler.disconnect(Text.of(reason));
	}

	@Override
	public int getLevel() {
		return entity.experienceLevel;
	}
	
	@Override
	public int getFoodLevel() {
		return entity.getHungerManager().getFoodLevel();
	}
	
	@Override
	public void setFoodLevel(int foodlevel) {
		entity.getHungerManager().setFoodLevel(foodlevel);
	}

	@Override
	public boolean getAllowFlight() {
		return entity.getAbilities().allowFlying;
	}

	@Override
	public Entity getVehicle() {
		return FabricEntityManager.getEntity(entity.getVehicle());
	}
	
	@Override
	public ItemStack getItemInHand() {
		return new FabricItemStack(entity.getActiveItem());
	}

	@Override
	public boolean isFlying() {
		return entity.getAbilities().flying;
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		ServerPlayNetworking.send(entity, channelId.equalsIgnoreCase("fml") ? FabricNegativity.fmlChannel : FabricNegativity.negativityChannel, PacketByteBufs.create().writeByteArray(writeMessage));
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
		// TODO implement riptide for sponge
		return false;
	}
	
	@Override
	public double getEyeHeight() {
		return Utils.getPlayerHeadHeight(entity);
	}
	
	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return entity.hasStatusEffect(FabricPotionEffectType.getEffect(type));
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		return entity.getStatusEffects().stream().map(effect -> new PotionEffect(FabricPotionEffectType.getEffect(effect.getEffectType()), effect.getDuration(), effect.getAmplifier())).collect(Collectors.toList());
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		StatusEffectInstance effect = entity.getStatusEffect(FabricPotionEffectType.getEffect(type));
		return effect == null ? Optional.empty() : Optional.of(new PotionEffect(type, effect.getDuration(), effect.getAmplifier()));
	}
	
	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		entity.addStatusEffect(new StatusEffectInstance(FabricPotionEffectType.getEffect(type), duration, amplifier));
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {
		entity.removeStatusEffect(FabricPotionEffectType.getEffect(type));
	}

	@Override
	public String getIP() {
		return entity.getIp();
	}

	@Override
	public boolean isOnline() {
		return !entity.isDisconnected();
	}

	@Override
	public void setSneaking(boolean b) {
		entity.isSneaking();
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
		entity.setPos(loc.getX(), loc.getY(), loc.getZ());
	}

	@Override
	public boolean isInsideVehicle() {
		return entity.getVehicle() != null;
	}

	@Override
	public float getFlySpeed() {
		return entity.getAbilities().getFlySpeed();
	}

	@Override
	public void setSprinting(boolean b) {
		entity.setSprinting(b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		// TODO manage near entity
		return new ArrayList<>();
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
		return new FabricItemStack(entity.getOffHandStack());
	}

	@Override
	public boolean isDead() {
		return getHealth() <= 0;
	}

	@Override
	public PlayerInventory getInventory() {
		return new FabricPlayerInventory(entity);
	}
	
	@Override
	public boolean hasOpenInventory() {
		return entity.currentScreenHandler != null;
	}

	@Override
	public Inventory getOpenInventory() {
		return new FabricInventory(entity.currentScreenHandler);
	}

	@Override
	public void openInventory(Inventory inv) {
		Object o = inv.getDefault();
		if(o instanceof NegativityScreenHandler) {
			NegativityScreenHandler screen = (NegativityScreenHandler) o;
			//Adapter.getAdapter().getLogger().info("Slot items: " + screen.slots.stream().map(Slot::getStack).map(net.minecraft.item.ItemStack::getItem).map(Item::toString).collect(Collectors.toList()));
			entity.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> screen, Text.of(inv.getInventoryName())));
			//Adapter.getAdapter().getLogger().info("rev: " + entity.currentScreenHandler);
		} else if(o instanceof ScreenHandler) {
			entity.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> (ScreenHandler) o, Text.of(inv.getInventoryName())));
		} else {
			Adapter.getAdapter().getLogger().warn("Unsupported opening of inventory " + o.getClass().getName());
		}
	}

	@Override
	public void closeInventory() {
		entity.closeScreenHandler();
	}

	@Override
	public void updateInventory() {
		
	}

	@Override
	public void setAllowFlight(boolean b) {
		entity.getAbilities().allowFlying = b;
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
		return null;// entity.networkHandler.connection.getAddress().getVirtualHost();
	}
	
	@Override
	public void sendToServer(String serverName) {
		ServerPlayNetworking.send(entity, FabricNegativity.bungeecordChannel, PacketByteBufs.create().writeString("String").writeString(serverName));
	}
	
	@Override
	public String getServerName() {
		return "FabricServer"; // TODO check if fabric can have a server name
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
