package su.plugin.glogin.bungee.listener;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UPlayerLoginEvent;
import su.plugin.core.common.api.event.c.player.UPlayerQuitEvent;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.glogin.bungee.GGLoginPlugin;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.glogin.bungee.api.event.LoginEvent;
import su.plugin.glogin.bungee.api.event.LogoutEvent;
import su.plugin.glogin.common.api.category.Type;
import su.plugin.glogin.common.api.object.Account;

public class ConnectListener implements Listener, UEventListener {
	
	private GGLoginAPI api = GGLoginPlugin.getApi();
	
	@UEventHandler(priority = -128)
	public void onLogin(UPlayerLoginEvent e) {
		String name = e.getPlayer().getPlayerKey().getName();
		
		if(!name.matches(api.getAllowNicknameRegex())) {
			e.setDisallow(true);
			e.setDisallowReason("사용할 수 없는 닉네임 입니다.");
			return;
		} else if(ProxyServer.getInstance().getPlayer(name) != null) {
			e.setDisallow(true);
			e.setDisallowReason("이미 서버에 접속 중입니다.");
			return;
		}
		
		Account account = api.getSQLManager().getAccount(e.getPlayer().getPlayerKey());
		if(account == null) return;
		
		api.getAccountManager().setAccount(e.getPlayer().getPlayerKey(), account);
	}
	
	@UEventHandler
	public void onQuit(UPlayerQuitEvent e) {
		Account account = api.getAccountManager().getAccount(e.getPlayer().getPlayerKey());
		if(account == null || !account.isLogin()) return;

		account.setLastLogout(System.currentTimeMillis());

		ProxyServer.getInstance().getScheduler().runAsync(GGLoginPlugin.getInstance(), () -> {
			api.getSQLManager().saveAccount(account);
			
			api.getSQLManager().writeLoginLog(e.getPlayer().getPlayerKey(), account.getIp(), Type.LOGOUT);
		});
		
		account.setLogin(false);

		ProxyServer.getInstance().getPluginManager().callEvent(new LogoutEvent((ProxiedPlayer) e.getPlayer().getPlatformSender()));
	}
	
	//
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onConnect(ServerConnectedEvent e) {
		ProxiedPlayer p = e.getPlayer();
		UPlayer up = Core.getUPlayer(p.getName());
		
		Account account = api.getAccountManager().getAccount(up.getPlayerKey());

		if(account != null && account.isLogin()) return;
		else if(api.isForceLoginOnConnect() || (api.isExcludeLoginIfOnlineMode() && up.isOnlineMode())) {
			if(account == null) {
				account = new Account(up.getPlayerKey());
				account.setPassword("");
				account.setRegisterTime(System.currentTimeMillis());

				api.getAccountManager().setAccount(up.getPlayerKey(), account);
			}

			ProxyServer.getInstance().getScheduler().schedule(GGLoginPlugin.getInstance(), () -> {
				Account laccount = api.getAccountManager().getAccount(up.getPlayerKey());

				api.sendLoginToServer(laccount.getPlayerKey(), true);

				laccount.setLogin(true);
				laccount.setName(p.getName());
				laccount.setIp(p.getAddress().getAddress().getHostAddress());
				laccount.setLastLogin(System.currentTimeMillis());
				
				ProxyServer.getInstance().getScheduler().runAsync(GGLoginPlugin.getInstance(), () -> {
					api.getSQLManager().saveAccount(laccount);
					
					api.getSQLManager().writeLoginLog(up.getPlayerKey(), laccount.getIp(), Type.LOGIN);
				});
				
				api.getTitleManager().sendLoginCompleteTitle(p);

				ProxyServer.getInstance().getPluginManager().callEvent(new LoginEvent(p));
				
				Core.log(up.getDisplayName() + (up.hasDisplayName() ? "(" + up.getName() +")" : "") + " 님께서 로그인했습니다. (" + laccount.getIp() + " / " + p.getUniqueId().toString() + ")");
			}, 1, TimeUnit.SECONDS);
			return;
		}
		
		if(account == null || account.getPassword().isEmpty()) {
			api.getTitleManager().sendRegisterTitle(p);
		} else {
			api.getTitleManager().sendLoginTitle(p);
		}
		
		api.getTaskManager().startLoginTimeoutTask(p);
	}
	
}