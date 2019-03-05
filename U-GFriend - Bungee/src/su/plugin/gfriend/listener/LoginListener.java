package su.plugin.gfriend.listener;

import java.util.List;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.gfriend.GFriendPlugin;
import su.plugin.gfriend.api.GFriendAPI;
import su.plugin.gfriend.api.object.FriendPlayer;
import su.plugin.glogin.bungee.api.event.LoginEvent;
import su.plugin.glogin.bungee.api.event.LogoutEvent;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.UnregisterableListener;
import su.plugin.core.common.api.player.PlayerKey;

public class LoginListener implements Listener, UnregisterableListener {
	
	private GFriendAPI api = GFriendPlugin.getApi();
	
	@EventHandler
	public void onLogin(LoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		FriendPlayer fp = api.getPlayerManager().getFriendPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
		
		List<ProxiedPlayer> friends = fp.getOnlineFriends();
		
		int requestCount = fp.getRequests().size();
		
		for(ProxiedPlayer friend : friends) {
			Core.msg(friend, "§a" + fp.getDisplayName() + " §a님께서 서버에 로그인했습니다.");
		}
		
		BungeeCord.getInstance().getScheduler().schedule(GFriendPlugin.getInstance(), () -> {
			
			if(friends.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for(ProxiedPlayer of : fp.getOnlineFriends()) {
					PlayerKey ofpk = PlayerKey.getPlayerKeyByPlatformPlayer(of);
					sb.append(sb.length() < 1 ? ofpk.getDisplayName() : "§f, " + ofpk.getDisplayName());
				}

				Core.msg(e.getPlayer(),
						new ComponentBuilder("§f" + friends.size() + "§a명의 친구가 서버를 플레이 중입니다.")
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f" + sb.toString() + "\n§f클릭 시 친구 목록을 확인합니다.").create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/친구 목록")).create());
			}
			
			if(requestCount > 0) {
				Core.msg(e.getPlayer(),
						new ComponentBuilder("§f" + requestCount + "§a개의 친구 요청이 있습니다.")
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f클릭 시 요청 목록을 확인합니다.").create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/친구 요청목록")).create());
			}
			
		}, 100, TimeUnit.MILLISECONDS);
	}
	
	@EventHandler
	public void onLogout(LogoutEvent e) {
		FriendPlayer fp = api.getPlayerManager().getFriendPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));
		
		for(ProxiedPlayer friend : fp.getOnlineFriends()) {
			Core.msg(friend, "§f" + fp.getDisplayName() + " §a님께서 서버에서 로그아웃했습니다.");
		}
	}
	
}