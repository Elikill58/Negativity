package com.elikill58.negativity.sponge.protocols;

import java.util.Collections;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class AutoClickProtocol extends Cheat {

	public static final DataType<Integer> CLICKS = new DataType<Integer>("clicks", "Clicks", () -> new IntegerDataCounter());

	public static final int CLICK_ALERT = Adapter.getAdapter().getConfig().getInt("cheats.autoclick.click_alert");

	public AutoClickProtocol() {
		super(CheatKeys.AUTO_CLICK, false, ItemTypes.FISHING_ROD, CheatCategory.COMBAT, true, "auto-click", "autoclic");
		Task.builder().delayTicks(20).execute(() -> {
			for(Player p : Sponge.getServer().getOnlinePlayers()) {
				SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
	            NegativityAccount account = np.getAccount();
	            if (account.getMostClicksPerSecond() < np.ACTUAL_CLICK) {
	                account.setMostClicksPerSecond(np.ACTUAL_CLICK);
				}
	            recordData(p.getUniqueId(), CLICKS, np.ACTUAL_CLICK);
	            np.LAST_CLICK = np.ACTUAL_CLICK;
	            np.ACTUAL_CLICK = 0;
			}
		}).submit(SpongeNegativity.getInstance());
	}
	
	@Listener
	public void onPlayerInteract(InteractEvent e, @First Player p) {
		ItemStackSnapshot usedItem = e.getContext().get(EventContextKeys.USED_ITEM).orElse(ItemStackSnapshot.NONE);
		if (usedItem.getType() == ItemTypes.REEDS || e.isCancelled()) {
			return;
		}

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		ItemUseBypass usedItemBypass = ItemUseBypass.ITEM_BYPASS.get(usedItem.getType().getId());
		if (usedItemBypass != null && usedItemBypass.getWhen().isClick() && usedItemBypass.isForThisCheat(this)) {
			return;
		}

		np.ACTUAL_CLICK++;
		int efficiency = 0;
		List<Enchantment> usedItemsEnchantments = usedItem.get(Keys.ITEM_ENCHANTMENTS).orElse(Collections.emptyList());
		for (Enchantment enchantment : usedItemsEnchantments) {
			if (enchantment.getType() == EnchantmentTypes.EFFICIENCY) {
				efficiency = enchantment.getLevel();
				break;
			}
		}

		int ping = Utils.getPing(p);
		int click = np.ACTUAL_CLICK - (ping / 9);
		if (click > CLICK_ALERT) {
			NegativityAccount account = np.getAccount();
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(np.ACTUAL_CLICK * 2 - efficiency * 2),
					"Clicks in one second: " + np.ACTUAL_CLICK + "; Last second: " + np.LAST_CLICK
							+ "; Better click in one second: " + account.getMostClicksPerSecond() + " Ping: " + ping, hoverMsg("main", "%click%", np.ACTUAL_CLICK));
			if (isSetBack() && mayCancel) {
				e.setCancelled(true);
			}
		}
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		int currentClick = ((SpongeNegativityPlayer) np).ACTUAL_CLICK;
		DataCounter<Integer> counter = data.getData(CLICKS);
		counter.add(currentClick);
		if(counter.getMax() == 0)
			return null;
		return Utils.coloredMessage("&aCurrent&7/&cMaximum&7/&6Average&7: &a" + currentClick + "&7/&c" + counter.getMax() + "&7/&6" + counter.getAverage() + " &7clicks");
	}
}
