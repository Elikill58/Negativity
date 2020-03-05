package com.elikill58.negativity.spigot.packets.protocollib;

import java.util.Arrays;
import java.util.List;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.elikill58.negativity.spigot.FakePlayer;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.ByteBuf;

public class ProtocollibSupport {

	private static List<String> channelCheckAntiJigsaw = Arrays.asList("MC|BEdit", "MC|BSign");

	public static void run(Plugin pl) {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(pl, ListenerPriority.LOWEST, PacketType.Play.Client.getInstance()) {
					public void onPacketSending(PacketEvent e) {}

					public void onPacketReceiving(PacketEvent e) {
						if (!SpigotNegativityPlayer.INJECTED.contains(e.getPlayer().getUniqueId())) {
							return;
						}
						SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer());
						np.ALL++;
						if (e.getPacketType().equals(PacketType.Play.Client.FLYING)) {
							np.FLYING++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.POSITION_LOOK)) {
							np.POSITION_LOOK++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.BLOCK_PLACE)) {
							np.BLOCK_PLACE++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.BLOCK_DIG)) {
							np.BLOCK_DIG++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.KEEP_ALIVE)) {
							np.KEEP_ALIVE++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.POSITION)) {
							np.POSITION++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.ARM_ANIMATION)) {
							np.ARM++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.USE_ENTITY)) {
							np.USE_ENTITY++;
							int id = e.getPacket().getIntegers().getValues().get(0);
							for(FakePlayer fp : np.getFakePlayers())
								if(fp.getEntityId() == id)
									np.removeFakePlayer(fp, true);
						} else if (e.getPacketType().equals(PacketType.Play.Client.ENTITY_ACTION)) {
							np.ENTITY_ACTION++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.CUSTOM_PAYLOAD) && Version.getVersion().equals(Version.V1_8)) { // this craching system is only available on some spigot 1.8 server
							manageAntiJigsaw(e, np);
						}
						if (!e.getPacketType().equals(PacketType.Play.Client.KEEP_ALIVE)) {
							np.TIME_OTHER_KEEP_ALIVE = System.currentTimeMillis();
							np.LAST_OTHER_KEEP_ALIVE = e.getPacketType().name();
						}
					}
				});
	}

	public static void manageAntiJigsaw(PacketEvent e, SpigotNegativityPlayer np) {
		try {
			String channel = e.getPacket().getStrings().getValues().get(0);
			int capacity = ((ByteBuf) e.getPacket().getModifier().getValues().get(1)).capacity();
			if (capacity > 25000) {
				if(channelCheckAntiJigsaw.contains(channel)) {
					e.setCancelled(true);
					Cheat c = Cheat.forKey(CheatKeys.BLINK);
					SpigotNegativity.alertMod(np.getAllWarn(c) > 3 ? ReportType.VIOLATION : ReportType.WARNING, np.getPlayer(),
							c, np.getAllWarn(c) > 3 ? 100 : 80, "Trying to crash the server with " + capacity + " requests. Channel used: " + channel + ", ", "Trying to crash the server with " + capacity + " requests");
				}
			}
		} catch (ArrayIndexOutOfBoundsException exc) {
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
