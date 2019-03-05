package su.plugin.ability.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class MapListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@EventHandler
	public void onRangeSelect(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(!gp.isRangeSelectMode()) return;
		
		Action action = e.getAction();
		if(action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
			Location location = e.getClickedBlock().getLocation();
			if(action.equals(Action.LEFT_CLICK_BLOCK)) {
				api.getMapManager().setLeftLocation(p, location);
			} else {
				api.getMapManager().setRightLocation(p, location);
			}
			e.setCancelled(true);
			Core.cmsg(p, ChatColor.BLUE, (action.equals(Action.LEFT_CLICK_BLOCK) ? "§b위치 1을" : "위치 2를") + "(X: §f" + location.getX() + "§b, Y: §f" + location.getY() + "§b, Z: §f" + location.getZ() + "§b) 로 설정했습니다.");
		}
	}
	
}