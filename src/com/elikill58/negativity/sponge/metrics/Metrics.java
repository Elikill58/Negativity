package com.elikill58.negativity.sponge.metrics;

import com.google.gson.JsonObject;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.List;

public interface Metrics {
    /**
     * Cancels this instance's scheduled data sending.
     */
    void cancel();

    /**
     * Gets all metrics instances known to this instance.
     * For taking over if replacing an older version.
     *
     * @return all known metrics instances to this instance
     */
    List<Metrics> getKnownMetricsInstances();

    /**
     * Gets the plugin specific data
     *
     * @return the plugin specific data or null if failure to acquire
     */
    JsonObject getPluginData();

    /**
     * Gets the plugin container for this instance.
     *
     * @return plugin container
     */
    PluginContainer getPluginContainer();

    /**
     * Gets the revision of this bStats instance.
     *
     * @return revision
     */
    int getRevision();

    /**
     * Links another metrics instance to this one, which should be the master instance.
     *
     * @param metrics metrics instance
     */
    void linkMetrics(Metrics metrics);
}