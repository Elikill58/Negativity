package com.elikill58.negativity.universal.multiproxy;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;

public interface MultiProxy {

	/**
	 * Know if the proxy sender is using multiproxy
	 * 
	 * @return
	 */
	boolean isMultiProxy();
	
	void sendMessage(Player p, NegativityMessage message);
}
