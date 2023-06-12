package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.StepData;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.logger.Debug;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class Step extends Cheat implements Listeners {

	public static final DataType<Double> BLOCKS_UP = new DataType<Double>("blocks_up", "Blocks UP", () -> new DoubleDataCounter());

	public Step() {
		super(CheatKeys.STEP, CheatCategory.MOVEMENT, Materials.SLIME_BLOCK, StepData::new, CheatDescription.VERIF, CheatDescription.NO_FIGHT);
	}

	@Check(name = "dif", description = "Distance about blocks up", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA, CheckConditions.NO_SWIM, CheckConditions.NO_ALLOW_FLY,
			CheckConditions.NO_ON_BEDROCK, CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_USE_SLIME, CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_BLOCK_MID_AROUND,
			CheckConditions.NO_USE_JUMP_BOOST, CheckConditions.NO_CLIMB_BLOCK })
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		Location from = e.getFrom(), to = e.getTo();
		Location down = to.clone().sub(0, 1, 0);
		if (down.getBlock().getType().getId().contains("SHULKER"))
			return;
		double dif = to.getY() - from.getY();
		if (dif > 0.45 && dif != 0.60 && p.getVelocity().getY() < 0.5) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(dif * 50), "dif", "Move " + dif + " blocks up.",
					hoverMsg("main", "%block%", String.format("%.2f", dif)));
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}

	@Check(name = "dif-boost", description = "Distance while counting jump", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA, CheckConditions.NO_SWIM,
			CheckConditions.NO_ALLOW_FLY, CheckConditions.NO_ON_BEDROCK, CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_USE_SLIME, CheckConditions.NO_USE_TRIDENT,
			CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_BLOCK_MID_AROUND, CheckConditions.NO_USE_JUMP_BOOST, CheckConditions.NO_MID_ENTITY_AROUND, CheckConditions.NO_CLIMB_BLOCK })
	public void onPlayerMoveDifBoost(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		if (p.getVelocity().getY() < -0.1)
			return; // will go down
		Location from = e.getFrom(), to = e.getTo();
		Location down = to.clone().sub(0, 1, 0);
		if (down.getBlock().getType().getId().contains("SHULKER"))
			return;
		double dif = to.getY() - from.getY();
		double amplifier = (p.hasPotionEffect(PotionEffectType.JUMP) ? p.getPotionEffect(PotionEffectType.JUMP).get().getAmplifier() : 0);
		double diffBoost = dif - (amplifier / 10) - p.getVelocity().getY();
		if (diffBoost > 0.2) {
			recordData(p.getUniqueId(), BLOCKS_UP, diffBoost);
			if ((diffBoost > 0.5) && !(diffBoost <= 0.6 && diffBoost >= 0.56) // 0.56-0.6 is to bypass carpet and other
																				// no-full blocks
					&& !(amplifier > 0 && diffBoost < 0.55)) {
				Negativity.alertMod(
						ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffBoost == 0.25 ? 95 : diffBoost * 120), "dif-boost", "Basic Y diff: " + dif + ", with boost: "
								+ diffBoost + " (amplifier " + amplifier + ") Dir Y: " + p.getLocation().getDirection().getY() + ", vel: " + p.getVelocity(),
						hoverMsg("main", "%block%", String.format("%.2f", dif)), (int) ((diffBoost - 0.6) / 0.2));
			}
		}
	}

	@Check(name = "dif-no-xz", description = "Distance without X/Z moving", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA, CheckConditions.NO_SWIM,
			CheckConditions.NO_ALLOW_FLY, CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_USE_SLIME, CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.NO_CLIMB_BLOCK })
	public void onDiffNoXZ(PlayerMoveEvent e, NegativityPlayer np, StepData data) {
		Player p = e.getPlayer();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		Location from = e.getFrom(), to = e.getTo();
		Location down = to.clone().sub(0, 1, 0);
		if (down.getBlock().getType().getId().contains("SHULKER"))
			return;
		double difY = to.getY() - from.getY(), difX = to.getX() - from.getX(), difZ = to.getZ() - from.getZ();
		if (difY <= 0.05 || difY % 0.125 == 0 || (difY == 0.5 && (difX != 0 || difZ != 0)) || p.getVelocity().getY() >= 0.3 || difY == 0.5799999999999983) {
			// all specific block such as snow. "0.5799999999999983" correspond to bedrock
			// jump
			data.oldY.clear();
			return;
		}
		if (difX == 0 && difZ == 0) { // going only UP
			if (difY < 0.3 || (p.isOnGround() && !data.oldY.isEmpty())) { // if too low or on ground after getting some
																			// values
				// going down, don't go on other block
				data.oldY.clear();
				return;
			}
			data.oldY.add(difY);
		} else {
			if (!data.oldY.isEmpty()) { // was moving UP, now move X/Z
				data.oldY.add(difY);
				double total = data.oldY.stream().mapToDouble(Double::doubleValue).sum(), min = data.oldY.stream().mapToDouble(Double::doubleValue).min().getAsDouble(),
						max = data.oldY.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
				if ((total == 1 || total > 1.2) && min > 0.2) { // prevent not exact up
					Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(difY * 300), "dif-no-xz",
							"Total: " + total + ", min/max: " + min + "/" + max + ", all: " + data.oldY + ", ground: " + p.isOnGround() + ", vel: " + p.getVelocity(),
							hoverMsg("main", "%block%", String.format("%.2f", max)), (long) ((total - min) * 10));
				} else
					Adapter.getAdapter().debug(Debug.CHECK,
							"[Step] Total: " + total + ", min/max: " + min + "/" + max + ", all: " + data.oldY + ", ground: " + p.isOnGround() + ", vel: " + p.getVelocity());
			}
			data.oldY.clear();
		}
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		return "Average of block up : " + ChatColor.GREEN + String.format("%.3f", data.getData(BLOCKS_UP).getAverage());
	}
}
