package com.elikill58.negativity.common;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.universal.Version;

public class PacketListener implements Listeners {

	@EventListener
	public void onPacketReceive(PacketReceiveEvent e) {
		if(!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		AbstractPacket packet = e.getPacket();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		np.ALL_PACKETS++;
		PacketType type = packet.getPacketType();
		np.PACKETS.put(type, np.PACKETS.getOrDefault(type, 0) + 1);
		if(type == PacketType.Client.BLOCK_DIG && !Version.getVersion().equals(Version.V1_7) && packet.getPacket() instanceof NPacketPlayInBlockDig) {
			NPacketPlayInBlockDig blockDig = (NPacketPlayInBlockDig) packet.getPacket();
			if(blockDig.action != DigAction.FINISHED_DIGGING)
				return;
			
			Block b = blockDig.getBlock(p.getWorld());
			BlockBreakEvent event = new BlockBreakEvent(p, b);
			EventManager.callEvent(event);
			if(event.isCancelled())
				packet.setCancelled(event.isCancelled());
			/*PacketContent content = packet.getContent();
			Object dig = content.getSpecificModifier(PacketUtils.getNmsClass("PacketPlayInBlockDig$EnumPlayerDigType")).read("c");
			if(!dig.toString().contains("STOP_DESTROY_BLOCK"))
				return;
			try {
				Object bp = content.getSpecificModifier(PacketUtils.getNmsClass("BlockPosition")).read("a");
				Class<?> baseBpClass = PacketUtils.getNmsClass("BaseBlockPosition");
				int x = (int) baseBpClass.getDeclaredMethod("getX").invoke(bp);
				int y = (int) baseBpClass.getDeclaredMethod("getY").invoke(bp);
				int z = (int) baseBpClass.getDeclaredMethod("getZ").invoke(bp);
				Block b = p.getWorld().getBlockAt(x, y, z);
				BlockBreakEvent event = new BlockBreakEvent(p, b);
				EventManager.callEvent(event);
				if(event.isCancelled())
					packet.setCancelled(event.isCancelled());
			} catch (Exception exc) {
				exc.printStackTrace();
			}*/
		}
	}
}
