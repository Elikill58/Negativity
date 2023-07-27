package com.elikill58.negativity.spigot.impl.plugin;

import com.elikill58.negativity.api.plugin.ExternalPlugin;
import org.bukkit.plugin.Plugin;

public class SpigotExternalPlugin extends ExternalPlugin {

    private final Plugin pl;

    public SpigotExternalPlugin(Plugin plugin) {
        this.pl = plugin;
    }

    @Override
    public String getId() {
        return pl.getName();
    }

    @Override
    public boolean isEnabled() {
        return pl.isEnabled();
    }

    @Override
    public Object getDefault() {
        return pl;
    }

    @Override
    public String getVersion() {
        return pl.getDescription().getVersion();
    }
}
