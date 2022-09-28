package com.elikill58.negativity.fabric.impl.entity;

import com.elikill58.negativity.api.commands.CommandSender;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class FabricCommandSender implements CommandSender {

	private final ServerCommandSource sender;
	
	public FabricCommandSender(ServerCommandSource src) {
		this.sender = src;
	}
	
	@Override
	public void sendMessage(String msg) {
		sender.sendFeedback(Text.of(msg), false);
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
