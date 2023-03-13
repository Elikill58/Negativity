package com.elikill58.negativity.api.packets.nms.versions;

import java.util.Arrays;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.PacketType.Client;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.NamedVersion;

public class Version1_12_2 extends NamedVersion {

	public Version1_12_2() {
		super("1.12.2");
		int i = 0;
		for (Client type : Arrays.asList(Client.TELEPORT_ACCEPT, Client.TAB_COMPLETE, Client.CHAT, Client.CLIENT_COMMAND, Client.SETTINGS, Client.PONG, Client.ENCHANT_ITEM,
				Client.WINDOW_CLICK, Client.CLOSE_WINDOW, Client.CUSTOM_PAYLOAD, Client.USE_ENTITY, Client.KEEP_ALIVE, Client.FLYING, Client.POSITION, Client.POSITION_LOOK, Client.LOOK,
				Client.VEHICLE_MOVE, Client.BOAT_MOVE, Client.AUTO_RECIPE, Client.ABILITIES, Client.BLOCK_DIG, Client.ENTITY_ACTION, Client.STEER_VEHICLE, Client.RECIPE_DISPLAYED, Client.RESOURCE_PACK_STATUS,
				Client.ADVANCEMENTS, Client.HELD_ITEM_SLOT, Client.SET_CREATIVE_SLOT, Client.UPDATE_SIGN, Client.ARM_ANIMATION, Client.SPECTATE, Client.BLOCK_PLACE, Client.USE_ITEM)) {
			playIn.put(i++, type);
		}
		i = 0;
		for (Server type : Arrays.asList(Server.SPAWN_ENTITY, Server.SPAWN_ENTITY_EXPERIENCE_ORB, Server.SPAWN_ENTITY_WEATHER, Server.SPAWN_ENTITY_LIVING, Server.SPAWN_ENTITY_PAINTING,
				Server.SPAWN_PLAYER, Server.ANIMATION, Server.STATISTIC, Server.BLOCK_BREAK_ANIMATION, Server.TILE_ENTITY_DATA, Server.BLOCK_ACTION, Server.BLOCK_CHANGE, Server.BOSS,
				Server.SERVER_DIFFICULTY, Server.TAB_COMPLETE, Server.CHAT, Server.MULTI_BLOCK_CHANGE, Server.PING, Server.CLOSE_WINDOW, Server.OPEN_WINDOW, Server.WINDOW_ITEMS,
				Server.WINDOW_DATA, Server.SET_SLOT, Server.SET_COOLDOWN, Server.CUSTOM_PAYLOAD, Server.CUSTOM_SOUND_EFFECT, Server.KICK_DISCONNECT, Server.ENTITY_STATUS, Server.EXPLOSION,
				Server.UNLOAD_CHUNK, Server.GAME_STATE_CHANGE, Server.KEEP_ALIVE, Server.MAP_CHUNK, Server.WORLD_EVENT, Server.WORLD_PARTICLES, Server.LOGIN, Server.MAP, Server.ENTITY,
				Server.REL_ENTITY_MOVE, Server.REL_ENTITY_MOVE_LOOK, Server.REL_ENTITY_LOOK, Server.VEHICLE_MOVE, Server.OPEN_SIGN_EDITOR, Server.RECIPE_UPDATE, Server.ABILITIES, Server.COMBAT_EVENT,
				Server.PLAYER_INFO, Server.POSITION, Server.BED, Server.RECIPES, Server.ENTITY_DESTROY, Server.REMOVE_ENTITY_EFFECT, Server.RESOURCE_PACK_SEND, Server.RESPAWN,
				Server.ENTITY_HEAD_ROTATION, Server.SELECT_ADVANCEMENT_TAB, Server.WORLD_BORDER, Server.CAMERA, Server.HELD_ITEM_SLOT, Server.SCOREBOARD_DISPLAY_OBJECTIVE,
				Server.ENTITY_METADATA, Server.ATTACH_ENTITY, Server.ENTITY_VELOCITY, Server.ENTITY_EQUIPMENT, Server.EXPERIENCE, Server.UPDATE_HEALTH, Server.SCOREBOARD_OBJECTIVE,
				Server.MOUNT, Server.SCOREBOARD_TEAM, Server.SCOREBOARD_SCORE, Server.SPAWN_POSITION, Server.UPDATE_TIME, Server.TITLE, Server.NAMED_SOUND_EFFECT,
				Server.PLAYER_LIST_HEADER_FOOTER, Server.COLLECT, Server.ENTITY_TELEPORT, Server.ADVANCEMENTS, Server.UPDATE_ATTRIBUTES, Server.ENTITY_EFFECT)) {
			playOut.put(i++, type);
		}

		entityTypes.put(1, EntityType.DROPPED_ITEM);
		entityTypes.put(2, EntityType.EXPERIENCE_ORB);
		entityTypes.put(3, EntityType.AREA_EFFECT_CLOUD);
		entityTypes.put(4, EntityType.ELDER_GUARDIAN);
		entityTypes.put(5, EntityType.WITHER_SKELETON);
		entityTypes.put(6, EntityType.STRAY);
		entityTypes.put(7, EntityType.EGG);
		entityTypes.put(8, EntityType.LEASH_KNOT);
		entityTypes.put(9, EntityType.PAINTING);
		entityTypes.put(10, EntityType.ARROW);
		entityTypes.put(11, EntityType.SNOW_BALL);
		entityTypes.put(12, EntityType.FIREBALL);
		entityTypes.put(13, EntityType.SMALL_FIREBALL);
		entityTypes.put(14, EntityType.ENDER_PEARL);
		entityTypes.put(15, EntityType.EYE_OF_ENDER);
		entityTypes.put(16, EntityType.SPLASH_POTION);
		entityTypes.put(17, EntityType.EXP_BOTTLE);
		entityTypes.put(18, EntityType.ITEM_FRAME);
		entityTypes.put(19, EntityType.WITHER_SKULL);
		entityTypes.put(20, EntityType.PRIMED_TNT);
		entityTypes.put(21, EntityType.FALLING_BLOCK);
		entityTypes.put(22, EntityType.FIREWORK);
		entityTypes.put(23, EntityType.HUSK);
		entityTypes.put(24, EntityType.SPECTRAL_ARROW);
		entityTypes.put(25, EntityType.SHULKER_BULLET);
		entityTypes.put(26, EntityType.DRAGON_FIREBALL);
		entityTypes.put(27, EntityType.ZOMBIE_VILLAGER);
		entityTypes.put(28, EntityType.SKELETON_HORSE);
		entityTypes.put(29, EntityType.ZOMBIE_HORSE);
		entityTypes.put(30, EntityType.ARMOR_STAND);
		entityTypes.put(31, EntityType.DONKEY);
		entityTypes.put(32, EntityType.MULE);
		entityTypes.put(33, EntityType.EVOKER_FANGS);
		entityTypes.put(34, EntityType.EVOKER);
		entityTypes.put(35, EntityType.VEX);
		entityTypes.put(36, EntityType.VINDICATOR);
		entityTypes.put(36, EntityType.ILLUSIONER);
		entityTypes.put(40, EntityType.MINECART_COMMAND_BLOCK);
		entityTypes.put(41, EntityType.BOAT);
		entityTypes.put(42, EntityType.MINECART);
		entityTypes.put(43, EntityType.MINECART_CHEST);
		entityTypes.put(44, EntityType.MINECART_FURNACE);
		entityTypes.put(45, EntityType.MINECART_TNT);
		entityTypes.put(46, EntityType.MINECART_HOPPER);
		entityTypes.put(47, EntityType.MINECART_MOB_SPAWNER);
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
		entityTypes.put(69, EntityType.SHULKER);
		entityTypes.put(90, EntityType.PIG);
		entityTypes.put(91, EntityType.SHEEP);
		entityTypes.put(92, EntityType.COW);
		entityTypes.put(93, EntityType.CHICKEN);
		entityTypes.put(94, EntityType.SQUID);
		entityTypes.put(95, EntityType.WOLF);
		entityTypes.put(96, EntityType.MOOSHROOM);
		entityTypes.put(97, EntityType.SNOW_GOLEM);
		entityTypes.put(98, EntityType.OCELOT);
		entityTypes.put(99, EntityType.IRON_GOLEM);
		entityTypes.put(100, EntityType.HORSE);
		entityTypes.put(101, EntityType.RABBIT);
		entityTypes.put(102, EntityType.POLAR_BEAR);
		entityTypes.put(103, EntityType.LLAMA);
		entityTypes.put(104, EntityType.LLAMA_SPIT);
		entityTypes.put(105, EntityType.PARROT);
		entityTypes.put(120, EntityType.VILLAGER);
		entityTypes.put(200, EntityType.ENDER_CRYSTAL);
		
		log();
	}

	@Override
	public Material getMaterial(int id) {
		return PreFlattening.get(id);
	}
}
