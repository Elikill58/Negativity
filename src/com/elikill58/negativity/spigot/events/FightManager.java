package com.elikill58.negativity.spigot.events;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.Potion;

import com.elikill58.negativity.spigot.FakePlayer;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

@SuppressWarnings("deprecation")
public class FightManager implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player))
			return;
		Player damager = (Player) e.getDamager();
		Player hit = (Player) e.getEntity();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(damager);
		FakePlayer willRemoved = null;
		for(FakePlayer tempFp : np.FAKE_PLAYER)
			if(tempFp.getId().equals(hit.getUniqueId()))
				willRemoved = tempFp;
		
		if(willRemoved != null) {
			np.FAKE_PLAYER.remove(willRemoved);
			np.fakePlayerTouched++;
			long diff = System.currentTimeMillis() - np.timeStartFakePlayer;
			double diffSec = diff / 1000;
			if(np.fakePlayerTouched >= 20 && np.fakePlayerTouched >= diffSec) {
				SpigotNegativity.alertMod(ReportType.VIOLATION, damager, Cheat.fromString("FORCEFIELD").get(), Utils.parseInPorcent(np.fakePlayerTouched * 10 * (1 / diffSec)), np.fakePlayerTouched + " touched in " + diffSec + " seconde(s)",  np.fakePlayerTouched + " hit in " + (int) (diffSec) + " seconde(s)");
			} else if(np.fakePlayerTouched >= 5 && np.fakePlayerTouched >= diffSec) {
				SpigotNegativity.alertMod(ReportType.WARNING, damager, Cheat.fromString("FORCEFIELD").get(), Utils.parseInPorcent(np.fakePlayerTouched * 10 * (1 / diffSec)), np.fakePlayerTouched + " touched in " + diffSec + " seconde(s)",  np.fakePlayerTouched + " hit in " + (int) (diffSec) + " seconde(s)");
			}
		}
		
		np.fight();
		SpigotNegativityPlayer.getNegativityPlayer(hit).fight();
	}

	@EventHandler
	public void onPlayerEmptyBucket(PlayerBucketEmptyEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 8);
	}

	@EventHandler
	public void onPlayerFillBucket(PlayerBucketFillEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 8);
	}

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 10);
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		switch(p.getItemInHand().getType().name()) {
		case "DIAMOND_SWORD":
		case "IRON_SWORD":
		case "STONE_SWORD":
		case "GOLDEN_SWORD":
		case "GOLD_SWORD":
		case "WOODEN_SWORD":
		case "WOOD_SWORD":
		case "DIAMOND_AXE":
		case "IRON_AXE":
		case "STONE_AXE":
		case "GOLDEN_AXE":
		case "GOLD_AXE":
		case "WOODEN_AXE":
		case "WOOD_AXE":
		case "BOW":
		case "POTION":
		case "GOLDEN_APPLE":
			manageFightBetweenTwoPlayers(p, 20);
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if(!e.getEntity().getType().equals(EntityType.SPLASH_POTION) || !(e.getEntity() instanceof Potion))
			return;
		Location loc = e.getEntity().getLocation();
		switch(((Potion) e.getEntity()).getType()) {
		case NIGHT_VISION:
		case INVISIBILITY:
		case WATER:
		case WATER_BREATHING:
			return;
		case FIRE_RESISTANCE:
		case INSTANT_HEAL:
		case REGEN:
		case SPEED:
		case STRENGTH:
			for(Player p : Utils.getOnlinePlayers())
				if(loc.getWorld().equals(p.getLocation().getWorld()))
					if(loc.distance(p.getLocation()) < 18 && loc.distance(p.getLocation()) > 4)
						SpigotNegativityPlayer.getNegativityPlayer(p).fight();
			break;
		case INSTANT_DAMAGE:
		case POISON:
		case WEAKNESS:
		case SLOWNESS:
			for(Player p : Utils.getOnlinePlayers())
				if(loc.getWorld().equals(p.getLocation().getWorld()))
					if(loc.distance(p.getLocation()) < 9)
						SpigotNegativityPlayer.getNegativityPlayer(p).fight();
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		SpigotNegativityPlayer.getNegativityPlayer(e.getEntity()).unfight();
	}
	
	private void manageFightBetweenTwoPlayers(Player p, int maxDistance) {
		if(!p.getWorld().getPVP())
			return;
		for(Player pl : Utils.getOnlinePlayers())
			if(pl.getLocation().getWorld().equals(p.getLocation().getWorld()))
				if(pl.getLocation().distance(p.getLocation()) < 8) {
					SpigotNegativityPlayer.getNegativityPlayer(p).fight();
					SpigotNegativityPlayer.getNegativityPlayer(pl).fight();
				}
		
	}
}
