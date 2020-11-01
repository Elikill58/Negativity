package com.elikill58.negativity.api.commands;

import com.elikill58.negativity.api.NegativityObject;

public abstract class CommandSender extends NegativityObject {
	
	public abstract void sendMessage(String msg);

	public abstract String getName();
}
