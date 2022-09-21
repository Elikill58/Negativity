package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.SNEAK;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.SneakData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Sneak extends Cheat {

	public Sneak() {
		super(SNEAK, CheatCategory.MOVEMENT, Materials.BLAZE_POWDER, SneakData::new);
	}

	@Check(name = "sneak-sprint", description = "Sneak while sprinting", conditions = { CheckConditions.SURVIVAL, CheckConditions.SNEAK, CheckConditions.SPRINT, CheckConditions.NO_FLY })
	public void onMove(PlayerMoveEvent e, NegativityPlayer np, SneakData data) {
		Player p = e.getPlayer();
		if (data.wasSneaking) {
			if(!p.getPlayerVersion().isNewerOrEquals(Version.V1_14)) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(105 - (p.getPing() / 10)),
						"sneak-sprint", "Sneak, sprint, no fly");
				if(mayCancel && isSetBack()) {
					e.setCancelled(true);
					p.setSprinting(false);
				}
			}
		}
		data.wasSneaking = p.isSneaking();
	}
	
	@Check(name = "packet", description = "Amount of sneacking packet")
	public void onPacketClear(PlayerPacketsClearEvent e, NegativityPlayer np, SneakData data) {
		Player p = e.getPlayer();
		int ping = p.getPing();
		if(ping < 140) {
			int entityAction = e.getPackets().getOrDefault(PacketType.Client.ENTITY_ACTION, 0);
			if(entityAction > 35){
				if(data.lastSecond){
					Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(55 + entityAction), "packet",
							"EntityAction packet: " + entityAction);
					if(isSetBack())
						p.setSneaking(false);
				}
				data.lastSecond = true;
			} else
				data.lastSecond = false;
		}
	}
}
