package su.plugin.channel.bukkit.platform;

import su.plugin.channel.bukkit.KChannelPlugin;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.channel.common.platform.PlatformProvider;
import su.plugin.core.bukkit.api.util.BungeeUtil;

public class KProvider implements PlatformProvider {
	
	@Override
	public boolean sendToChannel(Channel channel, String playerName) {
		if(channel.hasPlayer(playerName)) return false;
		
		BungeeUtil.sendMessageToBungeeCord(KChannelPlugin.getInstance(), "uchannel:main", "SendToChannel", channel.getName(), playerName);
		return true;
	}
	
	@Override
	public Channel sendToOptimizeChannel(ChannelGroup group, String playerName) {
		BungeeUtil.sendMessageToBungeeCord(KChannelPlugin.getInstance(), "uchannel:main", "SendToOptimizeChannel", group.getName(), playerName);
		
		return null;
	}

	@Override
	public void broadCast(Channel channel, String message) {
		BungeeUtil.sendMessageToBungeeCord(KChannelPlugin.getInstance(), "uchannel:main", "BroadCastChannel", channel.getName(), message);
	}

	@Override
	public void broadCast(ChannelGroup channelGroup, String message) {
		BungeeUtil.sendMessageToBungeeCord(KChannelPlugin.getInstance(), "uchannel:main", "BroadCastChannelGroup", channelGroup.getName(), message);
	}

}