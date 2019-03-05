package su.plugin.gessentials.bungee.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.category.ChatHandlingLocation;
import su.plugin.gessentials.bungee.api.object.EMute;
import su.plugin.gessentials.bungee.api.object.EPlayer;

public class ChatListener implements Listener {
	
	private GGEssentialsAPI api = GGEssentialsPlugin.getApi();
	
	@EventHandler
	public void onChat(ChatEvent e) {
		if(e.isCancelled() || e.isCommand()) return;

		EPlayer ep = api.getEPlayer((ProxiedPlayer) e.getSender());

		if(ep.isAdminChat()) {
			e.setCancelled(true);
			api.getChatManager().sendAdminChat(ep.getUPlayer(), e.getMessage());
			return;
		}

		if(!ep.getProxiedPlayer().hasPermission("gessentials.ignoremute")) {
			if(ep.isMuted()) {
				EMute mute = ep.getMute();

				String reason = mute.getReason() == null ? "" : "(이유: " + mute.getReason() + ") ";
				String timeStr = mute.isTimeMute() ? "(차단 해제 시간: " + StringUtil.buildDateString(mute.getUnMuteTime(), "yyyy년 MM월 dd일 a h시 mm분 ss초") + ") " : "";
				String adminName = mute.getAdminId() == -2 ? api.getWarningManager().getWarningDisplayName() : (mute.getAdminId() == -1 ? "콘솔" : PlayerKey.getPlayerKey(mute.getAdminId()).getDisplayName());

				e.setCancelled(true);

				Core.wmsg(ep.getProxiedPlayer(), "채팅 금지 상태입니다. " + reason + timeStr + "[처리자: " + adminName + "]");
				return;
			} else if(api.getChatManager().isMutedAll()) {
				e.setCancelled(true);

				Core.wmsg(ep.getProxiedPlayer(), "채팅 금지 상태입니다.");
				return;
			}
		}
		
		if(ep.getEChannel().getChatHandlingLocation() == ChatHandlingLocation.BUKKIT) return;
		
		e.setMessage(api.getChatManager().sendGlobalChat(ep, e.getMessage()));
	}
	
}