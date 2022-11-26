package com.elikill58.negativity.api.packets.nms.versions;

import java.util.Arrays;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.PacketType.*;
import com.elikill58.negativity.api.packets.nms.NamedVersion;

public class Version1_8 extends NamedVersion {

	public Version1_8() {
		int i = 0;
		for (Client type : Arrays.asList(Client.KEEP_ALIVE, Client.CHAT, Client.USE_ENTITY, Client.FLYING, Client.POSITION, Client.LOOK, Client.POSITION_LOOK, Client.BLOCK_DIG,
				Client.BLOCK_PLACE, Client.HELD_ITEM_SLOT, Client.ARM_ANIMATION, Client.ENTITY_ACTION, Client.STEER_VEHICLE, Client.CLOSE_WINDOW, Client.WINDOW_CLICK, Client.PONG,
				Client.SET_CREATIVE_SLOT, Client.ENCHANT_ITEM, Client.UPDATE_SIGN, Client.ABILITIES, Client.TAB_COMPLETE, Client.SETTINGS, Client.CLIENT_COMMAND, Client.CUSTOM_PAYLOAD,
				Client.SPECTATE, Client.RESOURCE_PACK_STATUS)) {
			playIn.put(i++, type);
		}
		i = 0;
		for (Server type : Arrays.asList(Server.KEEP_ALIVE, Server.LOGIN, Server.CHAT, Server.UPDATE_TIME, Server.ENTITY_EQUIPMENT, Server.SPAWN_POSITION, Server.UPDATE_HEALTH,
				Server.RESPAWN, Server.POSITION, Server.HELD_ITEM_SLOT, Server.BED, Server.ANIMATION, Server.SPAWN_PLAYER, Server.COLLECT, Server.SPAWN_ENTITY, Server.SPAWN_ENTITY,
				Server.SPAWN_ENTITY_PAINTING, Server.SPAWN_ENTITY_EXPERIENCE_ORB, Server.ENTITY_VELOCITY, Server.ENTITY_DESTROY, Server.ENTITY, Server.REL_ENTITY_MOVE,
				Server.REL_ENTITY_LOOK, Server.REL_ENTITY_MOVE_LOOK, Server.ENTITY_TELEPORT, Server.ENTITY_HEAD_ROTATION, Server.ENTITY_STATUS, Server.ATTACH_ENTITY, Server.ENTITY_METADATA,
				Server.ENTITY_EFFECT, Server.REMOVE_ENTITY_EFFECT, Server.EXPERIENCE, Server.UPDATE_ATTRIBUTES, Server.MAP_CHUNK, Server.MULTI_BLOCK_CHANGE, Server.BLOCK_CHANGE,
				Server.BLOCK_ACTION, Server.BLOCK_BREAK_ANIMATION, Server.MAP_CHUNK_BULK, Server.EXPLOSION, Server.WORLD_EVENT, Server.NAMED_SOUND_EFFECT, Server.WORLD_PARTICLES,
				Server.GAME_STATE_CHANGE, Server.SPAWN_ENTITY_WEATHER, Server.OPEN_WINDOW, Server.CLOSE_WINDOW, Server.SET_SLOT, Server.WINDOW_ITEMS, Server.WINDOW_DATA, Server.PING,
				Server.UPDATE_SIGN, Server.MAP, Server.TILE_ENTITY_DATA, Server.OPEN_SIGN_EDITOR, Server.STATISTIC, Server.PLAYER_INFO, Server.ABILITIES, Server.TAB_COMPLETE,
				Server.SCOREBOARD_OBJECTIVE, Server.SCOREBOARD_SCORE, Server.SCOREBOARD_DISPLAY_OBJECTIVE, Server.SCOREBOARD_TEAM, Server.CUSTOM_PAYLOAD, Server.KICK_DISCONNECT,
				Server.SERVER_DIFFICULTY, Server.COMBAT_EVENT, Server.CAMERA, Server.WORLD_BORDER, Server.TITLE, Server.SET_COMPRESSION, Server.PLAYER_LIST_HEADER_FOOTER,
				Server.RESOURCE_PACK_SEND, Server.UPDATE_ENTITY_NBT)) {
			playOut.put(i++, type);
		}

		entityTypes.put(1, EntityType.DROPPED_ITEM);
		entityTypes.put(2, EntityType.EXPERIENCE_ORB);
		entityTypes.put(7, EntityType.EGG);
		entityTypes.put(8, EntityType.LEASH_KNOT);
		entityTypes.put(9, EntityType.PAINTING);
		entityTypes.put(10, EntityType.ARROW);
		entityTypes.put(11, EntityType.SNOW_BALL);
		entityTypes.put(12, EntityType.FIREBALL);
		entityTypes.put(13, EntityType.SMALL_FIREBALL);
		entityTypes.put(14, EntityType.ENDER_PEARL);
		entityTypes.put(15, EntityType.ENDER_SIGNAL);
		entityTypes.put(16, EntityType.SPLASH_POTION);
		entityTypes.put(17, EntityType.EXP_BOTTLE);
		entityTypes.put(18, EntityType.ITEM_FRAME);
		entityTypes.put(19, EntityType.WITHER_SKULL);
		entityTypes.put(20, EntityType.PRIMED_TNT);
		entityTypes.put(21, EntityType.FALLING_BLOCK);
		entityTypes.put(22, EntityType.FIREWORK);
		entityTypes.put(30, EntityType.ARMOR_STAND);
		entityTypes.put(40, EntityType.MINECART_COMMAND_BLOCK);
		entityTypes.put(41, EntityType.BOAT);
		entityTypes.put(42, EntityType.MINECART);
		entityTypes.put(43, EntityType.MINECART_CHEST);
		entityTypes.put(44, EntityType.MINECART_FURNACE);
		entityTypes.put(45, EntityType.MINECART_TNT);
		entityTypes.put(46, EntityType.MINECART_HOPPER);
		entityTypes.put(47, EntityType.MINECART_MOB_SPAWNER);
		entityTypes.put(48, EntityType.UNKNOWN); // "mob" as spigot said
		entityTypes.put(49, EntityType.UNKNOWN); // "monster" as spigot said
		entityTypes.put(50, EntityType.CREEPER);
		entityTypes.put(51, EntityType.SKELETON);
		entityTypes.put(52, EntityType.SPIDER);
		entityTypes.put(53, EntityType.GIANT);
		entityTypes.put(54, EntityType.ZOMBIE);
		entityTypes.put(55, EntityType.SLIME);
		entityTypes.put(56, EntityType.GHAST);
		entityTypes.put(57, EntityType.PIG_ZOMBIE);
		entityTypes.put(58, EntityType.ENDERMAN);
		entityTypes.put(59, EntityType.CAVE_SPIDER);
		entityTypes.put(60, EntityType.SILVERFISH);
		entityTypes.put(61, EntityType.BLAZE);
		entityTypes.put(62, EntityType.MAGMA_CUBE);
		entityTypes.put(63, EntityType.ENDER_DRAGON);
		entityTypes.put(64, EntityType.WITHER);
		entityTypes.put(65, EntityType.BAT);
		entityTypes.put(66, EntityType.WITCH);
		entityTypes.put(67, EntityType.ENDERMITE);
		entityTypes.put(68, EntityType.GUARDIAN);
		entityTypes.put(90, EntityType.PIG);
		entityTypes.put(91, EntityType.SHEEP);
		entityTypes.put(92, EntityType.COW);
		entityTypes.put(93, EntityType.CHICKEN);
		entityTypes.put(94, EntityType.SQUID);
		entityTypes.put(95, EntityType.WOLF);
		entityTypes.put(96, EntityType.MUSHROOM_COW);
		entityTypes.put(97, EntityType.SNOW_GOLEM);
		entityTypes.put(98, EntityType.OCELOT);
		entityTypes.put(99, EntityType.IRON_GOLEM);
		entityTypes.put(100, EntityType.HORSE);
		entityTypes.put(101, EntityType.RABBIT);
		entityTypes.put(120, EntityType.VILLAGER);
		entityTypes.put(200, EntityType.ENDER_CRYSTAL);
	}

	@Override
	public Material getMaterial(int id) {
		return PreFlattening.get(id);
	}
}
