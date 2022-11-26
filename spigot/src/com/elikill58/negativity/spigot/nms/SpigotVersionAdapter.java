package com.elikill58.negativity.spigot.nms;

import static com.elikill58.negativity.spigot.utils.Utils.VERSION;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketContent;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyChannel;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public abstract class SpigotVersionAdapter extends VersionAdapter<Player> {

	protected Method getPlayerHandle;
	protected Field recentTpsField, pingField;
	public Object dedicatedServer;
	
	public SpigotVersionAdapter(int protocolVersion) {
		this.version = Version.getVersionByProtocolID(protocolVersion);
		try {
			dedicatedServer = PacketUtils.getDedicatedServer();
			
			Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer", "server.");
			recentTpsField = mcServer.getDeclaredField("recentTps");

			getPlayerHandle = PacketUtils.getObcClass("entity.CraftPlayer").getDeclaredMethod("getHandle");
			pingField = PacketUtils.getNmsClass("EntityPlayer", "server.level.").getDeclaredField(version.isNewerOrEquals(Version.V1_17) ? "e" : "ping");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract double getAverageTps();

	public List<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}

	public int getPlayerPing(Player player) {
		try {
			return pingField.getInt(getPlayerHandle.invoke(player));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double[] getTps() {
		try {
			return (double[]) recentTpsField.get(dedicatedServer);
		} catch (Exception e) {
			e.printStackTrace();
			return new double[] { 20, 20, 20 };
		}
	}

	public Object getPlayerConnection(Player p) {
		try {
			Object entityPlayer = PacketUtils.getEntityPlayer(p);
			return entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getNetworkManager(Player p) {
		try {
			Object playerConnection = getPlayerConnection(p);
			return new PacketContent(playerConnection).getSpecificModifier(PacketUtils.getNmsClass("NetworkManager", "network.")).readSafely(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Channel getChannel(Player p) {
		try {
			return new PacketContent(getNetworkManager(p)).getSpecificModifier(Channel.class).readSafely(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public AbstractChannel getPlayerChannel(Player p) {
		return new NettyChannel(getChannel(p));
	}

	public List<ChannelFuture> getFuturChannel() {
		try {
			Object mcServer = PacketUtils.getDedicatedServer();
			Object co = ReflectionUtils.getFirstWith(mcServer, PacketUtils.getNmsClass("MinecraftServer", "server."), PacketUtils.getNmsClass("ServerConnection", "server.network."));
			if(Version.getVersion().isNewerOrEquals(Version.V1_17)) {
				return (List<ChannelFuture>) ReflectionUtils.getPrivateField(co, "f");
			} else {
				try {
					return (List<ChannelFuture>) ReflectionUtils.getPrivateField(co, "g");
				} catch (NoSuchFieldException e) {
					return (List<ChannelFuture>) ReflectionUtils.getPrivateField(co, "listeningChannels");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public BoundingBox getBoundingBox(Entity et) {
		try {
			Class<?> craftEntityClass = PacketUtils.getObcClass("entity.CraftEntity");
			Object ep = craftEntityClass.getDeclaredMethod("getHandle").invoke(craftEntityClass.cast(et));
			Object bb = ReflectionUtils.getFirstWith(ep, PacketUtils.getNmsClass("Entity", "world.entity."), PacketUtils.getNmsClass("AxisAlignedBB", "world.phys."));
			Class<?> clss = bb.getClass();
			boolean hasMinField = false;
			for(Field f : clss.getFields())
				if(f.getName().equalsIgnoreCase("minX"))
					hasMinField = true;
			if(Version.getVersion().isNewerOrEquals(Version.V1_13) && hasMinField) {
				double minX = clss.getField("minX").getDouble(bb);
				double minY = clss.getField("minY").getDouble(bb);
				double minZ = clss.getField("minZ").getDouble(bb);
				
				double maxX = clss.getField("maxX").getDouble(bb);
				double maxY = clss.getField("maxY").getDouble(bb);
				double maxZ = clss.getField("maxZ").getDouble(bb);
				
				return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			} else {
				double minX = clss.getField("a").getDouble(bb);
				double minY = clss.getField("b").getDouble(bb);
				double minZ = clss.getField("c").getDouble(bb);
				
				double maxX = clss.getField("d").getDouble(bb);
				double maxY = clss.getField("e").getDouble(bb);
				double maxZ = clss.getField("f").getDouble(bb);
				
				return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public org.bukkit.inventory.ItemStack createSkull(OfflinePlayer owner) { // method used by old versions
		// should be "PLAYER_HEAD" and nothing else.
		// can't use direct material else we will have running issue on old versions
		org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack((org.bukkit.Material) Materials.PLAYER_HEAD.getDefault());
    	SkullMeta skullmeta = (SkullMeta) (itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
		skullmeta.setOwningPlayer(owner); // warn: this method seems to exist since 1.12.1
		return itemStack;
	}

	private static SpigotVersionAdapter instance;

	public static SpigotVersionAdapter getVersionAdapter() {
		if (instance == null) {
			switch (VERSION) {
			case "v1_8_R3":
				return instance = new Spigot_1_8_R3();
			case "v1_9_R1":
				return instance = new Spigot_1_9_R1();
			case "v1_9_R2":
				return instance = new Spigot_1_9_R2();
			case "v1_10_R1":
				return instance = new Spigot_1_10_R1();
			case "v1_11_R1":
				return instance = new Spigot_1_11_R1();
			case "v1_12_R1":
				return instance = new Spigot_1_12_R1();
			case "v1_13_R2":
				return instance = new Spigot_1_13_R2();
			case "v1_14_R1":
				return instance = new Spigot_1_14_R1();
			case "v1_15_R1":
				return instance = new Spigot_1_15_R1();
			case "v1_16_R1":
				return instance = new Spigot_1_16_R1();
			case "v1_16_R3":
				return instance = new Spigot_1_16_R3();
			case "v1_17_R1":
				try {
					return instance = (SpigotVersionAdapter) Class
							.forName("com.elikill58.negativity.spigot17.Spigot_1_17_R1").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			case "v1_18_R1":
				try {
					return instance = (SpigotVersionAdapter) Class
							.forName("com.elikill58.negativity.spigot18.Spigot_1_18_R1").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			case "v1_18_R2":
				try {
					return instance = (SpigotVersionAdapter) Class
							.forName("com.elikill58.negativity.spigot18.Spigot_1_18_R2").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			case "v1_19_R1":
				try {
					return instance = (SpigotVersionAdapter) Class
							.forName("com.elikill58.negativity.spigot19.Spigot_1_19_R1").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			default:
				return instance = new Spigot_UnknowVersion(VERSION);
			}
		}
		return instance;
	}
}
