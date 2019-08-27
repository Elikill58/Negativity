package com.elikill58.negativity.sponge.protocols;

import java.text.NumberFormat;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.FakePlayer;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.flowpowered.math.vector.Vector3d;

public class ForceFieldProtocol extends Cheat {

	//private Task task;
	
	public ForceFieldProtocol() {
		super("FORCEFIELD", true, ItemTypes.DIAMOND_SWORD, true, true, "ff", "killaura");
		/*if(isActive()) {
			task = Task.builder().delay(250, TimeUnit.MILLISECONDS).execute(new Runnable() {
				@Override
				public void run() {
					if(!isActive()) {
						task.cancel();
						return;
					}
					for(Player p : Sponge.getServer().getOnlinePlayers()) {
						SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
						if(!Perm.hasPerm(np, "bypass.forcefield"))
							np.spawnRandom();
					}
				}
			}).submit(SpongeNegativity.getInstance());
		}*/
	}
	
	@Listener
	public void onEntityDamageByEntity(DamageEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		double dis = e.getTargetEntity().getLocation().getPosition().distance(p.getLocation().getPosition());
		if(p.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
			if(p.getItemInHand(HandTypes.MAIN_HAND).get().getType().equals(ItemTypes.BOW))
				return;
		}
		if (dis > (Adapter.getAdapter().getDoubleInConfig("cheats.forcefield.reach") + (p.gameMode().get().equals(GameModes.CREATIVE) ? 1 : 0))) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumIntegerDigits(2);
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this,
					Utils.parseInPorcent(dis * 2 * 10),
					"Big distance with: " + e.getTargetEntity().getType().getName().toLowerCase() + ". Exact distance: "
							+ dis + ". Ping: " + Utils.getPing(p),
					"Distance with " + e.getTargetEntity().getType().getName() + ": " + nf.format(dis));
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}

	@Listener
	public void onPlayerInteract(InteractEvent e, @First Player p){
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		Location<World> ploc = p.getLocation();
		Vector3d eyeloc = p.getHeadRotation();
		FakePlayer c = null;
		double distanceWithPlayer = 500, distanceWithEye = 500;
		for (FakePlayer temp : np.FAKE_PLAYER) {
			Location<World> cloc = temp.getLocation();
			double nextDistanceWithPlayer = ploc.getPosition().distance(cloc.getPosition()), nextDistanceWithEye = eyeloc.distance(cloc.getPosition());
			if (nextDistanceWithPlayer < distanceWithPlayer && nextDistanceWithPlayer < 10) {
				distanceWithPlayer = nextDistanceWithPlayer;
				c = temp;
			} else if (nextDistanceWithEye < distanceWithEye && nextDistanceWithEye < 10) {
				distanceWithEye = nextDistanceWithEye;
				c = temp;
			}
		}
		if (c == null)
			return;

		np.fakePlayerTouched++;
		c.hide(p);
		manageForcefieldForFakeplayer(p, np);
	}

	public static void manageForcefieldForFakeplayer(Player p, SpongeNegativityPlayer np) {
		if (np.fakePlayerTouched < 5)
			return;
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		double rapport = np.fakePlayerTouched / (timeBehindStart / 1000);
		SpongeNegativity.alertMod(rapport > 20 ? ReportType.VIOLATION : ReportType.WARNING, p, Cheat.fromString("FORCEFIELD").get(),
				Utils.parseInPorcent(rapport * 10), "Hitting fake entities. " + np.fakePlayerTouched
						+ " entites touch in " + timeBehindStart + " millisecondes",
				np.fakePlayerTouched + " fake players touched in " + timeBehindStart + " ms");
	}
}
