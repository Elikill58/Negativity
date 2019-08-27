package com.elikill58.negativity.sponge.listeners;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.Identifiable;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.timers.ActualizerTimer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class InventoryClickManagerEvent {

	private final PotionEffect NIGHT_VISION_EFFECT = PotionEffect.builder().potionType(PotionEffectTypes.NIGHT_VISION).amplifier(1).duration(10000).build();

	@Listener
	public void onClick(ClickInventoryEvent e, @First Player p) {
		if (e.getTransactions().isEmpty())
			return;

		Identifiable invId = e.getTargetInventory().getProperty(Identifiable.class, Inv.INV_ID_KEY).orElse(null);
		if (invId == null)
			return;

		SlotTransaction transaction = e.getTransactions().get(0);
		ItemStackSnapshot clickedItem = transaction.getOriginal();
		ItemType m = clickedItem.getType();
		String invName = e.getTargetInventory().getName().get();
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (invId.equals(Inv.CHECK_INV_ID)) {
			e.setCancelled(true);
			if (m.equals(ItemTypes.BARRIER)) {
				delayedInvClose(p);
				return;
			}
			Player cible = Inv.CHECKING.get(p);
			if (m.equals(ItemTypes.ENDER_EYE)) {
				p.setLocation(cible.getLocation());
				delayedInvClose(p);
				Inv.CHECKING.remove(p);
			} else if (m.equals(ItemTypes.SPIDER_EYE)) {
				delayed(() -> p.openInventory(cible.getInventory()));
				Inv.CHECKING.remove(p);
			} else if (m.equals(ItemTypes.TNT)) {
				delayed(() -> Inv.openActivedCheat(p, cible));
			} else if (m.equals(ItemTypes.PACKED_ICE)) {
				SpongeNegativityPlayer npCible = SpongeNegativityPlayer.getNegativityPlayer(cible);
				npCible.isFreeze = !npCible.isFreeze;
				if (npCible.isFreeze) {
					if (ActualizerTimer.INV_FREEZE_ACTIVE)
						delayed(() -> Inv.openFreezeMenu(cible));
					Messages.sendMessage(p, "inventory.main.freeze", "%name%", cible.getName());
				} else {
					delayedInvClose(p);
					Messages.sendMessage(p, "inventory.main.unfreeze", "%name%", cible.getName());
				}
			} else if (m.equals(ItemTypes.ANVIL)) {
				delayed(() -> Inv.openAlertMenu(p, cible));
			} else if (m.equals(ItemTypes.GRASS)) {
				delayed(() -> Inv.openForgeModsMenu(cible));
			} else if (m.equals(ItemTypes.SKULL)) {
				delayedInvClose(p);
				SpongeNegativityPlayer.getNegativityPlayer(cible).makeAppearEntities();
			}
		} else if (invId.equals(Inv.ACTIVE_CHEAT_INV_ID)) {
			e.setCancelled(true);
			if (m.equals(ItemTypes.BARRIER)) {
				delayedInvClose(p);
			} else if (m.equals(ItemTypes.ARROW))
				delayed(() -> Inv.openCheckMenu(p, Inv.CHECKING.get(p)));
		} else if (invId.equals(Inv.FREEZE_INV_ID)) {
			e.setCancelled(true);
		} else if (invId.equals(Inv.MOD_INV_ID)) {
			e.setCancelled(true);
			if (m.equals(ItemTypes.BARRIER)) {
				delayedInvClose(p);
			} else if (m.equals(ItemTypes.GHAST_TEAR)) {
				PotionEffectData potionEffects = p.getOrCreate(PotionEffectData.class).orElse(null);
				if (potionEffects != null) {
					if (np.hasPotionEffect(PotionEffectTypes.NIGHT_VISION)) {
						Utils.removePotionEffect(potionEffects, PotionEffectTypes.NIGHT_VISION);
						Messages.sendMessage(p, "inventory.mod.vision_removed");
					} else {
						potionEffects.addElement(NIGHT_VISION_EFFECT);
						Messages.sendMessage(p, "inventory.mod.vision_added");
					}
					p.offer(potionEffects);
				}
			} else if (m.equals(ItemTypes.IRON_SHOVEL)) {
				delayedInvClose(p);
				p.getInventory().clear();
				Messages.sendMessage(p, "inventory.mod.inv_cleared");
			} else if (m.equals(ItemTypes.LEAD)) {
				delayedInvClose(p);
				Player randomPlayer = Utils.getRandomPlayer();
				if (randomPlayer != null) {
					if (randomPlayer.equals(p)) {
						Messages.sendMessage(p, "inventory.mod.random_tp_no_target");
						return;
					}

					boolean tpSuccessful = p.setLocation(randomPlayer.getLocation());
					if (!tpSuccessful) {
						Messages.sendMessage(p, "inventory.mod.random_tp_failed");
					}
				}
			} else if (m.equals(ItemTypes.PUMPKIN_PIE)) {
				delayedInvClose(p);
				InvisibilityData vanishData = p.getOrCreate(InvisibilityData.class).orElse(null);
				if (vanishData != null) {
					Value<Boolean> vanish = vanishData.vanish();
					if (vanish.get()) {
						vanish.set(false);
						Messages.sendMessage(p, "inventory.mod.no_longer_invisible");
					} else {
						vanish.set(true);
						Messages.sendMessage(p, "inventory.mod.now_invisible");
					}

					p.offer(vanish);
				}
			} else if (m.equals(ItemTypes.TNT)) {
				delayed(() -> Inv.openCheatManagerMenu(p));
			} else if (m.equals(ItemTypes.FEATHER)) {
				delayedInvClose(p);
				boolean b = !p.get(Keys.CAN_FLY).get();
				p.offer(Keys.CAN_FLY, b);
				p.sendMessage(Text.of("Flying: " + Messages.getStringMessage(p, "inventory.manager." + (b ? "enabled" : "disabled"))));
			}
		} else if (invId.equals(Inv.ALERT_INV_ID)) {
			e.setCancelled(true);
			if (m.equals(ItemTypes.BARRIER))
				delayedInvClose(p);
			else if (m.equals(ItemTypes.ARROW))
				delayed(() -> Inv.openCheckMenu(p, Inv.CHECKING.get(p)));
			else if (m.equals(ItemTypes.BONE))
				for (Cheat c : Cheat.values())
					SpongeNegativityPlayer.getNegativityPlayer(Inv.CHECKING.get(p)).setWarn(c, 0);
		} else if (invId.equals(Inv.FORGE_MODS_INV_ID)) {
			e.setCancelled(true);
		} else if (invId.equals(Inv.CHEAT_MANAGER_INV_ID)) {
			e.setCancelled(true);
			if (m.equals(ItemTypes.BARRIER))
				delayedInvClose(p);
			else if (m.equals(ItemTypes.ARROW))
				delayed(() -> Inv.openModMenu(p));
			else {
				Optional<Cheat> c = Utils.getCheatFromItem(m);
				c.ifPresent(cheat -> delayed(() -> Inv.openOneCheatMenu(p, cheat)));
			}
		} else {
			Optional<Cheat> cheat = Utils.getCheatFromName(invName);
			if (cheat.isPresent()) {
				if (m.equals(ItemTypes.BARRIER)) {
					e.setCancelled(true);
					delayedInvClose(p);
					return;
				} else if (m.equals(ItemTypes.ARROW)) {
					e.setCancelled(true);
					delayed(() -> Inv.openCheatManagerMenu(p));
					return;
				}

				Cheat c = cheat.get();
				if (m.equals(c.getMaterial())) {
					e.setCancelled(true);
					return;
				}

				if (m.equals(ItemTypes.TNT)) {
					updateItemName(e, c.setBack(!c.isSetBack()), transaction, clickedItem, p, np, "inventory.manager.setBack", "%back%");
				} else if (m.equals(ItemTypes.ENDER_EYE)) {
					updateItemName(e, c.setAutoVerif(!c.isAutoVerif()), transaction, clickedItem, p, np, "inventory.manager.autoVerif", "%auto%");
				} else if (m.equals(ItemTypes.BLAZE_ROD)) {
					updateItemName(e, c.setAllowKick(!c.allowKick()), transaction, clickedItem, p, np, "inventory.manager.allowKick", "%allow%");
				} else if (m.equals(ItemTypes.DIAMOND)) {
					updateItemName(e, c.setActive(!c.isActive()), transaction, clickedItem, p, np, "inventory.manager.setActive", "%active%");
				} else {
					e.setCancelled(true);
				}
			}
		}
	}

	private static void updateItemName(ClickInventoryEvent e, boolean state, SlotTransaction transaction, ItemStackSnapshot clickedItem, Player player, SpongeNegativityPlayer np, String diplaynameKey, String statePlaceholder) {
		ItemStack stack = clickedItem.createStack();
		String stateString = Messages.getStringMessage(player, "inventory.manager." + (state ? "enabled" : "disabled"));
		Text displayName = Messages.getMessage(np.getAccount(), diplaynameKey, statePlaceholder, stateString);
		stack.offer(Keys.DISPLAY_NAME, displayName);
		transaction.setCustom(stack);
		e.getCursorTransaction().setValid(false);
	}

	private static void delayedInvClose(Player player) {
		delayed(player::closeInventory);
	}

	private static void delayed(Runnable action) {
		Task.builder()
				.execute(action)
				.submit(SpongeNegativity.getInstance());
	}
}
