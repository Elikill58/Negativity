package com.elikill58.negativity.spigot.nms;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;

public class Spigot_1_8_R3 extends SpigotVersionAdapter {

	public Spigot_1_8_R3() {
		super("v1_8_R3");
		packetsPlayIn.put("PacketPlayInBlockDig", (packet) -> {
			PacketPlayInBlockDig blockDig = (PacketPlayInBlockDig) packet;
			BlockPosition pos = blockDig.a();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(), DigAction.getById(blockDig.c().ordinal()), DigFace.getById(blockDig.b().a()));
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (packet) -> {
			PacketPlayInBlockPlace place = (PacketPlayInBlockPlace) packet;
			BlockPosition pos = place.a();
			ItemStack item = new SpigotItemStack(CraftItemStack.asBukkitCopy(place.getItemStack()));
			Vector vector = new Vector(place.d(), place.e(), place.f());
			return new NPacketPlayInBlockPlace(pos.getX(), pos.getY(), pos.getZ(), item, place.getFace(), vector);
		});
		
	}
	
	
}
