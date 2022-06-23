package com.elikill58.negativity.common.protocols;

import java.text.NumberFormat;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class ForceField extends Cheat {

	public static final DataType<Integer> FAKE_PLAYERS = new DataType<Integer>("fake_players", "Fake Players",
			() -> new IntegerDataCounter());

	private NumberFormat nf = NumberFormat.getInstance();

	public ForceField() {
		super(CheatKeys.FORCEFIELD, CheatCategory.COMBAT, Materials.DIAMOND_SWORD);
		nf.setMaximumIntegerDigits(2);
	}

	@Check(name = "packet", description = "Count packet")
	public void onPacketClear(PlayerPacketsClearEvent e, NegativityPlayer np) {
		int arm = e.getPackets().getOrDefault(PacketType.Client.ARM_ANIMATION, 0);
		int useEntity = e.getPackets().getOrDefault(PacketType.Client.USE_ENTITY, 0);
		if (arm > 16 && useEntity > 20) {
			ReportType type = ReportType.WARNING;
			if (np.getWarn(this) > 5)
				type = ReportType.VIOLATION;
			Negativity.alertMod(type, e.getPlayer(), this,
					UniversalUtils.parseInPorcent(arm + useEntity + np.getWarn(this)), "packet",
					"ArmAnimation (Attack in one second): " + arm + ", UseEntity (interaction with other entity): "
							+ useEntity);
		}
	}

	/*@Check(name = "line-sight", description = "Player has line of sight the cible", conditions = {
			CheckConditions.SURVIVAL, CheckConditions.NO_INSIDE_VEHICLE })
	public void onEntityDamageByEntity(PlayerDamageEntityEvent e, NegativityPlayer np) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();
		Entity cible = e.getDamaged();
		if (cible.getType().equals(EntityType.WITHER) || cible.getType().equals(EntityType.ENDER_DRAGON)
				|| cible.isDead())
			return;
		EntityRayResult ray = new EntityRayBuilder(p).searched(cible).build().compile();
		List<Entity> lookingEntities = ray.getEntitiesFounded();
		boolean newSee = !lookingEntities.isEmpty()
				&& lookingEntities.stream().filter(cible::isSameId).findFirst().isPresent();
		double angle = LocationUtils.getAngleTo(p, cible.getLocation());
		Direction direction = LocationUtils.getDirection(angle);
		Adapter.getAdapter().debug("Player: " + p.getBoundingBox() + " > " + p.getLocation());
		Adapter.getAdapter()
				.debug("Def: " + p.hasLineOfSight(cible) + (newSee ? ", see cible" : ", don't see: " + lookingEntities) + ", dir: " + direction.name()
						+ " (" + angle + "°)");
		if (p != cible && !p.hasLineOfSight(cible) && !newSee && !ray.getRayResult().equals(RayResult.NEEDED_FOUND)) {
			if (Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(90 + np.getWarn(this)), "line-sight",
					"Hit " + cible.toString() + ", " + cible.getBoundingBox() + " (new: " + newSee + "). Looking: "
							+ lookingEntities + ". Angle: " + angle + ", direction: " + direction.name() + ", ray: "
							+ ray.toString(),
					hoverMsg("line_sight", "%name%", cible.getType().name().toLowerCase(Locale.ROOT)),
					direction.name().contains("FRONT") ? 1 : 5) && isSetBack())
				e.setCancelled(true);
		}
	}

	public void manageForcefieldForFakeplayer(Player p, NegativityPlayer np) {
		if (np.fakePlayerTouched == 0)
			return;
		recordData(p.getUniqueId(), FAKE_PLAYERS, 1);
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		Negativity.alertMod(np.fakePlayerTouched > 10 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
				parseInPorcent(np.fakePlayerTouched * 10), "ghost",
				"Hitting fake entities. " + np.fakePlayerTouched + " entites touch in " + timeBehindStart
						+ " millisecondes",
				hoverMsg("fake_players", "%nb%", np.fakePlayerTouched, "%time%", timeBehindStart));
	}*/
}
