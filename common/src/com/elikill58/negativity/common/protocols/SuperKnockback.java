package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SuperKnockback extends Cheat implements Listeners {

	public SuperKnockback() {
		super(CheatKeys.SUPER_KNOCKBACK, CheatCategory.COMBAT, Materials.EYE_OF_ENDER, true, false, "superkb",
				"gigakb");
	}

	@Check(name = "diff", description = "Check the time and when player sprint", conditions = {})
	public void onPacketReceive(PacketReceiveEvent e) {
		if (!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		AbstractPacket packet = e.getPacket();
		if (!packet.getPacketType().equals(PacketType.Client.ENTITY_ACTION))
			return;
		NPacketPlayInEntityAction entityAction = (NPacketPlayInEntityAction) packet.getPacket();
		if (entityAction.entityId != p.getEntityId())
			return;
		if (entityAction.action.equals(EnumPlayerAction.START_SPRINTING)
				|| entityAction.action.equals(EnumPlayerAction.STOP_SPRINTING)) {
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
			long last = np.longs.get(getKey(), "action-sneak", 0l), actual = System.currentTimeMillis();
			long diff = actual - last;
			if (diff < 15) {
				int reliability = UniversalUtils.parseInPorcent(100 - diff + (np.isAttacking ? 5 : 0));
				int amount = (int) ((np.isAttacking ? 5 : 1) * (diff < 10 ? 10 - diff : 1));
				Negativity.alertMod(ReportType.WARNING, p, this, reliability, "diff", "diff: " + diff + " (" + last
						+ " / " + actual + ") " + entityAction.action.name() + ", attack: " + np.isAttacking, null, amount);
			}
			np.longs.set(getKey(), "action-sneak", actual);
		}
	}
}
