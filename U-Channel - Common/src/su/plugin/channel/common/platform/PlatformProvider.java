package su.plugin.channel.common.platform;

import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;

public interface PlatformProvider {
	
	boolean sendToChannel(Channel channel, String playerName);
	
	Channel sendToOptimizeChannel(ChannelGroup group, String playerName);

	void broadCast(Channel channel, String message);

	void broadCast(ChannelGroup channelGroup, String message);

}