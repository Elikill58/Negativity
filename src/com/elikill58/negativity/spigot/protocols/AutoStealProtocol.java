package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ItemUseBypass;

@SuppressWarnings("deprecation")
public class AutoStealProtocol extends Cheat implements Listener {

	public AutoStealProtocol() {
		super("AUTOSTEAL", false, Material.CHEST, false, true, "steal");
	}

	public static final int TIME_CLICK = 55;
	
	@EventHandler(ignoreCancelled = true)
	public void onInvClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if(!np.ACTIVE_CHEAT.contains(this))
			return;
		if(p.getItemInHand() != null)
			if(ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType());
				if(ib.getWhen().isClick() && ib.isForThisCheat(this))
					if(e.getAction().name().toLowerCase().contains(ib.getWhen().name().toLowerCase()))
						return;
			}
		long actual = System.currentTimeMillis(), dif = actual - np.LAST_CLICK_INV;
		int ping = Utils.getPing(p);
		int tempSlot = np.LAST_SLOT_CLICK;
		if (e.getCurrentItem() != null) {
			if (tempSlot == e.getSlot())
				np.lastClick = e.getCurrentItem().getType();
			if (np.lastClick == e.getCurrentItem().getType())
				return;
		}
		np.LAST_SLOT_CLICK = e.getSlot();
		if(dif < 0)
			return;
		if((ping + TIME_CLICK) >= dif && tempSlot != e.getRawSlot()){
			if(np.lastClickInv){
				if(isSetBack())
					e.setCancelled(true);
				SpigotNegativity.alertMod(ReportType.WARNING, p, this, Utils.parseInPorcent((100 + TIME_CLICK) - dif - ping), "Time between 2 click: " + dif + ". Ping: " + ping, "Time between 2 clicks: " + dif + "ms");
			}
			np.lastClickInv = true;
		} else np.lastClickInv = false;
		np.LAST_CLICK_INV = actual;
	}

	@EventHandler(ignoreCancelled = true)
	public void onClose(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		if(!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if(!np.ACTIVE_CHEAT.contains(this))
			return;
		int ping = Utils.getPing(p), dif = (int) (System.currentTimeMillis() - np.LAST_CLICK_INV);
		if((dif + ping) <= TIME_CLICK && dif > 0)
			SpigotNegativity.alertMod(ReportType.WARNING, p, this, Utils.parseInPorcent((100 + TIME_CLICK) - (dif * 1.5) - ping), "Time between last click and close inv: " + dif + ". Ping: " + ping);
	}
}
