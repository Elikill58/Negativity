package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventPriority;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent.ToggleAction;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.common.protocols.data.SuperKnockbackData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;

public class SuperKnockback extends Cheat implements Listeners {

	public SuperKnockback() {
		super(CheatKeys.SUPER_KNOCKBACK, CheatCategory.COMBAT, Materials.EYE_OF_ENDER, SuperKnockbackData::new);
	}

	@Check(name = "diff", description = "Check the time and when player sprint", conditions = {})
	public void onPacketReceive(PacketReceiveEvent e, NegativityPlayer np, SuperKnockbackData data) {
		if (!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		NPacket packet = e.getPacket();
		PacketType type = packet.getPacketType();
		long time = System.currentTimeMillis();
		if (!type.equals(PacketType.Client.ENTITY_ACTION) && !type.equals(PacketType.Client.USE_ENTITY)) {
			if (type.equals(PacketType.Client.POSITION)) {
				data.waiting = PacketWaiting.NOTHING;
				data.actionSneak = time;
			}
			return;
		}
		if (type.equals(PacketType.Client.ENTITY_ACTION)) {
			NPacketPlayInEntityAction entityAction = (NPacketPlayInEntityAction) packet;
			if (!p.isSameId(entityAction.entityId))
				return;

			if (entityAction.action.equals(EnumPlayerAction.START_SPRINTING)
					|| entityAction.action.equals(EnumPlayerAction.STOP_SPRINTING)) {
				if (entityAction.action.equals(EnumPlayerAction.START_SPRINTING)) {
					if (data.waiting == PacketWaiting.FIRST_STOP) {
						data.waiting = PacketWaiting.SECOND_START;
					} else {
						data.waiting = PacketWaiting.FIRST_START;
						data.timeFirstStart = time;
					}
				} else if (entityAction.action.equals(EnumPlayerAction.STOP_SPRINTING)) {
					if (data.waiting == PacketWaiting.FIRST_START) {
						data.waiting = PacketWaiting.FIRST_STOP;
						data.timeStop = time;
					} else if (data.waiting == PacketWaiting.USE_ENTITY) {
						data.waiting = PacketWaiting.SECOND_STOP;
					}
				}
				if (data.actionSneak < 1000) {
					data.diffAction = data.actionSneak;
				}
				data.actionSneak = time;
			}
		} else {
			NPacketPlayInUseEntity useEntity = (NPacketPlayInUseEntity) packet;
			if (!useEntity.action.equals(EnumEntityUseAction.ATTACK)) {
				data.waiting = PacketWaiting.USE_ENTITY;
				return;
			}
			if (data.waiting == PacketWaiting.SECOND_START) {
				long diff = time - data.actionSneak;
				if (diff < 25) {
					long totalTimeDiff = data.timeStop - data.timeFirstStart;
					if (totalTimeDiff > 10)
						return;
					int amount = (int) ((10 - diff) * (10 - data.diffAction) * (10 - totalTimeDiff));
					if(Negativity.alertMod(ReportType.VIOLATION, p, this, parseInPorcent(100 - (diff * totalTimeDiff)),
							"diff", "diff: " + diff + ", totalTime: " + totalTimeDiff + ", action: " + data.diffAction,
							null, amount) && isSetBack())
						e.setCancelled(true);
				}
			}
			data.waiting = PacketWaiting.USE_ENTITY;
		}
	}

	@EventListener(priority = EventPriority.POST)
	public void onToggle(PlayerToggleActionEvent e) {
		if (e.getAction().equals(ToggleAction.SPRINT) && e.isCancelled())
			NegativityPlayer.getNegativityPlayer(e.getPlayer()).<SuperKnockbackData>getCheckData(this).actionSneak = 0;
	}

	public static enum PacketWaiting {
		NOTHING, FIRST_START, FIRST_STOP, SECOND_START, SECOND_STOP, USE_ENTITY;
	}
}
