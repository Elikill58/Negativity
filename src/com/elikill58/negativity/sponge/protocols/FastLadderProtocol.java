package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.NeedListener;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Cheat;
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;

public class FastLadderProtocol implements NeedListener {

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(Cheat.FASTLADDERS))
			return;
		Location<?> loc = p.getLocation();
		if (!loc.getBlock().getType().equals(BlockTypes.LADDER)){
			np.isOnLadders = false;
			return;
		}
		if (!np.isOnLadders) {
			np.isOnLadders = true;
			return;
		}
		for (PotionEffect pe : p.getOrCreate(PotionEffectData.class).get().asList())
			if (pe.getType().equals(PotionEffectTypes.SPEED) && pe.getAmplifier() > 2)
				return;
		Transform<World> from = e.getFromTransform(), to = e.getToTransform();
		double distance = to.getPosition().distance(from.getPosition());
		int nbLadder = 0;
		Location<?> tempLoc = loc.copy();
		while(tempLoc.getBlock().getType().equals(BlockTypes.LADDER)) {
			nbLadder++;
			tempLoc = tempLoc.add(0, -1, 0);
		}
		if (distance > 0.23 && distance < 3.8 && nbLadder > 2) {
			Location<World> fl = from.getLocation().copy().sub(to.getLocation().getX(), to.getLocation().getY(), to.getLocation().getZ());
			int ping = Utils.getPing(p);
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.FASTLADDERS, Utils.parseInPorcent(distance * 350),
					"On ladders. Distance from/to : " + distance + ". Ping: " + ping + "ms. Number of Ladder: " + nbLadder);
			if (Cheat.FASTLADDERS.isSetBack() && mayCancel)
				e.setToTransform(new Transform<>(new Location<World>(from.getExtent(), fl.getX() / 2, (fl.getY() / 2) + 0.5, fl.getZ())));
		}
	}
}
