package com.elikill58.negativity.spigot.timers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.protocols.NukerProtocol;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

@SuppressWarnings({"deprecation"})
public class TimerAnalyzePacket extends BukkitRunnable {

	@Override
	public void run() {
		for (Player p : Utils.getOnlinePlayers()) {
			if(!p.isOnline()){
				SpigotNegativityPlayer.removeFromCache(p.getUniqueId());
				continue;
			}
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			if (np.SEC_ACTIVE < 2) {
				np.SEC_ACTIVE++;
				return;
			}
			int ping = np.ping = Utils.getPing(p);
			if (ping == 0)
				ping = 1;
			
			int flying = np.PACKETS.getOrDefault(PacketType.Client.FLYING, 0);
			int arm = np.PACKETS.getOrDefault(PacketType.Client.ARM_ANIMATION, 0);
			int useEntity = np.PACKETS.getOrDefault(PacketType.Client.USE_ENTITY, 0);
			int entityAction = np.PACKETS.getOrDefault(PacketType.Client.ENTITY_ACTION, 0);
			int blockDig = np.PACKETS.getOrDefault(PacketType.Client.BLOCK_DIG, 0);
			
			int flyingWithPing = flying - (ping / 6);
			if (flyingWithPing > 28) {
				Cheat c = np.flyingReason.getCheat();
				if (np.hasDetectionActive(c)) {
					if(p.getItemInHand().getType().equals(Material.BOW))
						np.flyingReason = FlyingReason.BOW;
					double[] allTps = Utils.getTPS();
					int porcent = UniversalUtils.parseInPorcent(flyingWithPing - (ping / (allTps[1] - allTps[0] > 0.5 ? 9 : 8)));
					SpigotNegativity.alertMod(flyingWithPing > 30 ? ReportType.WARNING : ReportType.VIOLATION, p, c, porcent,
							"Flying in one second: " + flying + ", ping: " + ping + ", max_flying: " + np.MAX_FLYING,
							c.hoverMsg("packet", "%flying%", flyingWithPing), (int) flyingWithPing / 30);
					if(c.isSetBack()){
						switch(np.flyingReason){
						case BOW:
							break;
						case EAT:
							p.getInventory().addItem(new ItemStack(np.eatMaterial));
							break;
						case POTION:
							List<PotionEffect> po = new ArrayList<>(np.POTION_EFFECTS);
							for(PotionEffect pe : po)
								if(!p.hasPotionEffect(pe.getType())){
									p.addPotionEffect(pe);
									np.POTION_EFFECTS.remove(pe);
								}
							break;
						case REGEN:
							for(int i = 20; i < flyingWithPing; i++) {
								p.damage(0.5);
							}
							break;
						default:
							break;
						}
					}
				}
			}
			Cheat FORCEFIELD = Cheat.forKey(CheatKeys.FORCEFIELD);
			if (np.hasDetectionActive(FORCEFIELD)) {
				if (arm > 16 && useEntity > 20) {
					ReportType type = ReportType.WARNING;
					if (np.getWarn(FORCEFIELD) > 5)
						type = ReportType.VIOLATION;
					SpigotNegativity.alertMod(type, p, FORCEFIELD,
							UniversalUtils.parseInPorcent(arm + useEntity + np.getWarn(FORCEFIELD)),
							"ArmAnimation (Attack in one second): " + arm
									+ ", UseEntity (interaction with other entity): " + useEntity + " And warn: "
									+ np.getWarn(FORCEFIELD) + ". Ping: " + ping);
				}
			}
			Cheat SNEAK = Cheat.forKey(CheatKeys.SNEAK);
			if(np.hasDetectionActive(SNEAK)){
				if(ping < 140){
					if(entityAction > 35){
						if(np.IS_LAST_SEC_SNEAK){
							SpigotNegativity.alertMod(ReportType.WARNING, p, SNEAK, UniversalUtils.parseInPorcent(55 + entityAction), "EntityAction packet: " + entityAction + " Ping: " + ping + " Warn for Sneak: " + np.getWarn(SNEAK));
							if(SNEAK.isSetBack())
								p.setSneaking(false);
						}
						np.IS_LAST_SEC_SNEAK = true;
					} else np.IS_LAST_SEC_SNEAK = false;
				}
			}
			Cheat NUKER = Cheat.forKey(CheatKeys.NUKER);
			if(np.hasDetectionActive(NUKER))
				if(ping < NUKER.getMaxAlertPing() && (blockDig - (ping / 10)) > 20 && !NukerProtocol.hasDigSpeedEnchant(p.getItemInHand()))
					SpigotNegativity.alertMod(blockDig > 200 ? ReportType.VIOLATION : ReportType.WARNING, p, NUKER, UniversalUtils.parseInPorcent(20 + blockDig), "BlockDig packet: " + blockDig + ", ping: " + ping + " Warn for Nuker: " + np.getWarn(NUKER));

			Cheat SPEED = Cheat.forKey(CheatKeys.SPEED);
			if(np.hasDetectionActive(SPEED))
				if(np.MOVE_TIME > 60)
					SpigotNegativity.alertMod(np.MOVE_TIME > 100 ? ReportType.VIOLATION : ReportType.WARNING, p, SPEED, UniversalUtils.parseInPorcent(np.MOVE_TIME * 2), "Move " + np.MOVE_TIME + " times. Ping: " + ping + " Warn for Speed: " + np.getWarn(SPEED));
			np.MOVE_TIME = 0;
			np.clearPackets();
		}
	}
}
