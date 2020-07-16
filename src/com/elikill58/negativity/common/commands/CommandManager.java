package com.elikill58.negativity.common.commands;

import java.util.HashMap;

import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.TabListeners;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;

public class CommandManager implements Listeners {

	private final HashMap<String, CommandListeners> commands = new HashMap<>();
	private final HashMap<String, TabListeners> tabs = new HashMap<>();
	
	public CommandManager() {
		NegativityCommand negativity = new NegativityCommand();
		commands.put("negativity", negativity);
		tabs.put("negativity", negativity);
		
		ConfigAdapter conf = Adapter.getAdapter().getConfig();
		if(conf.getBoolean("commands.ban")) {
			BanCommand ban = new BanCommand();
			commands.put("ban", ban);
			tabs.put("ban", ban);
		}
		if(conf.getBoolean("commands.kick")) {
			KickCommand kick = new KickCommand();
			commands.put("kick", kick);
			tabs.put("kick", kick);
		}
		if(conf.getBoolean("commands.lang")) {
			LangCommand lang = new LangCommand();
			commands.put("lang", lang);
			tabs.put("lang", lang);
		}
		if(conf.getBoolean("commands.mod")) {
			ModCommand mod = new ModCommand();
			commands.put("mod", mod);
		}
		if(conf.getBoolean("commands.report")) {
			ReportCommand report = new ReportCommand();
			commands.put("report", report);
			tabs.put("report", report);
		}
		if(conf.getBoolean("commands.unban")) {
			UnbanCommand unban = new UnbanCommand();
			commands.put("unban", unban);
			tabs.put("unban", unban);
		}
	}
	
	@EventListener
	public void onCommand(CommandExecutionEvent e) {
		CommandListeners cmd = commands.get(e.getCommand().toLowerCase());
		if(cmd != null)
			cmd.onCommand(e.getSender(), e.getArgument(), e.getPrefix());
	}
	
	@EventListener
	public void onTab(TabExecutionEvent e) {
		TabListeners cmd = tabs.get(e.getCommand().toLowerCase());
		if(cmd != null)
			cmd.onTabComplete(e.getSender(), e.getArgument(), e.getPrefix());
	}
	
}
