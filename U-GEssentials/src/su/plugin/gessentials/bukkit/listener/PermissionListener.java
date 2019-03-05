package su.plugin.gessentials.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import su.plugin.gessentials.bukkit.KGEssentialsPlugin;
import su.plugin.gessentials.bukkit.api.KGEssentialsAPI;
import su.plugin.core.common.api.event.UnregisterableListener;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.event.GroupPrefixChangedEvent;
import su.plugin.permission.api.event.OnlinePlayerGroupChangedEvent;
import su.plugin.permission.api.event.OnlinePlayerPrefixChangedEvent;
import su.plugin.permission.api.object.PermissionPlayer;

public class PermissionListener implements Listener, UnregisterableListener {
	
	private KGEssentialsAPI api = KGEssentialsPlugin.getApi();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(e.getPlayer().getName());
		
		String prefix = PermissionAPI.getPlayerManager().getPermissionPlayer(playerKey).getPrefix();
		if(prefix == null) return;
		
		Bukkit.getScheduler().runTaskLater(KGEssentialsPlugin.getInstance(), () -> api.getChatManager().sendPermissionPrefix(playerKey, prefix), 10);
	}
	
	@EventHandler
	public void onPlayerPrefixChanged(OnlinePlayerPrefixChangedEvent e) {
		if(e.getPrefix() == null) {
			api.getChatManager().sendPermissionPrefix(e.getPlayerKey(), "$null");
		} else {
			api.getChatManager().sendPermissionPrefix(e.getPlayerKey(), e.getPrefix());
		}
	}
	
	@EventHandler
	public void onPlayerGroupChanged(OnlinePlayerGroupChangedEvent e) {
		PermissionPlayer pp = PermissionAPI.getPlayerManager().getPermissionPlayer(e.getPlayerKey());
		if(pp.hasPrefix() && e.getGroup().hasPrefix() && pp.getPrefix().equals(e.getGroup().getPrefix())) return;

		if(e.getGroup().getPrefix() == null) {
			api.getChatManager().sendPermissionPrefix(e.getPlayerKey(), "$null");
		} else {
			api.getChatManager().sendPermissionPrefix(e.getPlayerKey(), e.getGroup().getPrefix());
		}
	}
	
	@EventHandler
	public void onGroupPrefixChanged(GroupPrefixChangedEvent e) {
		for(PermissionPlayer pp : e.getGroup().getOnlinePlayers()) {
			if(e.getGroup().getPrefix() == null) {
				api.getChatManager().sendPermissionPrefix(pp.getPlayerKey(), "$null");
			} else {
				api.getChatManager().sendPermissionPrefix(pp.getPlayerKey(), e.getPrefix());
			}
		}
	}
	
}