package com.elikill58.negativity.spigot.nms;

import static com.elikill58.negativity.spigot.utils.Utils.VERSION;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.utils.Utils;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.Vec3D;
import net.minecraft.server.v1_7_R4.WorldServer;

public class Spigot_1_7_R4 extends SpigotVersionAdapter {
	
	public Spigot_1_7_R4() {
		super("v1_7_R4");
		packetsPlayIn.put("PacketPlayInBlockDig", (player, packet) -> {
			PacketPlayInBlockDig blockDig = (PacketPlayInBlockDig) packet;
			return new NPacketPlayInBlockDig(blockDig.c(), blockDig.d(), blockDig.e(), DigAction.getById(blockDig.g()), DigFace.getById(blockDig.f()));
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (p, packet) -> {
			@SuppressWarnings("deprecation")
			ItemStack handItem = new SpigotItemStack(p.getItemInHand());
			EntityPlayer player = ((CraftPlayer) p).getHandle();
			float f1 = player.pitch;
			float f2 = player.yaw;
			double d0 = player.locX;
			double d1 = player.locY + player.getHeadHeight();
			double d2 = player.locZ;
			Vec3D vec3d = Vec3D.a(d0, d1, d2);
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
			MovingObjectPosition vec = worldServer.rayTrace(vec3d, vec3d1, false);
			return vec == null ? null : new NPacketPlayInBlockPlace(vec.b, vec.c, vec.d, handItem,
				new Vector(loc.getX(), loc.getY() + p.getEyeHeight(), loc.getZ()));
		});
		packetsPlayOut.put("PacketPlayOutBlockBreakAnimation", (player, packet) -> {
			// in 1.7 -> no BlockPos, directly use x/y/z
			return new NPacketPlayOutBlockBreakAnimation(get(packet, "b"), get(packet, "c"), get(packet, "d"), get(packet, "a"), get(packet, "c"));
		});
		log();
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
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		try {
			Class<?> mcServer = Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			Object craftServer = server.getClass().getField("server").get(server);
			Object getted = craftServer.getClass().getMethod("getOnlinePlayers").invoke(craftServer);
			if (getted instanceof Player[])
				for (Player obj : (Player[]) getted)
					list.add(obj);
			else if (getted instanceof List)
				for (Object obj : (List<?>) getted)
					list.add((Player) obj);
			else
				System.out.println("Unknow getOnlinePlayers");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public float sin(float f) {
		return MathHelper.sin(f);
	}
	
	@Override
	public com.elikill58.negativity.api.location.BlockPosition getBlockPosition(Object obj) {
		return null; // no block pos object in 1.7
	}
	
	@Override
	public org.bukkit.inventory.ItemStack createSkull(OfflinePlayer owner) { // method used by old versions
		return Utils.createSkullOldVersion(owner);
	}
}
