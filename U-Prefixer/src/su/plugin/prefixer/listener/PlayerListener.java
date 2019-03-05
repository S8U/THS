package su.plugin.prefixer.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionPlayer;
import su.plugin.prefixer.PrefixerPlugin;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.object.PrefixPlayer;

public class PlayerListener implements Listener {
	
	private PrefixerAPI api = PrefixerPlugin.getApi();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		api.getPlayerManager().registerPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		api.getPlayerManager().removePrefixPlayer(PlayerKey.getPlayerKey(e.getPlayer().getName()));
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e) {
		e.setFormat(makeChatFormat(e.getFormat(), api.getPlayerManager().getPrefixPlayer(PlayerKey.getPlayerKey(e.getPlayer().getName()))));
	}
	
	private String makeChatFormat(String format, PrefixPlayer pfp) {
		String prefix = pfp.hasMainPrefix() ? StringUtil.connectString(pfp.getMainPrefixList(), "") : "";
		String prefixerFormat = String.format(format, prefix + "%1$s", "%2$s"); // Prefixer format
		
		if(!api.isUsePermission()) return prefixerFormat;
		
		PermissionPlayer pp = PermissionAPI.getPlayerManager().getPermissionPlayer(pfp.getPlayerKey());
		
		if(pp.getPrefix() == null && pp.getSuffix() == null) return prefixerFormat;
		
		String pPrefix = pp.getPrefix() == null ? "" : pp.getPrefix();
		String pSuffix = pp.getSuffix() == null ? "" : pp.getSuffix();
		
		return String.format(format, prefix + pPrefix + "%1$s" + pSuffix, "%2$s"); // Prefixer + Permission format
	}
	
}