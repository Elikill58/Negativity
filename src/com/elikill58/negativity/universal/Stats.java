package com.elikill58.negativity.universal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Stats {


    public static final String SITE = "https://eliapp.fr/", SITE_UPDATE = "https://api.eliapp.fr/";
    static final String SITE_FILE = SITE_UPDATE + "negativity.php";
    static boolean STATS_IN_MAINTENANCE = false;

	public static void updateStats(StatsType type, Object value) {
		if(STATS_IN_MAINTENANCE)
			return;
		if (!UniversalUtils.hasInternet() || !UniversalUtils.statsServerOnline())
			return;
		try {
			URLConnection conn = (HttpURLConnection) new URL(SITE_FILE).openConnection();
            doTrustToCertificates();
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
						"[Negativity] Error while updating stats. Please, report this to Elikill58 (Mail: arpetzouille@gmail.com | Discord: @Elikill58#0743 | Twitter: @Elikill58 / @elinegativity");
				System.out.println(end);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadStats() {
		if(!UniversalUtils.hasInternet())
			STATS_IN_MAINTENANCE = false;
		try {
        	StringBuilder result = new StringBuilder();
            URL url = new URL(SITE_UPDATE + "status.php?plateforme=negativity");
            doTrustToCertificates();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
                result.append(line);
            rd.close();
            STATS_IN_MAINTENANCE = result.toString().equalsIgnoreCase("on") ? false : true;
            if(STATS_IN_MAINTENANCE)
            	Adapter.getAdapter().log("Website is in maintenance mode.");
        } catch (SSLHandshakeException e) {
        	STATS_IN_MAINTENANCE = true;
        	Adapter.getAdapter().warn("Error while loading Stats for Negativity.");
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	public static void doTrustToCertificates() throws Exception {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        return;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        return;
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '" + session.getPeerHost() + "'.");
                }
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
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
