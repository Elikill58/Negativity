package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.ReportType;

public class AutoStealProtocol extends Cheat {

	public AutoStealProtocol() {
		super("AUTOSTEAL", false, ItemTypes.CHEST, false, true, "steal");
	}
	
	public static final int TIME_CLICK = 50;
	
	@Listener
	@Exclude(ClickInventoryEvent.Double.class)
	public void onInvClick(ClickInventoryEvent e, @First Player p) {
		if(!(p.gameMode().get().equals(GameModes.SURVIVAL) || p.gameMode().get().equals(GameModes.ADVENTURE)))
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this))
			return;
		np.haveClick = true;
		if(p.getItemInHand(HandTypes.MAIN_HAND).isPresent())
			if(ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand(HandTypes.MAIN_HAND).get().getType())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand(HandTypes.MAIN_HAND).get().getType());
				if(ib.getWhen().isClick() && ib.isForThisCheat(this))
					return;
			}
		long actual = System.currentTimeMillis(), dif = actual - np.LAST_CLICK_INV;
		int ping = Utils.getPing(p);
		if((ping + TIME_CLICK) >= dif){
			if(np.lastClickInv){
				if(isSetBack() && SpongeNegativity.alertMod(ReportType.WARNING, p, this, Utils.parseInPorcent((100 + TIME_CLICK) - dif - ping), "Time between 2 click: " + dif + ". Ping: " + ping, "Time between 2 clicks: " + dif))
					e.setCancelled(true);
			}
			np.lastClickInv = true;
		} else np.lastClickInv = false;
		np.LAST_CLICK_INV = actual;
	}

	@Listener
	public void onClose(InteractInventoryEvent.Close e, @First Player p){
		if(!(p.gameMode().get().equals(GameModes.SURVIVAL) || p.gameMode().get().equals(GameModes.ADVENTURE)))
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this))
			return;
		boolean last = np.haveClick;
		np.haveClick = false;
		if(!last)
			return;
		int ping = Utils.getPing(p), dif = (int) (System.currentTimeMillis() - np.LAST_CLICK_INV);
		if((dif + ping) < (TIME_CLICK / 2))
			SpongeNegativity.alertMod(ReportType.WARNING, p, this, Utils.parseInPorcent((100 + TIME_CLICK) - (dif * 1.5) - ping), "Time between last click and close inv: " + dif + ". Ping: " + ping);
	}
}
