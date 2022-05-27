package com.elikill58.negativity.sponge.impl.entity;

import org.spongepowered.api.util.Nameable;

import com.elikill58.negativity.api.commands.CommandSender;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class SpongeCommandSender<T extends Audience & Nameable> implements CommandSender {

	private final T sender;
	
	public SpongeCommandSender(T src) {
		this.sender = src;
	}
	
	@Override
	public void sendMessage(String msg) {
		sender.sendMessage(Component.text(msg));
	}

	@Override
	public String getName() {
		return sender.name();
	}
	
	@Override
	public Object getDefault() {
		return sender;
	}
}
