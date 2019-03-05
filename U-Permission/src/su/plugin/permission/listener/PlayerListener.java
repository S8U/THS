package su.plugin.permission.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import su.plugin.core.bukkit.api.event.player.FirstPlayerJoinEvent;
import su.plugin.core.bukkit.api.event.player.LastPlayerQuitEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.PermissionPlugin;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionPlayer;

public class PlayerListener implements Listener {
	
	private PermissionAPI api = PermissionPlugin.getApi();
	
	@EventHandler(priority=EventPriority.LOW)
	public void onJoin(PlayerJoinEvent e) {
		api.getPlayerManager().registerPlayer(e.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent e) {
		api.getPlayerManager().unRegisterPlayer(PlayerKey.getPlayerKey(e.getPlayer().getName()));
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onFirstPlayerJoin(FirstPlayerJoinEvent e) {
		if(!api.isUseBungeecord()) return;

		api.getSQLManager().loadConfig();
		api.getSQLManager().loadAllGroup();
	}
	
	@EventHandler
	public void onLastPlayerQuit(LastPlayerQuitEvent e) {
		if(!api.isUseBungeecord()) return;
		
		api.getGroupManager().getPermissionGroups().clear();
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e) {
		if(api.isUsePrefixer()) return;
		
		PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		
		if(pp.getPrefix() == null && pp.getSuffix() == null) return;
		
		String prefix = pp.getPrefix() == null ? "" : pp.getPrefix();
		String suffix = pp.getSuffix() == null ? "" : pp.getSuffix();
		
		String newFormat = String.format(e.getFormat(), prefix + "%1$s" + suffix, "%2$s");
		
		e.setFormat(newFormat);
	}
	
}