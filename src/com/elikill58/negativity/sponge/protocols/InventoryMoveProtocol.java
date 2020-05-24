package com.elikill58.negativity.sponge.protocols;

import java.util.concurrent.TimeUnit;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;

public class InventoryMoveProtocol extends Cheat {

	public InventoryMoveProtocol() {
		super(CheatKeys.INVENTORY_MOVE, false, ItemTypes.NETHER_STAR, CheatCategory.MOVEMENT, true, "invmove");
	}
	
	@Listener
	@Exclude(ClickInventoryEvent.Double.class)
	public void onInvClick(ClickInventoryEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove(p, true, "Click");
	}
	
	@Listener
	public void onInvOpen(InteractInventoryEvent.Open e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove(p, false, "Open");
	}

	private void checkInvMove(Player p, boolean check, String from) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}
		if (p.get(Keys.IS_SPRINTING).orElse(false) || p.get(Keys.IS_SNEAKING).orElse(false)) {
			Task.builder().delay(150, TimeUnit.MILLISECONDS).execute(() -> {
				if(p.get(Keys.IS_SPRINTING).orElse(false) || p.get(Keys.IS_SNEAKING).orElse(false))
					SpongeNegativity.alertMod(ReportType.WARNING, p, this,
							SpongeNegativityPlayer.getNegativityPlayer(p).getAllWarn(this) > 5 ? 100 : 95,
								"Detected when " + from + ". Sprint: " + p.get(Keys.IS_SPRINTING).orElse(false) + ", Sneak:" +
							p.get(Keys.IS_SNEAKING).orElse(false), getHover("main", "%name%", from));
			});
		} else if (check) {
			final Location<World> lastLoc = p.getLocation().copy();
			Task.builder().delay(250, TimeUnit.MILLISECONDS).execute(() -> {
				double dis = lastLoc.getPosition().distance(p.getLocation().getPosition());
				if (dis > 1 && (lastLoc.getY() - p.getLocation().getY()) < 0.1
						&& p.getOpenInventory() != null) {
					SpongeNegativity.alertMod(ReportType.WARNING, p, this,
							SpongeNegativityPlayer.getNegativityPlayer(p).getAllWarn(this) > 5 ? 100 : 95,
								"Detected when " + from + ", Distance: " + dis + " Diff Y: " + (lastLoc.getY() - p.getLocation().getY()), getHover("main", "%name%", from));
				}
			});
		}
	}
}
