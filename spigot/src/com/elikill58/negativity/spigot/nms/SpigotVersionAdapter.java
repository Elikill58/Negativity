package com.elikill58.negativity.spigot.nms;

import static com.elikill58.negativity.spigot.utils.Utils.VERSION;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.BlockPosition;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketContent;
import com.elikill58.negativity.api.packets.PacketContent.ContentModifier;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInListener;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInSetProtocol;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInArmAnimation;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

@SuppressWarnings("unchecked")
public abstract class SpigotVersionAdapter extends VersionAdapter<Player> {

	public SpigotVersionAdapter(String version) {
		packetsPlayIn.addTo("PacketPlayInArmAnimation",
				(p, packet) -> new NPacketPlayInArmAnimation(System.currentTimeMillis()));
		packetsPlayIn.addTo("PacketPlayInChat", (p, packet) -> new NPacketPlayInChat(get(packet, "a")));

		packetsPlayIn.addTo("PacketPlayInPositionLook", (p, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPositionLook(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"),
						get(f, c, "pitch"), get(f, c, getOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.addTo("PacketPlayInPosition", (p, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPosition(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"),
						get(f, c, "pitch"), get(f, c, getOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.addTo("PacketPlayInLook", (p, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInLook(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"),
						get(f, c, "pitch"), get(f, c, getOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			// return new NPacketPlayInLook(get(f, "x"), get(f, "y"), get(f, "z"), get(f,
			// "yaw"), get(f, "pitch"));
		});
		packetsPlayIn.addTo("PacketPlayInFlying", (p, f) -> {
			return new NPacketPlayInFlying(get(f, "x"), get(f, "y"), get(f, "z"), get(f, "yaw"), get(f, "pitch"),
					get(f, getOnGroundFieldName()), get(f, "hasPos"), get(f, "hasLook"));
		});
		packetsPlayIn.addTo("PacketPlayInKeepAlive",
				(p, f) -> new NPacketPlayInKeepAlive(new Long(getSafe(f, "a").toString())));
		packetsPlayIn.addTo("PacketPlayInUseEntity", (p, f) -> {
			Object vec3D = get(f, "c");
			Vector vec = vec3D == null ? new Vector(0, 0, 0) : getVectorFromVec3D(vec3D);
			return new NPacketPlayInUseEntity(get(f, "a"), vec,
					EnumEntityUseAction.valueOf(((Enum<?>) get(f, "action")).name()));
		});
		packetsPlayIn.addTo("PacketPlayInBlockPlace", (p, packet) -> {
			try {
				PlayerInventory inventory = p.getInventory();
				ItemStack handItem;
				if (getStr(packet, "a").equalsIgnoreCase("MAIN_HAND")) {
					handItem = new SpigotItemStack(inventory.getItemInMainHand());
				} else {
					handItem = new SpigotItemStack(inventory.getItemInOffHand());
				}
				Object player = PacketUtils.getEntityPlayer(p);
				Class<?> entityClass = PacketUtils.getNmsClass("Entity");
				float f1 = get(entityClass, player, "pitch");
				float f2 = get(entityClass, player, "yaw");
				double d0 = get(entityClass, player, "locX");
				double d1 = ((double) get(entityClass, player, "locY")) + ((double) getFromMethod(entityClass, player, "getHeadHeight"));
				double d2 = get(entityClass, player, "locZ");
				Class<?> vec3DClass = PacketUtils.getNmsClass("Vec3D", "world.phys.");
				Object vec3d = vec3DClass.getConstructor(double.class, double.class, double.class).newInstance(d0, d1, d2);
				float f3 = cos(-f2 * 0.017453292F - 3.1415927F);
				float f4 = sin(-f2 * 0.017453292F - 3.1415927F);
				float f5 = -cos(-f1 * 0.017453292F);
				float f6 = sin(-f1 * 0.017453292F);
				float f7 = f4 * f5;
				float f8 = f3 * f5;
				double d3 = (p.getGameMode().equals(GameMode.CREATIVE)) ? 5.0D : 4.5D;
				Object vec3d1 = vec3DClass.getMethod("add", double.class, double.class, double.class).invoke(vec3d, f7 * d3, f6 * d3, f8 * d3);
				Location loc = p.getLocation();
				Object worldServer = PacketUtils.getWorldServer(loc);
				Object movingObj = PacketUtils.getNmsClass("World", "world.level.").getMethod("rayTrace", vec3DClass, vec3DClass).invoke(worldServer, vec3d, vec3d1);
				Object vec = getFromMethod(movingObj, "a");
				return new NPacketPlayInBlockPlace(getFromMethod(vec, "getX"), getFromMethod(vec, "getY"), getFromMethod(vec, "getZ"), handItem,
					new Vector(loc.getX(), loc.getY() + p.getEyeHeight(), loc.getZ()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.addTo("PacketPlayInEntityAction", (p, f) -> {
			EnumPlayerAction action = EnumPlayerAction.getAction(getStr(f, Version.getVersion().isNewerOrEquals(Version.V1_17) ? "b" : "animation"));
			return new NPacketPlayInEntityAction(get(f, "a"), action, get(f, "c"));
		});
		packetsPlayIn.addTo("PacketPlayInTransaction", (p, f) -> {
			return new NPacketPlayInPong((int) (short) get(f, "b"));
		});
		

		packetsPlayOut.addTo("PacketPlayOutBlockBreakAnimation", (p, packet) -> {
			Object pos = get(packet, "b");
			return pos == null ? null : new NPacketPlayOutBlockBreakAnimation(getBlockPosition(pos), get(packet, "a"),
					get(packet, "c"));
		});
		packetsPlayOut.addTo("PacketPlayOutKeepAlive",
				(p, f) -> new NPacketPlayOutKeepAlive(new Long(getSafe(f, "a").toString())));
		packetsPlayOut.addTo("PacketPlayOutEntityTeleport", (p, packet) -> {
			return new NPacketPlayOutEntityTeleport(get(packet, "a"), Double.parseDouble(getStr(packet, "b")),
					Double.parseDouble(getStr(packet, "c")), Double.parseDouble(getStr(packet, "d")),
					Float.parseFloat(getStr(packet, "e")), Float.parseFloat(getStr(packet, "f")), get(packet, "g"));
		});
		packetsPlayOut.addTo("PacketPlayOutEntityVelocity",
				(p, f) -> new NPacketPlayOutEntityVelocity(get(f, "a"), get(f, "b"), get(f, "c"), get(f, "d")));
		packetsPlayOut.addTo("PacketPlayOutPosition", (p, f) -> new NPacketPlayOutPosition(get(f, "a"), get(f, "b"),
				get(f, "c"), get(f, "d"), get(f, "e")));
		packetsPlayOut.addTo("PacketPlayOutExplosion", (p, f) -> new NPacketPlayOutExplosion(get(f, "a"), get(f, "b"),
				get(f, "c"), get(f, "f"), get(f, "g"), get(f, "h")));
		packetsPlayOut.addTo("PacketPlayOutEntity", (p, f) -> {
			return new NPacketPlayOutEntity(get(f, "a"), Double.parseDouble(getStr(f, "b")),
					Double.parseDouble(getStr(f, "c")), Double.parseDouble(getStr(f, "d")));
		});
		packetsPlayOut.addTo("PacketPlayOutEntityEffect", (p, packet) -> {
			return new NPacketPlayOutEntityEffect(get(packet, "a"), get(packet, "b"), get(packet, "c"), get(packet, "d"), get(packet, "e"));
		});
		packetsPlayOut.addTo("PacketPlayOutTransaction", (p, f) -> {
			return new NPacketPlayOutPing((int) (short) get(f, "b"));
		});
		
		packetsHandshake.addTo("PacketHandshakingInListener", (p, t) -> new NPacketHandshakeInListener());
		packetsHandshake.addTo("PacketHandshakingInSetProtocol", (p, raw) -> {
			PacketContent content = new PacketContent(raw);
			ContentModifier<Integer> ints = content.getIntegers();
			return new NPacketHandshakeInSetProtocol(ints.read("a", 0), content.getStrings().readSafely(0, "0.0.0.0"), ints.read("port", 0));
		});

		SpigotNegativity.getInstance().getLogger().info("[Packets-" + version + "] Loaded " + packetsPlayIn.size()
				+ " PlayIn, " + packetsPlayOut.size() + " PlayOut, " + packetsHandshake.size() + " Handshake and " + packetsStatus.size() + " Status.");
	}

	protected abstract String getOnGroundFieldName();

	public abstract double getAverageTps();

	public abstract float cos(float f);
	public abstract float sin(float f);

	public List<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}

	public abstract int getPlayerPing(Player player);

	public Class<?> getEnumPlayerInfoAction() {
		try {
			try {
				return Class.forName("net.minecraft.server." + VERSION + ".EnumPlayerInfoAction");
			} catch (Exception e) {
				for (Class<?> clazz : Class.forName("net.minecraft.server." + VERSION + ".PacketPlayOutPlayerInfo")
						.getDeclaredClasses())
					if (clazz.getName().contains("EnumPlayerInfoAction"))
						return clazz;
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double[] getTps() {
		try {
			Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer", "server.");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			return (double[]) server.getClass().getField("recentTps").get(server);
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().warn("Cannot get TPS (Work on Spigot but NOT CraftBukkit).");
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

	@Override
	public void sendPacket(Player p, Object packet) {
		try {
			Object playerConnection = getPlayerConnection(p);
			playerConnection.getClass().getMethod("sendPacket", PacketUtils.getNmsClass("Packet", "network.protocol.game."))
					.invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Channel getPlayerChannel(Player p) {
		try {
			Object playerConnection = getPlayerConnection(p);
			Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
			return new PacketContent(networkManager).getSpecificModifier(Channel.class).readSafely(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<ChannelFuture> getFuturChannel() {
		try {
			Object mcServer = PacketUtils.getDedicatedServer();
			Object co = ReflectionUtils.getFirstWith(mcServer, PacketUtils.getNmsClass("MinecraftServer", "server."), PacketUtils.getNmsClass("ServerConnection", "server.network."));
			try {
				return (List<ChannelFuture>) ReflectionUtils.getPrivateField(co, "g");
			} catch (NoSuchFieldException e) {
				return (List<ChannelFuture>) ReflectionUtils.getPrivateField(co, "listeningChannels");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Entity> getEntities(World w){
		return w.getEntities();
	}
	
	public abstract BlockPosition getBlockPosition(Object obj);

	private static SpigotVersionAdapter instance;

	public static SpigotVersionAdapter getVersionAdapter() {
		if (instance == null) {
			switch (VERSION) {
			case "v1_7_R4":
				return instance = new Spigot_1_7_R4();
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
			default:
				return instance = new Spigot_UnknowVersion(VERSION);
			}
		}
		return instance;
	}

	public Vector getVectorFromVec3D(Object vec) {
		if(Version.getVersion().isNewerOrEquals(Version.V1_9)) {
			return new Vector((double) get(vec, "x"), (double) get(vec, "x"), (double) get(vec, "x"));
		} else {
			return new Vector((double) get(vec, "a"), (double) get(vec, "b"), (double) get(vec, "c"));
		}
	}
}
