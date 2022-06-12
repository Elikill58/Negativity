package com.elikill58.negativity.common.timers;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.common.protocols.AutoClick;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AnalyzePacketTimer implements Runnable {

	@Override
	public void run() {
		for (NegativityPlayer np : NegativityPlayer.getAllNegativityPlayers()) {
			Player p = np.getPlayer();
			if(p == null || !p.isOnline()){
				NegativityPlayer.removeFromCache(np.getUUID());
				continue;
			}
			int ping = p.getPing();
			if (ping == 0)
				ping = 1;
			
			int flying = np.packets.getOrDefault(PacketType.Client.FLYING, 0);
			
			int flyingWithPing = flying - (ping / 6);
			if (flyingWithPing > 28) {
				if(p.getItemInHand().getType().equals(Materials.BOW))
					np.flyingReason = FlyingReason.BOW;
				Cheat c = np.flyingReason.getCheat();
				if (np.hasDetectionActive(c) && c.checkActive("packet")) {
					double[] allTps = Adapter.getAdapter().getTPS();
					int porcent = UniversalUtils.parseInPorcent(flyingWithPing - (ping / (allTps[1] - allTps[0] > 0.5 ? 9 : 8)));
					boolean back = Negativity.alertMod(flyingWithPing > 30 ? ReportType.WARNING : ReportType.VIOLATION, p, c, porcent,
							"packet", "Flying in one second: " + flying + ", ping: " + ping,
							c.hoverMsg("packet", "%flying%", flyingWithPing), flyingWithPing / 30);
					if(c.isSetBack() && back){
						switch(np.flyingReason){
						case BOW:
							break;
						case EAT:
							p.getInventory().addItem(ItemBuilder.Builder(np.eatMaterial).build());
							break;
						case POTION:
							List<PotionEffect> po = new ArrayList<>(np.potionEffects);
							for(PotionEffect pe : po)
								if(!p.hasPotionEffect(pe.getType())){
									p.addPotionEffect(pe.getType(), pe.getDuration(), pe.getAmplifier());
									np.potionEffects.remove(pe);
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

			NegativityAccount account = np.getAccount();
			int click = np.getClick();
			if (account.getMostClicksPerSecond() < click) {
				account.setMostClicksPerSecond(click);
			}
			Cheat.forKey(CheatKeys.AUTO_CLICK).recordData(p.getUniqueId(), AutoClick.CLICKS, click);
			np.lastClick = click;
			np.clearClick();
			np.clearPackets();
		}
	}
}
