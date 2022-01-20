package su.plugin.channel.common.api;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import su.plugin.channel.common.api.manager.ChannelGroupManager;
import su.plugin.channel.common.api.manager.ChannelManager;
import su.plugin.channel.common.api.manager.SQLManager;
import su.plugin.channel.common.api.manager.ScriptManager;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.platform.PlatformProvider;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;

public class ChannelAPI {

	@Setter
	@Getter
	protected static String broadCastPrefix;

	@Getter
	protected static ChannelManager channelManager = new ChannelManager();
	@Getter
	protected static ChannelGroupManager channelGroupManager = new ChannelGroupManager();
	@Getter
	protected static ScriptManager scriptManager = new ScriptManager();
	@Getter
	protected static SQLManager SQLManager;
	
	@Getter
	protected static PlatformProvider platformProvider;
	
	public static boolean existsPlayer(String playerName) {
		for(Channel channel : channelManager.getChannels().values()) {
			if(channel.hasPlayer(playerName)) return true;
		}
		
		 return false;
	}

	public static List<String> getAllPlayers() {
		List<String> list = new NotDuplicatedArrayList<>();

		for(Channel channel : channelManager.getChannels().values()) {
			if (!channel.isOnline()) continue;

			for(String player : channel.getPlayerList()) {
				list.add(player);
			}
		}

		return list;
	}

	public static int getAllPlayerCount() {
		return getAllPlayers().size();
	}
	
}