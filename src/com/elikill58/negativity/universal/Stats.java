package com.elikill58.negativity.universal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Stats {

	public static final String SITE = "https://elicompagny.ddns.net/";
	private static final String SITE_FILE = SITE + "negativity-infos.php";
	public static final boolean STATS_IN_MAINTENANCE = false;

	public static void updateStats(StatsType type, Object value/*, Object... useless*/) {
		if(STATS_IN_MAINTENANCE)
			return;
		if (!UniversalUtils.hasInternet() || !UniversalUtils.statsServerOnline())
			return;
		try {
			URLConnection conn = (HttpURLConnection) new URL(SITE_FILE).openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write("from=negativity&type=" + type.getKey() + "&" + type.getKey() + "=" + value);
			writer.flush();
			writer.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String respons = "", end = "";
			while ((respons = br.readLine()) != null)
				end += respons;
			if (!end.equalsIgnoreCase("")) {
				System.out.println(
						"[Negativity] Logs while updating stats. Please, report this to Elikill58 (Mail: arpetzouille@gmail.com | Discord: @Elikill58#0743 | Twitter: @Elikill58 / @elinegativity");
				System.out.println(end);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static enum StatsType {
		ONLINE("online"), PLAYERS("players"), CHEATS("cheats"), PORT("port");

		private String key;

		private StatsType(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
}
