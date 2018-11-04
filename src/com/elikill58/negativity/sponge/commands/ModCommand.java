package com.elikill58.negativity.sponge.commands;

import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModCommand implements CommandExecutor, CommandCallable {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {
        if(!(sender instanceof Player))
            return CommandResult.empty();
        Player p = (Player) sender;
        if(!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "mod"))
            Messages.sendMessage(p, "not_permission");
        else
            Inv.openModMenu(p);
        return CommandResult.empty();
    }

    @Override
    public CommandResult process(CommandSource sender, String arguments) throws CommandException {
        if(!(sender instanceof Player))
            return CommandResult.empty();
        Player p = (Player) sender;
        if(!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "mod"))
            Messages.sendMessage(p, "not_permission");
        else
            Inv.openModMenu(p);
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return new ArrayList<>();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
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
        return Text.of("/mod");
    }
}
