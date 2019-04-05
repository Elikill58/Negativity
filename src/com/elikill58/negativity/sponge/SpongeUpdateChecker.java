package com.elikill58.negativity.sponge;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.Consumer;

import com.elikill58.negativity.universal.UpdateCheckResult;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

public class SpongeUpdateChecker {

	public static Optional<UpdateCheckResult> checkForUpdate() throws IOException {
		Optional<String> currentPluginVersion = SpongeNegativity.getInstance().getContainer().getVersion();
		if (!currentPluginVersion.isPresent())
			return Optional.empty();

		ConfigurationNode projectNode = GsonConfigurationLoader.builder()
				.setURL(new URL("https://ore.spongepowered.org/api/v1/projects/negativity"))
				.build().load();

		ConfigurationNode recommendedNode = projectNode.getNode("recommended");
		String recommendedVersion = recommendedNode.getNode("name").getString();
		if (recommendedVersion == null)
			return Optional.empty();

		String downloadUrlHref = recommendedNode.getNode("href").getString("/Elikill58/Negativity/versions");
		boolean isNewerVersion = !recommendedVersion.equals(currentPluginVersion.get());
		String downloadUrl = "https://ore.spongepowered.org" + downloadUrlHref;
		UpdateCheckResult result = new UpdateCheckResult(recommendedVersion, isNewerVersion, downloadUrl);
		return Optional.of(result);
	}

	public static void ifUpdateAvailable(Consumer<UpdateCheckResult> action) {
		try {
			UpdateCheckResult updateCheckResult = checkForUpdate().orElse(null);
			if (updateCheckResult == null || !updateCheckResult.isNewerVersion())
				return;

			action.accept(updateCheckResult);
		} catch (UnknownHostException e) {
			SpongeNegativity.getInstance().getLogger().warn("Cannot reach Ore to check for updates.");
		} catch (IOException e) {
			SpongeNegativity.getInstance().getLogger().warn("Unable to check for updates.", e);
		}
	}
}
