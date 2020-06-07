package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;

public class StepProtocol extends Cheat implements Listener {

	public StepProtocol() {
		super(CheatKeys.STEP, false, Material.BRICK_STAIRS, CheatCategory.MOVEMENT, true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (np.hasElytra() || p.getItemInHand().getType().name().contains("TRIDENT") || np.isUsingSlimeBlock)
			return;
		Location from = e.getFrom(), to = e.getTo();
		double dif = to.getY() - from.getY();
		if (!p.hasPotionEffect(PotionEffectType.JUMP) && dif > 0) {
			int ping = Utils.getPing(p), relia = UniversalUtils.parseInPorcent(dif * 50);
			if (dif > 1.499 && ping < 200) {
				boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, relia, "Warn for Step: "
						+ np.getWarn(this) + ". Move " + dif + " blocks up. ping: " + ping, hoverMsg("main", "%block%", String.format("%.2f", dif)));
				if (isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
		double amplifier = 0;
		for(PotionEffect pe : p.getActivePotionEffects())
			if(pe.getType().equals(PotionEffectType.JUMP))
				amplifier = pe.getAmplifier();
		double diffBoost = dif - (amplifier / 10);
		if(diffBoost > 0.2) {
			np.verificatorForMod.forEach((s, verif) -> {
				VerifData data = verif.getVerifData(this);
				if(data != null)
					data.getData(DataType.DOUBLE).add(diffBoost);
			});
			if(diffBoost > 0.6) {
				SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffBoost * 125),
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
	public String compile(VerifData data) {
		return "Average of block up : " + data.getData(DataType.DOUBLE).getAverage();
	}
}
