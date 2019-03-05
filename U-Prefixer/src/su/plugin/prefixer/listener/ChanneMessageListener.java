package su.plugin.prefixer.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import su.plugin.core.bukkit.api.event.ChannelMessageEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.prefixer.PrefixerPlugin;
import su.plugin.prefixer.api.PrefixerAPI;

public class ChanneMessageListener implements Listener {
	
	private PrefixerAPI api = PrefixerPlugin.getApi();
	
	@EventHandler
	public void onChannelMessage(ChannelMessageEvent e) {
		if(!e.getKey().equals("U-Prefixer")) return;
		
		String task = e.getTask();
		
		if(task.equals("PrefixUpdate")) {
			int playerId = e.getByteArrayDataInput().readInt();
			
			PlayerKey playerKey = PlayerKey.getPlayerKey(playerId);
			if(playerKey == null) return;
			
			Bukkit.getScheduler().runTaskAsynchronously(PrefixerPlugin.getInstance(), () -> api.getPlayerManager().setPrefixPlayer(playerKey, api.getSQLManager().getPrefixPlayer(playerKey)));
		}
	}
	
	
}