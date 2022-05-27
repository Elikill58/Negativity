package com.elikill58.negativity.sponge7.impl.entity;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.commands.CommandSender;

public class SpongeCommandSender implements CommandSender {

	private final CommandSource sender;
	
	public SpongeCommandSender(CommandSource src) {
		this.sender = src;
	}
	
	@Override
	public void sendMessage(String msg) {
		sender.sendMessage(Text.of(msg));
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
