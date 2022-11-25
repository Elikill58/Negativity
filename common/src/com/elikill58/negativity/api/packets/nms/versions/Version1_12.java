package com.elikill58.negativity.api.packets.nms.versions;

import java.util.Arrays;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.PacketType.Client;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.NamedVersion;

public class Version1_12 extends NamedVersion {

	public Version1_12() {
		int i = 0;
		for (Client type : Arrays.asList(Client.TELEPORT_ACCEPT, Client.TAB_COMPLETE, Client.CHAT,
				Client.CLIENT_COMMAND, Client.SETTINGS, Client.PONG, Client.ENCHANT_ITEM, Client.WINDOW_CLICK,
				Client.CLOSE_WINDOW, Client.CUSTOM_PAYLOAD, Client.USE_ENTITY, Client.KEEP_ALIVE, Client.FLYING,
				Client.POSITION, Client.POSITION_LOOK, Client.LOOK, Client.VEHICLE_MOVE, Client.BOAT_MOVE,
				Client.ABILITIES, Client.BLOCK_DIG, Client.ENTITY_ACTION, Client.STEER_VEHICLE, Client.RECIPE_DISPLAYED,
				Client.RESOURCE_PACK_STATUS, Client.ADVANCEMENTS, Client.HELD_ITEM_SLOT, Client.SET_CREATIVE_SLOT,
				Client.UPDATE_SIGN, Client.ARM_ANIMATION, Client.SPECTATE, Client.BLOCK_PLACE, Client.USE_ITEM)) {
			playIn.put(i++, type);
		}
		i = 0;
		for (Server type : Arrays.asList(Server.SPAWN_ENTITY, Server.SPAWN_ENTITY_EXPERIENCE_ORB,
				Server.SPAWN_ENTITY_WEATHER, Server.SPAWN_ENTITY_LIVING, Server.SPAWN_ENTITY_PAINTING,
				Server.SPAWN_PLAYER, Server.ANIMATION, Server.STATISTIC, Server.BLOCK_BREAK_ANIMATION,
				Server.TILE_ENTITY_DATA, Server.BLOCK_ACTION, Server.BLOCK_CHANGE, Server.BOSS,
				Server.SERVER_DIFFICULTY, Server.TAB_COMPLETE, Server.CHAT, Server.MULTI_BLOCK_CHANGE, Server.PING,
				Server.CLOSE_WINDOW, Server.OPEN_WINDOW, Server.WINDOW_ITEMS, Server.WINDOW_DATA, Server.SET_SLOT,
				Server.SET_COOLDOWN, Server.CUSTOM_PAYLOAD, Server.CUSTOM_SOUND_EFFECT, Server.KICK_DISCONNECT,
				Server.ENTITY_STATUS, Server.EXPLOSION, Server.UNLOAD_CHUNK, Server.GAME_STATE_CHANGE,
				Server.KEEP_ALIVE, Server.MAP_CHUNK, Server.WORLD_EVENT, Server.WORLD_PARTICLES, Server.LOGIN,
				Server.MAP, Server.ENTITY, Server.REL_ENTITY_MOVE, Server.REL_ENTITY_MOVE_LOOK, Server.REL_ENTITY_LOOK,
				Server.VEHICLE_MOVE, Server.OPEN_SIGN_EDITOR, Server.ABILITIES, Server.COMBAT_EVENT, Server.PLAYER_INFO,
				Server.POSITION, Server.BED, Server.RECIPES, Server.ENTITY_DESTROY, Server.REMOVE_ENTITY_EFFECT,
				Server.RESOURCE_PACK_SEND, Server.RESPAWN, Server.ENTITY_HEAD_ROTATION, Server.SELECT_ADVANCEMENT_TAB,
				Server.WORLD_BORDER, Server.CAMERA, Server.HELD_ITEM_SLOT, Server.SCOREBOARD_DISPLAY_OBJECTIVE,
				Server.ENTITY_METADATA, Server.ATTACH_ENTITY, Server.ENTITY_VELOCITY, Server.ENTITY_EQUIPMENT,
				Server.EXPERIENCE, Server.UPDATE_HEALTH, Server.SCOREBOARD_OBJECTIVE, Server.MOUNT,
				Server.SCOREBOARD_TEAM, Server.SCOREBOARD_SCORE, Server.SPAWN_POSITION, Server.UPDATE_TIME,
				Server.TITLE, Server.NAMED_SOUND_EFFECT, Server.PLAYER_LIST_HEADER_FOOTER, Server.COLLECT,
				Server.ENTITY_TELEPORT, Server.ADVANCEMENTS, Server.UPDATE_ATTRIBUTES, Server.ENTITY_EFFECT)) {
			playOut.put(i++, type);
		}
	}
	
	@Override
	public Material getMaterial(int id) {
		return PreFlattening.get(id);
	}
}
