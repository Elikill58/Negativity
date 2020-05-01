package com.elikill58.negativity.spigot.packets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.FakePlayer;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.NegativityPacketEvent;
import com.elikill58.negativity.spigot.packets.PacketType.Client;

@SuppressWarnings("deprecation")
public class NegativityPacketManager {

	private IPacketManager packetManager;
	private SpigotNegativity plugin;
	
	public NegativityPacketManager(SpigotNegativity pl) {
		this.plugin = pl;
		packetManager = PacketHandler.run(pl);
		packetManager.addHandler(new PacketHandler() {
			
			@Override
			public void onSend(AbstractPacket packet) {}
			
			@Override
			public void onReceive(AbstractPacket packet) {
				Player p = packet.getPlayer();
				if (!SpigotNegativityPlayer.INJECTED.contains(p.getUniqueId()))
					return;
				if(!plugin.isEnabled())
					return;
				SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
				Bukkit.getScheduler().runTask(plugin, new Runnable() {
					@Override
					public void run() {
						Bukkit.getPluginManager().callEvent(new NegativityPacketEvent(np, packet));
					}
				});
				manageReceive(packet);
			}
		});
	}
	
	public IPacketManager getPacketManager() {
		return packetManager;
	}
	
	private void manageReceive(AbstractPacket packet) {
		Player p = packet.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		np.ALL++;
		if(packet.getPacketType() instanceof PacketType.Client) {
			switch (((Client) packet.getPacketType())) {
			case FLYING:
				np.FLYING++;
				break;
			case KEEP_ALIVE:
				np.KEEP_ALIVE++;
				break;
			case POSITION_LOOK:
				np.POSITION_LOOK++;
				break;
			case BLOCK_PLACE:
				np.BLOCK_PLACE++;
				break;
			case BLOCK_DIG:
				np.BLOCK_DIG++;
				break;
			case POSITION:
				np.POSITION++;
				break;
			case ARM_ANIMATION:
				np.ARM++;
				break;
			case USE_ENTITY:
				np.USE_ENTITY++;
				try {
					/*Object pa = Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayInUseEntity").cast(packet.getPacket());
					Field f = pa.getClass().getDeclaredField("a");
					f.setAccessible(true);
					f.getInt(pa);*/
					int id = packet.getContent().getIntegers().read(0);
					for(FakePlayer fp : np.getFakePlayers())
						if(fp.getEntityId() == id)
							np.removeFakePlayer(fp, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case ENTITY_ACTION:
				np.ENTITY_ACTION++;
				break;
			default:
			}
		}
		if (packet.getPacketType() != Client.KEEP_ALIVE) {
			np.TIME_OTHER_KEEP_ALIVE = System.currentTimeMillis();
			np.LAST_OTHER_KEEP_ALIVE = packet.getPacketName();
		}
	}
}
