package com.elikill58.negativity.sponge.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.UniversalUtils;
import com.elikill58.negativity.universal.permissions.Perm;

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
		ItemStack item = ItemStack.builder().quantity(amount == 0 ? 1 : amount).itemType(m).build();
		item.offer(Keys.DISPLAY_NAME, Text.of(coloredMessage(name)));
		List<Text> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(Text.of(coloredMessage(lores)));
		item.offer(Keys.ITEM_LORE, textLore);
		return item;
	}

	public static ItemStack createItem(ItemType m, String name, int amount, DyeColor color, String... lore) {
		ItemStack item = createItem(m, name, amount, lore);
		item.offer(Keys.DYE_COLOR, color);
		return item;
	}

	public static ItemStack createSkull(String name, int amount, Player owner, String... lore) {
		ItemStack skull = ItemStack.builder().itemType(ItemTypes.SKULL).add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
				.quantity(amount == 0 ? 1 : amount).build();
		skull.offer(Keys.DISPLAY_NAME, Text.of(name));
		skull.offer(Keys.TAMED_OWNER, Optional.of(owner.getUniqueId()));
		List<Text> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(Text.of(coloredMessage(lores)));
		skull.offer(Keys.ITEM_LORE, textLore);
		return skull;
	}

	public static Inventory rempliInvWith(ItemStack item, Inventory inv) {
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

	public static void sendUpdateMessageIfNeed(Player p) {
		if (!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "showAlert"))
			return;
		if (!(UniversalUtils.hasInternet() && !UniversalUtils
				.isLatestVersion(Optional.of(SpongeNegativity.getInstance().getContainer().getVersion().get()))))
			return;
		try {
			p.sendMessage(Text
					.builder("New version available (" + UniversalUtils.getLatestVersion().orElse("unknow")
							+ "). Download it here.")
					.color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Click here")))
					.onClick(TextActions.openUrl(new URL(
							"https://www.spigotmc.org/resources/aac-negativity-spigot-1-7-sponge-bungeecord-optimized.48399/")))
					.build());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static double getLastTPS() {
		return Sponge.getServer().getTicksPerSecond();
	}
}
