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
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInCustomPayload;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInGround;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInHeldItemSlot;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInSetCreativeSlot;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInSettings;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInSteerVehicle;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInTeleportAccept;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseItem;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockChange;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutCustomPayload;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityDestroy;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityHeadRotation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutMultiBlockChange;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutRelEntityLook;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutRelEntityMove;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutRelEntityMoveLook;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnloadChunk;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.api.packets.packet.status.NPacketStatusUnset;
import com.elikill58.negativity.universal.utils.ChatUtils;

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
	default NPacket createNewPacket() {
		try {
			NPacket packet = getPacketCreatorFunction().call();
			if(packet.getPacketType().isUnset()) {
				((NPacketUnset) packet).setPacketTypeCible(this);
			}
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	Callable<NPacket> getPacketCreatorFunction();
	
	/**
	 * Get the packet direction
	 * 
	 * @return direction of the packet type
	 */
	PacketDirection getDirection();
	
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
			if(packet.getPacketName().equalsIgnoreCase(packetName)) {
				NPacket p = packet.createNewPacket();
				if(p != null && p instanceof NPacketUnset) {// change packename if can
					NPacketUnset unset = (NPacketUnset) p;
					unset.setPacketName(packetName);
					unset.setPacketTypeCible(packet);
				}
				return p;
			}
		}
		return dir.createUnsetPacket(packetName);
	}
	
	/**
	 * Get packet type of the given name
	 * 
	 * @param packetName the packet name
	 * @return the packet type, or the UNSET value of the PacketType section or null
	 */
	public static PacketType getType(String packetName) {
		if(packetName == null)
			return null;
		for(PacketDirection dir : PacketDirection.values()) {
			if(packetName.startsWith(dir.getPrefix()))
				return getPacketTypeFor(dir, packetName);
		}
		return null;
	}
	
	static PacketType getPacketTypeFor(String packetName, List<PacketType> types, PacketType unset) {
		for(PacketType packet : types)
			if(packet.getPacketName().equalsIgnoreCase(packetName))
				return packet;
		return unset;
	}
	
	static PacketType getPacketTypeFor(PacketDirection dir, String packetName) {
		for(PacketType packet : dir.getTypes())
			if(packet.getPacketName().equalsIgnoreCase(packetName))
				return packet;
		return dir.getUnset();
	}
	
	enum Client implements PacketType {
		
		ABILITIES,
		ADVANCEMENTS,
		ARM_ANIMATION(NPacketPlayInArmAnimation::new),
		AUTO_RECIPE,
		BEACON,
		BEDIT,
		BOAT_MOVE,
		BLOCK_DIG(NPacketPlayInBlockDig::new),
		BLOCK_PLACE(NPacketPlayInBlockPlace::new),
		CHAT(NPacketPlayInChat::new),
		CHAT_ACK,
		CHAT_COMMAND,
		CHAT_PREVIEW,
		CHAT_SESSION_UPDATE,
		CLIENT_COMMAND,
		CLOSE_WINDOW,
		CUSTOM_PAYLOAD(NPacketPlayInCustomPayload::new),
		DIFFICULTY_CHANGE,
		DIFFICULTY_LOCK,
		ENCHANT_ITEM,
		ENTITY_ACTION(NPacketPlayInEntityAction::new),
		ENTITY_NBT_QUERY,
		FLYING(NPacketPlayInFlying::new),
		GROUND(NPacketPlayInGround::new),
		HELD_ITEM_SLOT(NPacketPlayInHeldItemSlot::new),
		ITEM_NAME,
		JIGSAW_GENERATE,
		KEEP_ALIVE(NPacketPlayInKeepAlive::new),
		LOOK(NPacketPlayInLook::new),
		PICK_ITEM,
		POSITION(NPacketPlayInPosition::new),
		POSITION_LOOK(NPacketPlayInPositionLook::new),
		RECIPE_SETTINGS,
		RECIPE_DISPLAYED,
		RESOURCE_PACK_STATUS,
		SET_COMMAND_BLOCK,
		SET_COMMAND_MINECART,
		SET_CREATIVE_SLOT(NPacketPlayInSetCreativeSlot::new),
		SET_JIGSAW,
		SETTINGS(NPacketPlayInSettings::new),
		SPECTATE,
		STEER_VEHICLE(NPacketPlayInSteerVehicle::new),
		STRUCT,
		TAB_COMPLETE,
		TELEPORT_ACCEPT(NPacketPlayInTeleportAccept::new),
		TILE_NBT_QUERY,
		TR_SEL,
		PONG(NPacketPlayInPong::new),
		UPDATE_SIGN,
		USE_ENTITY(NPacketPlayInUseEntity::new),
		USE_ITEM(NPacketPlayInUseItem::new),
		VEHICLE_MOVE,
		WINDOW_CLICK,
		UNSET;
		
		private final Callable<NPacket> fun;
		private final String packetName;
		
		Client() {
			this(NPacketPlayInUnset::new);
		}
		
		Client(Callable<NPacket> fun) {
			this.fun = fun;
			this.packetName = ChatUtils.capitalize(name());
		}
		
		@Override
		public String getPacketName() {
			return packetName;
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
		public Callable<NPacket> getPacketCreatorFunction() {
			return fun;
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.CLIENT_TO_SERVER;
		}
	}
	
	enum Server implements PacketType {

		ABILITIES,
		ADVANCEMENTS,
		ADD_VIBRATION_SIGNAL,
		ANIMATION,
		ATTACH_ENTITY,
		AUTO_RECIPE,
		BED,
		BLOCK_ACTION,
		BLOCK_BREAK,
		BLOCK_BREAK_ANIMATION(NPacketPlayOutBlockBreakAnimation::new),
		BLOCK_CHANGE(NPacketPlayOutBlockChange::new),
		BLOCK_CHANGED_ACK,
		BUNDLE,
		BOSS,
		CAMERA,
		CHAT,
		CHAT_CUSTOM_COMPLETION,
		CHAT_DELETE,
		CHAT_DISGUISED,
		CHAT_HEADER,
		CHAT_PREVIEW,
		CLEAR_TITLE,
		CLOSE_WINDOW,
		COLLECT,
		COMBAT_EVENT,
		COMBAT_END_EVENT,
		COMBAT_ENTER_EVENT,
		COMBAT_KILL_EVENT,
		COMMANDS,
		CHUNK_BIOMES,
		CUSTOM_PAYLOAD(NPacketPlayOutCustomPayload::new),
		CUSTOM_SOUND_EFFECT,
		DAMAGE_EVENT,
		ENTITY,
		ENTITY_DESTROY(NPacketPlayOutEntityDestroy::new),
		ENTITY_EFFECT(NPacketPlayOutEntityEffect::new),
		ENTITY_EQUIPMENT,
		ENTITY_HEAD_ROTATION(NPacketPlayOutEntityHeadRotation::new),
		ENTITY_METADATA,
		ENTITY_STATUS,
		ENTITY_SOUND,
		ENTITY_TELEPORT(NPacketPlayOutEntityTeleport::new),
		ENTITY_VELOCITY(NPacketPlayOutEntityVelocity::new),
		EXPERIENCE,
		EXPLOSION(NPacketPlayOutExplosion::new),
		GAME_STATE_CHANGE,
		HELD_ITEM_SLOT,
		HIT_ANIMATION,
		INITIALIZE_BORDER,
		KEEP_ALIVE(NPacketPlayOutKeepAlive::new),
		KICK_DISCONNECT,
		LIGHT_UPDATE,
		LEVEL_CHUNK_LIGHT,
		LOOK_AT,
		LOGIN,
		MAP,
		MAP_CHUNK,
		MAP_CHUNK_BULK,
		MOUNT,
		MULTI_BLOCK_CHANGE(NPacketPlayOutMultiBlockChange::new),
		NAMED_SOUND_EFFECT,
		NBT_QUERY,
		OPEN_BOOK,
		OPEN_SIGN_EDITOR,
		OPEN_WINDOW,
		OPEN_WINDOW_MERCHANT,
		OPEN_WINDOW_HORSE,
		PLAYER_INFO,
		PLAYER_INFO_REMOVE,
		PLAYER_INFO_UPDATE,
		PLAYER_LIST_HEADER_FOOTER,
		POSITION(NPacketPlayOutPosition::new),
		RECIPES,
		RECIPE_UPDATE,
		REL_ENTITY_LOOK(NPacketPlayOutRelEntityLook::new),
		REL_ENTITY_MOVE(NPacketPlayOutRelEntityMove::new),
		REL_ENTITY_MOVE_LOOK(NPacketPlayOutRelEntityMoveLook::new),
		REMOVE_ENTITY_EFFECT,
		RESOURCE_PACK_SEND,
		RESPAWN,
		SCOREBOARD_DISPLAY_OBJECTIVE,
		SCOREBOARD_OBJECTIVE,
		SCOREBOARD_SCORE,
		SCOREBOARD_TEAM,
		SERVER_DATA,
		SERVER_DIFFICULTY,
		SELECT_ADVANCEMENT_TAB,
		SET_DISPLAY_CHAT_PREVIEW,
		SET_COMPRESSION,
		SET_COOLDOWN,
		SET_SLOT,
		SET_ACTION_BAR_TEXT,
		SET_BORDER_CENTER,
		SET_BORDER_LERP_SIZE,
		SET_BORDER_SIZE,
		SET_BORDER_WARNING_DELAY,
		SET_BORDER_WARNING_DISTANCE,
		SET_SUBTITLE_TEXT,
		SET_TITLE_TEXT,
		SET_TITLE_ANIMATION,
		SIMULATION_DISTANCE,
		SPAWN_ENTITY,
		SPAWN_ENTITY_LIVING,
		SPAWN_ENTITY_EXPERIENCE_ORB,
		SPAWN_ENTITY_PAINTING,
		SPAWN_ENTITY_WEATHER,
		SPAWN_PLAYER,
		SPAWN_POSITION,
		STATISTIC,
		STOP_SOUND,
		SYSTEM_CHAT,
		TAB_COMPLETE,
		TAGS,
		TILE_ENTITY_DATA,
		TITLE,
		PING(NPacketPlayOutPing::new),
		UNLOAD_CHUNK(NPacketPlayOutUnloadChunk::new),
		UPDATE_ATTRIBUTES,
		UPDATE_ENABLED_FEATURES,
		UPDATE_ENTITY_NBT,
		UPDATE_HEALTH,
		UPDATE_SIGN,
		UPDATE_TIME,
		VEHICLE_MOVE,
		VIEW_DISTANCE,
		VIEW_CENTRE,
		WINDOW_DATA,
		WINDOW_ITEMS,
		WORLD_BORDER,
		WORLD_EVENT,
		WORLD_PARTICLES,
		UNSET;
		
		private final Callable<NPacket> fun;
		private final String packetName;

		Server() {
			this(NPacketPlayOutUnset::new);
		}
		
		Server(Callable<NPacket> fun) {
			this.fun = fun;
			this.packetName = ChatUtils.capitalize(name());
		}
		
		@Override
		public String getPacketName() {
			return packetName;
		}
		
		@Override
		public boolean isFlyingPacket() {
			return this == ENTITY || this == REL_ENTITY_LOOK || this == REL_ENTITY_MOVE || this == REL_ENTITY_MOVE_LOOK;
		}
		
		@Override
		public boolean isUnset() {
			return this == UNSET;
		}
		
		@Override
		public Callable<NPacket> getPacketCreatorFunction() {
			return fun;
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.SERVER_TO_CLIENT;
		}
	}
	
	enum Login implements PacketType {

		CUSTOM_PAYLOAD_OUT,
		CUSTOM_PAYLOAD_IN,
		DISCONNECT,
		ENCRYPTION_BEGIN_OUT,
		ENCRYPTION_BEGIN_IN,
		LISTENER_OUT,
		LISTENER_IN,
		SET_COMPRESSION,
		START,
		SUCCESS,
		UNSET;
		
		private final Callable<NPacket> fun;
		private final String packetName;
		
		Login() {
			this(NPacketLoginUnset::new);
		}
		
		Login(Callable<NPacket> fun) {
			this.fun = fun;
			this.packetName = ChatUtils.capitalize(name());
		}
		
		@Override
		public String getPacketName() {
			return packetName;
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
		public Callable<NPacket> getPacketCreatorFunction() {
			return fun;
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.LOGIN;
		}
	}
	
	enum Status implements PacketType {

		LISTENER,
		LISTENER_IN,
		LISTENER_OUT,
		PING,
		START,
		PONG,
		SERVER_INFO,
		UNSET;
		
		private final Callable<NPacket> fun;
		private final String packetName;
		
		Status() {
			this(NPacketStatusUnset::new);
		}
		
		Status(Callable<NPacket> fun) {
			this.fun = fun;
			this.packetName = ChatUtils.capitalize(name());
		}
		
		@Override
		public String getPacketName() {
			return packetName;
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
		public Callable<NPacket> getPacketCreatorFunction() {
			return fun;
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.STATUS;
		}
	}

	enum Handshake implements PacketType {
		
		IN_LISTENER(NPacketHandshakeInListener::new),
		IS_SET_PROTOCOL(NPacketHandshakeInSetProtocol::new),
		UNSET;
		
		private final Callable<NPacket> fun;
		private final String packetName;
		
		Handshake() {
			this(NPacketHandshakeUnset::new);
		}
		
		Handshake(Callable<NPacket> fun) {
			this.fun = fun;
			this.packetName = ChatUtils.capitalize(name());
		}
		
		@Override
		public String getPacketName() {
			return packetName;
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
		public Callable<NPacket> getPacketCreatorFunction() {
			return fun;
		}
		
		@Override
		public PacketDirection getDirection() {
			return PacketDirection.HANDSHAKE;
		}
	}
}
