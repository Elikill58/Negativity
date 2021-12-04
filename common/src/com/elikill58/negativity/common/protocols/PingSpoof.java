package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.PINGSPOOF;

import java.io.IOException;
import java.util.TimerTask;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.report.ReportType;

public class PingSpoof extends Cheat implements Listeners {

	public PingSpoof() {
		super(PINGSPOOF, CheatCategory.PLAYER, Materials.SPONGE, false, false, "ping", "spoofing");

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
		if (newPing == lastPing)
			return;
		np.ints.set(PINGSPOOF, "last-ping", newPing);
		if(!np.booleans.get(PINGSPOOF, "can-ping-spoof", false) && newPing < 10000) {
			if(newPing <= 200)
				np.booleans.set(PINGSPOOF, "can-ping-spoof", true);
			return;
		}
		if (newPing <= getConfig().getInt("checks.reachable.min_ping", 400) || lastPing == 0)
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
