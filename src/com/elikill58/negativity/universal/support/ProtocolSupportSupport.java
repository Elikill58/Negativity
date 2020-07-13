package com.elikill58.negativity.universal.support;

import org.bukkit.entity.Player;

import com.elikill58.negativity.universal.Version;

import protocolsupport.api.ProtocolSupportAPI;

public class ProtocolSupportSupport {

	public static Version getPlayerVersion(Player p) {
		return Version.getVersionByProtocolID(ProtocolSupportAPI.getProtocolVersion(p).getId());
	}
}
