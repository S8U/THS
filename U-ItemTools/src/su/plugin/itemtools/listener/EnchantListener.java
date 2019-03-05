package su.plugin.itemtools.listener;

import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.material.Dye;

import su.plugin.itemtools.api.ItemToolsAPI;

public class EnchantListener implements Listener {
	
	@EventHandler
	public void onEnchant(PrepareItemEnchantEvent e) {
		for(HumanEntity h : e.getViewers()) {
			//if(ItemToolsAPI.getEnchantPlayers().contains(h.getUniqueId().toString())) {
				for(EnchantmentOffer offer : e.getOffers()) {
					if(offer == null) continue;
					offer.setCost(30);
				}
				break;
			//}
		}
	}
	
	@EventHandler
	public void onOpenEnchant(InventoryOpenEvent e) {
		if(!(e.getInventory() instanceof EnchantingInventory)) return;
		
		e.getInventory().setItem(1, new Dye(DyeColor.BLUE).toItemStack(64));
	}
	
	@EventHandler
	public void onCloseEnchant(InventoryCloseEvent e) {
		if(!(e.getInventory() instanceof EnchantingInventory)) return;
		
		e.getInventory().setItem(1, null);
	}
	
	@EventHandler
	public void onClickLapis(InventoryClickEvent e) {
		if(!(e.getInventory() instanceof EnchantingInventory) || e.getRawSlot() != 1) return;
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEnchant(EnchantItemEvent e) {
		e.getInventory().setItem(1, new Dye(DyeColor.BLUE).toItemStack(64));
		
		Player p = e.getEnchanter();
		if(ItemToolsAPI.getEnchantPlayers().contains(p.getUniqueId().toString())) {
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3f, 1);
		}
	}
	
}