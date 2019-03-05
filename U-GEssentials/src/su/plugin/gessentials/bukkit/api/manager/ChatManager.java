package su.plugin.gessentials.bukkit.api.manager;

import su.plugin.gessentials.bukkit.KGEssentialsPlugin;
import su.plugin.core.bukkit.api.util.BungeeUtil;
import su.plugin.core.common.api.player.PlayerKey;

public class ChatManager {
	
	public void sendPrefixerPrefix(PlayerKey playerKey, int priority, String prefix) {
		BungeeUtil.sendMessageToBungeeCord(KGEssentialsPlugin.getInstance(), "U-GEssentials", "SetPrefixerPrefix", playerKey.getId(), priority, prefix);
	}
	
	public void sendPermissionPrefix(PlayerKey playerKey, String prefix) {
		BungeeUtil.sendMessageToBungeeCord(KGEssentialsPlugin.getInstance(), "U-GEssentials", "SetPermissionPrefix", playerKey.getId(), prefix);
	}
	
	public void sendChat(PlayerKey playerKey, String chat) {
		BungeeUtil.sendMessageToBungeeCord(KGEssentialsPlugin.getInstance(), "U-GEssentials", "Chat", playerKey.getId(), chat);
	}
	
}