package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SneakProtocol extends Cheat {

	public SneakProtocol() {
		super(CheatKeys.SNEAK, true, ItemTypes.BLAZE_POWDER, CheatCategory.MOVEMENT, true, "sneack", "sneac");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		boolean isSneaking = p.get(Keys.IS_SNEAKING).orElse(false);
		if (isSneaking && p.get(Keys.IS_SPRINTING).orElse(false) && !p.get(Keys.IS_FLYING).orElse(false) && np.contentBoolean.getOrDefault("sneak-was-sneaking", false)) {
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(105 - (Utils.getPing(p) / 10)), "Sneaking, sprinting and not flying.");
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
		np.contentBoolean.put("sneak-was-sneaking", isSneaking);
	}
}
