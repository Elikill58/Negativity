package com.elikill58.negativity.sponge.inventories;

import static com.elikill58.negativity.sponge.utils.ItemUtils.createItem;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.inventories.holders.ModHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.sponge.utils.ItemUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.permissions.Perm;

public class ModInventory extends AbstractInventory {

	private final PotionEffect NIGHT_VISION_EFFECT = PotionEffect.builder().potionType(PotionEffectTypes.NIGHT_VISION).amplifier(1).duration(10000).build();
	
	public ModInventory() {
		super(InventoryType.MOD);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ModHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Inventory inv = Inventory.builder().withCarrier(new ModHolder())
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(Inv.NAME_MOD_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.property(Inv.INV_ID_KEY, Inv.MOD_INV_ID).build(SpongeNegativity.getInstance());
		Utils.fillInventoryWith(Inv.EMPTY, inv);


		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(1, 1, createItem(ItemTypes.GHAST_TEAR, Messages.getStringMessage(p, "inventory.mod.night_vision")));
		invGrid.set(2, 1, createItem(ItemTypes.PUMPKIN_PIE, Messages.getStringMessage(p, "inventory.mod.invisible")));
		invGrid.set(3, 1, createItem(ItemTypes.FEATHER, "&rFly: " + Messages.getStringMessage(p,
				"inventory.manager." + (p.get(Keys.CAN_FLY).get() ? "enabled" : "disabled"))));
		if (Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), Perm.MANAGE_CHEAT))
			invGrid.set(4, 1, createItem(ItemTypes.TNT, Messages.getStringMessage(p, "inventory.mod.cheat_manage")));
		invGrid.set(6, 1, createItem(ItemTypes.LEAD, Messages.getStringMessage(p, "inventory.mod.random_tp")));
		invGrid.set(7, 1, ItemUtils.hideAttributes(createItem(ItemTypes.IRON_SHOVEL, Messages.getStringMessage(p, "inventory.mod.clear_inv"))));
		invGrid.set(8, 2, createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, NegativityHolder nh) {
		if (m.equals(ItemTypes.GHAST_TEAR)) {
			delayedInvClose(p);
			PotionEffectData potionEffects = p.getOrCreate(PotionEffectData.class).orElse(null);
			if (potionEffects != null) {
				if (SpongeNegativityPlayer.getNegativityPlayer(p).hasPotionEffect(PotionEffectTypes.NIGHT_VISION)) {
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
				p.setLocation(randomPlayer.getLocation());
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
			delayed(() -> AbstractInventory.open(InventoryType.CHEAT_MANAGER, p, false));
		} else if (m.equals(ItemTypes.FEATHER)) {
			delayedInvClose(p);
			boolean b = !p.get(Keys.CAN_FLY).get();
			p.offer(Keys.CAN_FLY, b);
			p.sendMessage(Text.of("Flying: " + Messages.getStringMessage(p, "inventory.manager." + (b ? "enabled" : "disabled"))));
		}
	}
}
