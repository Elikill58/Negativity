package com.elikill58.negativity.api.commands;

import com.elikill58.negativity.api.NegativityObject;

public interface CommandSender extends NegativityObject {
	
	void sendMessage(String msg);

	String getName();
}
