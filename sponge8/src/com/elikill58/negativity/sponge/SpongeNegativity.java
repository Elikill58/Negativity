package com.elikill58.negativity.sponge;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

import com.google.inject.Inject;

@Plugin("negativity")
public class SpongeNegativity {

	private final Logger logger;
	private final PluginContainer container;

	@Inject
	public SpongeNegativity(Logger logger, PluginContainer container) {
		this.logger = logger;
		this.container = container;
	}

	@Listener
	public void onLoadedGame(LoadedGameEvent event) {
		logger.info("Hello from Negativity v{}", container.getMetadata().getVersion());
 	}
}
