package su.plugin.itemtools.listener;

import org.bukkit.event.Listener;
import su.plugin.itemtools.api.ItemToolsAPI;

public class SoundListener implements Listener {
	
	private ItemToolsAPI api = new ItemToolsAPI();
	
/*	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		int slot = e.getRawSlot();
		InventoryType type = e.getInventory().getType();
		InventoryAction a = e.getAction();
		
		if(type.equals(InventoryType.ANVIL) && api.getAnvilPlayers().contains(p.getUniqueId().toString())) {
			if(slot == 0 || slot == 1) {
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1.2f);
			} else if(slot == 2 && (a.equals(InventoryAction.PICKUP_ALL) || a.equals(InventoryAction.PICKUP_HALF) || a.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))) {
				p.playSound(p.getLocation(), Sound.ANVIL_USE, 0.5f, 1);
			}
		} else if(type.equals(InventoryType.WORKBENCH) && api.getWorkbenchPlayers().contains(p.getUniqueId().toString())) {
			if(slot == 0) {
				p.playSound(p.getLocation(), Sound.ANVIL_USE, 0.5f, 1);
			} else if(slot > 0 && slot < 10 && (a.equals(InventoryAction.PLACE_ALL) || a.equals(InventoryAction.PLACE_ONE))) {
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1.2f);
			}
		} else if(type.equals(InventoryType.ENCHANTING) && api.getEnchantPlayers().contains(p.getUniqueId().toString())) {
			if((e.getInventory().getItem(0) == null && a.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) || (slot == 0 && (a.equals(InventoryAction.PLACE_ALL) || a.equals(InventoryAction.PLACE_ONE)))) {
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1.2f);
			}
		}
	}*/
	
}