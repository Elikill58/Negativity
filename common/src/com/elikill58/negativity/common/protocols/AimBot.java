package com.elikill58.negativity.common.protocols;

import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.api.utils.LocationUtils.Direction;
import com.elikill58.negativity.common.protocols.data.AimbotData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerManager;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class AimBot extends Cheat {

	public static final DataType<Double> GCD = new DataType<Double>("gcd", "Gcd of pitchs", () -> new DoubleDataCounter());
	public static final DataType<Double> PITCHS = new DataType<Double>("pitchs", "Pitchs movements", () -> new DoubleDataCounter());
	public static final DataType<Integer> INVALID_CHANGE = new DataType<Integer>("invalid_changes", "Invalid changes", () -> new IntegerDataCounter());

	public AimBot() {
		super(CheatKeys.AIM_BOT, CheatCategory.COMBAT, Materials.TNT, AimbotData::new, CheatDescription.VERIF);
	}

	// many killauras use a constant pitch in order to bypass the GDC check
	// this check will fight against that and fail these killauras
	@Check(name = "ratio", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_INSIDE_VEHICLE }, description = "Checks for invalid rotation ratios", ignoreCancel = true)
	public void ratio(PacketReceiveEvent e, NegativityPlayer np, AimbotData data) {
		if (!e.hasPlayer())
			return;
		PacketType type = e.getPacket().getPacketType();
		if (type.isFlyingPacket()) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) e.getPacket();
			if (!flying.hasLook || !np.isAttacking)
				return;
			double difference = Math.abs(np.delta.getPitch() - data.lastDeltaPitchStreak);
			double absoluteDeltaYaw = Math.abs(np.delta.getYaw());
			if (difference < 0.005 && absoluteDeltaYaw > .65 && difference != 0) {
				// increment streak

				if (data.ratioStreak++ > 7) {
					if (Negativity.alertMod(ReportType.WARNING, np.getPlayer(), this, 100, "ratio",
							"absYaw: " + String.format("%.3f", absoluteDeltaYaw) + ", streak: " + data.ratioStreak + ", difference: " + String.format("%.3f", difference)) && isSetBack())
						e.setCancelled(true);

					data.ratioStreak -= 3;
				}
			} else {
				data.ratioStreak = 0;
			}
			data.lastDeltaPitchStreak = np.delta.getPitch();
		}
	}

	@Check(name = "gcd", conditions = CheckConditions.SURVIVAL, description = "Calculate GCD between attacks", ignoreCancel = true)
	public void gcd(PacketReceiveEvent e, NegativityPlayer np, AimbotData data) {
		if (!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		PacketType type = e.getPacket().getPacketType();
		if (type.isFlyingPacket()) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) e.getPacket();
			if (!flying.hasLook || !np.isAttacking)
				return;

			double pitch = flying.pitch;
			data.allPitchs.add(pitch);

			int invalidChange = 0;
			double last = pitch, pitchMore = pitch, pitchLess = pitch;
			Boolean up = null;
			for (double actual : data.allPitchs) {
				boolean isUp = actual > last;
				if (up != null && isUp != up && Math.abs(actual - last) > 2) {
					invalidChange++;
				}
				if (last == actual) // same as before
					invalidChange--;
				up = isUp;
				last = actual;
				if (actual > pitchMore)
					pitchMore = actual;
				else if (actual < pitchLess)
					pitchLess = actual;
			}
			data.allInvalidChanges.add(invalidChange);

			double gcd = getGcdForLong((long) np.delta.getPitch(), (long) np.lastDelta.getPitch());
			int averageInvalid = (int) data.allInvalidChanges.stream().mapToInt(a -> a).average().orElse(0);
			boolean exempt = !(Math.abs(pitch) < 82.5F) || np.delta.getYaw() < 5.0;
			if (!exempt && Math.abs(gcd) > np.sensitivity / getConfig().getInt("checks.gcd.sensitivity-divider", 15) && invalidChange > getConfig().getInt("checks.gcd.invalid-change", 3)
					&& averageInvalid > 2 && !(pitchLess < 0 && pitchMore < 50) && Math.abs(pitchLess - pitchMore) > 20) {
				String allPitchStr = data.allPitchs.stream().map((d) -> String.format("%.3f", d)).collect(Collectors.toList()).toString();
				if (Negativity.alertMod(ReportType.WARNING, p, this, 100, "gcd",
						"GCD: " + gcd + ", allPitchs: " + allPitchStr + ", sens: " + String.format("%.3f", np.sensitivity) + ", changes: " + invalidChange + ", allChanges: "
								+ data.allInvalidChanges + ", avInvalid: " + averageInvalid + ", More/Less: " + String.format("%.3f", pitchMore) + "/" + String.format("%.3f", pitchLess))
						&& isSetBack())
					e.setCancelled(true);
			}
			recordData(p.getUniqueId(), GCD, gcd);
			recordData(p.getUniqueId(), PITCHS, pitch);
			recordData(p.getUniqueId(), INVALID_CHANGE, invalidChange);

			data.allPitchs.remove(0);
			data.allInvalidChanges.remove(0);
		}
	}

	private long getGcdForLong(final long current, final long previous) {
		return (previous <= 16384L) ? current : getGcdForLong(previous, current % previous);
	}

	// Warn: this check can be removed in next versions and replaced with new one
	//@Check(name = "direction", description = "Check for the direction between player look and cible position", conditions = CheckConditions.NO_THORNS)
	public void onEntityDamageByEntity(PlayerDamageEntityEvent e, NegativityPlayer np) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();
		Entity cible = e.getDamaged();
		Location loc = np.getPingedLocation(), cloc = cible instanceof Player
				? NegativityPlayer.getNegativityPlayer((Player) cible).getPingedLocation()
				: cible.getLocation();
		if (!p.getWorld().equals(cloc.getWorld()) || (cloc.getYaw() == 0.0 && cloc.getPitch() == 0.0)) // entity just beeing tp
			return;
		double xzDistance = loc.distanceXZ(cloc);
		if (xzDistance < 0.5)
			return;
		boolean notSure = xzDistance < 1; // if X/Z distance too low
		Direction direction = LocationUtils.getDirection(p, cloc);
		long amount = 0;
		int reliability = 0;
		switch (direction) {
		case BACK: // should never appear, clearly a cheat
			amount = 5;
			reliability = 100;
			break;
		case BACK_LEFT:
		case BACK_RIGHT:
			amount = 3;
			reliability = 95;
			break;
		case FRONT:
			return; // here it's fine
		case FRONT_LEFT:
		case FRONT_RIGHT:
			if (BedrockPlayerManager.isBedrockPlayer(p.getUniqueId())) {
				return; // allowed for bedrock
			}
			amount = 1;
			reliability = 60; // low reliability
			break;
		case LEFT:
		case RIGHT:
			if (notSure)
				return;
			amount = 1;
			reliability = 75;
			break;
		}
		if (amount > 0)
			Negativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(reliability + (notSure ? -10 : 0) - 10), "direction",
					"Pos: " + p.getLocation() + " / " + cloc + ", dir: " + direction.name() + ", xzDis: "
							+ xzDistance,
					null, amount);
	}

	@Override
	public @Nullable String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> gcdCounter = data.getData(GCD);
		DataCounter<Double> pitchCounter = data.getData(PITCHS);
		DataCounter<Integer> invalidChangeCounter = data.getData(INVALID_CHANGE);
		return ChatColor.color("&7Average GCD: &e" + String.format("%.2f", gcdCounter.getAverage()) + "&7. Pitchs: max/min/ave &e" + String.format("%.2f", pitchCounter.getMax()) + "&7/&e"
				+ String.format("%.2f", pitchCounter.getMin()) + "&7/&e" + String.format("%.2f", pitchCounter.getAverage()) + "&7. " + (invalidChangeCounter.getMax() > 2 ? "&c" : "&a")
				+ "Invalid changes: " + invalidChangeCounter.getAverage());
	}
}
