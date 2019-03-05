package su.plugin.antirecipe.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import su.plugin.antirecipe.AntiRecipePlugin;
import su.plugin.antirecipe.api.AntiRecipeAPI;
import su.plugin.core.common.api.Core;

public class PlayerListener implements Listener {
	
	private AntiRecipeAPI api = AntiRecipePlugin.getApi();
	
	@EventHandler
	public void onMake(CraftItemEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if(!api.isBannedItem(e.getCurrentItem())) return;
		
		if(p.isOp() || p.hasPermission("antirecipe.bypass")) {
			Core.msg(p, "관리자 권한으로 조합하였습니다.");
			return;
		}
		
		Core.wmsg(p, "조합이 금지 된 아이템입니다.");
		e.setCancelled(true);
	}

}
