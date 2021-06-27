package com.elikill58.negativity.spigot17;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;

import io.netty.channel.Channel;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;

public class Spigot_1_17_R1 extends SpigotVersionAdapter {
	
	@SuppressWarnings("unused") // Used via reflection in SpigotVersionAdapter
	public Spigot_1_17_R1() {
		super("v1_17_R1");
		packetsPlayIn.put("PacketPlayInChat", (player, raw) -> new NPacketPlayInChat(((ServerboundChatPacket) raw).getMessage()));
		
		packetsPlayIn.put("PacketPlayInPositionLook", (player, raw) -> {
			var packet = (ServerboundMovePlayerPacket.PosRot) raw;
			return new NPacketPlayInPositionLook(packet.x, packet.y, packet.z, packet.xRot, packet.yRot, packet.isOnGround());
		});
		packetsPlayIn.put("PacketPlayInPosition", (player, raw) -> {
			var packet = (ServerboundMovePlayerPacket.Pos) raw;
			return new NPacketPlayInPosition(packet.x, packet.y, packet.z, packet.xRot, packet.yRot, packet.isOnGround());
		});
		packetsPlayIn.put("PacketPlayInLook", (player, raw) -> {
			var packet = (ServerboundMovePlayerPacket.Rot) raw;
			return new NPacketPlayInLook(packet.x, packet.y, packet.z, packet.xRot, packet.yRot, packet.isOnGround());
		});
		
		packetsPlayIn.put("PacketPlayInBlockDig", (player, raw) -> {
			var packet = (ServerboundPlayerActionPacket) raw;
			var action = NPacketPlayInBlockDig.DigAction.values()[packet.getAction().ordinal()];
			var face = NPacketPlayInBlockDig.DigFace.values()[packet.getDirection().ordinal()];
			var pos = packet.getPos();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(), action, face);
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (player, raw) -> {
			if (raw instanceof ServerboundUseItemOnPacket packet) {
				var inventory = player.getInventory();
				ItemStack item;
				if (packet.getHand() == InteractionHand.MAIN_HAND) {
					item = new SpigotItemStack(inventory.getItemInMainHand());
				} else {
					item = new SpigotItemStack(inventory.getItemInOffHand());
				}
				var rawBlockPos = packet.getHitResult().getBlockPos();
				var blockPos = new BlockPosition(rawBlockPos.getX(), rawBlockPos.getY(), rawBlockPos.getZ());
				var face = packet.getHitResult().getDirection().ordinal();
				var rawPoint = packet.getHitResult().getLocation();
				var point = new Vector(rawPoint.x, rawPoint.y, rawPoint.z);
				return new NPacketPlayInBlockPlace(blockPos, item, face, point);
			}
			return new NPacketPlayInUnset();
		});
		
		packetsPlayIn.put("PacketPlayInKeepAlive", (player, raw) -> new NPacketPlayInKeepAlive(((ServerboundKeepAlivePacket) raw).getId()));
		
		packetsPlayOut.put("PacketPlayOutBlockBreakAnimation", (player, raw) -> {
			var packet = (ClientboundBlockDestructionPacket) raw;
			var pos = packet.getPos();
			return new NPacketPlayOutBlockBreakAnimation(pos.getX(), pos.getY(), pos.getZ(), packet.getId(), packet.getProgress());
		});
		
		packetsPlayOut.put("PacketPlayOutKeepAlive", (player, raw) -> new NPacketPlayOutKeepAlive(((ClientboundKeepAlivePacket) raw).getId()));
	}
	
	@Override
	protected String isOnGroundFieldName() {
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
