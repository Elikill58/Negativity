package com.elikill58.negativity.common.protocols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
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
import com.elikill58.negativity.api.utils.Utils;
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

	public static final DataType<Double> GCD = new DataType<Double>("gcd", "Gcd of pitchs",
			() -> new DoubleDataCounter());
	public static final DataType<Double> PITCHS = new DataType<Double>("pitchs", "Pitchs movements",
			() -> new DoubleDataCounter());
	public static final DataType<Integer> INVALID_CHANGE = new DataType<Integer>("invalid_changes", "Invalid changes",
			() -> new IntegerDataCounter());

	public AimBot() {
		super(CheatKeys.AIM_BOT, CheatCategory.COMBAT, Materials.TNT, CheatDescription.VERIF);
	}

	@Check(name = "gcd", conditions = CheckConditions.SURVIVAL, description = "Calculate GCD between attacks", ignoreCancel = true)
	public void gcd(PacketReceiveEvent e, NegativityPlayer np) {
		if (!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		PacketType type = e.getPacket().getPacketType();
		if (type.isFlyingPacket()) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) e.getPacket().getPacket();
			if (!flying.hasLook)
				return;
			List<Double> allPitchs = np.listDoubles.get(getKey(), "all-pitchs",
					new ArrayList<Double>(Arrays.asList(0d, 0d, 0d, 0d, 0d, 0d, 0d)));
			List<Integer> allInvalidChanges = np.listIntegers.get(getKey(), "all-invalid-changes",
					new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0)));
			double deltaYaw = flying.yaw - np.doubles.get(getKey(), "last-yaw", 0.0);
			double deltaPitch = flying.pitch - np.doubles.get(getKey(), "last-pitch", 0.0);
			double pitch = flying.pitch;
			double lastDeltaPitch = np.doubles.get(getKey(), "last-delta-pitch", 0.0);

			np.doubles.set(getKey(), "last-yaw", (double) flying.yaw);
			np.doubles.set(getKey(), "last-yaw", (double) flying.pitch);
			np.doubles.set(getKey(), "last-delta-pitch", deltaPitch);
			if (!np.isAttacking)
				return;

			allPitchs.add(pitch);

			int invalidChange = 0;
			double last = pitch, pitchMore = pitch, pitchLess = pitch;
			Boolean up = null;
			for (double actual : allPitchs) {
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
			allInvalidChanges.add(invalidChange);

			double gcd = getGcdForLong((long) deltaPitch, (long) lastDeltaPitch);
			int averageInvalid = (int) allInvalidChanges.stream().mapToInt(a -> a).average().orElse(0);
			boolean exempt = !(Math.abs(pitch) < 82.5F) || deltaYaw < 5.0;
			if (!exempt && Math.abs(gcd) > np.sensitivity / getConfig().getInt("checks.gcd.sensitivity-divider", 15)
					&& invalidChange > getConfig().getInt("checks.gcd.invalid-change", 3) && averageInvalid > 2
					&& !(pitchLess < 0 && pitchMore < 50) && Math.abs(pitchLess - pitchMore) > 20) {
				String allPitchStr = allPitchs.stream().map((d) -> String.format("%.3f", d))
						.collect(Collectors.toList()).toString();
				Negativity.alertMod(ReportType.WARNING, p, this, 100, "gcd", "GCD: " + gcd + ", allPitchs: "
						+ allPitchStr + ", sens: " + String.format("%.3f", np.sensitivity) + ", changes: "
						+ invalidChange + ", allChanges: " + allInvalidChanges + ", avInvalid: " + averageInvalid
						+ ", More/Less: " + String.format("%.3f", pitchMore) + "/" + String.format("%.3f", pitchLess));
			}
			recordData(p.getUniqueId(), GCD, gcd);
			recordData(p.getUniqueId(), PITCHS, pitch);
			recordData(p.getUniqueId(), INVALID_CHANGE, invalidChange);

			allPitchs.remove(0);
			allInvalidChanges.remove(0);
		}
	}

	private long getGcdForLong(final long current, final long previous) {
		return (previous <= 16384L) ? current : getGcdForLong(previous, current % previous);
	}
	
	@Check(name = "direction", description = "Check for the direction between player look and cible position", conditions = CheckConditions.NO_THORNS)
	public void onEntityDamageByEntity(PlayerDamageEntityEvent e, NegativityPlayer np) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();
		Entity cible = e.getDamaged();
		Location loc = p.getLocation(), cloc = cible.getLocation();
		boolean notSure = new Location(p.getWorld(), loc.getX(), 0, loc.getZ()).distance(new Location(p.getWorld(), cloc.getX(), 0, cloc.getZ())) < 0.5; // if X/Z distance too low
		Direction direction = LocationUtils.getDirection(p, cloc);
		long amount = 0;
		int reliability = 0;
		switch (direction) {
		case BACK: // should never appear, clearly a cheat
			amount = np.getWarn(this) + 1;
			reliability = 100;
			break;
		case BACK_LEFT:
		case BACK_RIGHT:
			amount = 5;
			reliability = 95;
			break;
		case FRONT:
			return; // here it's fine
		case FRONT_LEFT:
		case FRONT_RIGHT:
			if(BedrockPlayerManager.isBedrockPlayer(p.getUniqueId())) {
				return; // allowed for bedrock
			}
			amount = 1;
			reliability = 60; // low reliability
			break;
		case LEFT:
		case RIGHT:
			if(notSure)
				return;
			amount = 2;
			reliability = 90;
			break;
		}
		if(amount > 0)
			Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(reliability + (notSure ? -10 : 0)), "direction", "Pos: " + p.getLocation() + " / " + cible.getLocation() + ", dir: " + direction.name(), null, amount);
	}
	
	@Override
	public @Nullable String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> gcdCounter = data.getData(GCD);
		DataCounter<Double> pitchCounter = data.getData(PITCHS);
		DataCounter<Integer> invalidChangeCounter = data.getData(INVALID_CHANGE);
		return Utils.coloredMessage("&7Average GCD: &e" + String.format("%.2f", gcdCounter.getAverage())
				+ "&7. Pitchs: max/min/ave &e" + String.format("%.2f", pitchCounter.getMax()) + "&7/&e"
				+ String.format("%.2f", pitchCounter.getMin()) + "&7/&e"
				+ String.format("%.2f", pitchCounter.getAverage()) + "&7. "
				+ (invalidChangeCounter.getMax() > 2 ? "&c" : "&a") + "Invalid changes: "
				+ invalidChangeCounter.getAverage());
	}
}
