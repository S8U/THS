package su.plugin.gparty.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.bukkit.api.object.KPartyPlayer;
import su.plugin.core.common.api.player.PlayerKey;

public class ConnectListener implements Listener {
	
	private KGPartyAPI api = KGPartyPlugin.getApi();
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		KPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		if(pp == null) {
			pp = new KPartyPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
		}

		api.getPlayerManager().setPartyPlayer(p, pp);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		KPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		if(pp == null) return;
		else if(pp.hasParty()) {
			pp.getParty().removePlayer(p);
		}

		api.getPlayerManager().removePartyPlayer(p);
	}

}