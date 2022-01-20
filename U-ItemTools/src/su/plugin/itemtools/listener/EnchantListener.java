package su.plugin.itemtools.listener;

import org.bukkit.DyeColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.material.Dye;
import su.plugin.itemtools.api.ItemToolsAPI;

public class EnchantListener implements Listener {
	
	@EventHandler
	public void onEnchant(PrepareItemEnchantEvent e) {
		if(!ItemToolsAPI.getEnchantPlayers().contains(e.getEnchanter().getUniqueId().toString())) return;

		int[] lvl = e.getExpLevelCostsOffered();
		lvl[0] = 30;
		lvl[1] = 30;
		lvl[2] = 30;
	}
	
	@EventHandler
	public void onOpenEnchant(InventoryOpenEvent e) {
		if(!ItemToolsAPI.getEnchantPlayers().contains(e.getPlayer().getUniqueId().toString())) return;
		if(!(e.getInventory() instanceof EnchantingInventory)) return;
		
		e.getInventory().setItem(1, new Dye(DyeColor.BLUE).toItemStack(64));
	}
	
/*	@EventHandler
	public void onCloseEnchant(InventoryCloseEvent e) {
		if(!(e.getInventory() instanceof EnchantingInventory)) return;
		
		e.getInventory().setItem(1, null);
	}*/
	
	@EventHandler
	public void onClickLapis(InventoryClickEvent e) {
		if(!(e.getInventory() instanceof EnchantingInventory) || e.getRawSlot() != 1) return;
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEnchant(EnchantItemEvent e) {
		e.getInventory().setItem(1, new Dye(DyeColor.BLUE).toItemStack(64));

/*		Player p = e.getEnchanter();
		if(ItemToolsAPI.getEnchantPlayers().contains(p.getUniqueId().toString())) {
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.3f, 1);
		}*/
	}
	
}