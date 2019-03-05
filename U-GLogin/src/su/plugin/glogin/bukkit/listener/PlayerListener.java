package su.plugin.glogin.bukkit.listener;

import su.plugin.glogin.bukkit.KGLoginPlugin;
import su.plugin.glogin.bukkit.api.KGLoginAPI;
import su.plugin.glogin.common.api.object.Account;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UPlayerLoginEvent;
import su.plugin.core.common.api.event.c.player.UPlayerQuitEvent;

public class PlayerListener implements UEventListener {
	
	private KGLoginAPI api = KGLoginPlugin.getApi();
	
	@UEventHandler
	public void onLogin(UPlayerLoginEvent e) {
		Account account = api.getSQLManager().getAccount(e.getPlayer().getPlayerKey());
		if(account == null) return;
		
		api.getAccountManager().setAccount(e.getPlayer().getPlayerKey(), account);
	}
	
	@UEventHandler
	public void onQuit(UPlayerQuitEvent e) {
		api.getAccountManager().removeAccount(e.getPlayer().getPlayerKey());
	}
	
}