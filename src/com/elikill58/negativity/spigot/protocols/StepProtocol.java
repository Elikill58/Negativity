package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

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
		if (np.hasElytra() || p.getItemInHand().getType().name().contains("TRIDENT"))
			return;
		Location from = e.getFrom(), to = e.getTo();
		if (!p.hasPotionEffect(PotionEffectType.JUMP)) {
			double dif = to.getY() - from.getY();
			if (!np.isUsingSlimeBlock) {
				if(dif < 0)
					return;
				int ping = Utils.getPing(p), relia = UniversalUtils.parseInPorcent(dif * 50);
				if (dif > 1.499 && ping < 200) {
					boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, relia, "Warn for Step: "
							+ np.getWarn(this) + ". Move " + dif + " blocks up. ping: " + ping);
					if (isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
		}
	}
	
	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
