package su.plugin.channel.bukkit.api.event;

import su.plugin.channel.bukkit.api.KChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.core.bukkit.api.event.UKEvent;

public class KCurrentChannelUpdatedEvent extends UKEvent {
	
	public Channel getCurrentChannel() {
		return KChannelAPI.getChannelManager().getChannel(KChannelAPI.getChannelName());
	}
	
}