package com.elikill58.negativity.spigot17;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.BlockPosition;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;

import io.netty.channel.Channel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class Spigot_1_17_R1 extends SpigotVersionAdapter {

	public Spigot_1_17_R1() {
		super("v1_17_R1");
		packetsPlayIn.put("PacketPlayInChat", (player, raw) -> new NPacketPlayInChat(((ServerboundChatPacket) raw).getMessage()));
		
		packetsPlayIn.put("PacketPlayInPositionLook", (player, raw) -> {
			ServerboundMovePlayerPacket.PosRot packet = (ServerboundMovePlayerPacket.PosRot) raw;
			return new NPacketPlayInPositionLook(packet.x, packet.y, packet.z, packet.xRot, packet.yRot, packet.isOnGround());
		});
		packetsPlayIn.put("PacketPlayInPosition", (player, raw) -> {
			ServerboundMovePlayerPacket.Pos packet = (ServerboundMovePlayerPacket.Pos) raw;
			return new NPacketPlayInPosition(packet.x, packet.y, packet.z, packet.xRot, packet.yRot, packet.isOnGround());
		});
		packetsPlayIn.put("PacketPlayInLook", (player, raw) -> {
			ServerboundMovePlayerPacket.Rot packet = (ServerboundMovePlayerPacket.Rot) raw;
			return new NPacketPlayInLook(packet.x, packet.y, packet.z, packet.xRot, packet.yRot, packet.isOnGround());
		});
		
		packetsPlayIn.put("PacketPlayInBlockDig", (player, raw) -> {
			ServerboundPlayerActionPacket packet = (ServerboundPlayerActionPacket) raw;
			NPacketPlayInBlockDig.DigAction action = NPacketPlayInBlockDig.DigAction.values()[packet.getAction().ordinal()];
			NPacketPlayInBlockDig.DigFace face = NPacketPlayInBlockDig.DigFace.values()[packet.getDirection().ordinal()];
			BlockPos pos = packet.getPos();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(), action, face);
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (player, raw) -> {
			if (raw instanceof ServerboundUseItemOnPacket) {
				ServerboundUseItemOnPacket packet = (ServerboundUseItemOnPacket) raw;
				PlayerInventory inventory = player.getInventory();
				ItemStack item;
				if (packet.getHand() == InteractionHand.MAIN_HAND) {
					item = new SpigotItemStack(inventory.getItemInMainHand());
				} else {
					item = new SpigotItemStack(inventory.getItemInOffHand());
				}
				BlockPos rawBlockPos = packet.getHitResult().getBlockPos();
				BlockPosition blockPos = new BlockPosition(rawBlockPos.getX(), rawBlockPos.getY(), rawBlockPos.getZ());
				int face = packet.getHitResult().getDirection().ordinal();
				Vec3 rawPoint = packet.getHitResult().getLocation();
				Vector point = new Vector(rawPoint.x, rawPoint.y, rawPoint.z);
				return new NPacketPlayInBlockPlace(blockPos, item, face, point);
			}
			return new NPacketPlayInUnset();
		});
		packetsPlayIn.remove("PacketPlayInUseEntity"); // temporary remove unsupported packet
		// TODO add support of packet PacketPlayInUseEntity
		/*packetsPlayIn.put("PacketPlayInUseEntity", (player, f) -> {
			ServerboundEntityTagQuery packet = (ServerboundEntityTagQuery) f;
			return new NPacketPlayInUseEntity(packet.getEntityId(), new Vector(0, 0, 0), EnumEntityUseAction.INTERACT);
		});*/
		
		packetsPlayIn.put("PacketPlayInKeepAlive", (player, raw) -> new NPacketPlayInKeepAlive(((ServerboundKeepAlivePacket) raw).getId()));
		
		packetsPlayOut.put("PacketPlayOutBlockBreakAnimation", (player, raw) -> {
			ClientboundBlockDestructionPacket packet = (ClientboundBlockDestructionPacket) raw;
			BlockPos pos = packet.getPos();
			return new NPacketPlayOutBlockBreakAnimation(pos.getX(), pos.getY(), pos.getZ(), packet.getId(), packet.getProgress());
		});
		
		packetsPlayOut.put("PacketPlayOutKeepAlive", (player, raw) -> new NPacketPlayOutKeepAlive(((ClientboundKeepAlivePacket) raw).getId()));
		packetsPlayOut.put("PacketPlayOutEntityTeleport", (player, raw) -> {
			ClientboundTeleportEntityPacket packet = (ClientboundTeleportEntityPacket) raw;
			return new NPacketPlayOutEntityTeleport(packet.getId(), packet.getX(), packet.getY(), packet.getZ(), packet.getxRot(), packet.getyRot(), packet.isOnGround());
		});
		packetsPlayOut.put("PacketPlayOutEntityVelocity", (p, pa) -> {
			ClientboundSetEntityMotionPacket packet = (ClientboundSetEntityMotionPacket) pa;
			return new NPacketPlayOutEntityVelocity(packet.getId(), packet.getXa(), packet.getYa(), packet.getZa());
		});
		packetsPlayOut.put("PacketPlayOutPosition", (p, raw) -> {
			ClientboundPlayerPositionPacket packet = (ClientboundPlayerPositionPacket) raw;
			return new NPacketPlayOutPosition(packet.getX(), packet.getY(), packet.getZ(), packet.getXRot(), packet.getYRot());
		});
		packetsPlayOut.put("PacketPlayOutExplosion", (p, raw) -> {
			ClientboundExplodePacket packet = (ClientboundExplodePacket) raw;
			return new NPacketPlayOutExplosion(packet.getX(), packet.getY(), packet.getZ(), packet.getKnockbackX(), packet.getKnockbackY(), packet.getKnockbackZ());
		});
		packetsPlayOut.put("PacketPlayOutEntity", (player, packet) -> {
			return new NPacketPlayOutEntity(get(packet, "a"), Double.parseDouble(getStr(packet, "b")), Double.parseDouble(getStr(packet, "c")), Double.parseDouble(getStr(packet, "d")));
		});
	}
	
	@Override
	protected String getOnGroundFieldName() {
		throw new UnsupportedOperationException("Should not be called");
	}
	
	@Override
	public double getAverageTps() {
		return Mth.average(getServer().tickTimes);
	}
	
	@Override
	public List<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}
	
	@Override
	public int getPlayerPing(Player player) {
		return ((CraftPlayer) player).getHandle().latency;
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
		return ((CraftPlayer) p).getHandle().connection;
	}
	
	@Override
	public void sendPacket(Player p, Object packet) {
		getPlayerConnection(p).send((Packet<?>) packet);
	}
	
	@Override
	public Channel getPlayerChannel(Player p) {
		return getPlayerConnection(p).connection.channel;
	}
	
	private DedicatedServer getServer() {
		return ((CraftServer) Bukkit.getServer()).getServer();
	}
}
