package com.elikill58.negativity.spigot17;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInSetProtocol;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("resource")
public class Spigot_1_17_R1 extends SpigotVersionAdapter {

	public Spigot_1_17_R1() {
		super("v1_17_R1");
		packetsPlayIn.put("PacketPlayInChat",
				(player, raw) -> new NPacketPlayInChat(((ServerboundChatPacket) raw).getMessage()));

		packetsPlayIn.put("PacketPlayInPositionLook", (player, raw) -> {
			ServerboundMovePlayerPacket.PosRot packet = (ServerboundMovePlayerPacket.PosRot) raw;
			return new NPacketPlayInPositionLook(packet.x, packet.y, packet.z, packet.xRot, packet.yRot,
					packet.isOnGround());
		});
		packetsPlayIn.put("PacketPlayInPosition", (player, raw) -> {
			ServerboundMovePlayerPacket.Pos packet = (ServerboundMovePlayerPacket.Pos) raw;
			return new NPacketPlayInPosition(packet.x, packet.y, packet.z, packet.xRot, packet.yRot,
					packet.isOnGround());
		});
		packetsPlayIn.put("PacketPlayInLook", (player, raw) -> {
			ServerboundMovePlayerPacket.Rot packet = (ServerboundMovePlayerPacket.Rot) raw;
			return new NPacketPlayInLook(packet.x, packet.y, packet.z, packet.xRot, packet.yRot, packet.isOnGround());
		});

		packetsPlayIn.put("PacketPlayInBlockDig", (player, raw) -> {
			ServerboundPlayerActionPacket packet = (ServerboundPlayerActionPacket) raw;
			NPacketPlayInBlockDig.DigAction action = NPacketPlayInBlockDig.DigAction.values()[packet.getAction()
					.ordinal()];
			NPacketPlayInBlockDig.DigFace face = NPacketPlayInBlockDig.DigFace.values()[packet.getDirection()
					.ordinal()];
			BlockPos pos = packet.getPos();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(), action, face);
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (p, raw) -> {
			ServerboundUseItemPacket packet = (ServerboundUseItemPacket) raw;
			PlayerInventory inventory = p.getInventory();
			ItemStack handItem;
			if (getStr(packet, "a").equalsIgnoreCase("MAIN_HAND")) {
				handItem = new SpigotItemStack(inventory.getItemInMainHand());
			} else {
				handItem = new SpigotItemStack(inventory.getItemInOffHand());
			}
			ServerPlayer ep = (ServerPlayer) PacketUtils.getEntityPlayer(p);
			float f1 = ep.getXRot();
			float f2 = ep.getYRot();
			double d0 = ep.getX();
			double d1 = ep.getY() + ep.getEyeHeight();
			double d2 = ep.getZ();
			Vec3 vec3d = new Vec3(d0, d1, d2);
			float f3 = cos(-f2 * 0.017453292F - 3.1415927F);
			float f4 = sin(-f2 * 0.017453292F - 3.1415927F);
			float f5 = -cos(-f1 * 0.017453292F);
			float f6 = sin(-f1 * 0.017453292F);
			float f7 = f4 * f5;
			float f8 = f3 * f5;
			double d3 = (p.getGameMode() == GameMode.CREATIVE) ? 5.0D : 4.5D;
			Vec3 vec3d1 = vec3d.add(f7 * d3, f6 * d3, f8 * d3);
			BlockHitResult hitResult = ep.level.rayTraceBlock(
					new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, ep),
					ep.blockPosition());
			if (hitResult == null) { // ignore because it should be only interact and not block pose
				return new NPacketPlayInUnset("PacketPlayInBlockPlace");
			}
			if (hitResult.isInside()) {
				BlockPos pos = hitResult.getBlockPos();
				Vec3 vec = hitResult.getLocation();
				return new NPacketPlayInBlockPlace(pos.getX(), pos.getY(), pos.getZ(), handItem,
						new Vector(vec.x, vec.y, vec.z));
			} else {
				p.sendMessage("Failed to find something " + ep.blockPosition());
				return new NPacketPlayInUnset("PacketPlayInBlockPlace");
			}
		});
		packetsPlayIn.put("PacketPlayInUseEntity", (player, f) -> {
			ServerboundInteractPacket packet = (ServerboundInteractPacket) f;
			return new NPacketPlayInUseEntity(get(packet, "a"), new Vector(0, 0, 0),
					EnumEntityUseAction.valueOf(((Object) getFromMethod(get(packet, "b"), "a")).toString()));
		});

		packetsPlayIn.put("PacketPlayInKeepAlive",
				(player, f) -> new NPacketPlayInKeepAlive(((ServerboundKeepAlivePacket) f).getId()));
		packetsPlayIn.put("PacketPlayInPong", (player, f) -> new NPacketPlayInPong(((ServerboundPongPacket) f).getId()));

		packetsPlayOut.put("PacketPlayOutBlockBreakAnimation", (player, raw) -> {
			ClientboundBlockDestructionPacket packet = (ClientboundBlockDestructionPacket) raw;
			BlockPos pos = packet.getPos();
			return new NPacketPlayOutBlockBreakAnimation(pos.getX(), pos.getY(), pos.getZ(), packet.getId(),
					packet.getProgress());
		});

		packetsPlayOut.put("PacketPlayOutKeepAlive",
				(player, raw) -> new NPacketPlayOutKeepAlive(((ClientboundKeepAlivePacket) raw).getId()));
		packetsPlayOut.put("PacketPlayOutEntityTeleport", (player, raw) -> {
			ClientboundTeleportEntityPacket packet = (ClientboundTeleportEntityPacket) raw;
			return new NPacketPlayOutEntityTeleport(packet.getId(), packet.getX(), packet.getY(), packet.getZ(),
					packet.getxRot(), packet.getyRot(), packet.isOnGround());
		});
		packetsPlayOut.put("PacketPlayOutEntityVelocity", (p, pa) -> {
			ClientboundSetEntityMotionPacket packet = (ClientboundSetEntityMotionPacket) pa;
			return new NPacketPlayOutEntityVelocity(packet.getId(), packet.getXa(), packet.getYa(), packet.getZa());
		});
		packetsPlayOut.put("PacketPlayOutPosition", (p, raw) -> {
			ClientboundPlayerPositionPacket packet = (ClientboundPlayerPositionPacket) raw;
			return new NPacketPlayOutPosition(packet.getX(), packet.getY(), packet.getZ(), packet.getXRot(),
					packet.getYRot());
		});
		packetsPlayOut.put("PacketPlayOutExplosion", (p, raw) -> {
			ClientboundExplodePacket packet = (ClientboundExplodePacket) raw;
			return new NPacketPlayOutExplosion(packet.getX(), packet.getY(), packet.getZ(), packet.getKnockbackX(),
					packet.getKnockbackY(), packet.getKnockbackZ());
		});
		packetsPlayOut.put("PacketPlayOutEntity", (player, packet) -> {
			return new NPacketPlayOutEntity(get(packet, "a"), Double.parseDouble(getStr(packet, "b")),
					Double.parseDouble(getStr(packet, "c")), Double.parseDouble(getStr(packet, "d")));
		});
		packetsPlayOut.put("PacketPlayOutEntityEffect", (player, packet) -> {
			return new NPacketPlayOutEntityEffect(get(packet, "d"), get(packet, "e"), get(packet, "f"), get(packet, "g"), get(packet, "h"));
		});
		packetsPlayOut.put("PacketPlayOutPong", (player, f) -> new NPacketPlayOutPing(((ClientboundPingPacket) f).getId()));

		packetsHandshake.put("PacketHandshakingInSetProtocol", (player, raw) -> {
			ClientIntentionPacket packet = (ClientIntentionPacket) raw;
			return new NPacketHandshakeInSetProtocol(packet.getProtocolVersion(), packet.hostName, packet.port);
		});
	}

	@Override
	protected String getOnGroundFieldName() {
		throw new UnsupportedOperationException("Should not be called");
	}

	@Override
	public List<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}

	@Override
	public double getAverageTps() {
		return Mth.average(getServer().tickTimes);
	}

	@Override
	public int getPlayerPing(Player player) {
		return ((ServerPlayer) PacketUtils.getEntityPlayer(player)).latency;
	}

	@Override
	public Class<?> getEnumPlayerInfoAction() {
		return ServerboundPlayerActionPacket.Action.class;
	}

	@Override
	public double[] getTps() {
		return getServer().recentTps;
	}

	@Override
	public ServerGamePacketListenerImpl getPlayerConnection(Player p) {
		return ((ServerPlayer) PacketUtils.getEntityPlayer(p)).connection;
	}

	@Override
	public void sendPacket(Player p, Object packet) {
		getPlayerConnection(p).send((Packet<?>) packet);
	}

	@Override
	public Channel getPlayerChannel(Player p) {
		return getPlayerConnection(p).connection.channel;
	}

	@Override
	public List<Entity> getEntities(World w) {
		List<Entity> entities = new ArrayList<>();
		((CraftWorld) w).getHandle().entityManager.getEntityGetter().getAll().forEach((mcEnt) -> {
			if(mcEnt != null) {
				CraftEntity craftEntity = mcEnt.getBukkitEntity();
				if (craftEntity != null && craftEntity instanceof LivingEntity && craftEntity.isValid())
					entities.add((LivingEntity) craftEntity);
			}
		});
		return entities;
	}

	private DedicatedServer getServer() {
		return (DedicatedServer) PacketUtils.getDedicatedServer();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChannelFuture> getFuturChannel() {
		try {
			DedicatedServer mcServer = (DedicatedServer) PacketUtils.getDedicatedServer();
			Object co = ReflectionUtils.getFirstWith(mcServer, MinecraftServer.class, ServerConnectionListener.class);
			return ((List<ChannelFuture>) ReflectionUtils.getField(co, "f"));
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public float cos(float f) {
		return Mth.cos(f);
	}

	@Override
	public float sin(float f) {
		return Mth.sin(f);
	}
	
	@Override
	public com.elikill58.negativity.api.location.BlockPosition getBlockPosition(Object obj) {
		BlockPos pos = (BlockPos) obj;
		return new com.elikill58.negativity.api.location.BlockPosition(pos.getX(), pos.getY(), pos.getZ());
	}
}
