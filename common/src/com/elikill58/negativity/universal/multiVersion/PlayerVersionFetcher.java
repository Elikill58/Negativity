package com.elikill58.negativity.universal.multiVersion;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Version;

public interface PlayerVersionFetcher {
	
	Version getPlayerVersion(Player player);
}
