package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.mariuszgromada.math.mxparser.Expression;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.packets.PacketContent.ContentModifier;
import com.elikill58.negativity.spigot.packets.event.PacketSendEvent;
import com.elikill58.negativity.spigot.utils.LocationUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class AntiKnockbackProtocol extends Cheat implements Listener {

	public static final DataType<Double> DISTANCE_DAMAGE = new DataType<Double>("distance_damage",
			"Distance after Damage", () -> new DoubleDataCounter());

	public AntiKnockbackProtocol() {
		super(CheatKeys.ANTI_KNOCKBACK, false, Material.STICK, CheatCategory.COMBAT, true, "antikb", "anti-kb", "no-kb",
				"nokb");
	}

	@EventHandler
	public void onPacket(PacketSendEvent e) {
		if (!e.getPacket().getPacketType().equals(PacketType.Server.ENTITY_VELOCITY))
			return;
		ContentModifier<Integer> ints = e.getPacket().getContent().getIntegers();
		int entId = ints.read("a", -1);
		int velY = ints.read("c", -1);
		
		Adapter ada = Adapter.getAdapter();
		if(entId == -1 || velY == -1) {
			ada.debug("The AntiKnockback is disabled because the entity ID is " + entId + " and the velocity is " + velY + " for EntityVelocity.");
			return;
		}
		
		String algo = ada.getConfig().getString("cheats.antiknockback.algo");
		if(algo.equalsIgnoreCase("0"))
			return;
		
		// search for player
		for (Player p : Bukkit.getOnlinePlayers()) {

			// found player
			if (p.getEntityId() == entId) {
				SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
				if(!np.hasDetectionActive(this))
					return;
				
				if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
					return;
				if (!np.isOnGround() || np.isOnLadders || p.isInsideVehicle() || p.getFireTicks() > 0 || p.isFlying()
						|| p.isDead())
					return;
				
				Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
					// don't check if there is a ceiling or anything that could block from taking kb
					if (LocationUtils.hasAntiKbBypass(p)) {
						ada.debug("AntiKb detection: " + p.getName() + " has bypass.");
						return;
					}

					final int ticksToReact = (int) (1 * 20);// seconds for the client to get up

					if (velY < 5000) {
						// give client some time to react
						new BukkitRunnable() {
							public int iterations = 0;
							public double reachedY = 0 /* diff reached */, baseY = p.getLocation().getY();
							//public Vector baseVector = p.getVelocity().clone();
							//public Location baseLoc = p.getLocation().clone();
							//public boolean vectorChanged = false;

							@Override
							public void run() {
								iterations++;
								Location loc = p.getLocation();
								if (loc.getY() - baseY > reachedY)
									reachedY = loc.getY() - baseY;
								/*if(iterations <= 5) {
									double d = baseVector.distance(p.getVelocity());
									if(d != 0)
										vectorChanged = true;
									ada.debug("KB Distance: " + d);
								} else {
									if(!vectorChanged && loc.distance(baseLoc) < 0.4) {
										SpigotNegativity.alertMod(ReportType.WARNING, p, AntiKnockbackProtocol.this, 90 + iterations, "No changes for the " + iterations
												+ " times. Vector: " + baseVector.toString(), new CheatHover.Literal("No direction changes during " + (((double) iterations) / 20) + " second for " + loc.distance(baseLoc)));
									}
								}*/
								if (iterations > ticksToReact) {
									// default algo : (0.00000008 * velY * velY) + (0.0001 * velY) - 0.0219
									double predictedY = new Expression(algo.replaceAll("velY", String.valueOf(velY))).calculate();
									double percentage = Math.abs(((reachedY - predictedY) / predictedY));
									if (predictedY > reachedY && percentage > 50) {
										// hack
										SpigotNegativity.alertMod(ReportType.WARNING, p, AntiKnockbackProtocol.this,
												UniversalUtils.parseInPorcent(percentage), "ReachedY: " + reachedY + ", predictedY: " + predictedY + ", percentage: "
												+ percentage + ", algo: " + algo + ".", new CheatHover.Literal("Reached Y too different from predicted Y"));
									} else
										ada.debug("AntiKb detection: prediction: " + predictedY + ", percentage: " + percentage + ", reachedY: " + reachedY);
									cancel();
								}
							}
						}.runTaskTimer(SpigotNegativity.getInstance(), 1, 1);
					}
				});
				return;
			}
		}
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> counter = data.getData(DISTANCE_DAMAGE);
		double av = counter.getAverage(), low = counter.getMin();
		String colorAverage = (av < 1 ? (av < 0.5 ? "&c" : "&6") : "&a");
		String colorLow = (low < 1 ? (low < 0.5 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("&6Distance after damage: &7Average: " + colorAverage + String.format("%.2f", av)
				+ "&7, Lower: " + colorLow + String.format("%.2f", low) + " &7(In " + counter.getSize() + " hits)");
	}
}
