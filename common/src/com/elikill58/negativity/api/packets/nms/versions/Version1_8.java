package com.elikill58.negativity.api.packets.nms.versions;

import java.util.Arrays;

import com.elikill58.negativity.api.packets.PacketType.Client;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.NamedVersion;

public class Version1_8 extends NamedVersion {

	public Version1_8() {
		int i = 0;
		for (Client type : Arrays.asList(Client.KEEP_ALIVE, Client.CHAT, Client.USE_ENTITY, Client.FLYING,
				Client.POSITION, Client.LOOK, Client.POSITION_LOOK, Client.BLOCK_DIG, Client.BLOCK_PLACE,
				Client.HELD_ITEM_SLOT, Client.ARM_ANIMATION, Client.ENTITY_ACTION, Client.STEER_VEHICLE,
				Client.CLOSE_WINDOW, Client.WINDOW_CLICK, Client.PONG, Client.SET_CREATIVE_SLOT, Client.ENCHANT_ITEM,
				Client.UPDATE_SIGN, Client.ABILITIES, Client.TAB_COMPLETE, Client.SETTINGS, Client.CLIENT_COMMAND,
				Client.CUSTOM_PAYLOAD, Client.SPECTATE, Client.RESOURCE_PACK_STATUS)) {
			playIn.put(i++, type);
		}
		i = 0;
		for (Server type : Arrays.asList(Server.KEEP_ALIVE, Server.LOGIN, Server.CHAT, Server.UPDATE_TIME,
				Server.ENTITY_EQUIPMENT, Server.SPAWN_POSITION, Server.UPDATE_HEALTH, Server.RESPAWN, Server.POSITION,
				Server.HELD_ITEM_SLOT, Server.BED, Server.ANIMATION, Server.SPAWN_PLAYER, Server.COLLECT,
				Server.SPAWN_ENTITY, Server.SPAWN_ENTITY, Server.SPAWN_ENTITY_PAINTING,
				Server.SPAWN_ENTITY_EXPERIENCE_ORB, Server.ENTITY_VELOCITY, Server.ENTITY_DESTROY, Server.ENTITY,
				Server.REL_ENTITY_MOVE, Server.REL_ENTITY_LOOK, Server.REL_ENTITY_MOVE_LOOK, Server.ENTITY_TELEPORT,
				Server.ENTITY_HEAD_ROTATION, Server.ENTITY_STATUS, Server.ATTACH_ENTITY, Server.ENTITY_METADATA,
				Server.ENTITY_EFFECT, Server.REMOVE_ENTITY_EFFECT, Server.EXPERIENCE, Server.UPDATE_ATTRIBUTES,
				Server.MAP_CHUNK, Server.MULTI_BLOCK_CHANGE, Server.BLOCK_CHANGE, Server.BLOCK_ACTION,
				Server.BLOCK_BREAK_ANIMATION, Server.MAP_CHUNK_BULK, Server.EXPLOSION, Server.WORLD_EVENT,
				Server.NAMED_SOUND_EFFECT, Server.WORLD_PARTICLES, Server.GAME_STATE_CHANGE,
				Server.SPAWN_ENTITY_WEATHER, Server.OPEN_WINDOW, Server.CLOSE_WINDOW, Server.SET_SLOT,
				Server.WINDOW_ITEMS, Server.WINDOW_DATA, Server.PING, Server.UPDATE_SIGN, Server.MAP,
				Server.TILE_ENTITY_DATA, Server.OPEN_SIGN_EDITOR, Server.STATISTIC, Server.PLAYER_INFO,
				Server.ABILITIES, Server.TAB_COMPLETE, Server.SCOREBOARD_OBJECTIVE, Server.SCOREBOARD_SCORE,
				Server.SCOREBOARD_DISPLAY_OBJECTIVE, Server.SCOREBOARD_TEAM, Server.CUSTOM_PAYLOAD,
				Server.KICK_DISCONNECT, Server.SERVER_DIFFICULTY, Server.COMBAT_EVENT, Server.CAMERA,
				Server.WORLD_BORDER, Server.TITLE, Server.SET_COMPRESSION, Server.PLAYER_LIST_HEADER_FOOTER,
				Server.RESOURCE_PACK_SEND, Server.UPDATE_ENTITY_NBT)) {
			playOut.put(i++, type);
		}
	}
}
