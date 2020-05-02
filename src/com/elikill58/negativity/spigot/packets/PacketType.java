package com.elikill58.negativity.spigot.packets;

import com.elikill58.negativity.spigot.SpigotNegativity;

public interface PacketType {

	public String name();
	public String getPacketName();
	public String getFullName();
	
	static boolean LOG_UNKNOW_PACKET = false;
	static String CLIENT_PREFIX = "PacketPlayIn", SERVER_PREFIX = "PacketPlayOut", LOGIN_PREFIX = "PacketLogin", STATUS_PREFIX = "PacketStatus";
	
	public static PacketType getType(String packetName) {
		if(packetName.startsWith(CLIENT_PREFIX)) {
			for(Client client : Client.values())
				if(client.getFullName().equalsIgnoreCase(packetName))
					return client;
			if(LOG_UNKNOW_PACKET)
				SpigotNegativity.getInstance().getLogger().info("[Packet] Unknow client packet " + packetName);
			return Client.UNSET;
		} else if(packetName.startsWith(SERVER_PREFIX)) {
			for(Server srv : Server.values())
				if(srv.getFullName().equalsIgnoreCase(packetName))
					return srv;
			if(LOG_UNKNOW_PACKET)
				SpigotNegativity.getInstance().getLogger().info("[Packet] Unknow server packet " + packetName);
			return Server.UNSET;
		} else if(packetName.startsWith(LOGIN_PREFIX)) {
			for(Login login : Login.values())
				if(login.getFullName().equalsIgnoreCase(packetName))
					return login;
			if(LOG_UNKNOW_PACKET)
				SpigotNegativity.getInstance().getLogger().info("[Packet] Unknow login packet " + packetName);
			return Login.UNSET;
		} else if(packetName.startsWith(STATUS_PREFIX)) {
			for(Status status : Status.values())
				if(status.getFullName().equalsIgnoreCase(packetName))
					return status;
			if(LOG_UNKNOW_PACKET)
				SpigotNegativity.getInstance().getLogger().info("[Packet] Unknow status packet " + packetName);
			return Status.UNSET;
		} else {
			if(LOG_UNKNOW_PACKET)
				SpigotNegativity.getInstance().getLogger().info("[Packet] Unknow packet " + packetName);
			return null;
		}
	}
	
	public static enum Client implements PacketType {
		ABILITIES("Abilities"),
		ADVANCEMENTS("Advancements"),
		ARM_ANIMATION("ArmAnimation"),
		AUTO_RECIPE("AutoRecipe"),
		BEACON("Beacon"),
		BEDIT("BEdit"),
		BOAT_MOVE("BoatMove"),
		BLOCK_DIG("BlockDig"),
		BLOCK_PLACE("BlockPlace"),
		CHAT("Chat"),
		CLIENT_COMMAND("ClientCommand"),
		CLOSE_WINDOW("CloseWindow"),
		CUSTOM_PAYLOAD("CustomPayload"),
		DIFFICULTY_CHANGE("DifficultyChange"),
		DIFFICULTY_LOCK("DifficultyLock"),
		ENCHANT_ITEM("EnchantItem"),
		ENTITY_ACTION("EntityAction"),
		ENTITY_NBT_QUERY("EntityNBTQuery"),
		FLYING("Flying"),
		HELD_ITEM_SLOT("HeldItemSlot"),
		ITEM_NAME("ItemName"),
		KEEP_ALIVE("KeepAlive"),
		LOOK("Look"),
		PICK_ITEM("PickItem"),
		POSITION("Position"),
		POSITION_LOOK("PositionLook"),
		RECIPE_DISPLAYED("RecipeDisplayed"),
		RESOURCE_PACK_STATUS("ResourcePackStatus"),
		SET_COMMAND_BLOCK("SetCommandBlock"),
		SET_COMMAND_MINECART("SetCommandMinecart"),
		SET_CREATIVE_SLOT("SetCreativeSlot"),
		SET_JIGSAW("SetJigsaw"),
		SETTINGS("Settings"),
		SPECTATE("Spectate"),
		STEER_VEHICLE("SteerVehicle"),
		STRUCT("Struct"),
		TAB_COMPLETE("TabComplete"),
		TELEPORT_ACCEPT("TeleportAccept"),
		TILE_NBT_QUERY("TileNBTQuery"),
		TR_SEL("TrSel"),
		TRANSACTION("Transaction"),
		UPDATE_SIGN("UpdateSign"),
		USE_ENTITY("UseEntity"),
		USE_ITEM("UseItem"),
		VEHICLE_MOVE("VehicleMove"),
		WINDOW_CLICK("WindowClick"),
		UNSET("Unset");
		
		private final String packetName, fullName;
		
		private Client(String packetName) {
			this.packetName = packetName;
			this.fullName = CLIENT_PREFIX + packetName;
		}
		
		@Override
		public String getPacketName() {
			return packetName;
		}

		@Override
		public String getFullName() {
			return fullName;
		}
	}
	
	public static enum Server implements PacketType {
		
		ABILITIES("Abilities"),
		ADVANCEMENTS("Advancements"),
		ANIMATION("Animation"),
		ATTACH_ENTITY("AttachEntity"),
		AUTO_RECIPE("AutoRecipe"),
		BED("Bed"),
		BLOCK_ACTION("BlockAction"),
		BLOCK_BREAK("BlockBreak"),
		BLOCK_BREAK_ANIMATION("BlockBreakAnimation"),
		BLOCK_CHANGE("BlockChange"),
		BOSS("Boss"),
		CAMERA("Camera"),
		CHAT("Chat"),
		CLOSE_WINDOW("CloseWindow"),
		COLLECT("Collect"),
		COMBAT_EVENT("CombatEvent"),
		COMMANDS("Commands"),
		CUSTOM_PAYLOAD("CustomPayload"),
		CUSTOM_SOUND_EFFECT("CustomSoundEffect"),
		ENTITY("Entity"),
		ENTITY_DESTROY("EntityDestroy"),
		ENTITY_EFFECT("EntityEffect"),
		ENTITY_EQUIPMENT("EntityEquipment"),
		ENTITY_HEAD_ROTATION("EntityHeadRotation"),
		ENTITY_LOOK("EntityLook"),
		ENTITY_METADATA("EntityMetadata"),
		ENTITY_STATUS("EntityStatus"),
		ENTITY_SOUND("EntitySound"),
		ENTITY_TELEPORT("EntityTeleport"),
		ENTITY_VELOCITY("EntityVelocity"),
		EXPERIENCE("Experience"),
		EXPLOSION("Explosion"),
		GAME_STATE_CHANGE("GameStateChange"),
		HELD_ITEM_SLOT("HeldItemSlot"),
		KEEP_ALIVE("KeepAlive"),
		KICK_DISCONNECT("KickDisconnect"),
		LIGHT_UPDATE("LightUpdate"),
		LOOK_AT("LookAt"),
		LOGIN("Login"),
		MAP("Map"),
		MAP_CHUNK("MapChunk"),
		MAP_CHUNK_BULK("MapChunkBulk"),
		MOUNT("Mount"),
		MULTI_BLOCK_CHANGE("MultiBlockChange"),
		NAMED_ENTITY_SPAWN("NamedEntitySpawn"),
		NAMED_SOUND_EFFECT("NamedSoundEffect"),
		NBT_QUERY("NBTQuery"),
		OPEN_BOOK("OpenBook"),
		OPEN_SIGN_EDITOR("OpenSignEditor"),
		OPEN_WINDOW("OpenWindow"),
		OPEN_WINDOW_MERCHANT("OpenWindowMerchant"),
		OPEN_WINDOW_HORSE("OpenWindowHorse"),
		PLAYER_INFO("PlayerInfo"),
		PLAYER_LIST_HEADER_FOOTER("PlayerListHeaderFooter"),
		POSITION("Position"),
		RECIPES("Recipes"),
		RECIPE_UPDATE("RecipeUpdate"),
		REL_ENTITY_MOVE("RelEntityMove"),
		REL_ENTITY_MOVE_LOOK("RelEntityMoveLook"),
		REMOVE_ENTITY_EFFECT("RemoveEntityEffect"),
		RESOURCE_PACK_SEND("ResourcePackSend"),
		RESPAWN("Respawn"),
		SCOREBOARD_DISPLAY_OBJECTIVE("ScoreboardDisplayObjective"),
		SCOREBOARD_OBJECTIVE("ScoreboardObjective"),
		SCOREBOARD_SCORE("ScoreboardScore"),
		SCOREBOARD_TEAM("ScoreboardTeam"),
		SERVER_DIFFICULTY("ServerDifficulty"),
		SET_COMPRESSION("SetCompression"),
		SET_COOLDOWN("SetCooldown"),
		SET_SLOT("SetSlot"),
		SPAWN_ENTITY("SpawnEntity"),
		SPAWN_ENTITY_EXPERIENCE_ORB("SpawnEntityExperienceOrb"),
		SPAWN_ENTITY_LIVING("SpawnEntityLiving"),
		SPAWN_ENTITY_PAINTING("SpawnEntityPainting"),
		SPAWN_ENTITY_WEATHER("SpawnEntityWeather"),
		SPAWN_POSITION("SpawnPosition"),
		STATISTIC("Statistic"),
		STOP_SOUND("StopSound"),
		TAB_COMPLETE("TabComplete"),
		TAGS("Tags"),
		TILE_ENTITY_DATA("TileEntityData"),
		TITLE("Title"),
		TRANSACTION("Transaction"),
		UNLOAD_CHUNK("UnloadChunk"),
		UPDATE_ATTRIBUTES("UpdateAttributes"),
		UPDATE_ENTITY_NBT("UpdateEntityNBT"),
		UPDATE_HEALTH("UpdateHealth"),
		UPDATE_SIGN("UpdateSign"),
		UPDATE_TIME("UpdateTime"),
		VEHICLE_MOVE("VehicleMove"),
		VIEW_DISTANCE("ViewDistance"),
		VIEW_CENTRE("ViewCentre"),
		WINDOW_DATA("WindowData"),
		WINDOW_ITEMS("WindowItems"),
		WORLD_BORDER("WorldBorder"),
		WORLD_EVENT("WorldEvent"),
		WORLD_PARTICLES("WorldParticles"),
		UNSET("Unset");
		
		private final String packetName, fullName;
		
		private Server(String packetName) {
			this.packetName = packetName;
			this.fullName = SERVER_PREFIX + packetName;
		}

		@Override
		public String getPacketName() {
			return packetName;
		}

		@Override
		public String getFullName() {
			return fullName;
		}
	}
	
	public static enum Login implements PacketType {
		
		CUSTOM_PAYLOAD_OUT("OutCustomPayload"),
		CUSTOM_PAYLOAD_IN("InCustomPayload"),
		DISCONNECT("OutDisconnect"),
		ENCRYPTION_BEGIN_OUT("OutEncryptionBegin"),
		ENCRYPTION_BEGIN_IN("InEncryptionBegin"),
		LISTENER_OUT("OutListener"),
		LISTENER_IN("InListener"),
		SET_COMPRESSION("OutSetCompression"),
		START("InStart"),
		SUCCESS("OutSuccess"),
		UNSET("Unset");
		
		private final String packetName, fullName;
		
		private Login(String packetName) {
			this.packetName = packetName;
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
	}
	
	public static enum Status implements PacketType {

		LISTENER("Listener"),
		LISTENER_IN("InListener"),
		LISTENER_OUT("OutListener"),
		PING("InPing"),
		START("InStart"),
		PONG("OutPong"),
		SERVER_INFO("OutServerInfo"),
		UNSET("Unset");
		
		private final String packetName, fullName;
		
		private Status(String packetName) {
			this.packetName = packetName;
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
	}
}
