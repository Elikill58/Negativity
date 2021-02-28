package com.elikill58.negativity.universal;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Stats {

	//private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private static final String SITE_FILE = "https://api.eliapp.fr/negativity.php";
    public static boolean STATS_IN_MAINTENANCE = false;

    public static void updateStats(StatsType type, String... value) {
    	if(!Adapter.getAdapter().getConfig().getBoolean("stats"))
    		return;
    	String post = "";
    	switch (type) {
		case BAN:
			post = "&value=" + value[0];
			break;
		case CHEAT:
			post = "&hack=" + value[0] + "&reliability=" + value[1] + "&amount=" + (value.length > 2 ? value[2] : "1");
			break;
		case ONLINE:
			post = "&value=" + value[0];
			break;
		case PORT:
			post = "&value=" + value[0];
			break;
		}
    	sendUpdateStats(type, "platform=" + Adapter.getAdapter().getName() + "&type=" + type.getKey() + post);
    }
    
	private static void sendUpdateStats(StatsType type, String post) {
		if(STATS_IN_MAINTENANCE || !UniversalUtils.HAVE_INTERNET)
			return;
		Adapter ada = Adapter.getAdapter();
		final LoggerAdapter log = ada.getLogger();
		Runnable task = () -> {
			try {
				String end = UniversalUtils.getContentFromURL(SITE_FILE, post).orElse(null);
				if(end == null) {
					log.info("Error while updating stats, it seems to be a firewall that blocking the stats.");
					STATS_IN_MAINTENANCE = true;
				} else if (!end.equalsIgnoreCase("")) {
					log.info("Error while updating stats. Please, report this to Elikill58 (Mail: arpetzouille@gmail.com | Discord: @Elikill58#0743 | Twitter: @Elikill58 / @elinegativity");
					log.info(end);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		ada.runAsync(task);
		/*try {
			THREAD_POOL.submit(task);
		} catch (RejectedExecutionException e) {
			log.error("Could not update stats: " + e.getMessage());
		}*/
	}

	public static void loadStats() {
		Runnable task = () -> {
			try {
				if(!UniversalUtils.HAVE_INTERNET)
					STATS_IN_MAINTENANCE = true;
				else {
					String result = UniversalUtils.getContentFromURL("https://api.eliapp.fr/status.php?plateforme=negativity").orElse("off");
					STATS_IN_MAINTENANCE = result.toString().equalsIgnoreCase("on") ? false : true;
					if (STATS_IN_MAINTENANCE)
						Adapter.getAdapter().getLogger().info("Website is in maintenance mode.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Adapter.getAdapter().runAsync(task);
		/*try {
			THREAD_POOL.submit(task);
		} catch (RejectedExecutionException e) {
			Adapter.getAdapter().getLogger()
					.error("Could not load stats: " + e.getMessage());
			e.printStackTrace();
		}*/
	}
	
	public static enum StatsType {
		ONLINE("online"), PORT("port"), CHEAT("cheat"), BAN("ban");

		private String key;

		private StatsType(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
}
