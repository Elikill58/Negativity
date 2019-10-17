package com.elikill58.negativity.universal;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.elikill58.negativity.universal.adapter.Adapter;

public class TranslatedMessages {

	public static String DEFAULT_LANG = Adapter.getAdapter().getStringInConfig("Translation.default");
	public static List<String> LANGS = Adapter.getAdapter().getStringListInConfig("Translation.lang_available");
	public static String column = Adapter.getAdapter().getStringInConfig("Database.column_lang");
	public static boolean activeTranslation = Adapter.getAdapter().getBooleanInConfig("Translation.active"),
			useDb = Adapter.getAdapter().getBooleanInConfig("Translation.use_db");

	public static void init() {
		DEFAULT_LANG = Adapter.getAdapter().getStringInConfig("Translation.default");
		LANGS = Adapter.getAdapter().getStringListInConfig("Translation.lang_available");
		column = Adapter.getAdapter().getStringInConfig("Database.column_lang");
		activeTranslation = Adapter.getAdapter().getBooleanInConfig("Translation.active");
		useDb = Adapter.getAdapter().getBooleanInConfig("Translation.use_db");
	}

	public static String loadLang(NegativityAccount np) {
		try {
			String value = "";
			if (useDb) {
				try (PreparedStatement stm = Database.getConnection()
						.prepareStatement("SELECT * FROM " + Database.table_lang + " WHERE uuid = ?")) {
					stm.setString(1, np.getUUID());
					ResultSet result = stm.executeQuery();
					if (result.next())
						value = result.getString(column);
				}
			}

			if (value.isEmpty()) {
				value = Adapter.getAdapter().getStringInOtherConfig(File.separator + "user" + File.separator, "lang", np.getUUID() + ".yml");
			}

			if (value.isEmpty()) {
				value = DEFAULT_LANG;
			}

			np.setLang(value);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return DEFAULT_LANG;
		}
	}

	public static String getDefaultLang() {
		return DEFAULT_LANG;
	}

	public static String getLang(NegativityAccount np) {
		if (!activeTranslation)
			return DEFAULT_LANG;

		String playerLang = np.getLang();
		if (!playerLang.isEmpty()) {
			return playerLang;
		}

		return loadLang(np);
	}

	public static List<String> getStringListFromLang(String lang, String key) {
		return Adapter.getAdapter().getStringListFromLang(lang, key);
	}

	public static String getStringFromLang(String lang, String key) {
		return Adapter.getAdapter().getStringFromLang(lang, key);
	}
}
