package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.LongDataCounter;

public class FastPlaceProtocol extends Cheat implements Listener {
	
	public static final DataType<Long> TIME_PLACE = new DataType<Long>("time_player", "Time between places", () -> new LongDataCounter());

	public FastPlaceProtocol() {
		super(CheatKeys.FAST_PLACE, false, Material.DIRT, CheatCategory.WORLD, true, "fp");
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if(Utils.getLastTPS() < 19.1)
			return;
		int ping = np.ping;
		if(ping < 50)
			ping = 50;
		long last = System.currentTimeMillis() - np.LAST_BLOCK_PLACE, lastPing = last + (ping / 50);
		if(last < 1000) // last block is too old
			recordData(p.getUniqueId(), TIME_PLACE, lastPing);
		np.LAST_BLOCK_PLACE = System.currentTimeMillis();
		if ((lastPing + 5) < Adapter.getAdapter().getConfig().getInt("cheats.fastplace.time_2_place")) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(50 + lastPing), "Block placed too quickly. Last time: " + last + ", Last with ping: "
							+ lastPing + ". Ping: " + np.ping, hoverMsg("main", "%time%", last));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Long> counter = data.getData(TIME_PLACE);
		long av = counter.getAverage(), low = counter.getMin();
		String colorAverage = (av < 100 ? (av < 20 ? "&c" : "&6") : "&a");
		String colorLow = (low < 100 ? (low < 20 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("&6Time between place: &7Average: " + colorAverage + av + "&7, Lower: " + colorLow + low + " &7(For " + counter.getSize() + " blocks)");
	}
}
