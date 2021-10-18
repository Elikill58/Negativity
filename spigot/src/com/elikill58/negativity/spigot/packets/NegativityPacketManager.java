package com.elikill58.negativity.spigot.packets;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketHandler;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.packet.SpigotPacketManager;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.packets.protocollib.ProtocollibPacketManager;
import com.elikill58.negativity.universal.Version;

public class NegativityPacketManager {

	private SpigotPacketManager spigotPacketManager;
	private SpigotNegativity plugin;
	
	public NegativityPacketManager(SpigotNegativity pl) {
		this.plugin = pl;
		Plugin protocolLibPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if (protocolLibPlugin != null) {
			if(checkProtocollibConditions()) {
				pl.getLogger().info("The plugin ProtocolLib has been detected. Loading Protocollib support ...");
				spigotPacketManager = new ProtocollibPacketManager(pl);
			} else {
				pl.getLogger().warning("The plugin ProtocolLib has been detected but you have an OLD version, so we cannot use it.");
				pl.getLogger().warning("Fallback to default Packet system ...");
				spigotPacketManager = new CustomPacketManager(pl);
			}
		} else
			spigotPacketManager = new CustomPacketManager(pl);
		spigotPacketManager.addHandler(new PacketHandler() {
			
			@Override
			public void onSend(AbstractPacket packet) {}
			
			@Override
			public void onReceive(AbstractPacket packet) {
				if(packet.getPlayer() == null)
					return;
				Player p = packet.getPlayer();
				if (!NegativityPlayer.INJECTED.contains(p.getUniqueId()))
					return;
				if(!plugin.isEnabled())
					return;
				manageReceive(packet);
			}
		});
	}
	
	public SpigotPacketManager getPacketManager() {
		return spigotPacketManager;
	}
	
	private boolean checkProtocollibConditions() {
		try {
			Class.forName("com.comphenix.protocol.injector.server.TemporaryPlayer");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	private void manageReceive(AbstractPacket packet) {
		Player p = packet.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		np.ALL_PACKETS++;
		PacketType type = packet.getPacketType();
		np.PACKETS.put(type, np.PACKETS.getOrDefault(type, 0) + 1);
		if(type == PacketType.Client.USE_ENTITY) {
			/*try {
				int id = packet.getContent().getIntegers().read(0);
				for(FakePlayer fp : np.getFakePlayers())
					if(fp.getEntityId() == id)
						np.removeFakePlayer(fp, true);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			NPacketPlayInUseEntity useEntityPacket = (NPacketPlayInUseEntity) packet.getPacket();
			if(useEntityPacket.action.equals(EnumEntityUseAction.ATTACK)) {
				for(Entity entity : p.getWorld().getEntities()) {
					if(entity.getEntityId() == useEntityPacket.entityId && useEntityPacket.action.equals(EnumEntityUseAction.ATTACK)) {
						PlayerDamageEntityEvent event = new PlayerDamageEntityEvent(p, entity);
						EventManager.callEvent(event);
						if(event.isCancelled())
							packet.setCancelled(event.isCancelled());
					}
				}
			}
		} else if(type == PacketType.Client.BLOCK_DIG && !Version.getVersion().equals(Version.V1_7) && packet.getPacket() instanceof NPacketPlayInBlockDig) {
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
