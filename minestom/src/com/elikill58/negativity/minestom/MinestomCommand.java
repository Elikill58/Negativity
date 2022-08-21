package com.elikill58.negativity.minestom;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;

public class MinestomCommand extends Command implements CommandExecutor {

	public MinestomCommand(String cmd, String... alias) {
		super(cmd, alias);
		setDefaultExecutor(this);
	}

	@Override
	public void apply(@NotNull CommandSender sender, @NotNull CommandContext context) {
		String input = context.getInput();
		String[] args = input.split(" ");
		args = Arrays.copyOfRange(args, 1, args.length);
		String prefix = "";
		if (args.length > 0) {
			prefix = args[args.length - 1];
		} else {
			args = new String[]{""};
		}
		EventManager.callEvent(new CommandExecutionEvent(getName(), MinestomEntityManager.getExecutor(sender), args, prefix));
	}
}
