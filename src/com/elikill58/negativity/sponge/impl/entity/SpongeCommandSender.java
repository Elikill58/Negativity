package com.elikill58.negativity.sponge.impl.entity;

import com.elikill58.negativity.api.commands.CommandSender;

public class SpongeCommandSender extends CommandSender {

	private final org.bukkit.command.CommandSender sender;
	
	public SpongeCommandSender(org.bukkit.command.CommandSender sender) {
		this.sender = sender;
	}
	
	@Override
	public void sendMessage(String msg) {
		sender.sendMessage(msg);
	}

	@Override
	public String getName() {
		return sender.getName();
	}
	
	@Override
	public Object getDefault() {
		return sender;
	}
}
