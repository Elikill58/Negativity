package com.elikill58.negativity.spigot.protocols;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ScaffoldProtocol extends Cheat implements Listener {

	private ScaffoldProtocol instance;
	
	public ScaffoldProtocol() {
		super(CheatKeys.SCAFFOLD, false, ItemUtils.GRASS, CheatCategory.WORLD, true);
		instance = this;
	}

	@SuppressWarnings("deprecation")
	@EventHandler (ignoreCancelled = true)
	public void onBlockBreak(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		int slot = p.getInventory().getHeldItemSlot();
		if(np.ping > 120)
			return;
		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				Material m = p.getItemInHand().getType(), placed = e.getBlockPlaced().getType();
				if ((m == null || (!m.isBlock() && !m.equals(placed))) && slot != p.getInventory().getHeldItemSlot() && !placed.equals(Material.AIR)) {
					int localPing = np.ping;
					if(localPing == 0)
						localPing = 1;
					boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, instance, UniversalUtils.parseInPorcent(120 / localPing),
							"Item in hand: " + m.name() + " Block placed: " + placed.name(),
							hoverMsg("main", "%item%", m.name().toLowerCase(Locale.ROOT), "%block%", placed.name().toLowerCase(Locale.ROOT)));
					if(isSetBack() && mayCancel) {
						p.getInventory().addItem(new ItemStack(placed));
						e.getBlockPlaced().setType(Material.AIR);
					}
				}
			}
		}, 0);
	}
}
