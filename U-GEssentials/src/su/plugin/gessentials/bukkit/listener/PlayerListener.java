package su.plugin.gessentials.bukkit.listener;

import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.DebugUtil;
import su.plugin.gessentials.bukkit.KGEssentialsPlugin;
import su.plugin.gessentials.bukkit.api.KGEssentialsAPI;

public class PlayerListener implements Listener {
	
	private KGEssentialsAPI api = KGEssentialsPlugin.getApi();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e) {
		DebugUtil.log("MoveSpy:" + PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));
		if(!api.isMoveSpy(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()))) return;
		
		e.setJoinMessage(null);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		if(!api.isMoveSpy(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()))) return;

		api.getMoveSpys().remove(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));

		e.setQuitMessage(null);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent e) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(e.getPlayer().getName());
		
		if(api.isSendChat()) {
			api.getChatManager().sendChat(playerKey, e.getMessage());
		}
		
		for(UPlayer up : Core.getOnlineUPlayers()) {
			if(up.getPlayerKey().equals(playerKey)) continue;
			
			if(Core.getOptionManager().existsPlayerOption(up.getPlayerKey(), "gessentials_chat_ignore")) {
				List<Double> ids = (List<Double>) Core.getOptionManager().getPlayerOption(up.getPlayerKey(), "gessentials_chat_ignore");
				for(Double id : ids) {
					if(id.intValue() == playerKey.getId()) {
						e.getRecipients().remove(up.getPlatformSender());
					}
				}
			}
			if(Core.getOptionManager().existsPlayerOption(up.getPlayerKey(), "gessentials_chat_ignore_all") && ((boolean) Core.getOptionManager().getPlayerOption(up.getPlayerKey(), "gessentials_chat_ignore_all"))) {
				e.getRecipients().remove(up.getPlatformSender());
			}
		}
	}
	
}