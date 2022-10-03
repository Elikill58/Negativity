package com.elikill58.negativity.spigot.nms;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseItem;
import com.elikill58.negativity.spigot.utils.Utils;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.MathHelper;
import net.minecraft.server.v1_11_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_11_R1.PacketPlayInBlockPlace;

public class Spigot_1_11_R1 extends SpigotVersionAdapter {
	
	public Spigot_1_11_R1() {
		super("v1_11_R1");
		packetsPlayIn.put("PacketPlayInBlockDig", (player, packet) -> {
			PacketPlayInBlockDig blockDig = (PacketPlayInBlockDig) packet;
			BlockPosition pos = blockDig.a();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(), DigAction.getById(blockDig.c().ordinal()), BlockFace.getById(blockDig.b().a()));
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (p, packet) -> new NPacketPlayInUseItem(Hand.getHand(((PacketPlayInBlockPlace) packet).a().name())));
		
		log();
	}
	
	@Override
	protected String getOnGroundFieldName() {
		return "f";
	}
	
	@Override
	public double getAverageTps() {
		return MathHelper.a(((CraftServer) Bukkit.getServer()).getServer().h);
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
	public com.elikill58.negativity.api.block.BlockPosition getBlockPosition(Object obj) {
		BlockPosition pos = (BlockPosition) obj;
		return new com.elikill58.negativity.api.block.BlockPosition(pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public org.bukkit.inventory.ItemStack createSkull(OfflinePlayer owner) { // method used by old versions
		return Utils.createSkullOldVersion(owner);
	}
}
