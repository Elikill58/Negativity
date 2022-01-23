package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventPriority;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class Step extends Cheat implements Listeners {

	public static final DataType<Double> BLOCKS_UP = new DataType<Double>("blocks_up", "Blocks UP", () -> new DoubleDataCounter());
	
	public Step() {
		super(CheatKeys.STEP, CheatCategory.MOVEMENT, Materials.SLIME_BLOCK, true, true);
	}
	
	@EventListener(priority = EventPriority.PRE)
	public void onJumpBoostUse(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		double amplifier = (p.hasPotionEffect(PotionEffectType.JUMP) ? p.getPotionEffect(PotionEffectType.JUMP).get().getAmplifier() : 0);
		if(p.isOnGround() && amplifier == 0) {
			np.booleans.remove(CheatKeys.ALL, "jump-boost-use");
		} else
			np.booleans.get(CheatKeys.ALL, "jump-boost-use", false);
	}
	
	@EventListener
	public void onPacket(PacketSendEvent e) {
		if(!e.getPacket().getPacketType().equals(PacketType.Server.ENTITY_EFFECT))
			return;
		NPacketPlayOutEntityEffect packet = (NPacketPlayOutEntityEffect) e.getPacket().getPacket();
		if(packet.type.equals(PotionEffectType.JUMP))
			NegativityPlayer.getNegativityPlayer(e.getPlayer()).booleans.set(CheatKeys.ALL, "jump-boost-use", true);
	}

	@Check(name = "dif", description = "Distance about blocks up", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA, CheckConditions.NO_SWIM,
			CheckConditions.NO_FLY, CheckConditions.NO_ON_BEDROCK, CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_USE_SLIME,
			CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_BLOCK_MID_AROUND_BELOW })
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if(Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		Location from = e.getFrom(), to = e.getTo();
		Location down = to.clone().sub(0, 1, 0);
		if(down.getBlock().getType().getId().contains("SHULKER"))
			return;
		double dif = to.getY() - from.getY();
		boolean isUsingJumpBoost = np.booleans.get(CheatKeys.ALL, "jump-boost-use", false);
		if (!isUsingJumpBoost && dif > 0 && dif != 0.60 && p.getVelocity().getY() < 0.5) {
			int ping = p.getPing(), relia = UniversalUtils.parseInPorcent(dif * 50);
			if ((dif > 1.499) && ping < 300) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, relia, "dif", "Move " + dif + " blocks up.", hoverMsg("main", "%block%", String.format("%.2f", dif)));
				if (isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
	}

	@Check(name = "dif-boost", description = "Distance while counting jump", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA,
			CheckConditions.NO_SWIM, CheckConditions.NO_FLY, CheckConditions.NO_ON_BEDROCK, CheckConditions.NO_USE_ELEVATOR,
			CheckConditions.NO_USE_SLIME, CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.NO_BLOCK_MID_AROUND_BELOW })
	public void onPlayerMoveDifBoost(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if(Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		Location from = e.getFrom(), to = e.getTo();
		Location down = to.clone().sub(0, 1, 0);
		if(down.getBlock().getType().getId().contains("SHULKER"))
			return;
		double dif = to.getY() - from.getY();
		double amplifier = (p.hasPotionEffect(PotionEffectType.JUMP) ? p.getPotionEffect(PotionEffectType.JUMP).get().getAmplifier() : 0);
		boolean isUsingJumpBoost = np.booleans.get(CheatKeys.ALL, "jump-boost-use", false);
		double diffBoost = dif - (amplifier / 10) - Math.abs(p.getVelocity().getY());
		if(diffBoost > 0.2) {
			recordData(p.getUniqueId(), BLOCKS_UP, diffBoost);
			if (!isUsingJumpBoost && (diffBoost > 0.5) && !(diffBoost <= 0.6 && diffBoost >= 0.56) // 0.56-0.6 is to bypass carpet and other no-full blocks
				&& !(amplifier > 0 && diffBoost < 0.55) && !LocationUtils.hasBoatAroundHim(p.getLocation())) {
				Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffBoost == 0.25 ? 95 : diffBoost * 125), "dif-boost",
					"Basic Y diff: " + dif + ", with boost: " + diffBoost + " (because of boost amplifier " + amplifier + ") Dir Y: " + p.getLocation().getDirection().getY(),
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
