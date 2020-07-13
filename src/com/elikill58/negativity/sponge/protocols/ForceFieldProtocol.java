package com.elikill58.negativity.sponge.protocols;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.sponge.FakePlayer;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.packets.AbstractPacket;
import com.elikill58.negativity.sponge.packets.events.PacketReceiveEvent;
import com.elikill58.negativity.sponge.utils.LocationUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class ForceFieldProtocol extends Cheat {

	public static final DataType<Double> HIT_DISTANCE = new DataType<Double>("hit_distance", "Hit Distance", () -> new DoubleDataCounter());
	public static final DataType<Integer> FAKE_PLAYERS = new DataType<Integer>("fake_players", "Fake Players", () -> new IntegerDataCounter());

	private final NumberFormat distanceFormatter = new DecimalFormat();

	public ForceFieldProtocol() {
		super(CheatKeys.FORCEFIELD, true, ItemTypes.DIAMOND_SWORD, CheatCategory.COMBAT, true, "ff", "killaura");
		distanceFormatter.setMaximumIntegerDigits(2);
	}

	@Listener
	public void onEntityDamageByEntity(DamageEntityEvent e, @First Player p) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}
		
		boolean mayCancel = false;
		Entity cible = e.getTargetEntity();
		if(!(LocationUtils.hasLineOfSight((org.spongepowered.api.entity.living.player.Player) p.getDefaultPlayer(), cible.getLocation()) || LocationUtils.hasLineOfSight(p, cible.getLocation().copy().sub(0, 1, 0)))) {
			mayCancel = SpongeNegativity.alertMod(ReportType.VIOLATION, p, this, UniversalUtils.parseInPorcent(90 + np.getWarn(this)), "Hit " + e.getTargetEntity().getType().getId()
					+ " but cannot see it, ping: " + Utils.getPing(p), hoverMsg("line_sight", "%name%", e.getTargetEntity().getType().getName()));
		}
		if(np.hasThorns(p)) {
			if(mayCancel && isSetBack())
				e.setCancelled(true);
			return;
		}
		
		Optional<ItemStackSnapshot> usedItem = e.getContext().get(EventContextKeys.USED_ITEM);

		double distance = e.getTargetEntity().getLocation().getPosition().distance(p.getLocation().getPosition());
		double allowedReach = Adapter.getAdapter().getConfig().getDouble("cheats.forcefield.reach") + (p.getGameMode().equals(GameMode.CREATIVE) ? 1 : 0);
		if (!(usedItem.isPresent() && usedItem.get().getType() == ItemTypes.BOW) && !e.getTargetEntity().getType().equals(EntityTypes.ENDER_DRAGON)) {
			recordData(p.getUniqueId(), HIT_DISTANCE, distance);
			if(distance > allowedReach)
				SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 2 * 10), "Big distance with: "
						+ e.getTargetEntity().getType().getName().toLowerCase() + ". Exact distance: " + distance + ". Ping: " + Utils.getPing(p),
						hoverMsg("distance", "%name%", e.getTargetEntity().getType().getName(), "%distance%", distanceFormatter.format(distance)));
		}
		if (isSetBack() && mayCancel)
			e.setCancelled(true);
	}

	@Listener
	public void onPacket(PacketReceiveEvent e, @First Player p) {
		AbstractPacket packet = e.getPacket();
		if(!packet.getPacketType().equals(PacketType.Client.USE_ENTITY))
			return;
		try {
			Object nmsPacket = packet.getPacket();
			Method getEntityMethod = null;
			for(Method m : nmsPacket.getClass().getDeclaredMethods())
				if(m.getReturnType() != null && m.getReturnType().getName().equalsIgnoreCase("net.minecraft.entity.Entity"))
					getEntityMethod = m;
			
			if(getEntityMethod == null)
				return;
			//Object et = getEntityMethod.invoke(nmsPacket, Class.forName("net.minecraft.world.World").cast(p.getWorld()));
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	@Listener
	public void onPlayerInteract(InteractEntityEvent e, @First Player p) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}

		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE)) {
			return;
		}
		
		FakePlayer c = np.FAKE_PLAYER.stream().distinct().filter((fp) -> e.getTargetEntity().getUniqueId().equals(fp.getEntity().getUniqueId())).findFirst().orElse(null);

		if (c == null)
			return;
		recordData(p.getUniqueId(), HIT_DISTANCE, e.getTargetEntity().getLocation().getPosition().distance(p.getLocation().getPosition()));

		np.fakePlayerTouched++;
		c.hide((org.spongepowered.api.entity.living.player.Player) p.getDefaultPlayer(), true);
		manageForcefieldForFakeplayer(p, np);
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		double av = data.getData(HIT_DISTANCE).getAverage();
		int nb = data.getData(FAKE_PLAYERS).getSize();
		String color = (av > 3 ? (av > 4 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("Hit distance : " + color + String.format("%.3f", av) + (nb > 0 ? " &7and &c" + nb + " &7fake players touched." : ""));
	}

	public static void manageForcefieldForFakeplayer(Player p, SpongeNegativityPlayer np) {
		Cheat forcefield = Cheat.forKey(CheatKeys.FORCEFIELD);
		forcefield.recordData(p.getUniqueId(), FAKE_PLAYERS, 1);
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		Negativity.alertMod(np.fakePlayerTouched > 10 ? ReportType.VIOLATION : ReportType.WARNING, p, forcefield,
				UniversalUtils.parseInPorcent(np.fakePlayerTouched * 10), "Hitting fake entities. " + np.fakePlayerTouched
						+ " entites touch in " + timeBehindStart + " millisecondes",
						forcefield.hoverMsg("fake_players", "%nb%", np.fakePlayerTouched, "%time%", timeBehindStart));
	}
}
