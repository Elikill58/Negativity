package com.elikill58.negativity.sponge.protocols;

import java.text.NumberFormat;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.*;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ForceFieldProtocol extends Cheat {

	public ForceFieldProtocol() {
		super("FORCEFIELD", true, ItemTypes.DIAMOND_SWORD, true, true, "ff", "killaura");
	}
	
	@Listener
	public void onEntityDamageByEntity(DamageEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		double dis = e.getTargetEntity().getLocation().getPosition().distance(p.getLocation().getPosition());
		if(p.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
			if(p.getItemInHand(HandTypes.MAIN_HAND).get().getType().equals(ItemTypes.BOW))
				return;
		}
		if (dis > (Adapter.getAdapter().getDoubleInConfig("cheats.forcefield.reach") + (p.gameMode().get().equals(GameModes.CREATIVE) ? 1 : 0))) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumIntegerDigits(2);
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this,
					Utils.parseInPorcent(dis * 2 * 10),
					"Big distance with: " + e.getTargetEntity().getType().getName().toLowerCase() + ". Exact distance: "
							+ dis + ". Ping: " + Utils.getPing(p),
					"Distance with " + e.getTargetEntity().getType().getName() + ": " + nf.format(dis));
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
}
