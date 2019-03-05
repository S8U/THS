package su.plugin.ability.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class KitListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if(!api.isAdmin(p, false)) return;
		
		Inventory inv = e.getInventory();
		if(!api.getKitManager().existsKit(inv.getName())) return;
		
		api.getKitManager().setKit(inv.getName(), inv);
		api.getConfigManager().saveKit(inv);
		
		Core.cmsg(p, ChatColor.BLUE, inv.getName() + " §b킷을 저장했습니다.");
	}

}