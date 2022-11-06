package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.protocols.CheckData;

public class ChatData extends CheckData {

	public PlayerChatEvent lastChatEvent;
	public int amountSameMessage = 0;
	public long timeLastMessage = 0;
	
	public ChatData(NegativityPlayer np) {
		super(np);
	}
}
