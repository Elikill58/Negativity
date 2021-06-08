package com.elikill58.negativity.spigot.nms;

import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;

import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;

public class Spigot_1_7_R4 extends SpigotVersionAdapter {
	
	
	public Spigot_1_7_R4() {
		super("v1_7_R4");
		packetsPlayIn.put("PacketPlayInBlockDig", (packet) -> {
			PacketPlayInBlockDig blockDig = (PacketPlayInBlockDig) packet;
			return new com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig(blockDig.c(), blockDig.d(), blockDig.e(), DigAction.getById(blockDig.g()), DigFace.getById(blockDig.f()));
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (packet) -> {
			PacketPlayInBlockPlace place = (PacketPlayInBlockPlace) packet;
			ItemStack item = new SpigotItemStack(CraftItemStack.asBukkitCopy(place.getItemStack()));
			Vector vector = new Vector(place.h(), place.i(), place.j());
			return new NPacketPlayInBlockPlace(place.c(), place.d(), place.e(), item, place.getFace(), vector);
		});
	}
	
	
	@Override
	protected String isOnGroundFieldName() {
		return "g";
	}
}
