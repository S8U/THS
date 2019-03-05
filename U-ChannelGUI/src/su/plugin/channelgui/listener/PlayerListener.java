package su.plugin.channelgui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import su.plugin.channelgui.ChannelGUIPlugin;
import su.plugin.channelgui.api.ChannelGUIAPI;
import su.plugin.channelgui.api.object.ChannelGUI;

public class PlayerListener implements Listener {
	
	private ChannelGUIAPI api = ChannelGUIPlugin.getApi();
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		for(ChannelGUI gui : api.getGUIManager().getGUIs().values()) {
			gui.removeCooldown(e.getPlayer().getName());
		}
	}
	
}