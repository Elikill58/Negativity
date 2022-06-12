package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class AutoClick extends Cheat {

	public static final DataType<Integer> CLICKS = new DataType<Integer>("clicks", "Clicks",
			() -> new IntegerDataCounter());

	public AutoClick() {
		super(CheatKeys.AUTO_CLICK, CheatCategory.COMBAT, Materials.FISHING_ROD, CheatDescription.VERIF);
	}

	@Check(name = "count", description = "Count click 1 by 1")
	public void onInteract(PacketReceiveEvent e, NegativityPlayer np) {
		if (!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		PacketType type = e.getPacket().getPacketType();
		if (type.equals(PacketType.Client.USE_ENTITY)) {
			np.entityClick++;
		} else if (type.equals(PacketType.Client.BLOCK_DIG)) {
			NPacketPlayInBlockDig dig = (NPacketPlayInBlockDig) e.getPacket().getPacket();
			if (dig.action.equals(DigAction.START_DIGGING))
				np.leftBlockClick++;
			else if (dig.action.equals(DigAction.CANCEL_DIGGING))
				np.leftCancelled++;
			else if (dig.action.equals(DigAction.FINISHED_DIGGING))
				np.leftFinished++;
		} else if (type.equals(PacketType.Client.BLOCK_PLACE)) {
			np.rightBlockClick++;
		}
		int click = np.getClick();
		int ping = p.getPing(), clickPinged = click - (ping / 9);
		if (clickPinged > getConfig().getInt("click_alert", 20)) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(click * 2.5), "count",
					"Clicks: " + click + ", pinged: " + clickPinged + ". Detailed: entity: " + np.entityClick + ", block start/cancel/finish: " + np.leftBlockClick + "/" + np.leftCancelled + "/" + np.leftFinished + ", place: " + np.rightBlockClick + " Last: " + np.lastClick
							+ "; Record: " + np.getAccount().getMostClicksPerSecond(),
					hoverMsg("main", "%click%", click));
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		int currentClick = np.getClick();
		DataCounter<Integer> counter = data.getData(CLICKS);
		counter.add(currentClick);
		if (counter.getMax() == 0)
			return null;
		return Utils.coloredMessage("&aCurrent&7/&cMaximum&7/&6Average&7: &a" + currentClick + "&7/&c"
				+ counter.getMax() + "&7/&6" + counter.getAverage() + " &7clicks");
	}
}
