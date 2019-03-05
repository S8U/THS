package su.plugin.glogin.bungee.api.task;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.glogin.bungee.GGLoginPlugin;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.glogin.common.api.object.Account;
import su.plugin.core.bungee.api.scheduler.UGRunnable;
import su.plugin.core.common.api.player.PlayerKey;

public class LoginTimeoutTask extends UGRunnable {
	
	private final PlayerKey playerKey;
	
	public LoginTimeoutTask(PlayerKey playerKey) {
		super(GGLoginPlugin.getInstance());
		this.playerKey = playerKey;
	}
	
	public LoginTimeoutTask(ProxiedPlayer player) {
		super(GGLoginPlugin.getInstance());
		playerKey = PlayerKey.getPlayerKey(player.getName());
	}

	@Override
	public void run() {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerKey.getName());
		Account account = GGLoginAPI.getAccountManager().getAccount(playerKey);
		
		if(!(p == null || (account != null && account.isLogin()))) {
			p.disconnect(new TextComponent("로그인 시간을 초과했습니다."));
		}
		
		GGLoginAPI.getTaskManager().stopLoginTimeoutTask(playerKey);
	}
	
}