package com.elikill58.negativity.spigot.nms;

import static com.elikill58.negativity.spigot.utils.Utils.VERSION;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketContent;
import com.elikill58.negativity.api.packets.PacketContent.ContentModifier;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyChannel;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SubPlatform;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public abstract class SpigotVersionAdapter extends VersionAdapter<Player> {

	protected Method getPlayerHandle, getEntityLookup, getBukkitEntity;
	protected Field recentTpsField, pingField, tpsField, playerConnectionField;
	protected Field minX, minY, minZ, maxX, maxY, maxZ, entityLookup;
	protected Object dedicatedServer;

	public SpigotVersionAdapter(int protocolVersion) {
		this.version = Version.getVersionByProtocolID(protocolVersion);
		try {
			dedicatedServer = PacketUtils.getDedicatedServer();

			Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer", "server.");
			recentTpsField = mcServer.getDeclaredField("recentTps");
			tpsField = mcServer.getDeclaredField(getTpsFieldName());
			tpsField.setAccessible(true);

			getPlayerHandle = PacketUtils.getObcClass("entity.CraftPlayer").getDeclaredMethod("getHandle");

			Class<?> entityPlayerClass = PacketUtils.getNmsClass("EntityPlayer", "server.level.");
			if (version.isNewerOrEquals(Version.V1_20_2)) {
				Class<?> ServerCommonPacketListenerImplClass = PacketUtils.getNmsClass("ServerCommonPacketListenerImpl", "server.network.");
				pingField = ServerCommonPacketListenerImplClass.getDeclaredField("i");
				pingField.setAccessible(true);
				playerConnectionField = entityPlayerClass.getDeclaredField("c");
			} else if (version.isNewerOrEquals(Version.V1_20)) {
				pingField = entityPlayerClass.getDeclaredField("f");
				playerConnectionField = entityPlayerClass.getDeclaredField("c");
			} else if (version.isNewerOrEquals(Version.V1_17)) {
				pingField = entityPlayerClass.getDeclaredField("e");
				playerConnectionField = entityPlayerClass.getDeclaredField("b");
			} else {
				pingField = entityPlayerClass.getDeclaredField("ping");
				playerConnectionField = entityPlayerClass.getDeclaredField("playerConnection");
			}
			Class<?> bbClass = PacketUtils.getNmsClass("AxisAlignedBB", "world.phys.");

			if (version.isNewerOrEquals(Version.V1_13) && hasMinField(bbClass)) {
				minX = bbClass.getDeclaredField("minX");
				minY = bbClass.getDeclaredField("minY");
				minZ = bbClass.getDeclaredField("minZ");

				maxX = bbClass.getDeclaredField("maxX");
				maxY = bbClass.getDeclaredField("maxY");
				maxZ = bbClass.getDeclaredField("maxZ");
			} else {
				minX = bbClass.getDeclaredField("a");
				minY = bbClass.getDeclaredField("b");
				minZ = bbClass.getDeclaredField("c");

				maxX = bbClass.getDeclaredField("d");
				maxY = bbClass.getDeclaredField("e");
				maxZ = bbClass.getDeclaredField("f");
			}
			this.getBukkitEntity = PacketUtils.getNmsClass("Entity", "world.entity.").getDeclaredMethod("getBukkitEntity");

			if (version.isNewerOrEquals(Version.V1_17)) {
				Class<?> worldServer = PacketUtils.getNmsClass("WorldServer", "server.level.");

				try {
					getEntityLookup = worldServer.getDeclaredMethod("getEntityLookup");
				} catch (NoSuchMethodException e) { // method not present
					Class<?> persistentEntitySectionClass = PacketUtils.getNmsClass("PersistentEntitySectionManager", "world.level.entity.");
					entityLookup = ReflectionUtils.getFirstFieldWith(worldServer, persistentEntitySectionClass);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean hasMinField(Class<?> bbClass) {
		for (Field f : bbClass.getDeclaredFields())
			if (f.getName().equalsIgnoreCase("minX"))
				return true;
		return false;
	}

	public double getAverageTps() {
		try {
			long[] array = (long[]) tpsField.get(dedicatedServer);
			long l = 0L;
			for (long m : array)
				l += m;
			return l / array.length;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public abstract String getTpsFieldName();

	public List<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}

	public int getPlayerPing(Player player) {
		try {
			if (version.isNewerOrEquals(Version.V1_20_2)) {
				return pingField.getInt(getPlayerConnection(player));
			} else {
				return pingField.getInt(getPlayerHandle.invoke(player));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double[] getTps() {
		if (SpigotNegativity.getSubPlatform().equals(SubPlatform.FOLIA)) {
			try {
				return (double[]) Bukkit.class.getDeclaredMethod("getTPS").invoke(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			return (double[]) recentTpsField.get(dedicatedServer);
		} catch (Exception e) {
			e.printStackTrace();
			return new double[] { 20, 20, 20 };
		}
	}

	public Object getPlayerConnection(Player p) {
		try {
			return playerConnectionField.get(PacketUtils.getEntityPlayer(p));
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
			PacketContent packet = new PacketContent(getNetworkManager(p));
			if (version.equals(Version.V1_17)) {
				ContentModifier<Object> all = packet.getAllObjects();
				if (all.has("k"))
					return (Channel) all.read("k");
			} else if (version.equals(Version.V1_16)) {
				ContentModifier<Object> all = packet.getAllObjects();
				if (all.has("channel"))
					return (Channel) all.read("channel");
			}
			return packet.getSpecificModifier(Channel.class).readSafely(0);
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
			Object co = ReflectionUtils.getFirstWith(dedicatedServer, PacketUtils.getNmsClass("MinecraftServer", "server."), PacketUtils.getNmsClass("ServerConnection", "server.network."));
			if (Version.getVersion().isNewerOrEquals(Version.V1_17)) {
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
			Object ep = PacketUtils.getNMSEntity(et);
			Object bb = ReflectionUtils.getFirstWith(ep, PacketUtils.getNmsClass("Entity", "world.entity."), PacketUtils.getNmsClass("AxisAlignedBB", "world.phys."));

			double minX = this.minX.getDouble(bb);
			double minY = this.minY.getDouble(bb);
			double minZ = this.minZ.getDouble(bb);

			double maxX = this.maxX.getDouble(bb);
			double maxY = this.maxY.getDouble(bb);
			double maxZ = this.maxZ.getDouble(bb);

			return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
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

	public List<Entity> getEntities(World w) {
		try {
			if (!version.isNewerOrEquals(Version.V1_17))
				return w.getEntities();
			List<Entity> entities = new ArrayList<>();
			Object worldServer = PacketUtils.getWorldServer(w);
			Object lookup;
			if (getEntityLookup != null)
				lookup = getEntityLookup.invoke(worldServer);
			else {
				Object persistentEntityManager = entityLookup.get(worldServer);
				lookup = persistentEntityManager.getClass().getDeclaredMethod("d").invoke(persistentEntityManager);
			}
			((Iterable<?>) lookup.getClass().getDeclaredMethod("a").invoke(lookup)).forEach(e -> {
				if (e != null) {
					try {
						Object craftEntity = getBukkitEntity.invoke(e);
						if (craftEntity != null && craftEntity instanceof Entity && ((Entity) craftEntity).isValid())
							entities.add((Entity) craftEntity);
					} catch (Exception exc) {
						exc.printStackTrace();
					}
				}
			});
			return entities;
		} catch (Exception e) { // shitty spigot -> entities not loaded yet
			return new ArrayList<>();
		}
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
				return instance = new Spigot_1_17_R1();
			case "v1_18_R1":
				return instance = new Spigot_1_18_R1();
			case "v1_18_R2":
				return instance = new Spigot_1_18_R2();
			case "v1_19_R1":
				return instance = new Spigot_1_19_R1();
			case "v1_19_R2":
				return instance = new Spigot_1_19_R2();
			case "v1_19_R3":
				return instance = new Spigot_1_19_R3();
			case "v1_20_R1":
				return instance = new Spigot_1_20_R1();
			case "v1_20_R2":
				return instance = new Spigot_1_20_R2();
			case "v1_20_R3":
				return instance = new Spigot_1_20_R3();
			default:
				return instance = new Spigot_UnknowVersion(VERSION);
			}
		}
		return instance;
	}
}
