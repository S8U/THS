package su.plugin.glogin.bungee.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.glogin.bungee.GGLoginPlugin;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.glogin.common.api.object.Account;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class ControlListener implements Listener {
	
	private GGLoginAPI api = GGLoginPlugin.getApi();
	
	@EventHandler
	public void onChat(ChatEvent e) {
		if(e.isCommand() && api.getExceptionCommands().contains(e.getMessage().toLowerCase().substring(1).split(" ")[0])) return;
		
		ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(p.getName()));
		if(account != null && account.isLogin()) return;
		else if(account == null) {
			Core.msg(p, "§c회원가입하시려면 '/회원가입 <비밀번호> <비밀번호>' 명령어를 사용하세요.");
		} else if(!account.isLogin()) {
			Core.msg(p, "§c로그인하시려면 '/로그인 <비밀번호>' 명령어를 사용하세요.");
		}
		
		e.setCancelled(true);
	}
	
}