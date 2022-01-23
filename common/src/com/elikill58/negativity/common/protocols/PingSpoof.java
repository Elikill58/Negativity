package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.PINGSPOOF;

import java.io.IOException;
import java.util.TimerTask;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class PingSpoof extends Cheat implements Listeners {

	public static final DataType<Integer> PLAYER_PING = new DataType<Integer>("player_ping", "Ping", () -> new IntegerDataCounter());
	
	public PingSpoof() {
		super(PINGSPOOF, CheatCategory.PLAYER, Materials.SPONGE, false, true, "ping", "spoofing");

		if (checkActive("reachable")) {
			new Thread(() -> {
				new java.util.Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
							NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
							if(np.hasDetectionActive(PingSpoof.this))
								managePingSpoof(p, np);
						}
					}
				}, 6l, 6l);
			}).start();
		}
	}
	
	@EventListener
	public void onMove(PlayerMoveEvent e) {
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).TIME_LAST_MOVE = System.currentTimeMillis();
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Integer> counters = data.getData(PLAYER_PING);
		return Utils.coloredMessage("Latency (Sum/Min/Max) : " + counters.getAverage() + "/" + counters.getMin() + "/" + counters.getMax());
	}

	/**
	 * Manage the ping and check if it spoof
	 * Method: reachable
	 * Warn: this function have to be called ASYNC.
	 * 
	 * @param p the player to check ping
	 * @param np the negativity player of player
	 */
	public void managePingSpoof(Player p, NegativityPlayer np) {
		int newPing = p.getPing(), lastPing = np.ints.get(PINGSPOOF, "last-ping", -1);
		if (newPing == lastPing) // ping don't change
			return;
		recordData(p.getUniqueId(), PLAYER_PING, newPing);
		np.ints.set(PINGSPOOF, "last-ping", newPing);
		if(!np.booleans.get(PINGSPOOF, "can-ping-spoof", false) && newPing < 10000) {
			if(newPing <= 200)
				np.booleans.set(PINGSPOOF, "can-ping-spoof", true);
			return;
		}
		long lastMove = System.currentTimeMillis() - np.TIME_LAST_MOVE;
		if (newPing <= getConfig().getInt("checks.reachable.min_ping", 400) || lastPing == 0 || lastMove < 2000)
			return;
		if (newPing < lastPing && ((newPing * 1.2) < lastPing || newPing < 1000)) // if ping is going normal
			return;
		try {
			if (p.getAddress().getAddress().isReachable(newPing)) {
				Negativity.alertMod(ReportType.WARNING, p, Cheat.forKey(PINGSPOOF), 98, "reachable",
						"Last ping: " + lastPing + ", new ping: " + newPing + ".");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
