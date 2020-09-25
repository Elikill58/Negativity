package com.elikill58.negativity.api.commands;

import java.util.HashMap;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.common.commands.BanCommand;
import com.elikill58.negativity.common.commands.KickCommand;
import com.elikill58.negativity.common.commands.LangCommand;
import com.elikill58.negativity.common.commands.ModCommand;
import com.elikill58.negativity.common.commands.NegativityCommand;
import com.elikill58.negativity.common.commands.ReportCommand;
import com.elikill58.negativity.common.commands.UnbanCommand;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BanManager;

public class CommandManager implements Listeners {

	private final HashMap<String, CommandListeners> commands = new HashMap<>();
	private final HashMap<String, TabListeners> tabs = new HashMap<>();
	
	public CommandManager() {
		NegativityCommand negativity = new NegativityCommand();
		commands.put("negativity", negativity);
		tabs.put("negativity", negativity);
		
		Configuration conf = Adapter.getAdapter().getConfig();
		if(conf.getBoolean("commands.kick")) {
			KickCommand kick = new KickCommand();
			commands.put("nkick", kick);
			tabs.put("nkick", kick);
		}
		if(conf.getBoolean("commands.lang")) {
			LangCommand lang = new LangCommand();
			commands.put("nlang", lang);
			tabs.put("nlang", lang);
		}
		if(conf.getBoolean("commands.mod")) {
			commands.put("nmod", new ModCommand());
		}
		if(conf.getBoolean("commands.report")) {
			ReportCommand report = new ReportCommand();
			commands.put("nreport", report);
			tabs.put("nreport", report);
		}
		
		conf = BanManager.getBanConfig();
		if(conf.getBoolean("commands.ban")) {
			BanCommand ban = new BanCommand();
			commands.put("nban", ban);
			tabs.put("nban", ban);
		}
		if(conf.getBoolean("commands.unban")) {
			UnbanCommand unban = new UnbanCommand();
			commands.put("nunban", unban);
			tabs.put("nunban", unban);
		}
	}
	
	@EventListener
	public void onCommand(CommandExecutionEvent e) {
		CommandListeners cmd = commands.get(e.getCommand().toLowerCase());
		if(cmd != null)
			e.setGoodResult(cmd.onCommand(e.getSender(), e.getArgument(), e.getPrefix()));
	}
	
	@EventListener
	public void onTab(TabExecutionEvent e) {
		TabListeners cmd = tabs.get(e.getCommand().toLowerCase());
		if(cmd != null)
			e.setTabContent(cmd.onTabComplete(e.getSender(), e.getArgument(), e.getPrefix()));
	}
	
}
