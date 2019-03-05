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
		
		BungeeUtil.sendMessageToBungeeCord(KChannelPlugin.getInstance(), "U-Channel", "SendToChannel", channel.getName(), playerName);
		return true;
	}
	
	@Override
	public Channel sendToOptimizeChannel(ChannelGroup group, String playerName) {
		BungeeUtil.sendMessageToBungeeCord(KChannelPlugin.getInstance(), "U-Channel", "SendToOptimizeChannel", group.getName(), playerName);
		
		return null;
	}

	@Override
	public void broadCast(Channel channel, String message) {
		BungeeUtil.sendMessageToBungeeCord(KChannelPlugin.getInstance(), "U-Channel", "BroadCastChannel", channel.getName(), message);
	}

	@Override
	public void broadCast(ChannelGroup channelGroup, String message) {
		BungeeUtil.sendMessageToBungeeCord(KChannelPlugin.getInstance(), "U-Channel", "BroadCastChannelGroup", channelGroup.getName(), message);
	}

}