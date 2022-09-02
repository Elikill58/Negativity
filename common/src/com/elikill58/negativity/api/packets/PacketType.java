package com.elikill58.negativity.api.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketUnset;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInListener;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInSetProtocol;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeUnset;
import com.elikill58.negativity.api.packets.packet.login.NPacketLoginUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInArmAnimation;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInGround;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInHeldItemSlot;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInSteerVehicle;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInTeleportAccept;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseItem;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.api.packets.packet.status.NPacketStatusUnset;
import com.elikill58.negativity.universal.Adapter;

public interface PacketType {

	/**
	 * Get the packet ID from the enum
	 * 
	 * @return packet key name
	 */
	String name();
	
	/**
	 * Get a showable packet name
	 * 
	 * @return the packet name
	 */
	String getPacketName();
	
	/**
	 * Get the full packet name with prefix
	 * 
	 * @return the full packet name
	 */
	String getFullName();
	
	/**
	 * Get all alias of the packet
	 * 
	 * @return all aliases
	 */
	List<String> getAlias();
	
	/**
	 * Check if it's flying packet
	 * 
	 * @return true if it's flying packet
	 */
	boolean isFlyingPacket();
	
	/**
	 * Check if the current type correspond to an unset type.
	 * 
	 * @return true if it's an unset packet
	 */
	boolean isUnset();
	
	/**
	 * Create a new packet of the current type
	 * 
	 * @return a new instance of his packet
	 */
	NPacket createNewPacket();
	
	/**
	 * Get the packet direction
	 * 
	 * @return direction of the packet type
	 */
	PacketDirection getDirection();
	
	@Deprecated
	String CLIENT_PREFIX = "PacketPlayIn", SERVER_PREFIX = "PacketPlayOut", LOGIN_PREFIX = "PacketLogin", STATUS_PREFIX = "PacketStatus", HANDSHAKE_PREFIX = "PacketHandshaking";

	public static List<PacketType> values() {
		List<PacketType> list = new ArrayList<>();
		list.addAll(Arrays.asList(Client.values()));
		list.addAll(Arrays.asList(Server.values()));
		list.addAll(Arrays.asList(Login.values()));
		list.addAll(Arrays.asList(Status.values()));
		list.addAll(Arrays.asList(Handshake.values()));
		return list;
	}
	
	public static NPacket createEmptyOrUnsetPacket(PacketDirection dir, String packetName) {
		for(PacketType packet : dir.getTypes()) {
			if(packet.getFullName().equalsIgnoreCase(packetName) || packet.getPacketName().equalsIgnoreCase(packetName)  || packet.getAlias().contains(packetName)) {
				NPacket p = packet.createNewPacket();
				if(p != null && p instanceof NPacketUnset) {// change packename if can
					NPacketUnset unset = (NPacketUnset) p;
					unset.setPacketName(packetName);
					unset.setPacketTypeCible(packet);
				}
				return p;
			}
		}
		Adapter.getAdapter().debug("[Packet] Unknow packet " + packetName);
		return dir.createUnsetPacket(packetName);
	}
	
	/**
	 * Get packet type of the given name
	 * 
	 * @param packetName the packet name
	 * @return the packet type, or the UNSET value of the PacketType section or null
	 */
	public static PacketType getType(String packetName) {
		for(PacketDirection dir : PacketDirection.values()) {
			if(packetName.startsWith(dir.getPrefix()))
				return getPacketTypeFor(dir, packetName);
		}
		return null;
	}
	
	static PacketType getPacketTypeFor(String packetName, List<PacketType> types, PacketType unset) {
		for(PacketType packet : types)
			if(packet.getFullName().equalsIgnoreCase(packetName) || packet.getPacketName().equalsIgnoreCase(packetName)  || packet.getAlias().contains(packetName))
				return packet;
		return unset;
	}
	
	static PacketType getPacketTypeFor(PacketDirection dir, String packetName) {
		for(PacketType packet : dir.getTypes())
			if(packet.getFullName().equalsIgnoreCase(packetName) || packet.getPacketName().equalsIgnoreCase(packetName)  || packet.getAlias().contains(packetName))
				return packet;
		return dir.getUnset();
	}
	
	enum Client implements PacketType {
		ABILITIES("Abilities", NPacketPlayInUnset::new),
		ADVANCEMENTS("Advancements", NPacketPlayInUnset::new),
		ARM_ANIMATION("ArmAnimation", NPacketPlayInArmAnimation::new),
		AUTO_RECIPE("AutoRecipe", NPacketPlayInUnset::new),
		BEACON("Beacon", NPacketPlayInUnset::new),
		BEDIT("BEdit", NPacketPlayInUnset::new),
		BOAT_MOVE("BoatMove", NPacketPlayInUnset::new),
		BLOCK_DIG("BlockDig", NPacketPlayInBlockDig::new),
		BLOCK_PLACE("BlockPlace", NPacketPlayInBlockPlace::new),
		CHAT("Chat", NPacketPlayInChat::new),
		CLIENT_COMMAND("ClientCommand", NPacketPlayInUnset::new),
		CLOSE_WINDOW("CloseWindow", NPacketPlayInUnset::new),
		CUSTOM_PAYLOAD("CustomPayload", NPacketPlayInUnset::new),
		DIFFICULTY_CHANGE("DifficultyChange", NPacketPlayInUnset::new),
		DIFFICULTY_LOCK("DifficultyLock", NPacketPlayInUnset::new),
		ENCHANT_ITEM("EnchantItem", NPacketPlayInUnset::new),
		ENTITY_ACTION("EntityAction", NPacketPlayInEntityAction::new),
		ENTITY_NBT_QUERY("EntityNBTQuery", NPacketPlayInUnset::new),
		FLYING("Flying", NPacketPlayInFlying::new),
		GROUND("Ground", NPacketPlayInGround::new),
		HELD_ITEM_SLOT("HeldItemSlot", NPacketPlayInHeldItemSlot::new),
		ITEM_NAME("ItemName", NPacketPlayInUnset::new),
		KEEP_ALIVE("KeepAlive", NPacketPlayInKeepAlive::new),
		LOOK("Look", NPacketPlayInLook::new, "Rotation"),
		PICK_ITEM("PickItem", NPacketPlayInUnset::new),
		POSITION("Position", NPacketPlayInPosition::new),
		POSITION_LOOK("PositionLook", NPacketPlayInPositionLook::new, "PositionRotation"),
		RECIPE_DISPLAYED("RecipeDisplayed", NPacketPlayInUnset::new),
		RESOURCE_PACK_STATUS("ResourcePackStatus", NPacketPlayInUnset::new),
		SET_COMMAND_BLOCK("SetCommandBlock", NPacketPlayInUnset::new),
		SET_COMMAND_MINECART("SetCommandMinecart", NPacketPlayInUnset::new),
		SET_CREATIVE_SLOT("SetCreativeSlot", NPacketPlayInUnset::new),
		SET_JIGSAW("SetJigsaw", NPacketPlayInUnset::new),
		SETTINGS("Settings", NPacketPlayInUnset::new, "ClientSettings"),
		SPECTATE("Spectate", NPacketPlayInUnset::new),
		STEER_VEHICLE("SteerVehicle", NPacketPlayInSteerVehicle::new),
		STRUCT("Struct", NPacketPlayInUnset::new),
		TAB_COMPLETE("TabComplete", NPacketPlayInUnset::new),
		TELEPORT_ACCEPT("TeleportAccept", NPacketPlayInTeleportAccept::new),
		TILE_NBT_QUERY("TileNBTQuery", NPacketPlayInUnset::new),
		TR_SEL("TrSel", NPacketPlayInUnset::new),
		PONG("Transaction", NPacketPlayInPong::new, "Transaction"),
		UPDATE_SIGN("UpdateSign", NPacketPlayInUnset::new),
		USE_ENTITY("UseEntity", NPacketPlayInUseEntity::new),
		USE_ITEM("UseItem", NPacketPlayInUseItem::new),
		VEHICLE_MOVE("VehicleMove", NPacketPlayInUnset::new),
		WINDOW_CLICK("WindowClick", NPacketPlayInUnset::new),
		UNSET("Unset", NPacketPlayInUnset::new);
		
		private final String packetName, fullName;
		private final List<String> alias = new ArrayList<>();
		private final Callable<NPacket> fun;
		
		Client(String packetName, Callable<NPacket> fun, String... alias) {
			this.packetName = packetName;
			for(String al : alias)
				this.alias.add(CLIENT_PREFIX + al);
			this.fullName = CLIENT_PREFIX + packetName;
			this.fun = fun;
		}
		
		@Override
		public String getPacketName() {
			return packetName;
		}

		@Override
		public String getFullName() {
			return fullName;
		}
		
		@Override
		public List<String> getAlias() {
			return alias;
		}
		
		@Override
		public boolean isFlyingPacket() {
			return this == FLYING || this == POSITION || this == LOOK || this == POSITION_LOOK || this == GROUND;
		}
		
		@Override
		public boolean isUnset() {
			return this == UNSET;
		}
		
		@Override
		public NPacket createNewPacket() {
			try {
				return fun.call();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.CLIENT_TO_SERVER;
		}
	}
	
	enum Server implements PacketType {

		ABILITIES("Abilities", NPacketPlayOutUnset::new),
		ADVANCEMENTS("Advancements", NPacketPlayOutUnset::new),
		ANIMATION("Animation", NPacketPlayOutUnset::new),
		ATTACH_ENTITY("AttachEntity", NPacketPlayOutUnset::new),
		AUTO_RECIPE("AutoRecipe", NPacketPlayOutUnset::new),
		BED("Bed", NPacketPlayOutUnset::new),
		BLOCK_ACTION("BlockAction", NPacketPlayOutUnset::new),
		BLOCK_BREAK("BlockBreak", NPacketPlayOutUnset::new),
		BLOCK_BREAK_ANIMATION("BlockBreakAnimation", NPacketPlayOutBlockBreakAnimation::new),
		BLOCK_CHANGE("BlockChange", NPacketPlayOutUnset::new),
		BOSS("Boss", NPacketPlayOutUnset::new),
		CAMERA("Camera", NPacketPlayOutUnset::new),
		CHAT("Chat", NPacketPlayOutUnset::new),
		CLOSE_WINDOW("CloseWindow", NPacketPlayOutUnset::new),
		COLLECT("Collect", NPacketPlayOutUnset::new),
		COMBAT_EVENT("CombatEvent", NPacketPlayOutUnset::new),
		COMMANDS("Commands", NPacketPlayOutUnset::new),
		CUSTOM_PAYLOAD("CustomPayload", NPacketPlayOutUnset::new),
		CUSTOM_SOUND_EFFECT("CustomSoundEffect", NPacketPlayOutUnset::new),
		ENTITY("Entity", NPacketPlayOutEntity::new),
		ENTITY_DESTROY("EntityDestroy", NPacketPlayOutUnset::new),
		ENTITY_EFFECT("EntityEffect", NPacketPlayOutEntityEffect::new),
		ENTITY_EQUIPMENT("EntityEquipment", NPacketPlayOutUnset::new),
		ENTITY_HEAD_ROTATION("EntityHeadRotation", NPacketPlayOutUnset::new),
		ENTITY_LOOK("EntityLook", NPacketPlayOutUnset::new),
		ENTITY_METADATA("EntityMetadata", NPacketPlayOutUnset::new),
		ENTITY_STATUS("EntityStatus", NPacketPlayOutUnset::new),
		ENTITY_SOUND("EntitySound", NPacketPlayOutUnset::new),
		ENTITY_TELEPORT("EntityTeleport", NPacketPlayOutEntityTeleport::new),
		ENTITY_VELOCITY("EntityVelocity", NPacketPlayOutEntityVelocity::new),
		EXPERIENCE("Experience", NPacketPlayOutUnset::new),
		EXPLOSION("Explosion", NPacketPlayOutExplosion::new),
		GAME_STATE_CHANGE("GameStateChange", NPacketPlayOutUnset::new),
		HELD_ITEM_SLOT("HeldItemSlot", NPacketPlayOutUnset::new),
		KEEP_ALIVE("KeepAlive", NPacketPlayOutKeepAlive::new),
		KICK_DISCONNECT("KickDisconnect", NPacketPlayOutUnset::new),
		LIGHT_UPDATE("LightUpdate", NPacketPlayOutUnset::new),
		LEVEL_CHUNK_LIGHT("LevelChunkWithLight", NPacketPlayOutUnset::new),
		LOOK_AT("LookAt", NPacketPlayOutUnset::new),
		LOGIN("Login", NPacketPlayOutUnset::new),
		MAP("Map", NPacketPlayOutUnset::new),
		MAP_CHUNK("MapChunk", NPacketPlayOutUnset::new),
		MAP_CHUNK_BULK("MapChunkBulk", NPacketPlayOutUnset::new),
		MOUNT("Mount", NPacketPlayOutUnset::new),
		MULTI_BLOCK_CHANGE("MultiBlockChange", NPacketPlayOutUnset::new),
		NAMED_ENTITY_SPAWN("NamedEntitySpawn", NPacketPlayOutUnset::new),
		NAMED_SOUND_EFFECT("NamedSoundEffect", NPacketPlayOutUnset::new),
		NBT_QUERY("NBTQuery", NPacketPlayOutUnset::new),
		OPEN_BOOK("OpenBook", NPacketPlayOutUnset::new),
		OPEN_SIGN_EDITOR("OpenSignEditor", NPacketPlayOutUnset::new),
		OPEN_WINDOW("OpenWindow", NPacketPlayOutUnset::new),
		OPEN_WINDOW_MERCHANT("OpenWindowMerchant", NPacketPlayOutUnset::new),
		OPEN_WINDOW_HORSE("OpenWindowHorse", NPacketPlayOutUnset::new),
		PLAYER_INFO("PlayerInfo", NPacketPlayOutUnset::new),
		PLAYER_LIST_HEADER_FOOTER("PlayerListHeaderFooter", NPacketPlayOutUnset::new),
		POSITION("Position", NPacketPlayOutPosition::new),
		RECIPES("Recipes", NPacketPlayOutUnset::new),
		RECIPE_UPDATE("RecipeUpdate", NPacketPlayOutUnset::new),
		REL_ENTITY_MOVE("RelEntityMove", NPacketPlayOutUnset::new),
		REL_ENTITY_MOVE_LOOK("RelEntityMoveLook", NPacketPlayOutUnset::new),
		REMOVE_ENTITY_EFFECT("RemoveEntityEffect", NPacketPlayOutUnset::new),
		RESOURCE_PACK_SEND("ResourcePackSend", NPacketPlayOutUnset::new),
		RESPAWN("Respawn", NPacketPlayOutUnset::new),
		SCOREBOARD_DISPLAY_OBJECTIVE("ScoreboardDisplayObjective", NPacketPlayOutUnset::new),
		SCOREBOARD_OBJECTIVE("ScoreboardObjective", NPacketPlayOutUnset::new),
		SCOREBOARD_SCORE("ScoreboardScore", NPacketPlayOutUnset::new),
		SCOREBOARD_TEAM("ScoreboardTeam", NPacketPlayOutUnset::new),
		SERVER_DIFFICULTY("ServerDifficulty", NPacketPlayOutUnset::new),
		SET_COMPRESSION("SetCompression", NPacketPlayOutUnset::new),
		SET_COOLDOWN("SetCooldown", NPacketPlayOutUnset::new),
		SET_SLOT("SetSlot", NPacketPlayOutUnset::new),
		SPAWN_ENTITY("SpawnEntity", NPacketPlayOutUnset::new),
		SPAWN_ENTITY_EXPERIENCE_ORB("SpawnEntityExperienceOrb", NPacketPlayOutUnset::new),
		SPAWN_ENTITY_LIVING("SpawnEntityLiving", NPacketPlayOutUnset::new),
		SPAWN_ENTITY_PAINTING("SpawnEntityPainting", NPacketPlayOutUnset::new),
		SPAWN_ENTITY_WEATHER("SpawnEntityWeather", NPacketPlayOutUnset::new),
		SPAWN_PLAYER("SpawnPlayer", NPacketPlayOutUnset::new),
		SPAWN_POSITION("SpawnPosition", NPacketPlayOutUnset::new),
		STATISTIC("Statistic", NPacketPlayOutUnset::new),
		STOP_SOUND("StopSound", NPacketPlayOutUnset::new),
		TAB_COMPLETE("TabComplete", NPacketPlayOutUnset::new),
		TAGS("Tags", NPacketPlayOutUnset::new),
		TILE_ENTITY_DATA("TileEntityData", NPacketPlayOutUnset::new),
		TITLE("Title", NPacketPlayOutUnset::new),
		PING("Ping", NPacketPlayOutPing::new, "Transaction"),
		UNLOAD_CHUNK("UnloadChunk", NPacketPlayOutUnset::new),
		UPDATE_ATTRIBUTES("UpdateAttributes", NPacketPlayOutUnset::new),
		UPDATE_ENTITY_NBT("UpdateEntityNBT", NPacketPlayOutUnset::new),
		UPDATE_HEALTH("UpdateHealth", NPacketPlayOutUnset::new),
		UPDATE_SIGN("UpdateSign", NPacketPlayOutUnset::new),
		UPDATE_TIME("UpdateTime", NPacketPlayOutUnset::new),
		VEHICLE_MOVE("VehicleMove", NPacketPlayOutUnset::new),
		VIEW_DISTANCE("ViewDistance", NPacketPlayOutUnset::new),
		VIEW_CENTRE("ViewCentre", NPacketPlayOutUnset::new),
		WINDOW_DATA("WindowData", NPacketPlayOutUnset::new),
		WINDOW_ITEMS("WindowItems", NPacketPlayOutUnset::new),
		WORLD_BORDER("WorldBorder", NPacketPlayOutUnset::new),
		WORLD_EVENT("WorldEvent", NPacketPlayOutUnset::new),
		WORLD_PARTICLES("WorldParticles", NPacketPlayOutUnset::new),
		UNSET("Unset", NPacketPlayOutUnset::new);
		
		private final String packetName, fullName;
		private final Callable<NPacket> fun;
		private List<String> alias = new ArrayList<>();
		
		Server(String packetName, Callable<NPacket> fun, String... alias) {
			this.packetName = packetName;
			this.fullName = SERVER_PREFIX + packetName;
			this.fun = fun;
			for(String al : alias)
				this.alias.add(SERVER_PREFIX + al);
		}

		@Override
		public String getPacketName() {
			return packetName;
		}

		@Override
		public String getFullName() {
			return fullName;
		}
		
		@Override
		public List<String> getAlias() {
			return alias;
		}
		
		@Override
		public boolean isFlyingPacket() {
			return this == LOOK_AT || this == POSITION || this == REL_ENTITY_MOVE || this == REL_ENTITY_MOVE_LOOK;
		}
		
		@Override
		public boolean isUnset() {
			return this == UNSET;
		}
		
		@Override
		public NPacket createNewPacket() {
			try {
				return fun.call();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.SERVER_TO_CLIENT;
		}
	}
	
	enum Login implements PacketType {
		
		CUSTOM_PAYLOAD_OUT("OutCustomPayload", NPacketLoginUnset::new),
		CUSTOM_PAYLOAD_IN("InCustomPayload", NPacketLoginUnset::new),
		DISCONNECT("OutDisconnect", NPacketLoginUnset::new),
		ENCRYPTION_BEGIN_OUT("OutEncryptionBegin", NPacketLoginUnset::new),
		ENCRYPTION_BEGIN_IN("InEncryptionBegin", NPacketLoginUnset::new),
		LISTENER_OUT("OutListener", NPacketLoginUnset::new),
		LISTENER_IN("InListener", NPacketLoginUnset::new),
		SET_COMPRESSION("OutSetCompression", NPacketLoginUnset::new),
		START("InStart", NPacketLoginUnset::new),
		SUCCESS("OutSuccess", NPacketLoginUnset::new),
		UNSET("Unset", NPacketLoginUnset::new);
		
		private final String packetName, fullName;
		private final Callable<NPacket> fun;
		private List<String> alias = new ArrayList<>();
		
		Login(String packetName, Callable<NPacket> fun, String... alias) {
			this.packetName = packetName;
			this.fun = fun;
			for(String al : alias)
				this.alias.add(LOGIN_PREFIX + al);
			this.fullName = LOGIN_PREFIX + packetName;
		}

		@Override
		public String getPacketName() {
			return packetName;
		}

		@Override
		public String getFullName() {
			return fullName;
		}
		
		@Override
		public List<String> getAlias() {
			return alias;
		}
		
		@Override
		public boolean isFlyingPacket() {
			return false;
		}
		
		@Override
		public boolean isUnset() {
			return this == UNSET;
		}
		
		@Override
		public NPacket createNewPacket() {
			try {
				return fun.call();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.LOGIN;
		}
	}
	
	enum Status implements PacketType {

		LISTENER("Listener", NPacketStatusUnset::new),
		LISTENER_IN("InListener", NPacketStatusUnset::new),
		LISTENER_OUT("OutListener", NPacketStatusUnset::new),
		PING("InPing", NPacketStatusUnset::new),
		START("InStart", NPacketStatusUnset::new),
		PONG("OutPong", NPacketStatusUnset::new),
		SERVER_INFO("OutServerInfo", NPacketStatusUnset::new),
		UNSET("Unset", NPacketStatusUnset::new);
		
		private final String packetName, fullName;
		private final Callable<NPacket> fun;
		private List<String> alias = new ArrayList<>();
		
		Status(String packetName, Callable<NPacket> fun, String... alias) {
			this.packetName = packetName;
			this.fun = fun;
			for(String al : alias)
				this.alias.add(STATUS_PREFIX + al);
			this.fullName = STATUS_PREFIX + packetName;
		}

		@Override
		public String getPacketName() {
			return packetName;
		}

		@Override
		public String getFullName() {
			return fullName;
		}
		
		@Override
		public List<String> getAlias() {
			return alias;
		}
		
		@Override
		public boolean isFlyingPacket() {
			return false;
		}
		
		@Override
		public boolean isUnset() {
			return this == UNSET;
		}
		
		@Override
		public NPacket createNewPacket() {
			try {
				return fun.call();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.STATUS;
		}
	}

	enum Handshake implements PacketType {
		
		IN_LISTENER("InListener", NPacketHandshakeInListener::new),
		IS_SET_PROTOCOL("InSetProtocol", NPacketHandshakeInSetProtocol::new),
		UNSET("Unset", NPacketHandshakeUnset::new);
		
		private final String packetName, fullName;
		private final Callable<NPacket> fun;
		private List<String> alias = new ArrayList<>();
		
		Handshake(String packetName, Callable<NPacket> fun, String... alias) {
			this.packetName = packetName;
			this.fun = fun;
			for(String al : alias)
				this.alias.add(HANDSHAKE_PREFIX + al);
			this.fullName = HANDSHAKE_PREFIX + packetName;
		}

		@Override
		public String getPacketName() {
			return packetName;
		}

		@Override
		public String getFullName() {
			return fullName;
		}
		
		@Override
		public List<String> getAlias() {
			return alias;
		}

		@Override
		public boolean isFlyingPacket() {
			return false;
		}

		@Override
		public boolean isUnset() {
			return this == UNSET;
		}

		@Override
		public NPacket createNewPacket() {
			try {
				return fun.call();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.HANDSHAKE;
		}
	}
}
