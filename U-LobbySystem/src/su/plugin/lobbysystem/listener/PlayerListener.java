package su.plugin.lobbysystem.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import su.plugin.core.common.api.Core;
import su.plugin.lobbysystem.LobbySystemPlugin;
import su.plugin.lobbysystem.api.LobbySystemAPI;

public class PlayerListener implements Listener {
	
	private LobbySystemAPI api = LobbySystemPlugin.getApi();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		if(api.getJoinSpeed() > 1) {
			api.setSpeed(p, api.getJoinSpeed());
			Core.msg(p, "속도가 " + api.getJoinSpeed() + "로 변경되었습니다.");
		}
		
		if(api.isInvincibilityOnJoin() && !api.isInvincibility(p)) {
			api.setInvincibility(p, true);
			Core.msg(p, "무적 모드가 활성화되었습니다.");
		}

		if(api.isUseSideBar()) {
			p.setScoreboard(LobbySystemAPI.makeScoreBoard(p));
		}

		e.setJoinMessage(api.getJoinMessage().equals("null") ? null : api.getJoinMessage());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(api.getQuitMessage().equals("null") ? null : api.getJoinMessage());
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();
		if(!api.isInvincibilityWorld(p.getWorld().getName()) || !api.isInvincibility(p)) return;
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(p.isOp() && api.canCraft(p)) return;
		
		e.setCancelled(true);
	}

	@EventHandler
	public void onUsePortal(PlayerPortalEvent e) {
		if(api.isUsePortal()) return;
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFall(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(p.getLocation().getY() < -10) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + p.getName());
			api.setSpeed(p, api.getJoinSpeed());
		}
	}
	
}
