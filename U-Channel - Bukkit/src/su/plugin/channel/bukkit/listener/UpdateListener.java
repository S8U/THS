package su.plugin.channel.bukkit.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import su.plugin.channel.bukkit.KChannelPlugin;
import su.plugin.channel.bukkit.api.KChannelAPI;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.player.FirstPlayerJoinEvent;
import su.plugin.core.bukkit.api.event.player.LastPlayerQuitEvent;

public class UpdateListener implements Listener {
	
	private KChannelAPI api = KChannelPlugin.getApi();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		api.updateThisChannelInfo(true, KCore.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), KCore.getOnlinePlayers());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		List<Player> players = KCore.getOnlinePlayers();
		players.remove(e.getPlayer());
		
		api.updateThisChannelInfo(true, players.size(), Bukkit.getMaxPlayers(), players);
	}
	
	@EventHandler
	public void onFirstPlayerJoin(FirstPlayerJoinEvent e) {
		api.startChannelLoadTask();
	}
	
	@EventHandler
	public void onLastPlayerQuit(LastPlayerQuitEvent e) {
		api.stopChannelLoadTask();
	}
	
}