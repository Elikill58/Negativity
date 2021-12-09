package com.elikill58.negativity.spigot.nms;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.Vec3D;
import net.minecraft.server.v1_11_R1.WorldServer;
import net.minecraft.server.v1_11_R1.MathHelper;

public class Spigot_1_11_R1 extends SpigotVersionAdapter {
	
	public Spigot_1_11_R1() {
		super("v1_11_R1");
		packetsPlayIn.put("PacketPlayInBlockDig", (player, packet) -> {
			PacketPlayInBlockDig blockDig = (PacketPlayInBlockDig) packet;
			BlockPosition pos = blockDig.a();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(), DigAction.getById(blockDig.c().ordinal()), DigFace.getById(blockDig.b().a()));
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (p, packet) -> {
			PlayerInventory inventory = p.getInventory();
			ItemStack handItem;
			if (getStr(packet, "a").equalsIgnoreCase("MAIN_HAND")) {
				handItem = new SpigotItemStack(inventory.getItemInMainHand());
			} else {
				handItem = new SpigotItemStack(inventory.getItemInOffHand());
			}
			EntityPlayer player = ((CraftPlayer) p).getHandle();
			float f1 = player.pitch;
			float f2 = player.yaw;
			double d0 = player.locX;
			double d1 = player.locY + player.getHeadHeight();
			double d2 = player.locZ;
			Vec3D vec3d = new Vec3D(d0, d1, d2);
			float f3 = cos(-f2 * 0.017453292F - 3.1415927F);
			float f4 = sin(-f2 * 0.017453292F - 3.1415927F);
			float f5 = -cos(-f1 * 0.017453292F);
			float f6 = sin(-f1 * 0.017453292F);
			float f7 = f4 * f5;
			float f8 = f3 * f5;
			double d3 = (p.getGameMode().equals(GameMode.CREATIVE)) ? 5.0D : 4.5D;
			Vec3D vec3d1 = vec3d.add(f7 * d3, f6 * d3, f8 * d3);
			Location loc = p.getLocation();
			WorldServer worldServer = ((CraftWorld) loc.getWorld()).getHandle();
			BlockPosition vec = worldServer.rayTrace(vec3d, vec3d1).a();
			return new NPacketPlayInBlockPlace(vec.getX(), vec.getY(), vec.getZ(), handItem,
				new Vector(loc.getX(), loc.getY() + p.getEyeHeight(), loc.getZ()));
		});
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
	public com.elikill58.negativity.api.location.BlockPosition getBlockPosition(Object obj) {
		BlockPosition pos = (BlockPosition) obj;
		return new com.elikill58.negativity.api.location.BlockPosition(pos.getX(), pos.getY(), pos.getZ());
	}
}
