package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.spigot.utils.LocationUtils.hasOtherThan;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.blocks.SpigotLocation;
import com.elikill58.negativity.spigot.listeners.NegativityPlayerMoveEvent;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AirJumpProtocol extends Cheat implements Listener {

	public AirJumpProtocol() {
		super(CheatKeys.AIR_JUMP, false, Material.FEATHER, CheatCategory.MOVEMENT, true, "airjump", "air", "jump");
	}

	@EventHandler
	public void onMove(NegativityPlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		if (!np.hasDetectionActive(this) || np.hasPotionEffect("JUMP_BOOST"))
			return;
		SpigotLocation loc = new SpigotLocation(p.getLocation().clone());
		if (p.isFlying() || p.getVehicle() != null || np.isUsingTrident() || np.hasElytra() || np.isInFight
				|| loc.getBlock().getType().name().contains("STAIR"))
			return;
		SpigotLocation locDown = loc.clone().subtract(0, 1, 0), locDownDown = locDown.clone().subtract(0, 1, 0);
		if (hasOtherThan(loc, "AIR") || hasOtherThan(locDown, "AIR"))
			return;

		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> {
			if (locDownDown.getBlock().getType().name().contains("STAIR")
					|| locDown.getBlock().getType().name().contains("STAIR"))
				return;
			boolean mayCancel = false;

			String allTypes = ", actual: " + loc.getBlock().getType().name() + ", down: "
					+ locDown.getBlock().getType().name() + ", Down Down: " + locDownDown.getBlock().getType().name();
			double diffYtoFromBasic = e.getTo().getY() - e.getFrom().getY();
			double diffYtoFrom = diffYtoFromBasic - Math.abs(e.getTo().getDirection().getY());
			double lastDiffY = np.contentDouble.getOrDefault("diff-y", 0.0), velLen = p.getVelocity().length();
			if (diffYtoFrom > 0.35 && np.lastYDiff < diffYtoFrom && np.lastYDiff > p.getVelocity().getY()) {
				mayCancel = SpigotNegativity.alertMod(
						diffYtoFrom > 0.5 && np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent((int) (diffYtoFrom * 210) - np.ping),
						"Actual diff Y: " + diffYtoFrom + ", last diff Y: " + np.lastYDiff + allTypes + ", velY: "
								+ p.getVelocity().getY());
			}
			np.lastYDiff = diffYtoFrom;

			boolean wasGoingDown = np.contentBoolean.getOrDefault("going-down", false);
			if (diffYtoFrom > lastDiffY && wasGoingDown && diffYtoFrom != 0.5 && velLen < p.getVelocity().getY()
					&& locDown.getBlock().getType().name().equalsIgnoreCase("AIR") && velLen < 1.5) { // 0.5 when use
																										// stairs or
																										// slab
				mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(diffYtoFrom * 200),
						"Was going down, last y " + lastDiffY + ", current: " + diffYtoFrom + allTypes);
			}
			np.contentDouble.put("diff-y", diffYtoFrom);
			np.contentBoolean.put("going-down", diffYtoFrom < 0);
			if (isSetBack() && mayCancel)
				Utils.teleportPlayerOnGround(p);
		}, (np.ping / 50) + 2);
	}
}
