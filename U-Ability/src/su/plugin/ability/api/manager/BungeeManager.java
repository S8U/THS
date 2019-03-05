package su.plugin.ability.api.manager;

import java.util.List;
import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.core.bukkit.api.util.BungeeUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class BungeeManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	public void sendToLobby(Player p) {
		String lobby = api.getBungeeLobby();

		if(api.isUseChannel()) {
			if(lobby.toLowerCase().startsWith("<channel:")) {
				List<String> arr = StringUtil.getValue("channel", lobby);
				Channel channel = null;

				if(arr.size() < 1 || (channel = ChannelAPI.getChannelManager().getChannel(arr.get(0))) == null) {
					Core.wlog("채널이 존재하지 않아 로비로 이동시킬 수 없습니다.");
					return;
				}

				channel.sendToChannel(p.getName());
				return;
			} else if(lobby.toLowerCase().startsWith("<channelgroup:")) {
				List<String> arr = StringUtil.getValue("channelgroup", lobby);
				ChannelGroup group = null;

				if(arr.size() < 1 || (group = ChannelAPI.getChannelGroupManager().getChannelGroup(arr.get(0))) == null) {
					Core.wlog("채널 그룹이 존재하지 않아 로비로 이동시킬 수 없습니다.");
					return;
				}

				group.sendToOptimizeChannel(p.getName());
				return;
			}
		}

		BungeeUtil.sendPlayer(p.getName(), lobby);
	}
	
	public void sendMessage(String to, String msg) {
		if(msg.contains("\n")) {
			String[] s = msg.split("\n");
			for(int i = 0; i < s.length; i++) {
				sendMessage(to, s[i]);
			}
		}

		BungeeUtil.sendMessage(to, msg);
	}

}
