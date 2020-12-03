package com.elikill58.negativity.universal;

/**
 * Implemented on extensions that should only be registered if the plugin identified by
 * the provided {@link #getPluginId() plugin id} is enabled on the server.
 *
 * @see Adapter#hasPlugin
 * @see Negativity#loadExtensions
 */
public interface PluginDependentExtension {

	String getPluginId();
}
