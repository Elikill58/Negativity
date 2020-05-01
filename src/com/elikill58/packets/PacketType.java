package com.elikill58.orebfuscator.packets;

public class PacketType {
	
	public static AbstractPacketType getType(String packetName) {
		if(packetName.startsWith("PacketPlayIn")) {
			for(Client client : Client.values())
				if(client.getFullName().equalsIgnoreCase(packetName))
					return client;
			return Client.UNSET;
		} else if(packetName.startsWith("PacketPlayOut")) {
			for(Server srv : Server.values())
				if(srv.getFullName().equalsIgnoreCase(packetName))
					return srv;
			return Server.UNSET;
		} else if(packetName.startsWith("PacketLoginOut")) {
			for(Login login : Login.values())
				if(login.getFullName().equalsIgnoreCase(packetName))
					return login;
			return Login.UNSET;
		} else if(packetName.startsWith("PacketStatus")) {
			for(Status status : Status.values())
				if(status.getFullName().equalsIgnoreCase(packetName))
					return status;
			return Status.UNSET;
		} else {
			return null;
		}
	}
	
	public interface AbstractPacketType {
		
	}
	
	public static enum Client implements AbstractPacketType {
		BLOCK_DIG("BlockDig"),
		BLOCK_PLACE("BlockPlace"),
		CHAT("Chat"),
		CLIENT_COMMAND("ClientCommand"),
		CLOSE_WINDOW("CloseWindow"),
		CUSTOM_PAYLOAD("CustomPayload"),
		ENCHANT_ITEM("EnchantItem"),
		ENTITY_ACTION("EntityAction"),
		FLYING("Flying"),
		HELD_ITEM_SLOT("HeldItemSlot"),
		KEPP_ALIVE("KeepAlive"),
		RESOURCE_PACK_STATUS("ResourcePackStatus"),
		SET_CREATIVE_SLOT("SetCreativeSlot"),
		SETTINGS("Settings"),
		SPECTATE("Spectate"),
		STEER_VEHICLE("SteerVehicle"),
		TAB_COMPLETE("TabComplete"),
		TRANSACTION("Transaction"),
		UPDATE_SIGN("UpdateSign"),
		USE_ENTITY("UseEntity"),
		WINDOW_CLICK("WindowClick"),
		UNSET("Unset");
		
		private final String packetName, fullName;
		
		private Client(String packetName) {
			this.packetName = packetName;
			this.fullName = "PacketPlayIn" + packetName;
		}
		
		public String getPacketName() {
			return packetName;
		}
		
		public String getFullName() {
			return fullName;
		}
	}
	
	public static enum Server implements AbstractPacketType {
		ABILITIES("Abilities"),
		ANIMATION("Animation"),
		ATTACH_ENTITY("AttachEntity"),
		BED("Bed"),
		BLOCK_ACTION("BlockAction"),
		BLOCK_BREAK_ANIMATION("BlockBreakAnimation"),
		BLOCK_CHANGE("BlockChange"),
		CAMERA("Camera"),
		CHAT("Chat"),
		CLOSE_WINDOW("CloseWindow"),
		COLLECT("Collect"),
		COMBAT_EVENT("CombatEvent"),
		CUSTOM_PAYLOAD("CustomPayload"),
		ENTITY("Entity"),
		ENTITY_DESTROY("EntityDestroy"),
		ENTITY_EFFECT("EntityEffect"),
		ENTITY_EQUIPMENT("EntityEquipment"),
		ENTITY_HEAD_ROTATION("EntityHeadRotation"),
		ENTITY_METADATA("EntityMetadata"),
		ENTITY_STATUS("EntityStatus"),
		ENTITY_TELEPORT("EntityTeleport"),
		ENTITY_VELOCITY("EntityVelocity"),
		EXPERIENCE("Experience"),
		EXPLOSION("Explosion"),
		GAME_STATE_CHANGE("GameStateChange"),
		HELD_ITEM_SLOT("HeldItemSlot"),
		KEEP_ALIVE("KeepAlive"),
		KICK_DISCONNECT("KickDisconnect"),
		LOGIN("Login"),
		MAP("Map"),
		MAP_CHUNK("MapChunk"),
		MAP_CHUNK_BULK("MapChunkBulk"),
		MULTI_BLOCK_CHANGE("MultiBlockChange"),
		NAMED_ENTITY_SPAWN("NamedEntitySpawn"),
		NAMED_SOUND_EFFECT("NamedSoundEffect"),
		OPEN_SIGN_EDITOR("OpenSignEditor"),
		OPEN_WINDOW("OpenWindow"),
		PLAYER_INFO("PlayerInfo"),
		PLAYER_LIST_HEADER_FOOTER("PlayerListHeaderFooter"),
		POSITION("Position"),
		REMOVE_ENTITY_EFFECT("RemoveEntityEffect"),
		RESOURCE_PACK_SEND("ResourcePackSend"),
		RESPAWN("Respawn"),
		SCOREBOARD_DISPLAY_OBJECTIVE("ScoreboardDisplayObjective"),
		SCOREBOARD_OBJECTIVE("ScoreboardObjective"),
		SCOREBOARD_SCORE("ScoreboardScore"),
		SCOREBOARD_TEAM("ScoreboardTeam"),
		SERVER_DIFFICULTY("ServerDifficulty"),
		SET_COMPRESSION("SetCompression"),
		SET_SLOT("SetSlot"),
		SPAWN_ENTITY("SpawnEntity"),
		SPAWN_ENTITY_EXPERIENCE_ORB("SpawnEntityExperienceOrb"),
		SPAWN_ENTITY_LIVING("SpawnEntityLiving"),
		SPAWN_ENTITY_PAINTING("SpawnEntityPainting"),
		SPAWN_ENTITY_WEATHER("SpawnEntityWeather"),
		SPAWN_POSITION("SpawnPosition"),
		STATISTIC("Statistic"),
		TAB_COMPLETE("TabComplete"),
		TILE_ENTITY_DATA("TileEntityData"),
		TITLE("Title"),
		TRANSACTION("Transaction"),
		UPDATE_ATTRIBUTES("UpdateAttributes"),
		UPDATE_ENTITY_NBT("UpdateEntityNBT"),
		UPDATE_HEALTH("UpdateHealth"),
		UPDATE_SIGN("UpdateSign"),
		UPDATE_TIME("UpdateTime"),
		WINDOW_DATA("WindowData"),
		WINDOW_ITEMS("WindowItems"),
		WORLD_BORDER("WorldBorder"),
		WORLD_EVENT("WorldEvent"),
		WORLD_PARTICLES("WorldParticles"),
		UNSET("Unset");
		
		private final String packetName, fullName;
		
		private Server(String packetName) {
			this.packetName = packetName;
			this.fullName = "PacketPlayOut" + packetName;
		}
		
		public String getPacketName() {
			return packetName;
		}
		
		public String getFullName() {
			return fullName;
		}
	}
	
	public static enum Login implements AbstractPacketType {
		
		DISCONNECT("Disconnect"),
		ENCRYPTION_BEGIN("EncryptionBegin"),
		LISTENER("Listener"),
		SET_COMPRESSION("SetCompression"),
		SUCCESS("Success"),
		UNSET("Unset");
		
		private final String packetName, fullName;
		
		private Login(String packetName) {
			this.packetName = packetName;
			this.fullName = "PacketLoginOut" + packetName;
		}
		
		public String getPacketName() {
			return packetName;
		}
		
		public String getFullName() {
			return fullName;
		}
	}
	
	public static enum Status implements AbstractPacketType {

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
			this.fullName = "PacketStatus" + packetName;
		}
		
		public String getPacketName() {
			return packetName;
		}
		
		public String getFullName() {
			return fullName;
		}
	}
}
