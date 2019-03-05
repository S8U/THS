package su.plugin.channel.bungee.api.event;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Event;
import su.plugin.channel.common.api.object.Channel;

@Getter
@RequiredArgsConstructor
public class GChannelLoadedEvent extends Event {
	
	private final List<Channel> loadedChannel;
	
}