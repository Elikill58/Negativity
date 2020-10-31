package com.elikill58.negativity.universal.support;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Version;

import protocolsupport.api.ProtocolSupportAPI;

public class ProtocolSupportSupport {

	public static Version getPlayerVersion(Player p) {
		return Version.getVersionByProtocolID(ProtocolSupportAPI.getProtocolVersion((org.bukkit.entity.Player) p.getDefault()).getId());
	}
}
