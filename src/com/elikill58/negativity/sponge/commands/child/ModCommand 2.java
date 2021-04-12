package com.elikill58.negativity.sponge.commands.child;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.inventories.AbstractInventory;
import com.elikill58.negativity.sponge.inventories.AbstractInventory.InventoryType;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;
import com.elikill58.negativity.universal.permissions.Perm;

public class ModCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		AbstractInventory.open(InventoryType.MOD, (Player) src);
		return CommandResult.success();
	}

	public static CommandCallable create() {
		return new NegativityCmdWrapper(
				CommandSpec.builder().executor(new ModCommand()).permission("negativity.mod").build(), true, Perm.MOD);
	}
}
