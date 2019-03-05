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
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.event.MainPrefixChangeEvent;

public class PrefixerListener implements Listener, UnregisterableListener {
	
	private KGEssentialsAPI api = KGEssentialsPlugin.getApi();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(e.getPlayer().getName());
		
		Bukkit.getScheduler().runTaskLater(KGEssentialsPlugin.getInstance(), () -> {
			PrefixerAPI.getMainPrefix(playerKey).forEach((priority, prefix) -> api.getChatManager().sendPrefixerPrefix(playerKey, priority, prefix));
		}, 10);
	}
	
	@EventHandler
	public void onMainPrefixChange(MainPrefixChangeEvent e) {
		if(e.getPrefix() == null) {
			api.getChatManager().sendPrefixerPrefix(e.getPrefixPlayer().getPlayerKey(), e.getPriority(), "$null");
		} else {
			api.getChatManager().sendPrefixerPrefix(e.getPrefixPlayer().getPlayerKey(), e.getPriority(), e.getPrefix());
		}
	}
	
}