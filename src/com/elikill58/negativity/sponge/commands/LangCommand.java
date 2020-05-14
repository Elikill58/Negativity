package com.elikill58.negativity.sponge.commands;

import java.util.function.Function;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.permissions.Perm;

public class LangCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}

		String language = args.requireOne("language");
		NegativityAccount account = Adapter.getAdapter().getNegativityAccount(((Player) src).getUniqueId());
		account.setLang(language);
		NegativityAccountStorage.getStorage().saveAccount(account);

		Messages.sendMessage(src, "lang.language_set");

		return CommandResult.success();
	}

	public static CommandCallable create() {
		CommandSpec command = CommandSpec.builder()
				.executor(new LangCommand())
				.arguments(GenericArguments.choices(Text.of("language"), () -> TranslatedMessages.LANGS, Function.identity()))
				.build();
		return new NegativityCmdWrapper(command, true, Perm.LANG);
	}
}
