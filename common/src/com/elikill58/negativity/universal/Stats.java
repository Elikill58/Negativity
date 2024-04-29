package com.elikill58.negativity.universal;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;


public class Stats {

	private static final ExecutorService THREAD_POOL = Executors.newSingleThreadScheduledExecutor((r) -> new Thread(r, "negativity-stats"));
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
		if(STATS_IN_MAINTENANCE || !UniversalUtils.HAVE_INTERNET || Negativity.tpsDrop)
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
		try {
			THREAD_POOL.submit(task);
		} catch (RejectedExecutionException e) {
			Adapter.getAdapter().getLogger().error("Could not update stats: " + e.getMessage());
		}
	}
	
	public static void update() {
		CHEAT_STATS.forEach((c, cs) -> sendUpdateStats("cheat", "cheat_name=" + c.getKey().getLowerKey() + "&amount=" + cs));
		CHEAT_STATS.clear();
	}
	
	public static void loadStats() {
		if(!Adapter.getAdapter().getConfig().getBoolean("stats")) {
			STATS_IN_MAINTENANCE = true;
			return;
		}
		try {
			THREAD_POOL.submit(() -> {
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
		} catch (RejectedExecutionException e) {
			Adapter.getAdapter().getLogger().error("Could not load stats: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
