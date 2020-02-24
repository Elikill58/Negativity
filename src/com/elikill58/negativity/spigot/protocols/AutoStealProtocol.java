package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class AutoStealProtocol extends Cheat implements Listener {

	public AutoStealProtocol() {
		super(CheatKeys.AUTO_STEAL, false, Material.CHEST, CheatCategory.PLAYER, true, "steal");
	}

	public static final int TIME_CLICK = 55;
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onInvClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if(!np.ACTIVE_CHEAT.contains(this))
			return;
		if(p.getItemInHand() != null)
			if(ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType().name())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType().name());
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
				boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, Utils.parseInPorcent((100 + TIME_CLICK) - dif - ping), "Time between 2 click: " + dif + ". Ping: " + ping, "Time between 2 clicks: " + dif + "ms", "Time between 2 clicks: " + dif + "ms");
				if(isSetBack() && mayCancel)
					e.setCancelled(true);
			}
			np.lastClickInv = true;
		} else np.lastClickInv = false;
		np.LAST_CLICK_INV = actual;
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
