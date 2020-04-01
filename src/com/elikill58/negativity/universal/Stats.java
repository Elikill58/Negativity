package com.elikill58.negativity.universal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Stats {

	private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private static final String SITE_FILE = "https://api.eliapp.fr/negativity.php";
    public static boolean STATS_IN_MAINTENANCE = false;

    public static void updateStats(StatsType type, String... value) {
    	String post = "";
    	switch (type) {
		case BAN:
			post = "&value=" + value[0];
			break;
		case CHEAT:
			post = "&hack=" + value[0] + "&reliability=" + value[1] + "&comment=" + value[2] + "&amount=" + (value.length > 3 ? value[3] : "1");
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
		if(STATS_IN_MAINTENANCE)
			return;
		Runnable task = () -> {
			try {
				URLConnection conn = (HttpsURLConnection) new URL(SITE_FILE).openConnection();
				UniversalUtils.doTrustToCertificates();
				conn.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
				writer.write(post);
				writer.flush();
				writer.close();
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String respons = "", end = "";
				while ((respons = br.readLine()) != null)
					end += respons;
				if (!end.equalsIgnoreCase("")) {
					Adapter.getAdapter().log(
							"Error while updating stats. Please, report this to Elikill58 (Mail: arpetzouille@gmail.com | Discord: @Elikill58#0743 | Twitter: @Elikill58 / @elinegativity");
					Adapter.getAdapter().log(end);
				}
				br.close();
			} catch (ConnectException e) {
				Adapter.getAdapter().log("Error while updating stats, it seems to be a firewall that blocking the stats");
				STATS_IN_MAINTENANCE = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		try {
			THREAD_POOL.submit(task);
		} catch (RejectedExecutionException e) {
			Adapter.getAdapter().error("Could not update stats: " + e.getMessage());
		}
	}

	public static void loadStats() {
		Runnable task = () -> {
			try {
				StringBuilder result = new StringBuilder();
				URL url = new URL("https://api.eliapp.fr/status.php?plateforme=negativity");
				UniversalUtils.doTrustToCertificates();
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null)
					result.append(line);
				rd.close();
				STATS_IN_MAINTENANCE = result.toString().equalsIgnoreCase("on") ? false : true;
				if (STATS_IN_MAINTENANCE)
					Adapter.getAdapter().log("Website is in maintenance mode.");
			} catch (SSLHandshakeException e) {
				STATS_IN_MAINTENANCE = true;
				Adapter.getAdapter().warn("Error while loading Stats for Negativity.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		try {
			THREAD_POOL.submit(task);
		} catch (RejectedExecutionException e) {
			Adapter.getAdapter()
					.error("Could not load stats: " + e.getMessage());
			e.printStackTrace();
		}
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
