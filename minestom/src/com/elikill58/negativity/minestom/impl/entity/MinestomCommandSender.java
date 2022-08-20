package com.elikill58.negativity.minestom.impl.entity;

import com.elikill58.negativity.api.commands.CommandSender;

public class MinestomCommandSender implements CommandSender {

	private final net.minestom.server.command.CommandSender sender;
	
	public MinestomCommandSender(net.minestom.server.command.CommandSender src) {
		this.sender = src;
	}
	
	@Override
	public void sendMessage(String msg) {
		sender.sendMessage(msg);
	}

	@Override
	public String getName() {
		return "console";
	}
	
	@Override
	public Object getDefault() {
		return sender;
	}
}
