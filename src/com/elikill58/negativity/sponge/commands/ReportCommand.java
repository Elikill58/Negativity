package com.elikill58.negativity.sponge.commands;

import static org.spongepowered.api.command.args.GenericArguments.player;
import static org.spongepowered.api.command.args.GenericArguments.remainingJoinedStrings;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class ReportCommand implements CommandExecutor {

	public static final List<Text> REPORT_LAST = new ArrayList<>();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player playerSource = (Player) src;
		SpongeNegativityPlayer nPlayerSource = SpongeNegativityPlayer.getNegativityPlayer(playerSource);
		if (nPlayerSource.TIME_REPORT > System.currentTimeMillis() && !Perm.hasPerm(nPlayerSource, Perm.REPORT_WAIT))
			throw new CommandException(Messages.getMessage(playerSource, "report_wait"));

		Player targetPlayer = args.requireOne("target");
		String reason = args.requireOne("reason");

		String message = Messages.getStringMessage(playerSource, "report.report_message",
				"%name%", targetPlayer.getName(), "%report%", playerSource.getName(), "%reason%", reason);
		if (ProxyCompanionManager.isIntegrationEnabled()) {
			SpongeNegativity.sendReportMessage(playerSource, reason, targetPlayer.getName());
		} else {
			Text spongeMsg = Text.builder(message)
					.onHover(TextActions.showText(Messages.getMessage(playerSource, "report.report_message_hover", "%name%", targetPlayer.getName())))
					.onClick(TextActions.runCommand("/negativity " + targetPlayer.getName()))
					.build();
			boolean hasOp = false;
			for (Player onlinePlayer : Utils.getOnlinePlayers()) {
				if (Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(onlinePlayer), Perm.SHOW_REPORT)) {
					hasOp = true;
					onlinePlayer.sendMessage(spongeMsg);
				}
			}

			if (!hasOp)
				REPORT_LAST.add(spongeMsg);
		}

		Messages.sendMessage(playerSource, "report.well_report", "%name%", targetPlayer.getName());
		nPlayerSource.TIME_REPORT = System.currentTimeMillis() + Adapter.getAdapter().getConfig().getInt("time_between_report");
		if (SuspectManager.WITH_REPORT && SuspectManager.ENABLED)
			SuspectManager.analyzeText(nPlayerSource, message);

		return CommandResult.success();
	}

	public static CommandCallable create() {
		CommandSpec command = CommandSpec.builder()
				.executor(new ReportCommand())
				.arguments(player(Text.of("target")),
						remainingJoinedStrings(Text.of("reason")))
				.build();
		return new NegativityCmdWrapper(command, true, Perm.REPORT);
	}
}
