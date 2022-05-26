package com.elikill58.negativity.common;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerDamagedByEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerDeathEvent;
import com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInHeldItemSlot;
import com.elikill58.negativity.universal.Adapter;

public class FightListeners implements Listeners {


	@EventListener
	public void onEntityDamageByEntity(PlayerDamagedByEntityEvent e) {
		if(e.getDamager() instanceof Player)
			NegativityPlayer.getNegativityPlayer((Player) e.getDamager()).fight();
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).fight();
	}
	
	@EventListener
	public void onEntityDamageByEntity(PlayerDamageEntityEvent e) {
		if(e.getDamaged() instanceof Player)
			NegativityPlayer.getNegativityPlayer((Player) e.getDamaged()).fight();
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).fight();
	}

	@EventListener
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 10);
	}
	
	@EventListener
	public void onPlayerItemHeld(PacketReceiveEvent e) {
		NPacket packet = e.getPacket().getPacket();
		if(!(packet instanceof NPacketPlayInHeldItemSlot))
			return;
		Player p = (Player) e.getPlayer();
		NPacketPlayInHeldItemSlot held = (NPacketPlayInHeldItemSlot) packet;
		String name = p.getInventory().get(held.slot).getType().getId().toUpperCase();
		if(name.contains("SWORD") || name.contains("AXE") || name.contains("APPLE") || name.contains("BOW") || name.contains("POTION"))
			manageFightBetweenTwoPlayers(p, 15);
	}
	
	/*@EventListener
	public void onProjectileHit(ProjectileHitEvent e) {
		Entity hittingEntity = e.getEntity();
		if (!(hittingEntity instanceof ThrownPotion)) {
			return;
		}
		
		Collection<PotionEffect> effects = ((ThrownPotion) hittingEntity).getEffects();
		Location loc = hittingEntity.getLocation();
		for (PotionEffect effect : effects) {
			PotionEffectType type = effect.getType();
			if (isPositiveFightEffect(type)) {
				for (Player p : loc.getWorld().getPlayers()) {
					if (loc.distance(p.getLocation()) < 18 && loc.distance(p.getLocation()) > 4)
						NegativityPlayer.getCached(p.getUniqueId()).fight();
				}
			} else if (isNegativeFightEffect(type)) {
				for (Player p : loc.getWorld().getPlayers()) {
					if (loc.distance(p.getLocation()) < 9)
						NegativityPlayer.getCached(p.getUniqueId()).fight();
				}
			}
		}
	}
	
	private boolean isNegativeFightEffect(PotionEffectType type) {
		return PotionEffectType.HARM.equals(type) || PotionEffectType.POISON.equals(type)
			|| PotionEffectType.WEAKNESS.equals(type) || type.name().contains("SLOW");
	}
	
	private boolean isPositiveFightEffect(PotionEffectType type) {
		return PotionEffectType.FIRE_RESISTANCE.equals(type) || type.name().contains("HEAL")
			|| PotionEffectType.REGENERATION.equals(type) || PotionEffectType.SPEED.equals(type)
			|| PotionEffectType.INSTANT_DAMAGE.equals(type);
	}*/
	
	@EventListener
	public void onDeath(PlayerDeathEvent e) {
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).unfight();
	}
	
	/*@EventListener
	public void blowUp(EntityExplodeEvent e) {
		if(!e.getEntityType().equals(EntityType.PRIMED_TNT))
			return;
		Location loc = e.getLocation();
		Utils.getOnlinePlayers().stream().filter((p) -> loc.getWorld().equals(p.getWorld()) && loc.distance(p.getLocation()) < 5).forEach((p) -> NegativityPlayer.getCached(p.getUniqueId()).fight());
	}*/
	
	private void manageFightBetweenTwoPlayers(Player p, int maxDistance) {
		if(!p.getWorld().isPVP())
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		for(Player pl : Adapter.getAdapter().getOnlinePlayers()) {
			if(pl == p)
				continue;
			NegativityPlayer npOther = NegativityPlayer.getNegativityPlayer(pl);
			if(npOther.isInFight && np.isInFight)
				continue;
			if(pl.getWorld().equals(p.getWorld())) {
				if(pl.getLocation().distance(p.getLocation()) < maxDistance) {
					np.fight();
					npOther.fight();
				}
			}
		}
	}
}
