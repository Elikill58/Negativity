package com.elikill58.negativity.api.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketUnknown;
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
	
	/**
	 * Get packet type of the given name<br>
	 * If the packet is not found and debug is enabled, if will log it as info
	 * 
	 * @param packetName the packet name
	 * @return the packet type, or the UNSET value of the PacketType section or null
	 */
	public static PacketType getType(String packetName) {
		for(PacketDirection dir : PacketDirection.values()) {
			if(packetName.startsWith(dir.getPrefix()))
				return getPacketTypeFor(packetName, dir.getTypes(), dir.getUnset());
		}
		return null;
	}
	
	static PacketType getPacketTypeFor(String packetName, List<PacketType> types, PacketType unset) {
		for(PacketType packet : types)
			if(packet.getFullName().equalsIgnoreCase(packetName) || packet.getPacketName().equalsIgnoreCase(packetName)  || packet.getAlias().contains(packetName))
				return packet;
		Adapter.getAdapter().debug("[Packet] Unknow packet " + packetName);
		return unset;
	}
	
	enum Client implements PacketType {
		ABILITIES("Abilities", NPacketUnknown::new),
		ADVANCEMENTS("Advancements", NPacketUnknown::new),
		ARM_ANIMATION("ArmAnimation", NPacketPlayInArmAnimation::new),
		AUTO_RECIPE("AutoRecipe", NPacketUnknown::new),
		BEACON("Beacon", NPacketUnknown::new),
		BEDIT("BEdit", NPacketUnknown::new),
		BOAT_MOVE("BoatMove", NPacketUnknown::new),
		BLOCK_DIG("BlockDig", NPacketPlayInBlockDig::new),
		BLOCK_PLACE("BlockPlace", NPacketPlayInBlockPlace::new),
		CHAT("Chat", NPacketPlayInChat::new),
		CLIENT_COMMAND("ClientCommand", NPacketUnknown::new),
		CLOSE_WINDOW("CloseWindow", NPacketUnknown::new),
		CUSTOM_PAYLOAD("CustomPayload", NPacketUnknown::new),
		DIFFICULTY_CHANGE("DifficultyChange", NPacketUnknown::new),
		DIFFICULTY_LOCK("DifficultyLock", NPacketUnknown::new),
		ENCHANT_ITEM("EnchantItem", NPacketUnknown::new),
		ENTITY_ACTION("EntityAction", NPacketPlayInEntityAction::new),
		ENTITY_NBT_QUERY("EntityNBTQuery", NPacketUnknown::new),
		FLYING("Flying", NPacketPlayInFlying::new),
		GROUND("Ground", NPacketPlayInGround::new),
		HELD_ITEM_SLOT("HeldItemSlot", NPacketPlayInHeldItemSlot::new),
		ITEM_NAME("ItemName", NPacketUnknown::new),
		KEEP_ALIVE("KeepAlive", NPacketPlayInKeepAlive::new),
		LOOK("Look", NPacketPlayInLook::new, "Rotation"),
		PICK_ITEM("PickItem", NPacketUnknown::new),
		POSITION("Position", NPacketPlayInPosition::new),
		POSITION_LOOK("PositionLook", NPacketPlayInPositionLook::new, "PositionRotation"),
		RECIPE_DISPLAYED("RecipeDisplayed", NPacketUnknown::new),
		RESOURCE_PACK_STATUS("ResourcePackStatus", NPacketUnknown::new),
		SET_COMMAND_BLOCK("SetCommandBlock", NPacketUnknown::new),
		SET_COMMAND_MINECART("SetCommandMinecart", NPacketUnknown::new),
		SET_CREATIVE_SLOT("SetCreativeSlot", NPacketUnknown::new),
		SET_JIGSAW("SetJigsaw", NPacketUnknown::new),
		SETTINGS("Settings", NPacketUnknown::new, "ClientSettings"),
		SPECTATE("Spectate", NPacketUnknown::new),
		STEER_VEHICLE("SteerVehicle", NPacketPlayInSteerVehicle::new),
		STRUCT("Struct", NPacketUnknown::new),
		TAB_COMPLETE("TabComplete", NPacketUnknown::new),
		TELEPORT_ACCEPT("TeleportAccept", NPacketPlayInTeleportAccept::new),
		TILE_NBT_QUERY("TileNBTQuery", NPacketUnknown::new),
		TR_SEL("TrSel", NPacketUnknown::new),
		PONG("Transaction", NPacketPlayInPong::new, "Transaction"),
		UPDATE_SIGN("UpdateSign", NPacketUnknown::new),
		USE_ENTITY("UseEntity", NPacketPlayInUseEntity::new),
		USE_ITEM("UseItem", NPacketPlayInUseItem::new),
		VEHICLE_MOVE("VehicleMove", NPacketUnknown::new),
		WINDOW_CLICK("WindowClick", NPacketUnknown::new),
		UNSET("Unset", NPacketUnknown::new);
		
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
		
		ABILITIES("Abilities", NPacketUnknown::new),
		ADVANCEMENTS("Advancements", NPacketUnknown::new),
		ANIMATION("Animation", NPacketUnknown::new),
		ATTACH_ENTITY("AttachEntity", NPacketUnknown::new),
		AUTO_RECIPE("AutoRecipe", NPacketUnknown::new),
		BED("Bed", NPacketUnknown::new),
		BLOCK_ACTION("BlockAction", NPacketUnknown::new),
		BLOCK_BREAK("BlockBreak", NPacketUnknown::new),
		BLOCK_BREAK_ANIMATION("BlockBreakAnimation", NPacketPlayOutBlockBreakAnimation::new),
		BLOCK_CHANGE("BlockChange", NPacketUnknown::new),
		BOSS("Boss", NPacketUnknown::new),
		CAMERA("Camera", NPacketUnknown::new),
		CHAT("Chat", NPacketUnknown::new),
		CLOSE_WINDOW("CloseWindow", NPacketUnknown::new),
		COLLECT("Collect", NPacketUnknown::new),
		COMBAT_EVENT("CombatEvent", NPacketUnknown::new),
		COMMANDS("Commands", NPacketUnknown::new),
		CUSTOM_PAYLOAD("CustomPayload", NPacketUnknown::new),
		CUSTOM_SOUND_EFFECT("CustomSoundEffect", NPacketUnknown::new),
		ENTITY("Entity", NPacketPlayOutEntity::new),
		ENTITY_DESTROY("EntityDestroy", NPacketUnknown::new),
		ENTITY_EFFECT("EntityEffect", NPacketPlayOutEntityEffect::new),
		ENTITY_EQUIPMENT("EntityEquipment", NPacketUnknown::new),
		ENTITY_HEAD_ROTATION("EntityHeadRotation", NPacketUnknown::new),
		ENTITY_LOOK("EntityLook", NPacketUnknown::new),
		ENTITY_METADATA("EntityMetadata", NPacketUnknown::new),
		ENTITY_STATUS("EntityStatus", NPacketUnknown::new),
		ENTITY_SOUND("EntitySound", NPacketUnknown::new),
		ENTITY_TELEPORT("EntityTeleport", NPacketPlayOutEntityTeleport::new),
		ENTITY_VELOCITY("EntityVelocity", NPacketPlayOutEntityVelocity::new),
		EXPERIENCE("Experience", NPacketUnknown::new),
		EXPLOSION("Explosion", NPacketPlayOutExplosion::new),
		GAME_STATE_CHANGE("GameStateChange", NPacketUnknown::new),
		HELD_ITEM_SLOT("HeldItemSlot", NPacketUnknown::new),
		KEEP_ALIVE("KeepAlive", NPacketPlayOutKeepAlive::new),
		KICK_DISCONNECT("KickDisconnect", NPacketUnknown::new),
		LIGHT_UPDATE("LightUpdate", NPacketUnknown::new),
		LEVEL_CHUNK_LIGHT("LevelChunkWithLight", NPacketUnknown::new),
		LOOK_AT("LookAt", NPacketUnknown::new),
		LOGIN("Login", NPacketUnknown::new),
		MAP("Map", NPacketUnknown::new),
		MAP_CHUNK("MapChunk", NPacketUnknown::new),
		MAP_CHUNK_BULK("MapChunkBulk", NPacketUnknown::new),
		MOUNT("Mount", NPacketUnknown::new),
		MULTI_BLOCK_CHANGE("MultiBlockChange", NPacketUnknown::new),
		NAMED_ENTITY_SPAWN("NamedEntitySpawn", NPacketUnknown::new),
		NAMED_SOUND_EFFECT("NamedSoundEffect", NPacketUnknown::new),
		NBT_QUERY("NBTQuery", NPacketUnknown::new),
		OPEN_BOOK("OpenBook", NPacketUnknown::new),
		OPEN_SIGN_EDITOR("OpenSignEditor", NPacketUnknown::new),
		OPEN_WINDOW("OpenWindow", NPacketUnknown::new),
		OPEN_WINDOW_MERCHANT("OpenWindowMerchant", NPacketUnknown::new),
		OPEN_WINDOW_HORSE("OpenWindowHorse", NPacketUnknown::new),
		PLAYER_INFO("PlayerInfo", NPacketUnknown::new),
		PLAYER_LIST_HEADER_FOOTER("PlayerListHeaderFooter", NPacketUnknown::new),
		POSITION("Position", NPacketPlayOutPosition::new),
		RECIPES("Recipes", NPacketUnknown::new),
		RECIPE_UPDATE("RecipeUpdate", NPacketUnknown::new),
		REL_ENTITY_MOVE("RelEntityMove", NPacketUnknown::new),
		REL_ENTITY_MOVE_LOOK("RelEntityMoveLook", NPacketUnknown::new),
		REMOVE_ENTITY_EFFECT("RemoveEntityEffect", NPacketUnknown::new),
		RESOURCE_PACK_SEND("ResourcePackSend", NPacketUnknown::new),
		RESPAWN("Respawn", NPacketUnknown::new),
		SCOREBOARD_DISPLAY_OBJECTIVE("ScoreboardDisplayObjective", NPacketUnknown::new),
		SCOREBOARD_OBJECTIVE("ScoreboardObjective", NPacketUnknown::new),
		SCOREBOARD_SCORE("ScoreboardScore", NPacketUnknown::new),
		SCOREBOARD_TEAM("ScoreboardTeam", NPacketUnknown::new),
		SERVER_DIFFICULTY("ServerDifficulty", NPacketUnknown::new),
		SET_COMPRESSION("SetCompression", NPacketUnknown::new),
		SET_COOLDOWN("SetCooldown", NPacketUnknown::new),
		SET_SLOT("SetSlot", NPacketUnknown::new),
		SPAWN_ENTITY("SpawnEntity", NPacketUnknown::new),
		SPAWN_ENTITY_EXPERIENCE_ORB("SpawnEntityExperienceOrb", NPacketUnknown::new),
		SPAWN_ENTITY_LIVING("SpawnEntityLiving", NPacketUnknown::new),
		SPAWN_ENTITY_PAINTING("SpawnEntityPainting", NPacketUnknown::new),
		SPAWN_ENTITY_WEATHER("SpawnEntityWeather", NPacketUnknown::new),
		SPAWN_PLAYER("SpawnPlayer", NPacketUnknown::new),
		SPAWN_POSITION("SpawnPosition", NPacketUnknown::new),
		STATISTIC("Statistic", NPacketUnknown::new),
		STOP_SOUND("StopSound", NPacketUnknown::new),
		TAB_COMPLETE("TabComplete", NPacketUnknown::new),
		TAGS("Tags", NPacketUnknown::new),
		TILE_ENTITY_DATA("TileEntityData", NPacketUnknown::new),
		TITLE("Title", NPacketUnknown::new),
		PING("Ping", NPacketPlayOutPing::new, "Transaction"),
		UNLOAD_CHUNK("UnloadChunk", NPacketUnknown::new),
		UPDATE_ATTRIBUTES("UpdateAttributes", NPacketUnknown::new),
		UPDATE_ENTITY_NBT("UpdateEntityNBT", NPacketUnknown::new),
		UPDATE_HEALTH("UpdateHealth", NPacketUnknown::new),
		UPDATE_SIGN("UpdateSign", NPacketUnknown::new),
		UPDATE_TIME("UpdateTime", NPacketUnknown::new),
		VEHICLE_MOVE("VehicleMove", NPacketUnknown::new),
		VIEW_DISTANCE("ViewDistance", NPacketUnknown::new),
		VIEW_CENTRE("ViewCentre", NPacketUnknown::new),
		WINDOW_DATA("WindowData", NPacketUnknown::new),
		WINDOW_ITEMS("WindowItems", NPacketUnknown::new),
		WORLD_BORDER("WorldBorder", NPacketUnknown::new),
		WORLD_EVENT("WorldEvent", NPacketUnknown::new),
		WORLD_PARTICLES("WorldParticles", NPacketUnknown::new),
		UNSET("Unset", NPacketUnknown::new);
		
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
