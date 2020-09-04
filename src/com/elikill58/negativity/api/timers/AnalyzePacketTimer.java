package com.elikill58.negativity.api.timers;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.utils.ItemUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AnalyzePacketTimer implements Runnable {

	@Override
	public void run() {
		// TODO move everything on protocols
		for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
			if(!p.isOnline()){
				NegativityPlayer.removeFromCache(p.getUniqueId());
				continue;
			}
			NegativityPlayer np = NegativityPlayer.getCached(p.getUniqueId());
			if (np.SEC_ACTIVE < 2) {
				np.SEC_ACTIVE++;
				return;
			}
			int ping = p.getPing();
			if (ping == 0)
				ping = 1;
			
			int flying = np.PACKETS.getOrDefault(PacketType.Client.FLYING, 0);
			int entityAction = np.PACKETS.getOrDefault(PacketType.Client.ENTITY_ACTION, 0);
			int blockDig = np.PACKETS.getOrDefault(PacketType.Client.BLOCK_DIG, 0);
			
			int flyingWithPing = flying - (ping / 6);
			if (flyingWithPing > 28) {
				if(p.getItemInHand().getType().equals(Materials.BOW))
					np.flyingReason = FlyingReason.BOW;
				Cheat c = np.flyingReason.getCheat();
				if (np.hasDetectionActive(c) && c.checkActive("packet")) {
					double[] allTps = Adapter.getAdapter().getTPS();
					int porcent = UniversalUtils.parseInPorcent(flyingWithPing - (ping / (allTps[1] - allTps[0] > 0.5 ? 9 : 8)));
					boolean back = Negativity.alertMod(flyingWithPing > 30 ? ReportType.WARNING : ReportType.VIOLATION, p, c, porcent,
							"packet", "Flying in one second: " + flying + ", ping: " + ping + ", max_flying: " + np.MAX_FLYING,
							c.hoverMsg("packet", "%flying%", flyingWithPing), (int) flyingWithPing / 30);
					if(c.isSetBack() && back){
						switch(np.flyingReason){
						case BOW:
							break;
						case EAT:
							p.getInventory().addItem(ItemBuilder.Builder(np.eatMaterial).build());
							break;
						case POTION:
							List<PotionEffect> po = new ArrayList<>(np.POTION_EFFECTS);
							for(PotionEffect pe : po)
								if(!p.hasPotionEffect(pe.getType())){
									p.addPotionEffect(pe.getType(), pe.getDuration(), pe.getAmplifier());
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
			
			Cheat SNEAK = Cheat.forKey(CheatKeys.SNEAK);
			if(np.hasDetectionActive(SNEAK) && SNEAK.checkActive("packet") && ping < 140){
				if(entityAction > 35){
					if(np.booleans.get(SNEAK.getKey(), "last-sec", false)){
						Negativity.alertMod(ReportType.WARNING, p, SNEAK, UniversalUtils.parseInPorcent(55 + entityAction), "packet",
								"EntityAction packet: " + entityAction + " Ping: " + ping + " Warn for Sneak: " + np.getWarn(SNEAK));
						if(SNEAK.isSetBack())
							p.setSneaking(false);
					}
					np.booleans.set(SNEAK.getKey(), "last-sec", true);
				} else np.booleans.set(SNEAK.getKey(), "last-sec", false);
			}
			
			Cheat NUKER = Cheat.forKey(CheatKeys.NUKER);
			if(np.hasDetectionActive(NUKER) && NUKER.checkActive("packet"))
				if(ping < NUKER.getMaxAlertPing() && (blockDig - (ping / 10)) > 20 && !ItemUtils.hasDigSpeedEnchant(p.getItemInHand()))
					Negativity.alertMod(blockDig > 200 ? ReportType.VIOLATION : ReportType.WARNING, p, NUKER, UniversalUtils.parseInPorcent(20 + blockDig),
							"packet", "BlockDig packet: " + blockDig + ", ping: " + ping + " Warn for Nuker: " + np.getWarn(NUKER));

			Cheat SPEED = Cheat.forKey(CheatKeys.SPEED);
			if(np.hasDetectionActive(SPEED) && SPEED.checkActive("move-amount"))
				if(np.MOVE_TIME > 60)
					Negativity.alertMod(np.MOVE_TIME > 100 ? ReportType.VIOLATION : ReportType.WARNING, p, SPEED, UniversalUtils.parseInPorcent(np.MOVE_TIME * 2),
							"move-amount", "Move " + np.MOVE_TIME + " times. Ping: " + ping + " Warn for Speed: " + np.getWarn(SPEED));
			np.MOVE_TIME = 0;
			np.clearPackets();
		}
	}
}
