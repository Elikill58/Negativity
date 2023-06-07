package com.elikill58.negativity.spigot.protocols;

import java.lang.reflect.Method;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.PacketContent;
import com.elikill58.negativity.spigot.packets.event.PacketReceiveEvent;
import com.elikill58.negativity.spigot.utils.HandUtils;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.PacketType.Client;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NukerProtocol extends Cheat implements Listener {

	private Method getX, getY, getZ;

	public NukerProtocol() {
		super(CheatKeys.NUKER, true, Material.BEDROCK, CheatCategory.WORLD, true, "breaker", "bed breaker", "bedbreaker");
		try {
			Class<?> baseBpClass = PacketUtils.getNmsClass("BaseBlockPosition", "core.");
			try {
				getX = baseBpClass.getDeclaredMethod("getX");
				getY = baseBpClass.getDeclaredMethod("getY");
				getZ = baseBpClass.getDeclaredMethod("getZ");
				SpigotNegativity.getInstance().getLogger().info("Founded getX baseBlock's methods");
			} catch (Exception e) {
				getX = baseBpClass.getDeclaredMethod("u");
				getY = baseBpClass.getDeclaredMethod("v");
				getZ = baseBpClass.getDeclaredMethod("w");
				SpigotNegativity.getInstance().getLogger().info("Founded u/v/w baseBlock's methods");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPacketDig(PacketReceiveEvent e) {
		AbstractPacket packet = e.getPacket();
		PacketType type = packet.getPacketType();
		if (!type.equals(Client.BLOCK_DIG))
			return;
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.hasPotionEffect(PotionEffectType.FAST_DIGGING))
			return;
		PacketContent content = packet.getContent();
		Object dig = content.getSpecificModifier(PacketUtils.getNmsClass("PacketPlayInBlockDig$EnumPlayerDigType", "network.protocol.game.")).read("c");
		if (!dig.toString().contains("STOP_DESTROY_BLOCK"))
			return;
		try {
			int x, y, z;
			Object bp = content.getSpecificModifier(PacketUtils.getNmsClass("BlockPosition", "core.")).read("a");
			x = (int) getX.invoke(bp);
			y = (int) getY.invoke(bp);
			z = (int) getZ.invoke(bp);
			Block b = p.getWorld().getBlockAt(x, y, z);
			manageNuker(e, p, np, b);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public void manageNuker(Cancellable e, Player p, SpigotNegativityPlayer np, Block b) {
		Block target = Utils.getTargetBlock(p, 5);
		if (target != null) {
			double distance = target.getLocation().distance(b.getLocation());
			if ((target.getType() != b.getType()) && distance > 3.5 && target.getType() != Material.AIR) {
				boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 15 - np.ping),
						"BlockDig " + b.getType().name() + ", player see " + target.getType().name() + ". Distance between blocks " + distance + " block. Warn: " + np.getWarn(this));
				if (isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
		long temp = System.currentTimeMillis(), dis = temp - np.LAST_BLOCK_BREAK;
		Material m = b.getType();
		if (dis < 50 && m.isSolid() && !isInstantBlock(m.name()) && !HandUtils.handHasEnchant(p, Enchantment.DIG_SPEED) && !p.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, this, (int) (100 - dis),
					"Type: " + b.getType().name() + ". Last: " + np.LAST_BLOCK_BREAK + ", Now: " + temp + ", diff: " + dis + ". Warn: " + np.getWarn(this),
					hoverMsg("breaked_in", "%time%", dis));
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
		np.LAST_BLOCK_BREAK = temp;
	}

	private boolean isInstantBlock(String m) {
		return m.contains("SLIME") || m.contains("TNT") || m.contains("LEAVE") || m.contains("NETHERRACK") || m.contains("BAMBOO") || m.contains("SNOW");
	}
}
