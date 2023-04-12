package com.elikill58.negativity.universal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Stats {

	private static final ExecutorService THREAD_POOL = Executors.newSingleThreadScheduledExecutor((r) -> new Thread(r, "negativity-stats"));

    private static final String SITE_FILE = "https://api.eliapp.fr/negativity.php";
    public static boolean STATS_IN_MAINTENANCE = false;

    public static void updateStats(StatsType type, String... value) {
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
		if(STATS_IN_MAINTENANCE || !UniversalUtils.HAVE_INTERNET || !Adapter.getAdapter().canSendStats())
			return;
		Runnable task = () -> {
			try {
				String end = UniversalUtils.getContentFromURL(SITE_FILE, post).orElse(null);
				if(end == null) {
					Adapter.getAdapter().getLogger().info("Error while updating stats, it seems to be a firewall that blocking the stats.");
					STATS_IN_MAINTENANCE = true;
				}
				else if (!end.equalsIgnoreCase("")) {
					Adapter.getAdapter().getLogger().info(
							"Error while updating stats. Please, report this to Elikill58 (Mail: arpetzouille@gmail.com | Discord: @Elikill58#0743 | Twitter: @Elikill58 / @elinegativity");
					Adapter.getAdapter().getLogger().info(end);
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

	public static void sendStartupStats(int port) {
    	if (!Adapter.getAdapter().canSendStats()) {
    		return;
		}
		Runnable task = () -> {
			try {
				if(!UniversalUtils.HAVE_INTERNET) {
					STATS_IN_MAINTENANCE = true;
				} else {
					String result = UniversalUtils.getContentFromURL("https://api.eliapp.fr/status.php?plateforme=negativity").orElse("off");
					STATS_IN_MAINTENANCE = !result.equalsIgnoreCase("on");
					if (STATS_IN_MAINTENANCE) {
						Adapter.getAdapter().getLogger().info("Website is in maintenance mode.");
					} else {
						Stats.updateStats(StatsType.ONLINE, 1 + "");
						Stats.updateStats(StatsType.PORT, port + "");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		try {
			THREAD_POOL.submit(task);
		} catch (RejectedExecutionException e) {
			Adapter.getAdapter().getLogger().error("Could not load stats: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public enum StatsType {
		ONLINE("online"), PORT("port"), CHEAT("cheat"), BAN("ban");

		private String key;

		StatsType(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
}
