package com.elikill58.negativity.api.events.plugins;

import java.util.List;

import com.elikill58.negativity.api.events.Event;

public class ProxyPluginListEvent implements Event {

	private final List<String> plugins;
	
	public ProxyPluginListEvent(List<String> plugins) {
		this.plugins = plugins;
	}
	
	public List<String> getPlugins() {
		return plugins;
	}
	
	public boolean contains(String other) {
		return plugins.contains(other);
	}
}
