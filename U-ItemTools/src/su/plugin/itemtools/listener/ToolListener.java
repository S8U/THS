package su.plugin.itemtools.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.util.ItemUtil;
import su.plugin.itemtools.api.ItemToolsAPI;
import su.plugin.itemtools.api.object.UAnvil;

public class ToolListener implements Listener {
	
	private ItemToolsAPI api = new ItemToolsAPI();
	
	@EventHandler
	public void onUse(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action a = e.getAction();
		
		if(a.equals(Action.LEFT_CLICK_AIR) || a.equals(Action.LEFT_CLICK_BLOCK)) return;
		
		ItemStack item = e.getItem();
		
		if(item == null) return;
		
		else if(ItemUtil.equalsItem(item, api.getEnchantTool())) {
			if(!api.getEnchantPlayers().contains(p.getUniqueId().toString())) {
				api.getEnchantPlayers().add(p.getUniqueId().toString());
			}
			p.openEnchanting(null, true);
		} else if(ItemUtil.equalsItem(item, api.getWorkbenchTool())) {
			if(!api.getWorkbenchPlayers().contains(p.getUniqueId().toString())) {
				api.getWorkbenchPlayers().add(p.getUniqueId().toString());
			}
			p.openWorkbench(null, true);
		} else if(ItemUtil.equalsItem(item, api.getAnvilTool())) {
			UAnvil.openAnvil(p);
		} else return;
		
		if(!a.equals(Action.RIGHT_CLICK_BLOCK)) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		
		InventoryType type = e.getInventory().getType();
		
		if(type.equals(InventoryType.ENCHANTING) && api.getEnchantPlayers().contains(p.getUniqueId().toString())) {
			api.getEnchantPlayers().remove(p.getUniqueId().toString());
		} else if(type.equals(InventoryType.ANVIL) && api.getAnvilPlayers().contains(p.getUniqueId().toString())) {
			api.getAnvilPlayers().remove(p.getUniqueId().toString());
		} else if(type.equals(InventoryType.WORKBENCH) && api.getWorkbenchPlayers().contains(p.getUniqueId().toString())) {
			api.getWorkbenchPlayers().remove(p.getUniqueId().toString());
		}
	}
	
}