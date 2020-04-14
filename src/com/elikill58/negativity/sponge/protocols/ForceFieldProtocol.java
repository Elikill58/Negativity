package com.elikill58.negativity.sponge.protocols;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.FakePlayer;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;

public class ForceFieldProtocol extends Cheat {

	private final NumberFormat distanceFormatter = new DecimalFormat();

	public ForceFieldProtocol() {
		super(CheatKeys.FORCEFIELD, true, ItemTypes.DIAMOND_SWORD, CheatCategory.COMBAT, true, "ff", "killaura");
		distanceFormatter.setMaximumIntegerDigits(2);
	}

	@Listener
	public void onEntityDamageByEntity(DamageEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}
		if(np.hasThorns(p))
			return;
		
		boolean mayCancel = false;
		if(!np.hasLineOfSight(e.getTargetEntity())) {
			mayCancel = SpongeNegativity.alertMod(ReportType.VIOLATION, p, this, UniversalUtils.parseInPorcent(90 + np.getWarn(this)), "Hit " + e.getTargetEntity().getType().getId()
					+ " but cannot see it, ping: " + Utils.getPing(p),
					"Hit " + e.getTargetEntity().getType().getName() + " without line of sight");
		}
		Optional<ItemStackSnapshot> usedItem = e.getContext().get(EventContextKeys.USED_ITEM);

		double distance = e.getTargetEntity().getLocation().getPosition().distance(p.getLocation().getPosition());
		double allowedReach = Adapter.getAdapter().getDoubleInConfig("cheats.forcefield.reach") + (p.gameMode().get().equals(GameModes.CREATIVE) ? 1 : 0);
		if (distance > allowedReach && !(usedItem.isPresent() && usedItem.get().getType() == ItemTypes.BOW) && e.getTargetEntity().getType().equals(EntityTypes.ENDER_DRAGON)) {
			SpongeNegativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(distance * 2 * 10),
					"Big distance with: " + e.getTargetEntity().getType().getName().toLowerCase() + ". Exact distance: "
							+ distance + ". Ping: " + Utils.getPing(p),
					"Distance with " + e.getTargetEntity().getType().getName() + ": " + distanceFormatter.format(distance), "Distance with " + e.getTargetEntity().getType().getName() + ": " + distanceFormatter.format(distance));
		}
		final Vector3d loc = p.getRotation().clone();
		Task.builder().delay(60, TimeUnit.MILLISECONDS).execute(() -> {
			Vector3d loc1 = p.getRotation();
			int gradeRounded = (int) Math.round(Math.abs(loc.getY() - loc1.getY()));
			if (gradeRounded > 76.0) {
				SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.FORCEFIELD), UniversalUtils.parseInPorcent(gradeRounded), "Player rotate too much (" + gradeRounded + "°) without thorns.", "Rotate " + gradeRounded + "°");
			}
		}).submit(SpongeNegativity.getInstance());
		if (isSetBack() && mayCancel)
			e.setCancelled(true);
	}

	@Listener
	public void onPlayerInteract(InteractEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}

		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}

		Location<World> ploc = p.getLocation();
		Vector3d eyeloc = p.getLocation().getPosition().add(0, 1.8, 0);
		FakePlayer c = null;
		double distanceWithPlayer = 500;
		double distanceWithEye = 500;
		for (FakePlayer temp : np.FAKE_PLAYER) {
			Location<World> cloc = temp.getLocation();
			double nextDistanceWithPlayer = ploc.getPosition().distance(cloc.getPosition());
			double nextDistanceWithEye = eyeloc.distance(cloc.getPosition());
			if (nextDistanceWithPlayer < distanceWithPlayer && nextDistanceWithPlayer < 10) {
				distanceWithPlayer = nextDistanceWithPlayer;
				c = temp;
			} else if (nextDistanceWithEye < distanceWithEye && nextDistanceWithEye < 10) {
				distanceWithEye = nextDistanceWithEye;
				c = temp;
			}
		}

		if (c == null) {
			return;
		}

		np.fakePlayerTouched++;
		c.hide(p);
		manageForcefieldForFakeplayer(p, np);
	}

	public static void manageForcefieldForFakeplayer(Player p, SpongeNegativityPlayer np) {
		if (np.fakePlayerTouched < 5) {
			return;
		}

		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		double rapport = np.fakePlayerTouched / (timeBehindStart / 1000);
		SpongeNegativity.alertMod(rapport > 20 ? ReportType.VIOLATION : ReportType.WARNING, p, Cheat.forKey(CheatKeys.FORCEFIELD),
				UniversalUtils.parseInPorcent(rapport * 10), "Hitting fake entities. " + np.fakePlayerTouched
						+ " entites touch in " + timeBehindStart + " millisecondes",
				np.fakePlayerTouched + " fake players touched in " + timeBehindStart + " ms", np.fakePlayerTouched + " fake players touched in " + timeBehindStart + " ms");
	}
}
