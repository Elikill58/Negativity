package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;

public class ReportCommand implements CommandExecutor {

	public static final List<String> REPORT_LAST = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(TranslatedMessages.getStringFromLang(TranslatedMessages.DEFAULT_LANG, "only_player"));
			return false;
		}
		Player p = (Player) sender;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if(np.TIME_REPORT > System.currentTimeMillis() && !Perm.hasPerm(np, "report_wait")){
			Messages.sendMessage(p, "report_wait");
			return false;
		}
		if (arg.length < 2)
			Messages.sendMessage(p, "report.report_usage");
		else {
			Player cible = Bukkit.getPlayer(arg[0]);
			if (cible == null) {
				Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
				return false;
			}
			String reason = "";
			for (String s : arg)
				if (!(s.equalsIgnoreCase(arg[0])))
					reason = reason + s + " ";
			String msg = Messages.getMessage(p, "report.report_message", "%name%", cible.getName(), "%report%",
					p.getName(), "%reason%", reason);
			if (SpigotNegativity.isOnBungeecord)
				SpigotNegativity.sendReportMessage(p, cible.getName() + "/**/" + reason + "/**/" + p.getName());
			else {
				boolean hasOp = false;
				for (Player pl : Utils.getOnlinePlayers())
					if (Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(pl), "showAlert")) {
						hasOp = true;
						pl.sendMessage(msg);
					}
				if (!hasOp)
					REPORT_LAST.add(msg);
			}
			Messages.sendMessage(p, "report.well_report", "%name%", cible.getName());
			np.TIME_REPORT = System.currentTimeMillis() + Adapter.getAdapter().getIntegerInConfig("time_between_report");
			if(SuspectManager.WITH_REPORT && SuspectManager.ENABLED)
				SuspectManager.analyzeText(np, msg);
		}
		return false;
	}
}
