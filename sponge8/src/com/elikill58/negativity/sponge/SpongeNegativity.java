package com.elikill58.negativity.sponge;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

import com.elikill58.negativity.universal.Adapter;
import com.google.inject.Inject;

@Plugin("negativity")
public class SpongeNegativity {
	
	private static SpongeNegativity INSTANCE;

	private final Logger logger;
	private final PluginContainer container;

	@Inject
	public SpongeNegativity(Logger logger, PluginContainer container) {
		this.logger = logger;
		this.container = container;
		INSTANCE = this;
	}
	
	@Listener
	public void onConstructPlugin(ConstructPluginEvent event) {
		Adapter.setAdapter(new SpongeAdapter());
	}
	
	@Listener
	public void onLoadedGame(LoadedGameEvent event) {
		logger.info("Hello from Negativity v{}", container.getMetadata().getVersion());
 	}
	
	public static SpongeNegativity getInstance() {
		return INSTANCE;
	}
	
	public static PluginContainer container() {
		return INSTANCE.container;
	}
}
