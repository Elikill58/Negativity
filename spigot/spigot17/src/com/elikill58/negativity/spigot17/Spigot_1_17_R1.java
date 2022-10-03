package com.elikill58.negativity.spigot17;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInSetProtocol;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInGround;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseItem;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
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
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

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
		packetsPlayIn.put(ServerboundMovePlayerPacket.StatusOnly.class.getSimpleName(), (player, raw) -> {
			if (raw instanceof ServerboundMovePlayerPacket.StatusOnly packet) {
				return new NPacketPlayInGround(packet.isOnGround());
			}
			return null;
		});

		packetsPlayIn.put("PacketPlayInBlockDig", (player, raw) -> {
			ServerboundPlayerActionPacket packet = (ServerboundPlayerActionPacket) raw;
			NPacketPlayInBlockDig.DigAction action = NPacketPlayInBlockDig.DigAction.values()[packet.getAction()
					.ordinal()];
			BlockFace face = BlockFace.getById(packet.getDirection().ordinal());
			BlockPos pos = packet.getPos();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(), action, face);
		});
		packetsPlayIn.put("PacketPlayInUseEntity", (player, f) -> {
			ServerboundInteractPacket packet = (ServerboundInteractPacket) f;
			return new NPacketPlayInUseEntity(get(packet, "a"), new Vector(0, 0, 0),
					EnumEntityUseAction.valueOf(((Object) getFromMethod(get(packet, "b"), "a")).toString()));
		});
		packetsPlayIn.put(ServerboundUseItemPacket.class.getSimpleName(), (p, packet) -> new NPacketPlayInUseItem(
				Hand.getHand(((ServerboundUseItemPacket) packet).getHand().name())));
		packetsPlayIn.put(ServerboundUseItemOnPacket.class.getSimpleName(), (p, packet) -> {
			ServerboundUseItemOnPacket pa = (ServerboundUseItemOnPacket) packet;
			BlockHitResult rs = pa.getHitResult();
			BlockPos pos = rs == null || rs.getBlockPos() != null ? new BlockPos(0, 0, 0) : rs.getBlockPos();
			return new NPacketPlayInBlockPlace(Hand.getHand(pa.getHand().name()), pos.getX(), pos.getY(), pos.getZ(),
					BlockFace.valueOf(rs.getDirection().name()));
		});

		packetsPlayIn.put("PacketPlayInKeepAlive",
				(player, f) -> new NPacketPlayInKeepAlive(((ServerboundKeepAlivePacket) f).getId()));
		packetsPlayIn.put("ServerboundPongPacket",
				(player, f) -> new NPacketPlayInPong(((ServerboundPongPacket) f).getId()));

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
		packetsPlayOut.put("PacketPlayOutEntityEffect", (player, packet) -> {
			return new NPacketPlayOutEntityEffect(get(packet, "d"), (byte) get(packet, "e"), get(packet, "f"),
					get(packet, "g"), get(packet, "h"));
		});
		packetsPlayOut.put("ClientboundPingPacket",
				(player, f) -> new NPacketPlayOutPing(((ClientboundPingPacket) f).getId()));

		packetsHandshake.put("PacketHandshakingInSetProtocol", (player, raw) -> {
			ClientIntentionPacket packet = (ClientIntentionPacket) raw;
			return new NPacketHandshakeInSetProtocol(packet.getProtocolVersion(), packet.hostName, packet.port);
		});

		negativityToPlatform.put(PacketType.Server.PING,
				(p, f) -> new ClientboundPingPacket((int) ((NPacketPlayOutPing) f).id));

		log();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void queuePacket(Player p, Object packet) {
		try {
			Queue queue = (Queue) get(getPlayerConnection(p).connection, "j");
			queue.add(callFirstConstructor(ReflectionUtils.getSubClassWithName(Connection.class, "QueuedPacket"),
					packet, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Channel getPlayerChannel(Player p) {
		return getPlayerConnection(p).connection.channel;
	}

	@Override
	public List<Entity> getEntities(World w) {
		List<Entity> entities = new ArrayList<>();
		((CraftWorld) w).getHandle().entityManager.getEntityGetter().getAll().forEach((mcEnt) -> {
			if (mcEnt != null) {
				CraftEntity craftEntity = mcEnt.getBukkitEntity();
				if (craftEntity != null && craftEntity instanceof Entity && craftEntity.isValid())
					entities.add((Entity) craftEntity);
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
	public com.elikill58.negativity.api.block.BlockPosition getBlockPosition(Object obj) {
		BlockPos pos = (BlockPos) obj;
		return new com.elikill58.negativity.api.block.BlockPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public BoundingBox getBoundingBox(Entity et) {
		AABB bb = ((CraftEntity) et).getHandle().getBoundingBox();
		return new BoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
	}
}
