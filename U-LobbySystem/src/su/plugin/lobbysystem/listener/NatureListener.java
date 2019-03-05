package su.plugin.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import su.plugin.lobbysystem.api.LobbySystemAPI;

public class NatureListener implements Listener {
	
	private LobbySystemAPI api = new LobbySystemAPI();
	
	@EventHandler
	public void onBurn(BlockBurnEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e) {
		if(api.getProtectExceptionWorlds().contains(e.getBlock().getWorld().getName())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onIceMelt(BlockFadeEvent e) {
		if(api.getProtectExceptionWorlds().contains(e.getBlock().getWorld().getName())) return;
		e.setCancelled(true);
	}
	
	
}