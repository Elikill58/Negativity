package com.elikill58.negativity.sponge.timers;

import java.sql.Timestamp;
import java.util.function.Consumer;

import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Cheat;
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;

public class PacketsTimers implements Consumer<Task> {

	@Override
	public void accept(Task task) {
		for (Player p : Utils.getOnlinePlayers()) {
			SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
			if(np.BETTER_CLICK < np.ACTUAL_CLICK)
				np.BETTER_CLICK = np.ACTUAL_CLICK;
			np.LAST_CLICK = np.ACTUAL_CLICK;
			np.ACTUAL_CLICK = 0;
			if (np.SEC_ACTIVE < 2) {
				np.SEC_ACTIVE++;
				return;
			}
			if(!p.isOnline()){
				np.destroy(false);
				return;
			}
			if(!SpongeNegativity.hasPacketGate)
				return;
			int ping = Utils.getPing(p);
			if (ping == 0)
				ping = 1;
			int flying = np.FLYING - (ping / 6);
			if (flying > 28) {
				if (np.hasDetectionActive(np.flyingReason.getCheat())) {
					if(np.getItemTypeInHand().getType().equals(ItemTypes.BOW))
						np.flyingReason = SpongeNegativityPlayer.FlyingReason.BOW;
					ReportType type = ReportType.WARNING;
					if (flying > 25)
						type = ReportType.VIOLATION;
					if(np.flyingReason.getCheat().isSetBack() && SpongeNegativity.alertMod(type, p, np.flyingReason.getCheat(), Utils.parseInPorcent(flying - (ping / 9)),
							"Flying in one second: " + np.FLYING + ", ping: " + ping + ", max_flying: " + np.MAX_FLYING,
							"Too many packet: " + flying + "\n(Valid packets with low ping: 20)")){
						switch(np.flyingReason){
							case BOW:
								break;
							case EAT:
								p.getInventory().offer(ItemStack.builder().itemType(np.eatMaterial).quantity(1).build());
								break;
							case POTION:
								for(PotionEffect pe : np.POTION_EFFECTS)
									if(!np.hasPotionEffect(pe.getType())){
										PotionEffectData effects = p.getOrCreate(PotionEffectData.class).get();
										effects.addElement(pe);
										p.offer(effects);
										np.POTION_EFFECTS.remove(pe);
									}
								break;
							case REGEN:
								break;
							default:
								break;
						}
					}
				}
			}
			if (np.hasDetectionActive(Cheat.FLY)) {
				if (np.FLYING > 4 && (np.POSITION + np.POSITION_LOOK + np.FLYING) < 9) {
					np.NO_PACKET++;
					if (np.NO_PACKET > 4) {
						int reliability = 0;
						ReportType type = ReportType.WARNING;
						if (np.ONLY_KEEP_ALIVE > 10)
							type = ReportType.VIOLATION;
						SpongeNegativity.alertMod(type, p, Cheat.FLY, reliability,
								np.ONLY_KEEP_ALIVE + " second of only KeepAlive. Last other: "
										+ np.LAST_OTHER_KEEP_ALIVE + "(" + new Timestamp(np.TIME_OTHER_KEEP_ALIVE)
										+ ", there is: " + (System.currentTimeMillis() - np.TIME_OTHER_KEEP_ALIVE)
										+ "ms)");
					}
				}
			}
			if (np.hasDetectionActive(Cheat.FORCEFIELD)) {
				if (np.ARM > 14 && np.USE_ENTITY > 20) {
					ReportType type = ReportType.WARNING;
					if (np.getWarn(Cheat.FORCEFIELD) > 4)
						type = ReportType.VIOLATION;
					SpongeNegativity.alertMod(type, p, Cheat.FORCEFIELD,
							Utils.parseInPorcent(np.ARM + np.USE_ENTITY + np.getWarn(Cheat.FORCEFIELD)),
							"ArmAnimation (Attack in one second): " + np.ARM
									+ ", UseEntity (interaction with other entity): " + np.USE_ENTITY + " And warn: "
									+ np.getWarn(Cheat.FORCEFIELD) + ". Ping: " + ping);
				}
			}
			if (np.hasDetectionActive(Cheat.BLINK) && !np.bypassBlink) {
				if (ping < 140) {
					int total = np.ALL - np.KEEP_ALIVE;
					if (total == 0) {
						boolean last = np.IS_LAST_SEC_BLINK == 2;
						np.IS_LAST_SEC_BLINK++;
						long time_last = System.currentTimeMillis() - np.TIME_OTHER_KEEP_ALIVE;
						if (last) {
							SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.BLINK, Utils.parseInPorcent(160 - ping),
									"No packet. Last other than KeepAlive: " + np.LAST_OTHER_KEEP_ALIVE + " there is: "
											+ time_last + "ms . Ping: " + ping + ". Warn: " + np.getWarn(Cheat.BLINK));
						}
					} else
						np.IS_LAST_SEC_BLINK = 0;
				} else
					np.IS_LAST_SEC_BLINK = 0;
			}
			if(np.hasDetectionActive(Cheat.SNEAK)){
				if(ping < 140){
					if(np.ENTITY_ACTION > 35){
						if(np.IS_LAST_SEC_SNEAK)
							SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.SNEAK, Utils.parseInPorcent(55 + np.ENTITY_ACTION), "EntityAction packet: " + np.ENTITY_ACTION + " Ping: " + ping + " Warn for Sneak: " + np.getWarn(Cheat.SNEAK));
						np.IS_LAST_SEC_SNEAK = true;
					} else np.IS_LAST_SEC_SNEAK = false;
				}
			}
			if(np.hasDetectionActive(Cheat.FASTPLACE)){
				if(ping < 200){
					if(np.BLOCK_PLACE > 10){
						SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.FASTPLACE, Utils.parseInPorcent(np.BLOCK_PLACE * 5), "BLockPlace: " + np.BLOCK_PLACE + " Ping: " + ping + " Warn for BlockPlace: " + np.getWarn(Cheat.FASTPLACE));
					}
				}
			}
			if(np.hasDetectionActive(Cheat.EDITED_CLIENT)){
				if(ping < Cheat.EDITED_CLIENT.getMaxAlertPing()){
					int allPos = np.POSITION_LOOK + np.POSITION;
					if(allPos > 30) {
						np.addWarn(Cheat.EDITED_CLIENT);
						SpongeNegativity.alertMod(allPos > 55 ? ReportType.VIOLATION : ReportType.WARNING, p, Cheat.EDITED_CLIENT, Utils.parseInPorcent(30 + allPos), "PositionLook packet: " + np.POSITION_LOOK + " Position Packet: " + np.POSITION +  " (=" + allPos + " Ping: " + ping + " Warn for SpeedHack: " + np.getWarn(Cheat.SPEEDHACK));
					}
				}
			}
			np.clearPackets();
		}
	}
}
