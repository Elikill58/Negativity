package com.elikill58.negativity.sponge.protocols;

import static com.elikill58.negativity.sponge.utils.LocationUtils.hasOtherThan;
import static com.elikill58.negativity.sponge.utils.LocationUtils.hasOtherThanExtended;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AirJumpProtocol extends Cheat {

	public AirJumpProtocol() {
		super(CheatKeys.AIR_JUMP, false, ItemTypes.FEATHER, CheatCategory.MOVEMENT, true, "airjump", "air", "jump");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (p.get(Keys.IS_FLYING).orElse(false) || p.getVehicle().isPresent() || p.get(Keys.IS_ELYTRA_FLYING).orElse(false) || np.isInFight)
			return;
		boolean mayCancel = false;
		
		Location<World> loc = p.getLocation(), locDown = loc.copy().sub(0, 1, 0);
		double diffYtoFrom = e.getToTransform().getLocation().getY() - e.getFromTransform().getLocation().getY();
		if (diffYtoFrom > 0.35 && np.lastYDiff < diffYtoFrom && !hasOtherThanExtended(loc.copy(), BlockTypes.AIR)
				&& !hasOtherThanExtended(locDown, BlockTypes.AIR)
				&& !hasOtherThanExtended(loc.copy().sub(0, 2, 0), BlockTypes.AIR)) {
			mayCancel = SpongeNegativity.alertMod(
					diffYtoFrom > 0.5 && np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent((int) (diffYtoFrom * 210) - Utils.getPing(p)),
					"Actual diff Y: " + np.lastYDiff + ", last diff Y: " + diffYtoFrom + ", ping: " + Utils.getPing(p)
							+ ". Warn for AirJump: " + np.getWarn(this));
		}
		np.lastYDiff = diffYtoFrom;
		
		boolean wasGoingDown = np.contentBoolean.getOrDefault("going-down", false);
		double d = np.contentDouble.getOrDefault("airjump-diff-y", 0.0);
		if(diffYtoFrom > d && wasGoingDown && diffYtoFrom != 0.5) {
			if(!hasOtherThanExtended(locDown, BlockTypes.AIR) && !hasOtherThan(loc, BlockTypes.AIR)) {
				mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffYtoFrom * 200), "Was going down, last y " + d + ", current: " + diffYtoFrom);
			}
		}
		np.contentDouble.put("airjump-diff-y", diffYtoFrom);
		np.contentBoolean.put("going-down", diffYtoFrom < 0);
		if (isSetBack() && mayCancel)
			Utils.teleportPlayerOnGround(p);
	}
}
