package com.elikill58.negativity.sponge.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class ReportCommand implements CommandCallable, CommandExecutor {

    public static final List<Text> REPORT_LAST = new ArrayList<>();
    
	@Override
    public CommandResult process(CommandSource sender, String arguments) throws CommandException {
        if (!(sender instanceof Player))
            return CommandResult.empty();
        String[] arg = arguments.split(" ");
        Player p = (Player) sender;
        SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
        if(np.TIME_REPORT > System.currentTimeMillis() && !Perm.hasPerm(np, "report_wait")){
            Messages.sendMessage(p, "report_wait");
            return CommandResult.empty();
        }
        if (arg.length < 2)
            Messages.sendMessage(p, "report.report_usage");
        else {
            Optional<Player> optcible = Sponge.getServer().getPlayer(arg[0]);
            if (!optcible.isPresent()) {
                Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
                return CommandResult.empty();
            }
            Player cible = optcible.get();
            String reason = "";
            for (String s : arg)
                if (!(s.equalsIgnoreCase(arg[0])))
                    reason = reason + s + " ";
            String stringMsg = Messages.getStringMessage(p, "report.report_message", "%name%", cible.getName(), "%report%",
                    p.getName(), "%reason%", reason);
            if (SpongeNegativity.isOnBungeecord)
            	SpongeNegativity.sendReportMessage(p, stringMsg, cible.getName());
            else {
            	Text msg = Text.of(stringMsg);
                boolean hasOp = false;
                for (Player pl : Utils.getOnlinePlayers())
                    if (Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(pl), "showAlert")) {
                        hasOp = true;
                        p.sendMessage(msg);
                    }
                if (!hasOp)
                    REPORT_LAST.add(msg);
            }
            Messages.sendMessage(p, "report.well_report", "%name%", cible.getName());
            np.TIME_REPORT = System.currentTimeMillis() + Adapter.getAdapter().getIntegerInConfig("time_between_report");
			if(SuspectManager.WITH_REPORT && SuspectManager.ENABLED)
				SuspectManager.analyzeText(np, stringMsg);
        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        List<String> s = new ArrayList<>();
        Sponge.getServer().getOnlinePlayers().forEach((p) -> {
            s.add(p.getName());
        });
        return s;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return false;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        if(!(source instanceof Player))
            return Text.of(Messages.getMessage("report.report_usage"));
        return Messages.getMessage((Player) source, "report.report_usage");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return null;
    }
}
