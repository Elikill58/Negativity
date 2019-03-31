package com.elikill58.negativity.sponge.listeners;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.adapter.Adapter;

import net.minecraft.inventory.ContainerChest;

public class InventoryClickManagerEvent {

    @SuppressWarnings("deprecation")
	@Listener
    public void onClick(ClickInventoryEvent e, @First Player p){
        Optional<ContainerChest> optiM = e.getCause().first(ContainerChest.class);
        if(!optiM.isPresent())
            return;
        ItemType m = e.getTransactions().get(0).getOriginal().getType();
        String invName = e.getTargetInventory().getName().get();
        SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
        if (invName.equals(Inv.NAME_CHECK_MENU)) {
            e.setCancelled(true);
            if (m.equals(ItemTypes.BARRIER)) {
                p.closeInventory();
                return;
            }
            Player cible = Inv.CHECKING.get(p);
            if(m.equals(ItemTypes.ENDER_EYE)){
                p.setLocation(cible.getLocation());
                p.closeInventory();
                Inv.CHECKING.remove(p);
            } else if(m.equals(ItemTypes.SPIDER_EYE)) {
                p.openInventory(cible.getInventory());
                Inv.CHECKING.remove(p);
            } else if(m.equals(ItemTypes.TNT)) {
                Inv.openActivedCheat(p, cible);
            } else if(m.equals(ItemTypes.PACKED_ICE)) {
                p.closeInventory();
                SpongeNegativityPlayer npCible = SpongeNegativityPlayer.getNegativityPlayer(cible);
                npCible.isFreeze = !npCible.isFreeze;
                if (npCible.isFreeze) {
                    if (Adapter.getAdapter().getBooleanInConfig("inventory.main.inv_freeze_active"))
                        Inv.openFreezeMenu(cible);
                    Messages.sendMessage(p, "inventory.main.freeze", "%name%", cible.getName());
                } else
                    Messages.sendMessage(p, "inventory.main.unfreeze", "%name%", cible.getName());
            } else if(m.equals(ItemTypes.ANVIL)){
                Inv.openAlertMenu(p, cible);
            } else if(m.equals(ItemTypes.GRASS)) {
				Inv.openForgeModsMenu(cible);
            }
        } else if (invName.equals(Inv.NAME_ACTIVED_CHEAT_MENU)) {
            e.setCancelled(true);
            if (m.equals(ItemTypes.BARRIER)) {
                p.closeInventory();
            } else if (m.equals(ItemTypes.ARROW))
                Inv.openCheckMenu(p, Inv.CHECKING.get(p));
        } else if (invName.equals(Inv.NAME_FREEZE_MENU))
            e.setCancelled(true);
        else if (invName.equals(Inv.NAME_MOD_MENU)) {
            e.setCancelled(true);
            if (m.equals(ItemTypes.BARRIER)) {
                p.closeInventory();
            } else if (m.equals(ItemTypes.GHAST_TEAR)) {
                if (np.hasPotionEffect(PotionEffectTypes.NIGHT_VISION)) {
                    p.getOrCreate(PotionEffectData.class).get().remove(PotionEffect.builder().potionType(PotionEffectTypes.NIGHT_VISION).amplifier(0).duration(10000).build());
                    Messages.sendMessage(p, "inventory.mod.vision_removed");
                } else {
                    p.getOrCreate(PotionEffectData.class).get().addElement(PotionEffect.builder().potionType(PotionEffectTypes.NIGHT_VISION).amplifier(0).duration(10000).build());
                    Messages.sendMessage(p, "inventory.mod.vision_added");
                }
            } else if (m.equals(ItemTypes.IRON_SHOVEL)) {
                p.closeInventory();
                p.getInventory().clear();
                Messages.sendMessage(p, "inventory.mod.inv_cleared");
            } else if (m.equals(ItemTypes.LEAD)) {
                p.closeInventory();
                p.setLocation(((Player) Utils.getOnlinePlayers().toArray()[Utils.getOnlinePlayers().size() - 1]).getLocation());
            } else if (m.equals(ItemTypes.PUMPKIN_PIE)) {
                p.closeInventory();
                np.isInvisible = !np.isInvisible;
                if (np.isInvisible) {
                    p.offer(Keys.VANISH, true);
                    Messages.sendMessage(p, "inventory.mod.now_invisible");
                } else {
                    p.offer(Keys.VANISH, false);
                    Messages.sendMessage(p, "inventory.mod.no_longer_invisible");
                }
            } else if (m.equals(ItemTypes.TNT)) {
                Inv.openCheatManagerMenu(p);
            } else if (m.equals(ItemTypes.FEATHER)) {
                p.closeInventory();
                boolean b = !p.get(Keys.CAN_FLY).get();
                p.offer(Keys.CAN_FLY, b);
				p.sendMessage(Text.of("Flying: " + Messages.getStringMessage(p, "inventory.manager." + (b ? "enabled" : "disabled"))));
            }
        } else if (invName.equals(Inv.NAME_ALERT_MENU)) {
            e.setCancelled(true);
            if (m.equals(ItemTypes.BARRIER))
                p.closeInventory();
            else if (m.equals(ItemTypes.ARROW))
                Inv.openCheckMenu(p, Inv.CHECKING.get(p));
			else if (m.equals(ItemTypes.BONE))
				for(Cheat c : Cheat.values())
					SpongeNegativityPlayer.getNegativityPlayer(Inv.CHECKING.get(p)).setWarn(c, 0);
        } else if (invName.equals(Inv.NAME_FORGE_MOD_MENU)) {
        	e.setCancelled(true);
        } else if (invName.equals("Cheat manager")) {
            e.setCancelled(true);
            if (m.equals(ItemTypes.BARRIER))
                p.closeInventory();
            else if (m.equals(ItemTypes.ARROW))
                Inv.openModMenu(p);
            else {
                Optional<Cheat> c = Utils.getCheatFromItem(m);
                if (c.isPresent())
                    Inv.openOneCheatMenu(p, c.get());
            }
		} else if(invName.equals(Inv.NAME_FORGE_MOD_MENU)) {
			e.setCancelled(true);
        } else if (Utils.getCheatFromName(invName).isPresent()) {
            e.setCancelled(true);
            if (m.equals(ItemTypes.BARRIER)) {
                p.closeInventory();
                return;
            } else if (m.equals(ItemTypes.ARROW)) {
                Inv.openCheatManagerMenu(p);
                return;
            }
            Cheat c = Utils.getCheatFromName(invName).get();
            if (m.equals(c.getMaterial()))
                return;

            Inventory inv = e.getTargetInventory();
            if (m.equals(ItemTypes.TNT))
                inv.query(new SlotPos(2,0)).set(Utils.createItem(m, Messages.getStringMessage(p, "inventory.manager.setBack", "%back%", Messages.getStringMessage(p, "inventory.manager." + (c.setBack(!c.isSetBack()) ? "enabled" : "disabled")))));
            else if (m.equals(ItemTypes.ENDER_EYE))
                inv.query(new SlotPos(5,0)).set(Utils.createItem(m, Messages.getStringMessage(p, "inventory.manager.autoVerif", "%auto%", Messages.getStringMessage(p, "inventory.manager." + (c.setAutoVerif(!c.isAutoVerif()) ? "enabled" : "disabled")))));
            else if (m.equals(ItemTypes.BLAZE_ROD))
                inv.query(new SlotPos(2,2)).set(Utils.createItem(m, Messages.getStringMessage(p, "inventory.manager.allowKick", "%allow%", Messages.getStringMessage(p, "inventory.manager." + (c.setAllowKick(!c.allowKick()) ? "enabled" : "disabled")))));
            else if (m.equals(ItemTypes.DIAMOND))
                inv.query(new SlotPos(5,2)).set(Utils.createItem(m, Messages.getStringMessage(p, "inventory.manager.setActive", "%active%", Messages.getStringMessage(p, "inventory.manager." + (c.setActive(!c.isActive()) ? "enabled" : "disabled")))));
        }
    }
}
