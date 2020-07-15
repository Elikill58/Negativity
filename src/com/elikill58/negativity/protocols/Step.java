package com.elikill58.negativity.protocols;

import com.elikill58.negativity.common.ChatColor;
import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.player.PlayerMoveEvent;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.potion.PotionEffect;
import com.elikill58.negativity.common.potion.PotionEffectType;
import com.elikill58.negativity.spigot.utils.Utils;
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
		super(CheatKeys.STEP, false, Materials.BRICK_STAIRS, CheatCategory.MOVEMENT, true);
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.hasElytra() || p.getItemInHand().getType().getId().contains("TRIDENT") || np.isUsingSlimeBlock || p.isSwimming())
			return;
		Location from = e.getFrom(), to = e.getTo();
		double dif = to.getY() - from.getY();
		if (!p.hasPotionEffect(PotionEffectType.JUMP) && dif > 0) {
			int ping = p.getPing(), relia = UniversalUtils.parseInPorcent(dif * 50);
			if (dif > 1.499 && ping < 200) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, relia, "Warn for Step: "
						+ np.getWarn(this) + ". Move " + dif + " blocks up. ping: " + ping, hoverMsg("main", "%block%", String.format("%.2f", dif)));
				if (isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
		double amplifier = 0;
		for(PotionEffect pe : p.getActivePotionEffect())
			if(pe.getType().equals(PotionEffectType.JUMP))
				amplifier = pe.getAmplifier();
		double diffBoost = dif - (amplifier / 10);
		if(diffBoost > 0.2) {
			recordData(p.getUniqueId(), BLOCKS_UP, diffBoost);
			if(diffBoost > 0.6) {
				Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffBoost * 125),
						"Basic Y diff: " + dif + ", with boost: " + diffBoost + " (because of boost amplifier " + amplifier + ")",
						hoverMsg("main", "%block%", String.format("%.2f", dif)), (int) ((diffBoost - 0.6) / 0.2));
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
