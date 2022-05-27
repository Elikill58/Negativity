package com.elikill58.negativity.sponge9;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import com.elikill58.negativity.universal.utils.SemVer;

public class SpongeUpdateChecker {
	
	private static final String BASE_URL = "https://ore.spongepowered.org/api/v2/";
	
	private static @Nullable String latestVersionString;
	
	private static boolean checkForUpdate() throws IOException {
		SemVer currentVersion = SemVer.parse(SpongeNegativity.getInstance().getContainer().metadata().version().toString());
		if (currentVersion == null) {
			return false;
		}
		
		String session = openSession();
		try {
			HttpURLConnection connection = prepareConnection("projects/negativity/versions?limit=1", session);
			ConfigurationNode response = readJsonResponse(connection);
			String versionString = response.node("result", 0, "name").getString();
			if (versionString == null) {
				return false;
			}
			
			latestVersionString = versionString;
			SemVer latestVersion = SemVer.parse(versionString);
			return latestVersion != null && latestVersion.isNewerThan(currentVersion);
		} finally {
			closeSession(session);
		}
	}
	
	private static String openSession() throws IOException {
		HttpURLConnection connection = prepareConnection("authenticate", null);
		connection.setRequestMethod("POST");
		ConfigurationNode response = readJsonResponse(connection);
		String session = response.node("session").getString();
		if (session == null) {
			throw new IOException("Could not open OreApi session: " + toJson(response));
		}
		return session;
	}
	
	private static void closeSession(String session) throws IOException {
		HttpURLConnection connection = prepareConnection("sessions/current", session);
		connection.setRequestMethod("DELETE");
		connection.connect();
		if (connection.getResponseCode() != 204) {
			SpongeNegativity.getInstance().getLogger().error("Could not close Ore API session correctly: {} - {}",
				connection.getResponseCode(), connection.getResponseMessage());
		}
	}
	
	private static HttpURLConnection prepareConnection(String href, @Nullable String session) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + href).openConnection();
		connection.setRequestProperty("User-Agent", "Negativity");
		if (session != null) {
			connection.addRequestProperty("Authorization", "OreApi session=\"" + session + "\"");
		}
		return connection;
	}
	
	private static ConfigurationNode readJsonResponse(HttpURLConnection connection) throws IOException {
		return GsonConfigurationLoader.builder()
			.source(() -> new BufferedReader(new InputStreamReader(connection.getInputStream())))
			.build().load();
	}
	
	private static String toJson(ConfigurationNode node) throws IOException {
		try (StringWriter writer = new StringWriter()) {
			GsonConfigurationLoader.builder().sink(() -> new BufferedWriter(writer)).build().save(node);
			return writer.toString();
		}
	}
	
	public static boolean isUpdateAvailable() {
		try {
			return checkForUpdate();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getDownloadUrl() {
		return "https://ore.spongepowered.org/Elikill58/Negativity/versions/" + latestVersionString;
	}
	
	public static @Nullable String getVersionString() {
		return latestVersionString;
	}
}
