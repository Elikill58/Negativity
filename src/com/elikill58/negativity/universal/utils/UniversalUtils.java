package com.elikill58.negativity.universal.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.DefaultConfigValue;
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.permissions.Perm;

public class UniversalUtils {

	public static final DateTimeFormatter GENERIC_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static int parseInPorcent(int i) {
		if (i > 100)
			return 100;
		else if (i < 0)
			return 0;
		else
			return i;
	}

	public static int parseInPorcent(double i) {
		if (i > 100)
			return 100;
		else if (i < 0)
			return 0;
		else
			return (int) i;
	}

	public static Optional<Cheat> getCheatFromName(String s) {
		for (Cheat c : Cheat.values())
			if (c.getName().equalsIgnoreCase(s))
				return Optional.of(c);
		return Optional.empty();
	}

	public static Optional<Cheat> getCheatFromItem(Object m) {
		for (Cheat c : Cheat.values())
			if (c.getMaterial().equals(m))
				return Optional.of(c);
		return Optional.empty();
	}

	public static List<String> getClasseNamesInPackage(String jarName, String packageName) {
		ArrayList<String> classes = new ArrayList<>();

		packageName = packageName.replaceAll("\\.", "/");
		try {
			JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
			JarEntry jarEntry;
			while (true) {
				jarEntry = jarFile.getNextJarEntry();
				if (jarEntry == null) {
					break;
				}
				if ((jarEntry.getName().startsWith(packageName)) && (jarEntry.getName().endsWith(".class"))) {
					classes.add(jarEntry.getName().replaceAll("/", "\\."));
				}
			}
			jarFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}

	public static boolean isMe(String uuid) {
		if (uuid.equals("195dbcbc-9f2e-389e-82c4-3d017795ca65") || uuid.equals("3437a701-efaf-49d5-95d4-a8814e67760d"))
			return true;
		return false;
	}

	public static boolean isMe(UUID uuid) {
		return isMe(uuid.toString());
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static boolean isLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static boolean isLatestVersion(String version) {
		if (version == null)
			return false;

		Optional<String> optVer = getLatestVersion();
		if (optVer.isPresent())
			return version.equalsIgnoreCase(optVer.get());
		else
			return false;
	}

	public static boolean getFromBoolean(String s) {
		if (s.toLowerCase().contains("true") || s.equalsIgnoreCase("true") || s.toLowerCase().contains("vrai")
				|| s.equalsIgnoreCase("vrai"))
			return true;
		else
			return false;
	}

	public static boolean isBoolean(String s) {
		if (s.toLowerCase().contains("true") || s.equalsIgnoreCase("true") || s.toLowerCase().contains("vrai")
				|| s.equalsIgnoreCase("vrai") || s.toLowerCase().contains("false") || s.equalsIgnoreCase("false")
				|| s.toLowerCase().contains("faux") || s.equalsIgnoreCase("faux"))
			return true;
		else
			return false;
	}

	public static Optional<String> getLatestVersion() {
		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=48399");
			doTrustToCertificates();
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			/*
			 * connection.setConnectTimeout(5); connection.setReadTimeout(5);
			 */
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			connection.setUseCaches(true);
			connection.setDoOutput(true);
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String content = "";
			String input;
			while ((input = br.readLine()) != null)
				content = content + input;
			br.close();
			return Optional.of(content);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static CompletableFuture<@Nullable String> requestMcleaksData(String uuid) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				URL url = new URL("https://mcleaks.themrgong.xyz/api/v3/isuuidmcleaks/" + uuid);
				doTrustToCertificates();
				HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
				connection.setUseCaches(true);
				connection.setDoOutput(true);
				try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					StringBuilder content = new StringBuilder();
					String input;
					while ((input = br.readLine()) != null) {
						content.append(input);
					}
					return content.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	public static boolean isValidIP(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}

			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ip.endsWith(".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static void doTrustToCertificates() throws Exception {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				return;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				return;
			}
		} };
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
					System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '"
							+ session.getPeerHost() + "'.");
				}
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	public static String replacePlaceholders(String rawMessage, Object... placeholders) {
		String message = rawMessage;
		for (int index = 0; index <= placeholders.length - 1; index += 2) {
			message = message.replace(String.valueOf(placeholders[index]), String.valueOf(placeholders[index + 1]));
		}
		return message;
	}

	@Nullable
	public static String trimExcess(@Nullable String string, int maxLength) {
		if (string == null || maxLength >= string.length()) {
			return string;
		}
		return string.substring(0, maxLength - 1);
	}

	public static void init() {
		DefaultConfigValue.init();
		Database.init();
		Perm.init();
		BanManager.init();
		SuspectManager.init();
		TranslatedMessages.init();
	}

	public static OS os = null;

	public static OS getOs() {
		if (os == null)
			os = OS.getOs();
		return os;
	}

	public enum OS {
		WINDOWS(StandardCharsets.ISO_8859_1), MAC(StandardCharsets.UTF_16), LINUX(StandardCharsets.UTF_8), SOLARIS(
				StandardCharsets.UTF_8), OTHER(StandardCharsets.UTF_16);

		private Charset ch;

		OS(Charset ch) {
			this.ch = ch;
		}

		public Charset getCharset() {
			return ch;
		}

		private static OS getOs() {
			String os = System.getProperty("os.name").toLowerCase();
			if (isWindows(os))
				return WINDOWS;
			else if (isMac(os))
				return MAC;
			else if (isUnix(os))
				return LINUX;
			else if (isSolaris(os))
				return SOLARIS;
			else
				return OTHER;
		}

		private static boolean isWindows(String OS) {
			return (OS.indexOf("win") >= 0);
		}

		private static boolean isMac(String OS) {
			return (OS.indexOf("mac") >= 0);
		}

		private static boolean isUnix(String OS) {
			return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
		}

		private static boolean isSolaris(String OS) {
			return (OS.indexOf("sunos") >= 0);
		}
	}
}
