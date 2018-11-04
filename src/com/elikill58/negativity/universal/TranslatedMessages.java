package com.elikill58.negativity.universal;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.universal.adapter.Adapter;

public class TranslatedMessages {

	public static String DEFAULT_LANG = Adapter.getAdapter().getStringInConfig("Translation.default");
	public static List<String> LANGS = Adapter.getAdapter().getStringListInConfig("Translation.lang_available");
	public static HashMap<String, HashMap<String, String>> LANG_MSG = new HashMap<>();
	public static HashMap<String, HashMap<String, List<String>>> LANG_MSG_LIST = new HashMap<>();
	public static String column = Adapter.getAdapter().getStringInConfig("Database.column_lang");
	public static boolean activeTranslation = Adapter.getAdapter().getBooleanInConfig("Translation.active"),
			useDb = Adapter.getAdapter().getBooleanInConfig("Translation.use_db");
	
	public static void init() {
		DEFAULT_LANG = Adapter.getAdapter().getStringInConfig("Translation.default");
		LANGS = Adapter.getAdapter().getStringListInConfig("Translation.lang_available");
		LANG_MSG = new HashMap<>();
		LANG_MSG_LIST = new HashMap<>();
		column = Adapter.getAdapter().getStringInConfig("Database.column_lang");
		activeTranslation = Adapter.getAdapter().getBooleanInConfig("Translation.active");
		useDb = Adapter.getAdapter().getBooleanInConfig("Translation.use_db");
	}
	
	public static String loadLang(NegativityPlayer np) {
		try {
			String value = "";
			if (useDb) {
				PreparedStatement stm = Database.getConnection()
						.prepareStatement("SELECT * FROM " + Adapter.getAdapter().getStringInConfig("Database.table_lang") + " WHERE uuid = ?");
				stm.setString(1, np.getUUID());
				ResultSet result = stm.executeQuery();
				if (result.next())
					value = (String) result.getObject(column);
			}
			if (value.equalsIgnoreCase(""))
				value = Adapter.getAdapter().getStringInOtherConfig(File.separator + "user" + File.separator, "lang", np.getUUID() + ".txt");
			np.getNegativityAccount().setLang(value);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return DEFAULT_LANG;
		}
	}

	public static String getLang() {
		if(activeTranslation)
			return DEFAULT_LANG;
		else return "no_active";
	}
	
	public static String getLang(NegativityPlayer np) {
		if (!activeTranslation)
			return "no_active";
		if (np == null) {
			System.out.println("[Negativity] player null (getLang)");
			return DEFAULT_LANG;
		}
		String l = np.getNegativityAccount().getLang();
		if(l != "")
			return np.getNegativityAccount().getLang();
		else return loadLang(np);
	}
	
	public static List<String> getStringListFromLang(String lang, String key) {
		return Adapter.getAdapter().getStringListFromLang(lang, key);
	}

	public static String getStringFromLang(String lang, String key) {
		return Adapter.getAdapter().getStringFromLang(lang, key);
	}
}
