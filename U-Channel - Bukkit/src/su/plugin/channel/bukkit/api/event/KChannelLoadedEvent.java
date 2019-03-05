package su.plugin.channel.bukkit.api.event;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.core.bukkit.api.event.UKEvent;

@Getter
@RequiredArgsConstructor
public class KChannelLoadedEvent extends UKEvent {
	
	private final List<Channel> loadedChannel;
	
}