package com.elikill58.negativity.spigot.listeners;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.channel.GameChannelNegativityMessageEvent;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.logger.Debug;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Arrays;
import java.util.HashMap;

public class ChannelListeners implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] data) {
        if (channel.equalsIgnoreCase(SpigotNegativity.CHANNEL_NAME_FML) && data[0] == 2) {
            NegativityPlayer.getCached(p.getUniqueId()).mods.putAll(getModData(data));
            return;
        }

        if (channel.equalsIgnoreCase(NegativityMessagesManager.CHANNEL_ID)) {
            Adapter.getAdapter().debug(Debug.GENERAL, "Receiving channel message");
            EventManager.callEvent(new GameChannelNegativityMessageEvent(SpigotEntityManager.getPlayer(p), data));
        }
    }

    private HashMap<String, String> getModData(byte[] data) {
        HashMap<String, String> mods = new HashMap<>();
        boolean store = false;
        String tempName = null;
        for (int i = 2; i < data.length; store = !store) {
            int end = i + data[i] + 1;
            byte[] range = Arrays.copyOfRange(data, i + 1, end);
            String string = new String(range);
            if (store)
                mods.put(tempName, string);
            else
                tempName = string;
            i = end;
        }
        return mods;
    }
}
