package com.elikill58.negativity.api.commands;

import java.util.HashMap;
import java.util.Locale;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.common.commands.KickCommand;
import com.elikill58.negativity.common.commands.LangCommand;
import com.elikill58.negativity.common.commands.ModCommand;
import com.elikill58.negativity.common.commands.NegativityCommand;
import com.elikill58.negativity.common.commands.NegativityTpCommand;
import com.elikill58.negativity.common.commands.ReportCommand;
import com.elikill58.negativity.common.commands.WarnCommand;
import com.elikill58.negativity.common.commands.ban.BanCommand;
import com.elikill58.negativity.common.commands.ban.UnbanCommand;
import com.elikill58.negativity.common.commands.chat.ClearChatCommand;
import com.elikill58.negativity.common.commands.chat.LockChatCommand;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.warn.WarnManager;

public class CommandManager implements Listeners {

	private final HashMap<String, CommandListeners> commands = new HashMap<>();
	private final HashMap<String, TabListeners> tabs = new HashMap<>();
	
	public CommandManager() {
		NegativityCommand negativity = new NegativityCommand();
		commands.put("negativity", negativity);
		tabs.put("negativity", negativity);

		EventManager.registerEvent(new NegativityTpCommand());
		
		Configuration conf = Adapter.getAdapter().getConfig();
		if(conf.getBoolean("commands.kick", true)) {
			KickCommand kick = new KickCommand();
			commands.put("nkick", kick);
			tabs.put("nkick", kick);
		}
		if(conf.getBoolean("commands.lang", true)) {
			LangCommand lang = new LangCommand();
			commands.put("nlang", lang);
			tabs.put("nlang", lang);
		}
		if(conf.getBoolean("commands.mod", true)) {
			commands.put("nmod", new ModCommand());
		}
		if(conf.getBoolean("commands.report", true)) {
			ReportCommand report = new ReportCommand();
			commands.put("nreport", report);
			tabs.put("nreport", report);
		}
		if(conf.getBoolean("commands.chat.clear", true)) {
			ClearChatCommand clearchat = new ClearChatCommand();
			commands.put("nclearchat", clearchat);
		}
		if(conf.getBoolean("commands.chat.lock", true)) {
			LockChatCommand lockchat = new LockChatCommand();
			commands.put("nlockchat", lockchat);
			EventManager.registerEvent(lockchat);
		}
		
		conf = BanManager.getBanConfig();
		if(conf.getBoolean("commands.ban", false)) {
			BanCommand ban = new BanCommand();
			commands.put("nban", ban);
			tabs.put("nban", ban);
		}
		if(conf.getBoolean("commands.unban", false)) {
			UnbanCommand unban = new UnbanCommand();
			commands.put("nunban", unban);
			tabs.put("nunban", unban);
		}
		
		conf = WarnManager.getWarnConfig();
		if(conf.getBoolean("commands.warn", false)) {
			WarnCommand unban = new WarnCommand();
			commands.put("nwarn", unban);
			tabs.put("nwarn", unban);
		}
	}
	
	@EventListener
	public void onCommand(CommandExecutionEvent e) {
		CommandListeners cmd = commands.get(e.getCommand().toLowerCase(Locale.ROOT));
		if(cmd != null) {
			e.setManagedByNegativity(true);
			e.setGoodResult(cmd.onCommand(e.getSender(), e.getArgument(), e.getPrefix()));
		}
	}
	
	@EventListener
	public void onTab(TabExecutionEvent e) {
		TabListeners cmd = tabs.get(e.getCommand().toLowerCase(Locale.ROOT));
		if(cmd != null)
			e.setTabContent(cmd.onTabComplete(e.getSender(), e.getArgument(), e.getPrefix()));
	}
	
}
