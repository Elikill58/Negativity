package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class Step extends Cheat implements Listeners {

	public static final DataType<Double> BLOCKS_UP = new DataType<Double>("blocks_up", "Blocks UP", () -> new DoubleDataCounter());
	
	public Step() {
		super(CheatKeys.STEP, false, Materials.SLIME_BLOCK, CheatCategory.MOVEMENT, true);
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.hasElytra() || p.getItemInHand().getType().getId().contains("TRIDENT") || np.isUsingSlimeBlock || p.isSwimming() ||
				p.isFlying() || LocationUtils.isUsingElevator(p))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double dif = to.getY() - from.getY();
		if(checkActive("dif")) {
			if (!p.hasPotionEffect(PotionEffectType.JUMP) && dif > 0) {
				int ping = p.getPing(), relia = UniversalUtils.parseInPorcent(dif * 50);
				if ((dif > 1.499) && ping < 200) {
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, relia, "dif", "Move " + dif + " blocks up.", hoverMsg("main", "%block%", String.format("%.2f", dif)));
					if (isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
		}
		if(checkActive("dif-boost")) {
			double amplifier = 0;
			for(PotionEffect pe : p.getActivePotionEffect())
				if(pe.getType().equals(PotionEffectType.JUMP))
					amplifier = pe.getAmplifier();
			double diffBoost = dif - (amplifier / 10);
			if(diffBoost > 0.2) {
				recordData(p.getUniqueId(), BLOCKS_UP, diffBoost);
				if(diffBoost > 0.5 || diffBoost == 0.25) {
					Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffBoost == 0.25 ? 95 : diffBoost * 125), "dif-boost",
							"Basic Y diff: " + dif + ", with boost: " + diffBoost + " (because of boost amplifier " + amplifier + ")",
							hoverMsg("main", "%block%", String.format("%.2f", dif)), (int) ((diffBoost - 0.6) / 0.2));
				}
			}
		}
	}
	
	@Override
	public boolean isBlockedInFight() {
		return true;
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		return "Average of block up : " + ChatColor.GREEN + String.format("%.3f", data.getData(BLOCKS_UP).getAverage());
	}
}
