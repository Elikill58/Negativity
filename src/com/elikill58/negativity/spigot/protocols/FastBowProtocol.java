package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

@SuppressWarnings("deprecation")
public class FastBowProtocol extends Cheat implements Listener {
	
	public FastBowProtocol() {
		super(CheatKeys.FAST_BOW, true, Material.BOW, CheatCategory.COMBAT, true, "bow");
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		ItemStack item = p.getItemInHand();
		if(item == null)
			return;
		
		if(ItemUseBypass.ITEM_BYPASS.containsKey(item.getType().name())) {
			ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(item.getType().name());
			if(ib.getWhen().isClick() && ib.isForThisCheat(this))
				if(e.getAction().name().toLowerCase().contains(ib.getWhen().name().toLowerCase()))
					return;
		}
		if (item.getType().equals(Material.BOW) && e.getAction().name().contains("RIGHT_CLICK")) {
			np.flyingReason = FlyingReason.BOW;
			long actual = System.currentTimeMillis(), dif = actual - np.LAST_SHOT_BOW;
			if (np.LAST_SHOT_BOW != 0) {
				int ping = Utils.getPing(p);
				if (dif < (200 + ping)) {
					boolean mayCancel = false;
					if (dif < (50 + ping))
						mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, this,
								UniversalUtils.parseInPorcent(200 - dif - ping), "Player use Bow, last shot: " + np.LAST_SHOT_BOW
										+ " Actual time: " + actual + " Difference: " + dif + ", Warn: " + np.getWarn(this), hoverMsg("main", "%time%", dif));
					else
						mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this,
								UniversalUtils.parseInPorcent(100 - dif - ping), "Player use Bow, last shot: " + np.LAST_SHOT_BOW
								+ " Actual time: " + actual + " Difference: " + dif + ", Warn: " + np.getWarn(this), hoverMsg("main", "%time%", dif));
					if(isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
			np.LAST_SHOT_BOW = actual;
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onShot(EntityShootBowEvent e){
		if(e.getEntity() instanceof Player)
			SpigotNegativityPlayer.getNegativityPlayer((Player) e.getEntity()).flyingReason = FlyingReason.BOW;
	}
}
