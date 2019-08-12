package com.elikill58.negativity.sponge;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

public class SpongeUpdateChecker {
	
	private static ConfigurationNode recommendedNode;
	
	public static boolean checkForUpdate() throws IOException {
		Optional<String> currentPluginVersion = SpongeNegativity.getInstance().getContainer().getVersion();
		if (!currentPluginVersion.isPresent())
			return false;

		ConfigurationNode projectNode = GsonConfigurationLoader.builder()
				.setURL(new URL("https://ore.spongepowered.org/api/v1/projects/negativity"))
				.build().load();

		recommendedNode = projectNode.getNode("recommended");
		String recommendedVersion = recommendedNode.getNode("name").getString();
		if (recommendedVersion == null)
			return false;

		//String downloadUrlHref = recommendedNode.getNode("href").getString("/Elikill58/Negativity/versions");
		//boolean isNewerVersion = !recommendedVersion.equals(currentPluginVersion.get());
		//String downloadUrl = "https://ore.spongepowered.org" + downloadUrlHref;
		return recommendedVersion.equals(currentPluginVersion.get());
	}

	public static boolean ifUpdateAvailable() {
		try {
			return checkForUpdate();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getDownloadUrl() {
		return "https://ore.spongepowered.org" + recommendedNode.getNode("href").getString("/Elikill58/Negativity/versions");
	}
	
	public static String getVersionString() {
		return recommendedNode.getNode("name").getString();
	}
}
