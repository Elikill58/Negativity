package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.SNEAK;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Sneak extends Cheat implements Listeners {

	public Sneak() {
		super(SNEAK, CheatCategory.MOVEMENT, Materials.BLAZE_POWDER, true, false, "sneack", "sneac");
	}

	public static double getHorizontalDistance(Location to, Location from) {
		double x = Math.abs(Math.abs(to.getX()) - Math.abs(from.getX()));
		double z = Math.abs(Math.abs(to.getZ()) - Math.abs(from.getZ()));
		return Math.sqrt(x * x + z * z);
	}

	@EventListener
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Cheat sneak = Cheat.forKey(SNEAK);
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (checkActive("sneak-sprint")) {
			if (p.isSneaking() && p.isSprinting() && !p.isFlying() && np.booleans.get(SNEAK, "was-sneaking", false)) {
				if (!p.getPlayerVersion().isNewerOrEquals(Version.V1_14)) {
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(105 - (p.getPing() / 10)), "sneak-sprint",
							"Sneaking, sprinting and not flying. Player version: " + p.getPlayerVersion().getName());
					if (mayCancel && isSetBack()) {
						e.setCancelled(true);
						p.setSprinting(false);
					}
				}
			}
			np.booleans.set(SNEAK, "was-sneaking", p.isSneaking());
			if (!p.getPlayerVersion().isNewerOrEquals(Version.V1_14)) { //1.14+ sneak is strange
				
				if (getHorizontalDistance(e.getFrom(), e.getTo()) >= 0.091258D && p.isOnGround() && p.isSneaking()
						&& !p.isDead()) {
					Scheduler.getInstance().runDelayed(() -> {
						if (p.isOnGround() && p.isSneaking()) {//checking if after 3 ticks player is onGround and Sneaking
							
							Negativity.alertMod(ReportType.VIOLATION, p, sneak, 10, "SneakSpeed",
									"Walking too fast while sneaking on ground (pre 1.14)");
						}
					}, 3);
				}
			}else {
				if (getHorizontalDistance(e.getFrom(), e.getTo()) >= 0.21258D && p.isOnGround() && p.isSneaking()
						&& !p.isDead()) {
					Scheduler.getInstance().runDelayed(() -> {
						if (p.isOnGround() && p.isSneaking()) {//checking if after 3 ticks player is onGround and Sneaking
							Negativity.alertMod(ReportType.VIOLATION, p, sneak, 10, "SneakSpeed",
									"Walking too fast while sneaking on ground (1.14 or newer)");
						}
					}, 3);
				}
			}
		}
	}

	@EventListener
	public void onPacketClear(PlayerPacketsClearEvent e) {
		NegativityPlayer np = e.getNegativityPlayer();
		if (np.hasDetectionActive(this) && checkActive("packet")) {
			Player p = e.getPlayer();
			int ping = p.getPing();
			if (ping < 140) {
				int entityAction = e.getPackets().getOrDefault(PacketType.Client.ENTITY_ACTION, 0);
				if (entityAction > 35) {
					if (np.booleans.get(SNEAK, "last-sec", false)) {
						Negativity.alertMod(ReportType.WARNING, p, this,
								UniversalUtils.parseInPorcent(55 + entityAction), "packet",
								"EntityAction packet: " + entityAction);
						if (isSetBack())
							p.setSneaking(false);
					}
					np.booleans.set(SNEAK, "last-sec", true);
				} else
					np.booleans.remove(SNEAK, "last-sec");
			}
		}
	}
}
