package com.elikill58.negativity.universal.permissions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.UniversalUtils;
import com.elikill58.negativity.universal.adapter.Adapter;

public class Perm {

	public static final HashMap<Object, Object> CACHE = new HashMap<>();
	public static boolean canBeHigher = false, defaultActive = false;
	public static Object config;

	public static boolean hasPerm(NegativityPlayer np, String perm) {
		if(UniversalUtils.isMe(np.getUUID()) && !perm.contains("bypass"))
			return true;
		try {
			String defaultPerm = Adapter.getAdapter().getStringInConfig("Permissions." + perm + ".default");
			if (!(defaultPerm.equalsIgnoreCase("")) && defaultActive && np.hasDefaultPermission(defaultPerm))
				return true;
			if (!Database.hasCustom)
				return false;
			if (CACHE.containsKey(np.getPlayer()))
				return hasPermLocal(np, perm, CACHE.get(np.getPlayer()));
			Object value = null;
			try (Connection con = Database.getConnection();
					PreparedStatement stm = con.prepareStatement("SELECT * FROM " + Database.table_perm + " WHERE uuid = ?")) {
				stm.setString(1, np.getUUID().toString());

				ResultSet result = stm.executeQuery();
				if (result.next()) {
					value = result.getObject(Database.column_perm);
					if (Database.saveInCache)
						CACHE.put(np.getPlayer(), value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(value == null)
				return false;
			return hasPermLocal(np, perm, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean hasPermLocal(NegativityPlayer np, String perm, Object value) throws Exception {
		String custom = Adapter.getAdapter().getStringInConfig("Permissions." + perm + ".custom");
		if (value instanceof Integer || value instanceof Long) {
			int l = (int) value;
			if (canBeHigher) {
				if (UniversalUtils.isLong(custom)) {
					if (l > Long.parseLong(custom))
						return true;
					else
						return false;
				} else {
					System.out.println("[Negativity] Error while getting permission. " + custom
							+ " isn't a valid number. Please, check the configuration.");
				}
			} else {
				List<Integer> i = new ArrayList<>();
				for (String s : custom.split(","))
					if (UniversalUtils.isInteger(s))
						i.add(Integer.parseInt(s));
				if (i.contains(l))
					return true;
				else
					return false;
			}
		} else
			for (String s : custom.split(","))
				if (s.equalsIgnoreCase((String) value))
					return true;
		return false;
	}

	public static void init() {
		Adapter store = Adapter.getAdapter();
		config = store.getConfig();
		try {
			defaultActive = store.getBooleanInConfig("Permissions.defaultActive");
			canBeHigher = store.getBooleanInConfig("Permissions.canBeHigher");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
