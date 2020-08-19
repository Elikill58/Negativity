package com.elikill58.negativity.sponge.commands.child;

import static com.elikill58.negativity.universal.verif.VerificationManager.CONSOLE;
import static org.spongepowered.api.command.args.GenericArguments.allOf;
import static org.spongepowered.api.command.args.GenericArguments.choices;
import static org.spongepowered.api.command.args.GenericArguments.integer;
import static org.spongepowered.api.command.args.GenericArguments.optionalWeak;
import static org.spongepowered.api.command.args.GenericArguments.player;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.impl.entity.SpongePlayer;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.verif.VerificationManager;
import com.elikill58.negativity.universal.verif.Verificator;

public class VerifCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player targetPlayer = args.<Player>getOne("target").orElse(null);
		if (targetPlayer == null) {
			throw new CommandException(Messages.getMessage(src, "only_player"));
		}

		Set<Cheat> cheats = new LinkedHashSet<>(args.getAll("cheats"));
		NegativityPlayer targetNPlayer = NegativityPlayer.getNegativityPlayer(targetPlayer.getUniqueId(), () -> new SpongePlayer(targetPlayer));
		int time = args.<Integer>getOne("time").orElse(VerificationManager.getTimeVerif() / 20);
		if (cheats.isEmpty()) {
			targetNPlayer.startAllAnalyze();
			Messages.sendMessage(src, "negativity.verif.start_all", "%name%", targetPlayer.getName(), "%time%", time);
			cheats.addAll(Cheat.CHEATS);
		} else {
			cheats.forEach(targetNPlayer::startAnalyze);
			String cheatNamesList = cheats.stream().map(Cheat::getName).collect(Collectors.joining(", "));
			Messages.sendMessage(src, "negativity.verif.start", "%name%", targetPlayer.getName(), "%cheat%", cheatNamesList, "%time%", time);
		}
		UUID askerUUID = (src instanceof Player ? ((Player) src).getUniqueId() : CONSOLE);
		VerificationManager.create(askerUUID, targetPlayer.getUniqueId(), new Verificator(targetNPlayer, src.getName(), cheats));
		SpongeNegativity pl = SpongeNegativity.getInstance();
		Sponge.getScheduler().createTaskBuilder().execute(() -> {
			Verificator verif = VerificationManager.getVerificationsFrom(targetPlayer.getUniqueId(), askerUUID).get();
			verif.generateMessage();
			verif.getMessages().forEach((s) -> src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&a[&2Verif&a] " + s)));
			verif.save();
			VerificationManager.remove(askerUUID, targetPlayer.getUniqueId());
		}).delay(time, TimeUnit.SECONDS).submit(pl);

		return CommandResult.success();
	}

	public static CommandCallable create() {
		CommandSpec command = CommandSpec.builder()
				.executor(new VerifCommand())
				.arguments(player(Text.of("target")),
						optionalWeak(integer(Text.of("time"))),
						allOf(choices(Text.of("cheats"), Cheat.getCheatByKeys(), true, false)))
				.build();
		return new NegativityCmdWrapper(command, false, Perm.VERIF);
	}
}
