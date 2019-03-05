package su.plugin.gfriend.listener;

import java.util.List;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.gfriend.GFriendPlugin;
import su.plugin.gfriend.api.GFriendAPI;
import su.plugin.gfriend.api.object.FriendPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UPlayerJoinEvent;
import su.plugin.core.common.api.event.c.player.UPlayerQuitEvent;

public class PlayerListener implements UEventListener {
	
	private GFriendAPI api = GFriendPlugin.getApi();
	
	@UEventHandler
	public void onPlayerJoin(UPlayerJoinEvent e) {
		FriendPlayer fp = new FriendPlayer(e.getPlayer().getPlayerKey());
		api.getSQLManager().loadPlayer(fp);
		api.getPlayerManager().setFriendPlayer(e.getPlayer().getPlayerKey(), fp);
		
		if(api.isUseGLogin()) return;
		
		List<ProxiedPlayer> friends = fp.getOnlineFriends();
		
		int requestCount = fp.getRequests().size();
		
		for(ProxiedPlayer friend : friends) {
			Core.msg(friend, "§f" + fp.getDisplayName() + " §a님께서 서버에 접속했습니다.");
		}
		
		BungeeCord.getInstance().getScheduler().schedule(GFriendPlugin.getInstance(), () -> {
			if(friends.size() > 0) {
				Core.msg(e.getPlayer(), friends.size() + "명의 친구가 서버에 접속 중입니다.");
			}
			
			if(requestCount > 0) {
				Core.msg(e.getPlayer(),
						new ComponentBuilder("§f" + requestCount + "§a개의 친구 요청이 있습니다.")
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f클릭 시 요청 목록을 확인합니다.").create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/친구 요청목록")).create());
			}
		}, 500, TimeUnit.MILLISECONDS);
		
	}
	
	@UEventHandler
	public void onPlayerQuit(UPlayerQuitEvent e) {
		FriendPlayer fp = api.getPlayerManager().getFriendPlayer(e.getPlayer().getPlayerKey());
		
		if(!api.isUseGLogin()) {
			for(ProxiedPlayer friend : fp.getOnlineFriends()) {
				Core.msg(friend, "§f", fp.getDisplayName() + " §a님께서 서버에서 퇴장했습니다.");
			}
		}
		
		api.getPlayerManager().removeFriendPlayer(e.getPlayer().getPlayerKey());
	}
	
}