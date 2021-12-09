package com.elikill58.negativity.spigot.nms;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;

import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;

public class Spigot_1_7_R4 extends SpigotVersionAdapter {
	
	public Spigot_1_7_R4() {
		super("v1_7_R4");
		packetsPlayIn.put("PacketPlayInBlockDig", (player, packet) -> {
			PacketPlayInBlockDig blockDig = (PacketPlayInBlockDig) packet;
			return new NPacketPlayInBlockDig(blockDig.c(), blockDig.d(), blockDig.e(), DigAction.getById(blockDig.g()), DigFace.getById(blockDig.f()));
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (player, packet) -> {
			PacketPlayInBlockPlace place = (PacketPlayInBlockPlace) packet;
			ItemStack item = new SpigotItemStack(CraftItemStack.asBukkitCopy(place.getItemStack()));
			Vector vector = new Vector(place.h(), place.i(), place.j());
			return new NPacketPlayInBlockPlace(place.c(), place.d(), place.e(), item, vector);
		});
		packetsPlayOut.put("PacketPlayOutBlockBreakAnimation", (player, packet) -> {
			// in 1.7 -> no BlockPos, directly use x/y/z
			return new NPacketPlayOutBlockBreakAnimation(get(packet, "b"), get(packet, "c"), get(packet, "d"), get(packet, "a"), get(packet, "c"));
		});
	}
	
	@Override
	protected String getOnGroundFieldName() {
		return "g";
	}
	
	@Override
	public double getAverageTps() {
		return MathHelper.a(MinecraftServer.getServer().g);
	}
	
	@Override
	public int getPlayerPing(Player player) {
		return ((CraftPlayer) player).getHandle().ping;
	}
	
	@Override
	public float cos(float f) {
		return MathHelper.cos(f);
	}
	
	@Override
	public float sin(float f) {
		return MathHelper.sin(f);
	}
	
	@Override
	public com.elikill58.negativity.api.location.BlockPosition getBlockPosition(Object obj) {
		return null; // no block pos object in 1.7
	}
}
