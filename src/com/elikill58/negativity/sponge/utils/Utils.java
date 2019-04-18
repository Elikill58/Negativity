package com.elikill58.negativity.sponge.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.UniversalUtils;

public class Utils {

	public static int getMultipleOf(int i, int multiple, int more) {
		while (i % multiple != 0)
			i += more;
		return i;
	}

	public static String coloredMessage(String msg) {
		return TextSerializers.FORMATTING_CODE.replaceCodes(msg, '\u00a7');
	}

	public static Collection<Player> getOnlinePlayers() {
		return Sponge.getServer().getOnlinePlayers();
	}

	@Nullable
	public static Player getRandomPlayer() {
		Collection<Player> onlinePlayers = Sponge.getServer().getOnlinePlayers();
		if (onlinePlayers.isEmpty())
			return null;

		int randomIndex = ThreadLocalRandom.current().nextInt(onlinePlayers.size());
		return onlinePlayers.toArray(new Player[0])[randomIndex];
	}

	public static boolean getFromBoolean(String s) {
		if (s.toLowerCase().contains("true") || s.equalsIgnoreCase("true") || s.toLowerCase().contains("vrai")
				|| s.equalsIgnoreCase("vrai"))
			return true;
		else
			return false;
	}

	public static int getPing(Player p) {
		return p.getConnection().getLatency();
	}

	public static int parseInPorcent(int i) {
		if (i > 100)
			return 100;
		else if (i < 0)
			return 0;
		else
			return i;
	}

	public static int parseInPorcent(double i) {
		if (i > 100)
			return 100;
		else if (i < 0)
			return 0;
		else
			return (int) i;
	}

	public static Optional<Cheat> getCheatFromName(String s) {
		for (Cheat c : Cheat.values())
			if (c.getName().equalsIgnoreCase(s))
				return Optional.of(c);
		return Optional.empty();
	}

	public static Optional<Cheat> getCheatFromItem(ItemType m) {
		for (Cheat c : Cheat.values())
			if (c.getMaterial().equals(m))
				return Optional.of(c);
		return Optional.empty();
	}

	public static ItemStack createItem(ItemType m, String name, String... lore) {
		return createItem(m, name, 1, lore);
	}

	public static ItemStack createItem(ItemType m, String name, int amount, String... lore) {
		ItemStack item = ItemStack.of(m, Math.max(amount, 1));
		item.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(name));
		List<Text> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(TextSerializers.FORMATTING_CODE.deserialize(lores));
		item.offer(Keys.ITEM_LORE, textLore);
		return item;
	}

	public static ItemStack createItem(ItemType m, String name, int amount, DyeColor color, String... lore) {
		ItemStack item = createItem(m, name, amount, lore);
		item.offer(Keys.DYE_COLOR, color);
		return item;
	}

	public static ItemStack createSkull(String name, int amount, Player owner, String... lore) {
		ItemStack skull = ItemStack.builder()
				.itemType(ItemTypes.SKULL)
				.add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
				.add(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(name))
				.add(Keys.REPRESENTED_PLAYER, owner.getProfile())
				.quantity(Math.max(amount, 1))
				.build();

		List<Text> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(TextSerializers.FORMATTING_CODE.deserialize(lores));
		skull.offer(Keys.ITEM_LORE, textLore);
		return skull;
	}

	public static Inventory fillInventoryWith(ItemStack item, Inventory inv) {
		inv.forEach(inventory -> inventory.slots().forEach(slot -> slot.set(item)));
		return inv;
	}

	public static HashMap<String, String> getModsNameVersionFromMessage(String modName) {
		HashMap<String, String> mods = new HashMap<String, String>();
		String caractere = "abcdefghijklmnopqrstuvwxyz0123456789.,_-;";
		List<String> listCaracters = Arrays.asList(caractere.split(""));
		String s = "", name = "", version = "unknow";
		boolean isVersion = false, checkVersion = false;
		for (String parts : modName.split("")) {
			if (listCaracters.contains(parts.toLowerCase())) {
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

	public static double getLastTPS() {
		return Sponge.getServer().getTicksPerSecond();
	}

	public static void removePotionEffect(PotionEffectData effects, PotionEffectType effectType) {
		for (PotionEffect effect : effects.getListValue()) {
			if (effect.getType().equals(effectType)) {
				effects.remove(effect);
			}
		}
	}
}
