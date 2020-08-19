package com.elikill58.negativity.sponge.commands.child;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;

public class ModCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		InventoryManager.open(NegativityInventory.MOD, SpongeEntityManager.getPlayer((Player) src));
		return CommandResult.success();
	}

	public static CommandCallable create() {
		return CommandSpec.builder()
				.executor(new ModCommand())
				.permission("negativity.mod")
				.build();
	}
}
