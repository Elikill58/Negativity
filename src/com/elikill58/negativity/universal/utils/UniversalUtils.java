package com.elikill58.negativity.universal.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.api.yaml.config.YamlConfiguration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;

public class UniversalUtils {

	public static final String NEGATIVITY_VERSION = "2.0";
	public static final DateTimeFormatter GENERIC_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static boolean HAVE_INTERNET = true, DEBUG = false;

	public static int getMultipleOf(int i, int multiple, int more) {
		return getMultipleOf(i, multiple, more, -1);
	}

	public static int getMultipleOf(int i, int multiple, int more, int limit) {
		if(i > limit)
			return limit;
		while (i % multiple != 0 && ((i < limit && limit != -1) || limit == -1))
			i += more;
		return i;
	}
	
	public static int floor(double d) {
		int i = (int) d;
		return d < i ? i - 1 : i;
	}

	public static int getPorcentFromBoolean(boolean b) {
		return getPorcentFromBoolean(b, 20);
	}
	
	public static int getPorcentFromBoolean(boolean b, int max) {
		return getPorcentFromBoolean(b, max, 0);
	}
	
	public static int getPorcentFromBoolean(boolean b, int max, int min) {
		return b ? max : min;
	}

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
	
	public static Optional<Integer> getFirstInt(String... args){
		for(String s : args)
			if(isInteger(s))
				return Optional.of(Integer.parseInt(s));
		return Optional.empty();
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

	public static int sum(HashMap<Integer, Integer> relia) {
		if(relia.isEmpty())
			return 0;
		int all = 0, divide = 0;
		for(Integer temp : relia.keySet()) {
			all += (temp * relia.get(temp));
			divide += relia.get(temp);
		}
		return parseInPorcent(all / divide);
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
			return true;
	}
	
	public static Optional<String> getContentFromURL(String url){
		return getContentFromURL(url, "");
	}
	
	public static Optional<String> getContentFromURL(String urlName, String post){
		if(!HAVE_INTERNET)
			return Optional.empty();
		try {
			Adapter ada = Adapter.getAdapter();
			URL url = new URL(urlName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(true);
			connection.setRequestProperty("User-Agent", "Negativity " + ada.getName() + " - " + ada.getVersion());
			connection.setDoOutput(true);
			if(!post.equalsIgnoreCase("")) {
				connection.setRequestMethod("POST");
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(post);
				writer.flush();
				writer.close();
			} else connection.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String content = "";
			String input;
			while ((input = br.readLine()) != null)
				content = content + input;
			br.close();
			return Optional.of(content);
        } catch (UnknownHostException | MalformedURLException e) {
        	HAVE_INTERNET = false;
        	Adapter.getAdapter().getLogger().info("Could not use the internet connection to check for update or send stats");
        } catch (ConnectException e) {
        	HAVE_INTERNET = false;
        	if(containsChineseCharacters(e.getMessage())) {
            	Adapter.getAdapter().getLogger().info("As chinese people, you cannot access to the website " + urlName + ".");
        	} else
            	Adapter.getAdapter().getLogger().warn("Cannot connect to " + urlName + " (Reason: " + e.getMessage() + ").");
        } catch (IOException e) {
        	e.printStackTrace();
		}
		return Optional.empty();
	}

	public static Optional<String> getLatestVersion() {
		return getContentFromURL("https://api.spigotmc.org/legacy/update.php?resource=48399");
	}

	public static CompletableFuture<@Nullable String> requestMcleaksData(String uuid) {
		return CompletableFuture.supplyAsync(() -> {
			Optional<String> optContent = getContentFromURL("https://mcleaks.themrgong.xyz/api/v3/isuuidmcleaks/" + uuid);
			if(optContent.isPresent()) {
				String content = optContent.get();
				if(content == null)
					Adapter.getAdapter().getLogger().warn("McLeaks API seem to be down. So, we cannot know if the player is using it.");
				return content;
			}
			return null;
		});
	}

	public static void doTrustToCertificates() throws KeyManagementException, NoSuchAlgorithmException {
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
					Adapter.getAdapter().getLogger().warn("Warning: URL host '" + urlHostName + "' is different to SSLSession host '"
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

	public static boolean isValidName(String name) {
		return name.matches("[0-9A-Za-z-_*]{3," + name.length() + "}");
	}
	
	/**
	 * Check if the given string contains a chinese characters
	 * 
	 * @param The string where we are looking for chinese char
	 * @return true if there is a chinese char
	 */
	public static boolean containsChineseCharacters(String s) {
	    return s.codePoints().anyMatch(codepoint ->
	            Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
	}
	
	public static void init() {
		getContentFromURL("https://google.fr");
	}
	
	public static Configuration loadConfig(File configFile, String configName) {
		if(!configFile.exists()) {
			try {
				URI migrationsDirUri = UniversalUtils.class.getResource("/assets/negativity").toURI();
				if (migrationsDirUri.getScheme().equals("jar")) {
					try (FileSystem jarFs = FileSystems.newFileSystem(migrationsDirUri, Collections.emptyMap())) {
						Path cheatPath = jarFs.getPath("/assets/negativity", configName);
						if(Files.isRegularFile(cheatPath)) {
							Files.copy(cheatPath, Paths.get(configFile.toURI()));
						} else {
							Adapter.getAdapter().getLogger().error("Cannot load config.");
							return null;
						}
					}
				}
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		return YamlConfiguration.load(configFile);
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
