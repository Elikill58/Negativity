package com.elikill58.negativity.api.packets.nms.versions;

import java.util.Arrays;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.packets.PacketType.Client;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.NamedVersion;

public class Version1_16 extends NamedVersion {

	public Version1_16() {
		super("1.16");
		int i = 0;
		for (Client type : Arrays.asList(Client.TELEPORT_ACCEPT, Client.TILE_NBT_QUERY, Client.DIFFICULTY_CHANGE, Client.CHAT, Client.CLIENT_COMMAND, Client.SETTINGS, Client.TAB_COMPLETE,
				Client.PONG, Client.ENCHANT_ITEM, Client.WINDOW_CLICK, Client.CLOSE_WINDOW, Client.CUSTOM_PAYLOAD, Client.BEDIT, Client.ENTITY_NBT_QUERY, Client.USE_ENTITY,
				Client.JIGSAW_GENERATE, Client.KEEP_ALIVE, Client.DIFFICULTY_LOCK, Client.POSITION, Client.POSITION_LOOK, Client.LOOK, Client.FLYING, Client.VEHICLE_MOVE, Client.BOAT_MOVE,
				Client.PICK_ITEM, Client.AUTO_RECIPE, Client.ABILITIES, Client.BLOCK_DIG, Client.ENTITY_ACTION, Client.STEER_VEHICLE, Client.RECIPE_SETTINGS, Client.RECIPE_DISPLAYED,
				Client.ITEM_NAME, Client.RESOURCE_PACK_STATUS, Client.ADVANCEMENTS, Client.TR_SEL, Client.BEACON, Client.HELD_ITEM_SLOT, Client.SET_COMMAND_BLOCK,
				Client.SET_COMMAND_MINECART, Client.SET_CREATIVE_SLOT, Client.SET_JIGSAW, Client.STRUCT, Client.UPDATE_SIGN, Client.ARM_ANIMATION, Client.SPECTATE, Client.BLOCK_PLACE,
				Client.USE_ITEM)) {
			playIn.put(i++, type);
		}
		i = 0;
		for (Server type : Arrays.asList(Server.SPAWN_ENTITY, Server.SPAWN_ENTITY_EXPERIENCE_ORB, Server.SPAWN_ENTITY_LIVING, Server.SPAWN_ENTITY_PAINTING, Server.SPAWN_PLAYER,
				Server.ANIMATION, Server.STATISTIC, Server.BLOCK_BREAK, Server.BLOCK_BREAK_ANIMATION, Server.TILE_ENTITY_DATA, Server.BLOCK_ACTION, Server.BLOCK_CHANGE, Server.BOSS,
				Server.SERVER_DIFFICULTY, Server.CHAT, Server.TAB_COMPLETE, Server.COMMANDS, Server.PING, Server.CLOSE_WINDOW, Server.WINDOW_ITEMS, Server.WINDOW_DATA, Server.SET_SLOT,
				Server.SET_COOLDOWN, Server.CUSTOM_PAYLOAD, Server.CUSTOM_SOUND_EFFECT, Server.KICK_DISCONNECT, Server.ENTITY_STATUS, Server.EXPLOSION, Server.UNLOAD_CHUNK,
				Server.GAME_STATE_CHANGE, Server.OPEN_WINDOW_HORSE, Server.KEEP_ALIVE, Server.MAP_CHUNK, Server.WORLD_EVENT, Server.WORLD_PARTICLES, Server.LIGHT_UPDATE, Server.LOGIN,
				Server.MAP, Server.OPEN_WINDOW_MERCHANT, Server.REL_ENTITY_MOVE, Server.REL_ENTITY_MOVE_LOOK, Server.REL_ENTITY_LOOK, Server.ENTITY, Server.VEHICLE_MOVE, Server.OPEN_BOOK,
				Server.OPEN_WINDOW, Server.OPEN_SIGN_EDITOR, Server.AUTO_RECIPE, Server.ABILITIES, Server.COMBAT_EVENT, Server.PLAYER_INFO, Server.LOOK_AT, Server.POSITION, Server.RECIPES,
				Server.ENTITY_DESTROY, Server.REMOVE_ENTITY_EFFECT, Server.RESOURCE_PACK_SEND, Server.RESPAWN, Server.ENTITY_HEAD_ROTATION, Server.MULTI_BLOCK_CHANGE,
				Server.SELECT_ADVANCEMENT_TAB, Server.WORLD_BORDER, Server.CAMERA, Server.HELD_ITEM_SLOT, Server.VIEW_CENTRE, Server.VIEW_DISTANCE, Server.SPAWN_POSITION,
				Server.SCOREBOARD_DISPLAY_OBJECTIVE, Server.ENTITY_METADATA, Server.ATTACH_ENTITY, Server.ENTITY_VELOCITY, Server.ENTITY_EQUIPMENT, Server.EXPERIENCE, Server.UPDATE_HEALTH,
				Server.SCOREBOARD_OBJECTIVE, Server.MOUNT, Server.SCOREBOARD_TEAM, Server.SCOREBOARD_SCORE, Server.UPDATE_TIME, Server.TITLE, Server.ENTITY_SOUND, Server.NAMED_SOUND_EFFECT,
				Server.STOP_SOUND, Server.PLAYER_LIST_HEADER_FOOTER, Server.NBT_QUERY, Server.COLLECT, Server.ENTITY_TELEPORT, Server.ADVANCEMENTS, Server.UPDATE_ATTRIBUTES,
				Server.ENTITY_EFFECT, Server.RECIPE_UPDATE, Server.TAGS)) {
			playOut.put(i++, type);
		}
		i = 0;
		for (EntityType type : Arrays.asList(EntityType.AREA_EFFECT_CLOUD, EntityType.ARMOR_STAND, EntityType.ARROW, EntityType.BAT, EntityType.BEE, EntityType.BLAZE, EntityType.BOAT,
				EntityType.CAT, EntityType.CAVE_SPIDER, EntityType.CHICKEN, EntityType.COD, EntityType.COW, EntityType.CREEPER, EntityType.DOLPHIN, EntityType.DONKEY,
				EntityType.DRAGON_FIREBALL, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDER_CRYSTAL, EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE,
				EntityType.EVOKER, EntityType.EVOKER_FANGS, EntityType.EXPERIENCE_ORB, EntityType.EYE_OF_ENDER, EntityType.FALLING_BLOCK, EntityType.FIREWORK, EntityType.FOX,
				EntityType.GHAST, EntityType.GIANT, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HORSE, EntityType.HUSK, EntityType.ILLUSIONER, EntityType.IRON_GOLEM,
				EntityType.DROPPED_ITEM, EntityType.ITEM_FRAME, EntityType.FIREBALL, EntityType.LEASH_KNOT, EntityType.LIGHTNING, EntityType.LLAMA, EntityType.LLAMA_SPIT,
				EntityType.MAGMA_CUBE, EntityType.MINECART, EntityType.MINECART_CHEST, EntityType.MINECART_COMMAND_BLOCK, EntityType.MINECART_FURNACE, EntityType.MINECART_HOPPER,
				EntityType.MINECART_MOB_SPAWNER, EntityType.MINECART_TNT, EntityType.MULE, EntityType.MOOSHROOM, EntityType.OCELOT, EntityType.PAINTING, EntityType.PANDA, EntityType.PARROT,
				EntityType.PHANTOM, EntityType.PIG, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.POLAR_BEAR, EntityType.PRIMED_TNT, EntityType.PUFFER_FISH,
				EntityType.RABBIT, EntityType.RAVAGER, EntityType.SALMON, EntityType.SHEEP, EntityType.SHULKER, EntityType.SHULKER_BULLET, EntityType.SILVERFISH, EntityType.SKELETON,
				EntityType.SKELETON_HORSE, EntityType.SLIME, EntityType.SMALL_FIREBALL, EntityType.SNOW_GOLEM, EntityType.SNOW_BALL, EntityType.SPECTRAL_ARROW, EntityType.SPIDER,
				EntityType.SQUID, EntityType.STRAY, EntityType.STRIDER, EntityType.EGG, EntityType.ENDER_PEARL, EntityType.EXP_BOTTLE, EntityType.SPLASH_POTION, EntityType.TRIDENT,
				EntityType.LLAMA_TRADER, EntityType.TROPICAL_FISH, EntityType.TURTLE, EntityType.VEX, EntityType.VILLAGER, EntityType.VINDICATOR, EntityType.WANDERING_TRADER,
				EntityType.WITCH, EntityType.WITHER, EntityType.WITHER_SKELETON, EntityType.WITHER_SKULL, EntityType.WOLF, EntityType.ZOGLIN, EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE,
				EntityType.ZOMBIE_VILLAGER, EntityType.PIGLIN, EntityType.PLAYER, EntityType.FISHING_HOOK)) {
			entityTypes.put(i++, type);
		}
		loadPostFlattening("/versions/v1_16/");
		
		log();
	}
}
