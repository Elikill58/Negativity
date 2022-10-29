package com.elikill58.negativity.spigot18;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

@SuppressWarnings("resource")
public class Spigot_1_18_R2 extends SpigotVersionAdapter {

	public Spigot_1_18_R2() {
		super("v1_18_R2");
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
	public int getPlayerPing(Player player) {
		return ((ServerPlayer) PacketUtils.getEntityPlayer(player)).latency;
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
		return ((ServerPlayer) PacketUtils.getEntityPlayer(p)).connection;
	}

	public Channel getChannel(Player p) {
		return getPlayerConnection(p).connection.channel;
	}

	@Override
	public List<Entity> getEntities(World w) {
		List<Entity> entities = new ArrayList<>();
		((CraftWorld) w).getHandle().entityManager.getEntityGetter().getAll().forEach((mcEnt) -> {
			if(mcEnt != null) {
				CraftEntity craftEntity = mcEnt.getBukkitEntity();
				if (craftEntity != null && craftEntity instanceof Entity && craftEntity.isValid())
					entities.add((Entity) craftEntity);
			}
		});
		return entities;
	}

	private DedicatedServer getServer() {
		return (DedicatedServer) ((CraftServer) Bukkit.getServer()).getServer();
	}

	@Override
	public List<ChannelFuture> getFuturChannel() {
		try {
			Object co = ReflectionUtils.getFirstWith(getServer(), MinecraftServer.class, ServerConnectionListener.class);
			return ((List<ChannelFuture>) ReflectionUtils.getField(co, "f"));
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public float cos(float f) {
		return Mth.cos(f);
	}

	@Override
	public float sin(float f) {
		return Mth.sin(f);
	}

	@Override
	public com.elikill58.negativity.api.block.BlockPosition getBlockPosition(Object obj) {
		BlockPos pos = (BlockPos) obj;
		return new com.elikill58.negativity.api.block.BlockPosition(pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public BoundingBox getBoundingBox(Entity et) {
		AABB bb = ((CraftEntity) et).getHandle().getBoundingBox();
		return new BoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
	}
}
