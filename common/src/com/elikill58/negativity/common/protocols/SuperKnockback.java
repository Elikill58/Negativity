package com.elikill58.negativity.common.protocols;

import java.util.HashMap;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventPriority;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent.ToggleAction;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

public class SuperKnockback extends Cheat implements Listeners {

	public SuperKnockback() {
		super(CheatKeys.SUPER_KNOCKBACK, CheatCategory.COMBAT, Materials.EYE_OF_ENDER, true, false, "superkb",
				"gigakb");
	}

	@Check(name = "diff", description = "Check the time and when player sprint", conditions = {})
	public void onPacketReceive(PacketReceiveEvent e, NegativityPlayer np) {
		if (!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		AbstractPacket packet = e.getPacket();
		PacketType type = packet.getPacketType();
		long time = System.currentTimeMillis();
		HashMap<String, Long> longContent = np.longs.getAllContent(getKey());
		if (!type.equals(PacketType.Client.ENTITY_ACTION) && !type.equals(PacketType.Client.USE_ENTITY)) {
			if (type.equals(PacketType.Client.POSITION)) {
				np.objects.set(getKey(), "packet-waiting", PacketWaiting.NOTHING);
				longContent.put("action-sneak", time);
			}
			return;
		}
		PacketWaiting actual = (PacketWaiting) np.objects.get(getKey(), "packet-waiting", PacketWaiting.NOTHING),
				oldActual = actual;
		if (type.equals(PacketType.Client.ENTITY_ACTION)) {
			NPacketPlayInEntityAction entityAction = (NPacketPlayInEntityAction) packet.getPacket();
			if (!p.isSameId(String.valueOf(entityAction.entityId)))
				return;

			if (entityAction.action.equals(EnumPlayerAction.START_SPRINTING)
					|| entityAction.action.equals(EnumPlayerAction.STOP_SPRINTING)) {
				if (entityAction.action.equals(EnumPlayerAction.START_SPRINTING)) {
					if (actual == PacketWaiting.FIRST_STOP) {
						actual = PacketWaiting.SECOND_START;
					} else {
						actual = PacketWaiting.FIRST_START;
						longContent.put("first-start", time);
					}
				} else if (entityAction.action.equals(EnumPlayerAction.STOP_SPRINTING)) {
					if (actual == PacketWaiting.FIRST_START) {
						actual = PacketWaiting.FIRST_STOP;
						longContent.put("time-stop", time);
					} else if (actual == PacketWaiting.USE_ENTITY) {
						actual = PacketWaiting.SECOND_STOP;
					}
				}
				long diffAction = time - longContent.getOrDefault("action-sneak", 0l);
				if (diffAction < 1000) {
					longContent.put("diff-action", diffAction);
				}
				longContent.put("action-sneak", time);
			}
			if (oldActual != actual)
				np.objects.set(getKey(), "packet-waiting", actual);
		} else {
			NPacketPlayInUseEntity useEntity = (NPacketPlayInUseEntity) packet.getPacket();
			if (!useEntity.action.equals(EnumEntityUseAction.ATTACK)) {
				np.objects.set(getKey(), "packet-waiting", PacketWaiting.USE_ENTITY);
				return;
			}
			if (actual == PacketWaiting.SECOND_START) {
				long diff = time - longContent.getOrDefault("action-sneak", 0l);
				if (diff < 25) {
					long firstStart = longContent.getOrDefault("first-start", 0l),
							timeStop = longContent.getOrDefault("time-stop", 0l);
					long totalTimeDiff = timeStop - firstStart;
					if (totalTimeDiff > 10)
						return;
					long diffAction = longContent.getOrDefault("diff-action", 0l);
					int amount = (int) ((10 - diff) * (10 - diffAction) * (10 - totalTimeDiff));
					Negativity.alertMod(ReportType.VIOLATION, p, this, parseInPorcent(100 - (diff * totalTimeDiff)),
							"diff", "diff: " + diff + ", totalTime: " + totalTimeDiff + ", action: " + diffAction,
							null, amount);
				}
			}
			np.objects.set(getKey(), "packet-waiting", PacketWaiting.USE_ENTITY);
		}
	}

	@EventListener(priority = EventPriority.POST)
	public void onToggle(PlayerToggleActionEvent e) {
		if (e.getAction().equals(ToggleAction.SPRINT) && e.isCancelled())
			NegativityPlayer.getNegativityPlayer(e.getPlayer()).longs.remove(getKey(), "action-sneak");
	}

	public static enum PacketWaiting {
		NOTHING, FIRST_START, FIRST_STOP, SECOND_START, SECOND_STOP, USE_ENTITY;
	}
}
