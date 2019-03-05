package su.plugin.glogin.bungee.api.manager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.glogin.bungee.api.GGLoginAPI;

public class TitleManager {
	
	public void sendRegisterTitle(ProxiedPlayer p) {
		Title title = ProxyServer.getInstance().createTitle();
		title.title(new TextComponent("[ ! ]"));
		title.subTitle(new TextComponent("'/회원가입 <비밀번호> <비밀번호>' 명령어를 사용하세요."));
		title.stay(GGLoginAPI.getLoginTimeout() * 20);
		title.send(p);
	}
	
	public void sendLoginTitle(ProxiedPlayer p) {
		Title title = ProxyServer.getInstance().createTitle();
		title.title(new TextComponent("[ ! ]"));
		title.subTitle(new TextComponent("'/로그인 <비밀번호>' 명령어를 사용하세요."));
		title.stay(GGLoginAPI.getLoginTimeout() * 20);
		title.send(p);
	}
	
	public void sendLoginCompleteTitle(ProxiedPlayer p) {
		Title title = ProxyServer.getInstance().createTitle();
		title.title(new TextComponent("환영합니다."));
		title.subTitle(new TextComponent(""));
		title.stay(20);
		title.send(p);
	}
	
}