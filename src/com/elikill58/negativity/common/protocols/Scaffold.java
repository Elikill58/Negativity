package com.elikill58.negativity.common.protocols;

import org.bukkit.Bukkit;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Scaffold extends Cheat implements Listeners {

	public Scaffold() {
		super(CheatKeys.SCAFFOLD, false, Materials.GRASS, CheatCategory.WORLD, true);
	}

	@EventListener
	public void onBlockBreak(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if(!checkActive("below"))
			return;
		int ping = p.getPing(), slot = p.getInventory().getHeldItemSlot();
		if (ping > 120)
			return;
		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				Material m = p.getItemInHand().getType(), placed = e.getBlock().getType();
				if ((m == null || (!m.isSolid() && !m.equals(placed))) && slot != p.getInventory().getHeldItemSlot()
						&& !placed.equals(Materials.AIR)) {
					int localPing = ping;
					if (localPing == 0)
						localPing = 1;
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, Scaffold.this,
							UniversalUtils.parseInPorcent(120 / localPing), "below",
							"Item in hand: " + m.getId() + " Block placed: " + placed.getId() + " Ping: " + ping,
							hoverMsg("main", "%item%", m.getId().toLowerCase(), "%block%",
									placed.getId().toLowerCase()));
					if (isSetBack() && mayCancel) {
						p.getInventory().addItem(ItemBuilder.Builder(placed).build());
						e.getBlock().setType(Materials.AIR);
					}
				}
			}
		}, 0);
	}
}
