package com.elikill58.negativity.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.universal.Database;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeMessages {

	public static final HashMap<ProxiedPlayer, String> PLAYER_LANG = new HashMap<>();
	public static final List<String> LANG = BungeeNegativity.CONFIG.getStringList("Translation.lang_available");
	public static final HashMap<String, Configuration> LANG_VALUES = new HashMap<>();
	public static final String column = BungeeNegativity.CONFIG.getString("Permissions.localDatabase.column_lang"),
			defaulttrans = BungeeNegativity.CONFIG.getString("Translation.default");
	public static boolean activeTranslation = BungeeNegativity.CONFIG.getBoolean("Translation.active"),
			useDb = BungeeNegativity.CONFIG.getBoolean("Translation.use_db");

	public static void load(Plugin pl) {
		try {
			File langDir = new File(pl.getDataFolder().getAbsolutePath() + "/lang/");
			if (!langDir.exists()) {
				langDir.mkdirs();
				for (String l : LANG)
					LANG_VALUES.put(l, ConfigurationProvider.getProvider(YamlConfiguration.class)
							.load(copy(pl, l, new File(langDir.getAbsolutePath() + "/" + l + ".yml"))));
			} else {
				for (String l : LANG) {
					File langFile = new File(langDir.getAbsolutePath() + "/" + l + ".yml");
					if (!langFile.exists())
						copy(pl, l, langFile);
					LANG_VALUES.put(l, ConfigurationProvider.getProvider(YamlConfiguration.class).load(langFile));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File copy(Plugin pl, String lang, File f) {
		String fileName = "bungee_en_US.yml";
		if (lang.toLowerCase().contains("fr") || lang.toLowerCase().contains("be"))
			fileName = "bungee_fr_FR.yml";
		if(lang.toLowerCase().contains("pt") || lang.toLowerCase().contains("br"))
			fileName = "bungee_pt_BR.yml";
		// TODO : Espagnol & Allemand
		try (InputStream in = pl.getResourceAsStream(fileName); OutputStream out = new FileOutputStream(f)) {
			ByteStreams.copy(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	public static String getLang(ProxiedPlayer p) {
		if (!activeTranslation)
			return getLang();

		if (p == null) {
			System.out.println("[Negativity] player null (get lang)");
			return defaulttrans;
		}

		if (PLAYER_LANG.containsKey(p))
			return PLAYER_LANG.get(p);
		try {
			String value = "";
			if (useDb) {
				PreparedStatement stm = Database.getConnection()
						.prepareStatement("SELECT * FROM " + BungeeNegativity.CONFIG.getString("Permissions.localDatabase.table_lang") + " WHERE uuid = ?");
				stm.setString(1, p.getUniqueId().toString());
				ResultSet result = stm.executeQuery();
				if (result.next())
					value = (String) result.getObject(column);
				if (Database.saveInCache)
					PLAYER_LANG.put(p, value);
			}
			if (value.equalsIgnoreCase("")) {
				// FILES SYSTEM
				System.out.println("file system SOON");
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return defaulttrans;
		}
	}

	public static String getLang() {
		return "en_US";
	}

	public static String getMessage(String dir, String... placeholders) {
		String message = "";
		try {
			message = ChatColor.RESET + LANG_VALUES.get(getLang()).getString(dir);
		} catch (NullPointerException e) {
			System.out.println("[BungeeNegativity] Unknow ! default: "
					+ BungeeNegativity.CONFIG.getString("Translation.default") + " Get: " + getLang());
		}
		for (int index = 0; index <= placeholders.length - 1; index += 2)
			message = message.replaceAll(placeholders[index], placeholders[index + 1]);
		if (message.equalsIgnoreCase("§rnull"))
			return dir;
		return coloredBungeeMessage(message);
	}

	public static String getMessage(ProxiedPlayer p, String dir, String... placeholders) {
		String message = ChatColor.RESET + LANG_VALUES.get((p != null ? getLang(p) : getLang())).getString(dir);
		for (int index = 0; index <= placeholders.length - 1; index += 2)
			message = message.replaceAll(placeholders[index], placeholders[index + 1]);
		if (message.equalsIgnoreCase("§rnull"))
			return dir;
		return coloredBungeeMessage(message);
	}

	public static String coloredBungeeMessage(String msg) {
		return msg.replaceAll("&0", String.valueOf(ChatColor.BLACK))
				.replaceAll("&1", String.valueOf(ChatColor.DARK_BLUE))
				.replaceAll("&2", String.valueOf(ChatColor.DARK_GREEN))
				.replaceAll("&3", String.valueOf(ChatColor.DARK_AQUA))
				.replaceAll("&4", String.valueOf(ChatColor.DARK_RED))
				.replaceAll("&5", String.valueOf(ChatColor.DARK_PURPLE))
				.replaceAll("&6", String.valueOf(ChatColor.GOLD)).replaceAll("&7", String.valueOf(ChatColor.GRAY))
				.replaceAll("&8", String.valueOf(ChatColor.DARK_GRAY)).replaceAll("&9", String.valueOf(ChatColor.BLUE))
				.replaceAll("&a", String.valueOf(ChatColor.GREEN)).replaceAll("&b", String.valueOf(ChatColor.AQUA))
				.replaceAll("&c", String.valueOf(ChatColor.RED))
				.replaceAll("&d", String.valueOf(ChatColor.LIGHT_PURPLE))
				.replaceAll("&e", String.valueOf(ChatColor.YELLOW)).replaceAll("&f", String.valueOf(ChatColor.WHITE))
				.replaceAll("&k", String.valueOf(ChatColor.MAGIC)).replaceAll("&l", String.valueOf(ChatColor.BOLD))
				.replaceAll("&m", String.valueOf(ChatColor.STRIKETHROUGH))
				.replaceAll("&n", String.valueOf(ChatColor.UNDERLINE))
				.replaceAll("&o", String.valueOf(ChatColor.ITALIC)).replaceAll("&r", String.valueOf(ChatColor.RESET));
	}
}
