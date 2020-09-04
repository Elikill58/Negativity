package com.elikill58.negativity.universal.support;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Version;

public class ProtocolSupportSupport {

	public static Version getPlayerVersion(Player p) {
		return null;//Version.getVersionByProtocolID(ProtocolSupportAPI.getProtocolVersion(p.getUniqueId()).getId());
	}
}
