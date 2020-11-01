package com.elikill58.negativity.api.events.others;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.events.Event;

public class TabExecutionEvent implements Event {

	private final CommandSender sender;
	private final String[] arg;
	private final String command, prefix;
	private List<String> tabContent;

	public TabExecutionEvent(String command, CommandSender sender, String[] arg, String prefix) {
		this.command = command;
		this.sender = sender;
		this.arg = arg;
		this.prefix = prefix;
		this.tabContent = new ArrayList<>();
	}

	public String getCommand() {
		return command;
	}

	public CommandSender getSender() {
		return sender;
	}

	public String[] getArgument() {
		return arg;
	}

	public String getPrefix() {
		return prefix;
	}

	public List<String> getTabContent() {
		return tabContent;
	}
	
	public void setTabContent(List<String> content) {
		this.tabContent = content;
	}
}
