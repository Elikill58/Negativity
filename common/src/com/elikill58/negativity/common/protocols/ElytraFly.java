package com.elikill58.negativity.common.protocols;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.ElytraFlyData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ElytraFly extends Cheat implements Listeners {

	private final List<Material> interactBypassTypes = Arrays.asList(Materials.FIREWORK, Materials.TRIDENT);
	
	public ElytraFly() {
		super(CheatKeys.ELYTRA_FLY, CheatCategory.COMBAT, Materials.ELYTRA, ElytraFlyData::new, CheatDescription.VERIF);
	}

	@Check(name = "diff-y", description = "Get move after a tick", conditions = { CheckConditions.ELYTRA, CheckConditions.SURVIVAL, CheckConditions.NO_USE_TRIDENT })
	public void onMove(PlayerMoveEvent e, NegativityPlayer np, ElytraFlyData data) {
		Player p = e.getPlayer();
		
		if (p.isOnGround())
			data.useBypass = false;

		double diffYtoFromBasic = e.getTo().getY() - e.getFrom().getY();
		double diffYtoFrom = diffYtoFromBasic - Math.abs(e.getTo().getDirection().getY());
		if (diffYtoFrom > 0.1 && !data.useBypass) {
			int amount = (int) (diffYtoFrom * 10);
			if(amount == 0)
				amount = 1;
			Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffYtoFrom * 80), "diff-y", "Diff basic: " + diffYtoFromBasic + ", diff from: " + diffYtoFrom, null, amount);
		}
	}

	@EventListener
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction().name().contains("RIGHT_CLICK") && p.hasElytra() && p.getItemInHand() != null && interactBypassTypes.contains(p.getItemInHand().getType())) {
			NegativityPlayer.getNegativityPlayer(p).<ElytraFlyData>getCheckData(this).useBypass = true;
		}
	}
}
