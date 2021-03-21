package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.bypass.checkers.ItemUseBypass;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class AutoClick extends Cheat implements Listeners {

	public static final DataType<Integer> CLICKS = new DataType<Integer>("clicks", "Clicks",
			() -> new IntegerDataCounter());

	public AutoClick() {
		super(CheatKeys.AUTO_CLICK, CheatCategory.COMBAT, Materials.FISHING_ROD, true, true, "auto-click", "autoclic");
		Scheduler.getInstance().runRepeating(() -> {
			for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
				NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
				NegativityAccount account = np.getAccount();
				if (account.getMostClicksPerSecond() < np.ACTUAL_CLICK) {
					account.setMostClicksPerSecond(np.ACTUAL_CLICK);
				}
				recordData(p.getUniqueId(), CLICKS, np.ACTUAL_CLICK);
				np.LAST_CLICK = np.ACTUAL_CLICK;
				np.ACTUAL_CLICK = 0;
				if (np.SEC_ACTIVE < 2) {
					np.SEC_ACTIVE++;
					return;
				}
			}
		}, 20, 20);
	}

	@EventListener
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction().name().contains("AIR")) {
			manageClick(e.getPlayer(), e);
		}
	}

	/*
	 * @EventHandler public void onPacket(PacketReceiveEvent e) {
	 * if(e.getPacket().getPacketType() == PacketType.Client.ARM_ANIMATION)
	 * manageClick(e.getPlayer(), e); }
	 */

	private void manageClick(Player p, PlayerInteractEvent e) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		ItemStack inHand = p.getItemInHand();
		if (inHand != null) {
			if (ItemUseBypass.hasBypassWithClick(p, this, inHand, e.getAction().name()))
				return;
		}
		np.ACTUAL_CLICK++;
		int ping = p.getPing(), click = np.ACTUAL_CLICK - (ping / 9);
		if (click > getConfig().getInt("click_alert", 20) && np.hasDetectionActive(this)) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(np.ACTUAL_CLICK * 2.5), "count",
					"Clicks in one second: " + np.ACTUAL_CLICK + "; Last second: " + np.LAST_CLICK
							+ "; Better click in one second: " + np.getAccount().getMostClicksPerSecond(),
					hoverMsg("main", "%click%", np.ACTUAL_CLICK));
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		int currentClick = np.ACTUAL_CLICK;
		DataCounter<Integer> counter = data.getData(CLICKS);
		counter.add(currentClick);
		if (counter.getMax() == 0)
			return null;
		return Utils.coloredMessage("&aCurrent&7/&cMaximum&7/&6Average&7: &a" + currentClick + "&7/&c"
				+ counter.getMax() + "&7/&6" + counter.getAverage() + " &7clicks");
	}

	

	@EventListener
	public void onPacket(PacketReceiveEvent event) {
		
		NegativityPlayer data = NegativityPlayer.getNegativityPlayer(event.getPlayer());
		if (event.getPacket().getPacketType() == PacketType.Client.ARM_ANIMATION) {
			data.setClicksTicks(0);
			data.setClicks(data.getClicksTicks() * 50);
			data.setAverageClicks(data.getAverageClicks() * 14 + data.getClicks() / 15);
			data.setDeviationClicks(Math.abs(data.getClicks() - data.getAverageClicks()));
			data.setAverageDeviation((data.getAverageDeviation() * 9) + data.getDeviationClicks() / 10);
			try {
				if (event.getPacket().getContent()
						.getSpecificModifier(Class.forName(
								"net.minecraft.server." + Adapter.getAdapter().getServerVersion() + ".EntityUseAction"))
						.read(0).toString().equalsIgnoreCase("ATTACK")) {
					Negativity.alertMod(ReportType.VIOLATION,event.getPlayer(), this, 60, "Consistent check ", "clicks are too frequent" );
				
					

				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (event.getPacket().getPacketType() == PacketType.Client.FLYING) {
			data.setClicksTicks(data.getClicksTicks() + 1);
		}
	}
}
