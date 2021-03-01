package com.elikill58.negativity.spigot.protocols;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.packets.event.PacketSendEvent;
import com.elikill58.negativity.spigot.utils.LocationUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class StepProtocol extends Cheat implements Listener {

	public static final DataType<Double> BLOCKS_UP = new DataType<Double>("blocks_up", "Blocks UP", () -> new DoubleDataCounter());
	
	public StepProtocol() {
		super(CheatKeys.STEP, true, Material.BRICK_STAIRS, CheatCategory.MOVEMENT, true);
	}
	
	@EventHandler
	public void onPacket(PacketSendEvent e) {
		if(!e.getPacket().getPacketType().equals(PacketType.Server.ENTITY_EFFECT))
			return;
		SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer()).contentBoolean.put("jump-boost-use", true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (np.hasElytra() || p.getItemInHand().getType().name().contains("TRIDENT") || np.isUsingSlimeBlock || Utils.isSwimming(p)
				|| p.isFlying() || LocationUtils.isUsingElevator(p) || p.isInsideVehicle())
			return;
		Location from = e.getFrom(), to = e.getTo();
		Location down = to.clone().subtract(0, 1, 0);
		if(down.getBlock().getType().name().contains("SHULKER"))
			return;
		if(Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		if(np.isBedrockPlayer() && LocationUtils.hasMaterialsAround(down, "SLAB", "FENCE", "STAIRS"))
			return;
		double dif = to.getY() - from.getY();
		double amplifier = (p.hasPotionEffect(PotionEffectType.JUMP) ? Utils.getPotionEffect(p, PotionEffectType.JUMP).getAmplifier() + 1 : 0);
		boolean isUsingJumpBoost = false;
		if(np.isOnGround() && amplifier == 0) {
			np.contentBoolean.remove("jump-boost-use");
		} else
			isUsingJumpBoost = np.contentBoolean.getOrDefault("jump-boost-use", false);
		if (!isUsingJumpBoost && dif > 0 && dif != 0.60) {
			int relia = UniversalUtils.parseInPorcent(dif * 50);
			if (dif > 1.499 && np.ping < 200) {
				boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, relia, "Warn for Step: "
						+ np.getWarn(this) + ". Move " + dif + " blocks up. ping: " + np.ping, hoverMsg("main", "%block%", String.format("%.2f", dif)));
				if (isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
		double diffBoost = dif - (amplifier / 10);
		if(diffBoost > 0.2) {
			recordData(p.getUniqueId(), BLOCKS_UP, diffBoost);
			if(diffBoost > 0.6 && !isUsingJumpBoost && p.getNearbyEntities(3, 3, 3).stream().filter((et) -> et.getType().equals(EntityType.BOAT)).count() == 0) {
				SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffBoost * 125),
						"Basic Y diff: " + dif + ", with boost: " + diffBoost + " (because of boost amplifier " + amplifier + ")",
						hoverMsg("main", "%block%", String.format("%.2f", dif)), (int) ((diffBoost - 0.6) / 0.2));
			}
		}
	}
	
	@Override
	public boolean isBlockedInFight() {
		return true;
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		return "Average of block up : " + ChatColor.GREEN + String.format("%.3f", data.getData(BLOCKS_UP).getAverage());
	}
}
