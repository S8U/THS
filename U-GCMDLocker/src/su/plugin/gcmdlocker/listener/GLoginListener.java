package su.plugin.gcmdlocker.listener;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.gcmdlocker.GCMDLockerPlugin;
import su.plugin.gcmdlocker.api.GCMDLockerAPI;
import su.plugin.glogin.bungee.api.event.LoginEvent;

public class GLoginListener implements Listener {
	
	private GCMDLockerAPI api = GCMDLockerPlugin.getApi();

	@EventHandler
	public void onLogin(LoginEvent e) {
		if(!api.isUseKeepLogin() || !api.isLoggedIp(e.getPlayer().getAddress().getHostString())) return;

		api.login(e.getPlayer());
	}
	
}