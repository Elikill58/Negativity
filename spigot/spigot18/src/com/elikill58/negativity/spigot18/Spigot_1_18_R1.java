package com.elikill58.negativity.spigot18;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.utils.PacketUtils;

import io.netty.channel.Channel;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

@SuppressWarnings("resource")
public class Spigot_1_18_R1 extends SpigotVersionAdapter {
	
	public Spigot_1_18_R1() {
		super(PacketUtils.getProtocolVersion());
	}
	
	@Override
	public double getAverageTps() {
		return Mth.average(getServer().tickTimes);
	}

	@Override
	public ServerGamePacketListenerImpl getPlayerConnection(Player p) {
		return ((ServerPlayer) PacketUtils.getEntityPlayer(p)).connection;
	}

	@Override
	public Channel getChannel(Player p) {
		return getPlayerConnection(p).connection.channel;
	}

	private DedicatedServer getServer() {
		return (DedicatedServer) ((CraftServer) Bukkit.getServer()).getServer();
	}	
	
	@Override
	public BoundingBox getBoundingBox(Entity et) {
		AABB bb = ((CraftEntity) et).getHandle().getBoundingBox();
		return new BoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
	}
}
