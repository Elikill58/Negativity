package com.elikill58.negativity.common.special;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Special;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.keys.SpecialKeys;

public class WorldDownloader extends Special implements Listeners {

	public WorldDownloader() {
		super(SpecialKeys.WORLD_DOWNLOADER, Materials.GRASS, false);
		Adapter.getAdapter().registerNewIncomingChannel(Version.getVersion().isNewerOrEquals(Version.V1_13) ? "wdl:init" : "WDL|INIT", (p, data) -> {
			if(!isActive())
				return;
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
			if(np.booleans.get(getKey(), "already-logged", false)) {
				if(getConfig().getBoolean("ban.active", false)) {
					if(!BanManager.banActive) {
						Adapter.getAdapter().getLogger().warn("Cannot ban player " + p.getName() + " for " + getName() + " because ban is NOT config.");
						Adapter.getAdapter().getLogger().warn("Please, enable ban in config and restart your server");
						if(getConfig().getBoolean("kick", true)) {
							p.kick(Messages.getMessage(p, "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
						}
					} else {
						BanManager.executeBan(Ban.active(p.getUniqueId(), getName(), "Negativity", BanType.PLUGIN,
								System.currentTimeMillis() + getConfig().getLong("ban.time", 2629800000l), "world_downloader", p.getIP()));
					}
				} else if(getConfig().getBoolean("kick", true)) {
					p.kick(Messages.getMessage(p, "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
				}
			}
			np.booleans.set(getKey(), "already-logged", true);
		});
	}

}
