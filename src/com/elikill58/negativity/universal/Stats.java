package com.elikill58.negativity.universal;

import java.util.HashMap;
import java.util.Locale;

import com.elikill58.deps.json.JSONObject;
import com.elikill58.deps.json.parser.JSONParser;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Stats {

    private static final String SITE_FILE = "https://api.negativity.fr/stats";
    public static boolean STATS_IN_MAINTENANCE = false;
    private static final HashMap<Cheat, Integer> CHEAT_STATS = new HashMap<>();

    public static void updateMessage(NegativityPlayer player, String message) {
    	sendUpdateStats("message", "author_uuid=" + player.getUUID() + "&author_name=" + player.getName() + "&message=" + message);
    }
    
    public static void updateCheat(Cheat c, int amount) {
    	CHEAT_STATS.put(c, CHEAT_STATS.getOrDefault(c, 0) + amount);
    }
    
	private static void sendUpdateStats(String url, String post) {
		if(STATS_IN_MAINTENANCE || !UniversalUtils.HAVE_INTERNET || UniversalUtils.TPS_DROP)
			return;
		Adapter ada = Adapter.getAdapter();
		Runnable task = () -> {
			try {
				String end = UniversalUtils.getContentFromURL(SITE_FILE + "/" + url, post + "&type=" + ada.getName() + "&version=" + ada.getPluginVersion() + "&version_mc=" + ada.getVersion()).orElse(null);
				if(end == null) {
					STATS_IN_MAINTENANCE = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		ada.runAsync(task);
	}
	
	public static void update() {
		CHEAT_STATS.forEach((c, cs) -> sendUpdateStats("cheat", "cheat_name=" + c.getKey().toLowerCase(Locale.ROOT) + "&amount=" + cs));
		CHEAT_STATS.clear();
	}
	
	public static void loadStats() {
		if(!Adapter.getAdapter().getConfig().getBoolean("stats")) {
			STATS_IN_MAINTENANCE = true;
			return;
		}
		Adapter.getAdapter().runAsync(() -> {
			try {
				if(!UniversalUtils.HAVE_INTERNET)
					STATS_IN_MAINTENANCE = true;
				else {
					String result = UniversalUtils.getContentFromURL("https://api.negativity.fr/status").orElse(null);
					if(result == null)
						STATS_IN_MAINTENANCE = true;
					else {
						JSONObject json = (JSONObject) new JSONParser().parse(result);
						STATS_IN_MAINTENANCE = !json.get("status").toString().equalsIgnoreCase("ok");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
