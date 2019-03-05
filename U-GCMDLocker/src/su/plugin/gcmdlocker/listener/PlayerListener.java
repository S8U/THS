package su.plugin.gcmdlocker.listener;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gcmdlocker.GCMDLockerPlugin;
import su.plugin.gcmdlocker.api.GCMDLockerAPI;

public class PlayerListener implements Listener {
	
	private GCMDLockerAPI api = GCMDLockerPlugin.getApi();

	@EventHandler
	public void onLogin(LoginEvent e) {
		if(e.isCancelled() || api.isUseGLogin() || !api.isUseKeepLogin()) return;

		ProxyServer.getInstance().getScheduler().schedule(GCMDLockerPlugin.getInstance(), () -> {
			if(!api.isLoggedIp(e.getConnection().getAddress().getHostString())) return;

			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(e.getConnection().getName());
			if(!p.hasPermission("gcmdlocker.admin")) return;

			api.login(p);
		}, 1, TimeUnit.SECONDS);
	}
	
	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		if(!api.isLogged(PlayerKey.getPlayerKeyByPlatformPlayer(p))) return;

		api.logout(p);
	}
	
	@EventHandler
	public void onChat(ChatEvent e) {
		if(!e.isCommand() || !(e.getSender() instanceof ProxiedPlayer)) return;

		ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		if(api.isLogged(PlayerKey.getPlayerKeyByPlatformPlayer(p)) || !api.isBlackListedCommand(e.getMessage())) return;

		Core.msg(p, "§c명령어를 사용하려면 로그인해주세요.");

		e.setCancelled(true);
	}

}