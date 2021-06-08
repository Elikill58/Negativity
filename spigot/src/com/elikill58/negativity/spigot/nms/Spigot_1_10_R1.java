package com.elikill58.negativity.spigot.nms;

import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.PacketPlayInBlockDig;

public class Spigot_1_10_R1 extends SpigotVersionAdapter {

	public Spigot_1_10_R1() {
		super("v1_10_R1");
		packetsPlayIn.put("PacketPlayInBlockDig", (packet) -> {
			PacketPlayInBlockDig blockDig = (PacketPlayInBlockDig) packet;
			BlockPosition pos = blockDig.a();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(), DigAction.getById(blockDig.c().ordinal()), DigFace.getById(blockDig.b().a()));
		});
		/*packetsPlayIn.put("PacketPlayInBlockPlace", (packet) -> {
			PacketPlayInBlockPlace place = (PacketPlayInBlockPlace) packet;
			BlockPosition pos = place.a();
			ItemStack item = new SpigotItemStack(CraftItemStack.asBukkitCopy(place.getItemStack()));
			Vector vector = new Vector(place.d(), place.e(), place.f());
			return new NPacketPlayInBlockPlace(pos.getX(), pos.getY(), pos.getZ(), item, place.getFace(), vector);
		});*/
	}
	
	@Override
	protected String isOnGroundFieldName() {
		return "f";
	}
}
