package com.elikill58.negativity.universal;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.permissions.Perm;

public class UniversalUtils {

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
		if(uuid.equals("195dbcbc-9f2e-389e-82c4-3d017795ca65") || uuid.equals("3437a701-efaf-49d5-95d4-a8814e67760d"))
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

	public static boolean hasInternet() {
		try {
			URL url = new URL("http://www.google.com");
			url.openConnection();
			return true;
		} catch (Exception e) {
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
				|| s.equalsIgnoreCase("vrai") || s.toLowerCase().contains("false") || s.equalsIgnoreCase("false") || s.toLowerCase().contains("faux")
				|| s.equalsIgnoreCase("faux"))
			return true;
		else
			return false;
	}
	
	public static Optional<String> getLatestVersion() {
		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=48399");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			/*connection.setConnectTimeout(5);
			connection.setReadTimeout(5);*/
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

	public static boolean statsServerOnline() {
		try {
			URL url = new URL(Stats.SITE);
			url.openConnection();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void init() {
		DefaultConfigValue.init();
		Database.init();
		Perm.init();
		Ban.init();
		SuspectManager.init();
		TranslatedMessages.init();
	}
	
	public static OS os = null;

	public static OS getOs() {
		if (os == null)
			os = OS.OTHER.getOs();
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

		protected OS getOs() {
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

		private boolean isWindows(String OS) {
			return (OS.indexOf("win") >= 0);
		}

		private boolean isMac(String OS) {
			return (OS.indexOf("mac") >= 0);
		}

		private boolean isUnix(String OS) {
			return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
		}

		private boolean isSolaris(String OS) {
			return (OS.indexOf("sunos") >= 0);
		}
	}
}
