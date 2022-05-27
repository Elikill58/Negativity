package com.elikill58.negativity.sponge8.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.model.PluginDependency;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge8.Messages;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Utils {

	//public static String coloredMessage(String msg) {
	//	return LegacyComponentSerializer.legacySection().deserialize(msg);
	//}

	@Nullable
	public static ServerPlayer getFirstOnlinePlayer() {
		Collection<ServerPlayer> onlinePlayers = Sponge.server().onlinePlayers();
		return onlinePlayers.isEmpty() ? null : onlinePlayers.iterator().next();
	}

	public static HashMap<String, String> getModsNameVersionFromMessage(String modName) {
		HashMap<String, String> mods = new HashMap<String, String>();
		String caractere = "abcdefghijklmnopqrstuvwxyz0123456789.,_-;";
		List<String> listCaracters = Arrays.asList(caractere.split(""));
		String s = "", name = "", version = "unknow";
		boolean isVersion = false, checkVersion = false;
		for (String parts : modName.split("")) {
			if (listCaracters.contains(parts.toLowerCase(Locale.ROOT))) {
				if (checkVersion) {
					if (UniversalUtils.isInteger(parts)) {
						checkVersion = false;
						isVersion = true;
					}
				}
				s += parts;
			} else {
				if (isVersion) {
					version = s;
					isVersion = false;
				} else {
					name = s;
					checkVersion = true;
				}
				if (!(s.isEmpty() || s.equalsIgnoreCase("")) && !version.equalsIgnoreCase("unknow")) {
					mods.put(name, version);
					version = "unknow";
					name = "";
				}
				s = "";
			}
		}
		return mods;
	}

	public static float getPlayerHeadHeight(Player p) {
		float f = 1.62F;
		if (p.get(Keys.IS_SLEEPING).orElse(false)) {
			f = 0.2F;
		}
		if (p.get(Keys.IS_SNEAKING).orElse(false)) {
			f -= 0.08F;
		}
		return f;
	}

	public static float getEntityHeadHeight(Entity et) {
		return 1.74F;
	}

	public static ItemStack getMcLeaksIndicator(Player player, NegativityPlayer nPlayer) {
		boolean usesMcLeaks = nPlayer.getAccount().isMcLeaks();
		ItemStack indicator = ItemStack.of(usesMcLeaks ? ItemTypes.RED_WOOL : ItemTypes.LIME_WOOL);
		indicator.offer(Keys.CUSTOM_NAME, Messages.getMessage(player, "inventory.main.mcleaks_indicator." + (usesMcLeaks ? "positive" : "negative")));
		indicator.offerSingle(Keys.LORE, Messages.getMessage(player, "inventory.main.mcleaks_indicator.description"));
		return indicator;
	}
	
	public static boolean dependsOn(PluginContainer plugin, String dependencyId) {
		for (PluginDependency dependency : plugin.metadata().dependencies()) {
			if (dependency.id().equalsIgnoreCase(dependencyId)) {
				return true;
			}
		}
		return false;
	}
	
	@Nullable
	public static ItemType getItemType(Material material) {
		Object platformType = material.getDefault();
		if (platformType instanceof ItemType) {
			return (ItemType) platformType;
		} else if (platformType instanceof BlockType) {
			BlockType bt = ((BlockType) platformType);
			return bt.item().orElseGet(() -> Sponge.game().registry(RegistryTypes.ITEM_TYPE).value(getKey(bt)));
		}
		return null;
	}
	
	@Nullable
	public static BlockType getBlockType(Material material) {
		Object platformType = material.getDefault();
		if (platformType instanceof BlockType) {
			return (BlockType) platformType;
		} else if (platformType instanceof ItemType) {
			return ((ItemType) platformType).block().orElse(null);
		}
		return null;
	}
	
	public static ResourceKey getKey(EntityType<?> entityType) {
		return RegistryTypes.ENTITY_TYPE.keyFor(Sponge.game(), entityType);
	}
	
	public static ResourceKey getKey(BlockType blockType) {
		return RegistryTypes.BLOCK_TYPE.keyFor(Sponge.game(), blockType);
	}
	
	public static ResourceKey getKey(ItemType itemType) {
		return RegistryTypes.ITEM_TYPE.keyFor(Sponge.game(), itemType);
	}
	
	public static ResourceKey getKey(PotionEffectType potionEffectType) {
		return RegistryTypes.POTION_EFFECT_TYPE.keyFor(Sponge.game(), potionEffectType);
	}
	
	public static ResourceKey getKey(EnchantmentType enchantmentType) {
		return RegistryTypes.ENCHANTMENT_TYPE.keyFor(Sponge.game(), enchantmentType);
	}
	
	public static ResourceKey getKey(Difficulty difficulty) {
		return RegistryTypes.DIFFICULTY.keyFor(Sponge.game(), difficulty);
	}
}
