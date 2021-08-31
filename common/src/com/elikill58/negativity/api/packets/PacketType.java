package com.elikill58.negativity.api.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketUnknown;
import com.elikill58.negativity.api.packets.packet.login.NPacketLoginUnset;
import com.elikill58.negativity.api.packets.packet.playin.*;
import com.elikill58.negativity.api.packets.packet.playout.*;
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
	
	String CLIENT_PREFIX = "PacketPlayIn", SERVER_PREFIX = "PacketPlayOut", LOGIN_PREFIX = "PacketLogin", STATUS_PREFIX = "PacketStatus";


	public static List<PacketType> values() {
		List<PacketType> list = new ArrayList<>();
		list.addAll(Arrays.asList(Client.values()));
		list.addAll(Arrays.asList(Server.values()));
		list.addAll(Arrays.asList(Login.values()));
		list.addAll(Arrays.asList(Status.values()));
		return list;
	}
	
	/**
	 * Get packet type of the given name
	 * If the packet is not found and {@link #LOG_UNKNOW_PACKET} is on true, if will log it as info
	 * 
	 * @param packetName the packet name
	 * @return the packet type, or the UNSET value of the PacketType section or null
	 */
	static PacketType getType(String packetName) {
		if(packetName.startsWith(CLIENT_PREFIX)) {
			for(Client client : Client.values())
				if(client.getFullName().equalsIgnoreCase(packetName) || client.getPacketName().equalsIgnoreCase(packetName) || client.getAlias().contains(packetName))
					return client;
			Adapter.getAdapter().debug("[Packet] Unknow client packet " + packetName);
			return Client.UNSET;
		} else if(packetName.startsWith(SERVER_PREFIX)) {
			for(Server srv : Server.values())
				if(srv.getFullName().equalsIgnoreCase(packetName) || srv.getPacketName().equalsIgnoreCase(packetName)  || srv.getAlias().contains(packetName))
					return srv;
			Adapter.getAdapter().debug("[Packet] Unknow server packet " + packetName);
			return Server.UNSET;
		} else if(packetName.startsWith(LOGIN_PREFIX)) {
			for(Login login : Login.values())
				if(login.getFullName().equalsIgnoreCase(packetName) || login.getPacketName().equalsIgnoreCase(packetName)  || login.getAlias().contains(packetName))
					return login;
			Adapter.getAdapter().debug("[Packet] Unknow login packet " + packetName);
			return Login.UNSET;
		} else if(packetName.startsWith(STATUS_PREFIX)) {
			for(Status status : Status.values())
				if(status.getFullName().equalsIgnoreCase(packetName) || status.getPacketName().equalsIgnoreCase(packetName)  || status.getAlias().contains(packetName))
					return status;
			Adapter.getAdapter().debug("[Packet] Unknow status packet " + packetName);
			return Status.UNSET;
		} else {
			Adapter.getAdapter().debug("[Packet] Unknow packet " + packetName);
			return null;
		}
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
		ENTITY_ACTION("EntityAction", NPacketUnknown::new),
		ENTITY_NBT_QUERY("EntityNBTQuery", NPacketUnknown::new),
		FLYING("Flying", NPacketPlayInFlying::new),
		HELD_ITEM_SLOT("HeldItemSlot", NPacketUnknown::new),
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
		STEER_VEHICLE("SteerVehicle", NPacketUnknown::new),
		STRUCT("Struct", NPacketUnknown::new),
		TAB_COMPLETE("TabComplete", NPacketUnknown::new),
		TELEPORT_ACCEPT("TeleportAccept", NPacketUnknown::new),
		TILE_NBT_QUERY("TileNBTQuery", NPacketUnknown::new),
		TR_SEL("TrSel", NPacketUnknown::new),
		TRANSACTION("Transaction", NPacketUnknown::new),
		UPDATE_SIGN("UpdateSign", NPacketUnknown::new),
		USE_ENTITY("UseEntity", NPacketPlayInUseEntity::new),
		USE_ITEM("UseItem", NPacketUnknown::new),
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
			return this == FLYING || this == POSITION || this == LOOK || this == POSITION_LOOK;
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
		ENTITY_EFFECT("EntityEffect", NPacketUnknown::new),
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
		TRANSACTION("Transaction", NPacketUnknown::new),
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
			return this == LOOK_AT || this == POSITION;
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
	}
}
