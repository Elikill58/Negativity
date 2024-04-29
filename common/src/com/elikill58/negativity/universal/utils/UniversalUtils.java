package com.elikill58.negativity.universal.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.api.json.parser.ParseException;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.api.yaml.YamlConfiguration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerManager;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.Special;

public class UniversalUtils {

	public static final String BUNDLED_ASSETS_BASE = "/assets/negativity/";
	public static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
	public static boolean HAVE_INTERNET = true, checkAgainForMcleaks = true;

	public static int getMultipleOf(int i, int multiple, int more) {
		return getMultipleOf(i, multiple, more, Integer.MAX_VALUE);
	}

	public static int getMultipleOf(int i, int multiple, int more, int limit) {
		if(i > limit)
			return limit;
		while (i % multiple != 0 && (i < limit || limit == -1))
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

	public static Optional<Cheat> getCheatFromItem(Material m) {
		for (Cheat c : Cheat.values())
			if (c.getMaterial().equals(m))
				return Optional.of(c);
		return Optional.empty();
	}

	public static Optional<Special> getSpecialFromItem(Object m) {
		for (Special c : Special.values())
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
		return (all / divide);
	}

	public static boolean isMe(String uuid) {
		return uuid.equals("195dbcbc-9f2e-389e-82c4-3d017795ca65") || uuid.equals("3437a701-efaf-49d5-95d4-a8814e67760d");
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

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
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

	public static boolean isUUID(String s) {
		try {
			UUID.fromString(s);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	public static boolean getBoolean(String s) {
		return s.equalsIgnoreCase("true") || s.equals("1");
	}

	public static Optional<String> getContentFromURL(String url){
		return getContentFromURL(url, "");
	}
	
	/**
	 * Get content from URL. WARN: this run on the actual thread. Before calling this, use async.
	 * 
	 * @param urlName the URL
	 * @param post post value, or empty if use get
	 * @return result of empty
	 */
	public static Optional<String> getContentFromURL(String urlName, String post){
		try {
			return getContentFromURLWithException(urlName, post);
        } catch (SSLException | SocketTimeoutException e) {
        	Adapter.getAdapter().getLogger().warn("Failed to connect with the internet connection to check for update.");
        	HAVE_INTERNET = false;
        } catch (Exception e) {
        	Adapter.getAdapter().getLogger().info("An error occured while trying to make web request to: " + urlName);
        	e.printStackTrace();
		}
		return Optional.empty();
	}
	
	/**
	 * Get content from URL. WARN: this run on the actual thread. Before calling this, use async.
	 * 
	 * @param urlName the URL
	 * @param post post value, or empty if use get
	 * @return result of empty
	 * @throws Exception if something gone wrong
	 */
	public static Optional<String> getContentFromURLWithException(String urlName, String post) throws Exception {
		if(!HAVE_INTERNET)
			return Optional.empty();
		try {
			Adapter ada = Adapter.getAdapter();
			URL url = new URL(urlName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(true);
			connection.setRequestProperty("User-Agent", "Negativity" + ada.getName() + "/" + ada.getVersion() + "(Linux x64) GoogleChrome");
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
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
        	if(!HAVE_INTERNET)
    			return Optional.empty();
        	HAVE_INTERNET = false;
        	Adapter.getAdapter().getLogger().info("Could not use the internet connection to check for update or send stats");
        } catch (ConnectException e) {
        	HAVE_INTERNET = false;
        	if(containsChineseCharacters(e.getMessage())) {
            	Adapter.getAdapter().getLogger().info("As chinese people, you cannot access to the website " + urlName + ".");
        	} else
            	Adapter.getAdapter().getLogger().warn("Cannot connect to " + urlName + " (Reason: " + e.getMessage() + ").");
		}
		return Optional.empty();
	}
	
	/**
	 * Get content from URL. WARN: this run on the actual thread. Before calling this, use async.
	 * 
	 * @param urlName the URL
	 * @return result of empty
	 */
	public static List<String> getListFromURL(String urlName){
		try {
			return getListFromURLWithException(urlName, "");
        } catch (SSLException e) {
        	Adapter.getAdapter().getLogger().warn("Failed to connect with the internet connection to check for update or send stats.");
        } catch (Exception e) {
        	Adapter.getAdapter().getLogger().info("An error occured while trying to make web request to: " + urlName);
        	e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	/**
	 * Get content from URL. WARN: this run on the actual thread. Before calling this, use async.
	 * 
	 * @param urlName the URL
	 * @param post post value, or empty if use get
	 * @return result of empty
	 * @throws Exception if something gone wrong
	 */
	public static List<String> getListFromURLWithException(String urlName, String post) throws Exception {
		if(!HAVE_INTERNET)
			return Collections.emptyList();
		List<String> content = new ArrayList<>();
		try {
			Adapter ada = Adapter.getAdapter();
			URL url = new URL(urlName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(true);
			connection.setRequestProperty("User-Agent", "Negativity" + ada.getName() + "/" + ada.getVersion() + "(Linux x64) GoogleChrome");
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			if(!post.equalsIgnoreCase("")) {
				connection.setRequestMethod("POST");
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(post);
				writer.flush();
				writer.close();
			} else connection.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String input;
			while ((input = br.readLine()) != null)
				content.add(input);
			br.close();
        } catch (UnknownHostException | MalformedURLException e) {
        	if(!HAVE_INTERNET)
    			return Collections.emptyList();
        	HAVE_INTERNET = false;
        	Adapter.getAdapter().getLogger().info("Could not use the internet connection to check for update or send stats");
        } catch (ConnectException e) {
        	HAVE_INTERNET = false;
        	if(containsChineseCharacters(e.getMessage())) {
            	Adapter.getAdapter().getLogger().info("As chinese people, you cannot access to the website " + urlName + ".");
        	} else
            	Adapter.getAdapter().getLogger().warn("Cannot connect to " + urlName + " (Reason: " + e.getMessage() + ").");
		}
		return content;
	}

	public static Optional<String> getLatestVersionString() {
		return getContentFromURL("https://api.spigotmc.org/legacy/update.php?resource=86874");
	}
	
	public static Optional<SemVer> getLatestVersion() {
		return getLatestVersionString().map(SemVer::parse);
	}
	
	public static @Nullable SemVer getLatestVersionIfNewer() {
		SemVer currentVersion = SemVer.parse(Adapter.getAdapter().getPluginVersion());
		if (currentVersion == null) {
			return null;
		}
		
		SemVer latestVersion = getLatestVersion().orElse(null);
		if (latestVersion != null  && latestVersion.isNewerThan(currentVersion)) {
			return latestVersion;
		}
		
		return null;
	}

	/**
	 * Check if the UUID is a McLeaks account
	 * 
	 * @param playerId the player to check
	 * @return a completable boolean
	 */
	public static CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				Object data = new JSONParser().parse(response);
				if (data instanceof JSONObject) {
					JSONObject json = (JSONObject) data;
					Object isMcleaks = json.get("isMcleaks");
					if (isMcleaks != null) {
						return Boolean.getBoolean(isMcleaks.toString());
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return false;
		});
	}

	public static CompletableFuture<@Nullable String> requestMcleaksData(String uuid) {
		if(!checkAgainForMcleaks) // previously get error
			return CompletableFuture.completedFuture(null);
		if(isUUID(uuid)) {
			UUID id = UUID.fromString(uuid);
			if(BedrockPlayerManager.isBedrockPlayer(id))
				return CompletableFuture.supplyAsync(() -> "{ \"isMcleaks\": false }");
		}
		return CompletableFuture.supplyAsync(() -> {
			try {
				Optional<String> optContent = getContentFromURLWithException("https://mcleaks.themrgong.xyz/api/v3/isuuidmcleaks/" + uuid, "");
				if(optContent.isPresent()) {
					String content = optContent.get();
					if(content == null)
						Adapter.getAdapter().getLogger().warn("McLeaks API seem to be down. So, we cannot know if the player is using it.");
					return content;
				}
			} catch (Exception e) {
				checkAgainForMcleaks = false;
				Adapter.getAdapter().getLogger().warn("Can't check mcleaks for " + uuid + ". An error occurred: " + e.getMessage());
			}
			return null;
		});
	}

	public static void doTrustToCertificates() throws KeyManagementException, NoSuchAlgorithmException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				return;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				return;
			}
		} };
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
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
	public static String truncate(@Nullable String string, int maxLength) {
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
	 * @param s The string where we are looking for chinese char
	 * @return true if there is a chinese char
	 */
	public static boolean containsChineseCharacters(String s) {
	    return s.codePoints().anyMatch(codepoint ->
	            Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
	}
	
	/**
	 * Opens a bundled file as an InputStream
	 *
	 * @param name the name of the bundled file
	 *
	 * @return the InputStream of the bundled file, or null if it does not exist
	 */
	@Nullable
	public static InputStream openBundledFile(String name) {
		return UniversalUtils.class.getResourceAsStream(name);
	}
	
	/**
	 * Copies a bundled file to the file denoted by the given Path
	 *
	 * @param name the name of the bundled file
	 * @param destFile the file Path it will be copied to
	 *
	 * @return the file Path it is copied to, or null if the bundled file does not exist
	 *
	 * @throws IOException if an IO exception occurred
	 */
	@Nullable
	public static Path copyBundledFile(String name, Path destFile) throws IOException {
		if (Files.notExists(destFile)) {
			Files.createDirectories(destFile.getParent());
			try (InputStream bundled = openBundledFile(name)) {
				if (bundled == null) {
					return null;
				}
				Files.copy(bundled, destFile);
			}
		}
		return destFile;
	}
	
	public static Configuration loadConfig(File configFile, String configName) {
		if(!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			try (InputStream defaultConfigStream = UniversalUtils.class.getResourceAsStream("/assets/negativity/" + configName)) {
				if (defaultConfigStream == null) {
					Adapter.getAdapter().getLogger().error("Could not find default file '" + configName + "'");
					return null;
				}

				Files.copy(defaultConfigStream, configFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return YamlConfiguration.load(configFile);
	}
	
	public static String hexToString(byte[] data) {
		char[] chars = new char[data.length * 2];
		for (int i = 0; i < data.length; i++) {
			chars[i * 2] = HEX_DIGITS[(data[i] << 4) & 0xF];
			chars[i * 2 + 1] = HEX_DIGITS[data[i] & 0xF];
		}
		return new String(chars);
	}
	
	public static byte[] stringToHex(String string) {
		int length = string.length();
		if (length % 2 != 0) {
			throw new IllegalArgumentException("Input string must have an even amount of characters");
		}

		byte[] bytes = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {
			int h = hexToBin(string.charAt(i));
			int l = hexToBin(string.charAt(i + 1));
			if (h == -1 || l == -1) {
				throw new IllegalArgumentException("contains illegal character for hexBinary: " + string);
			}
			
			
			bytes[i / 2] = (byte) (h * 16 + l);
		}
		return bytes;
	}
	
	private static int hexToBin(char ch) {
		if ('0' <= ch && ch <= '9') {
			return ch - '0';
		}
		if ('A' <= ch && ch <= 'F') {
			return ch - 'A' + 10;
		}
		if ('a' <= ch && ch <= 'f') {
			return ch - 'a' + 10;
		}
		return -1;
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
