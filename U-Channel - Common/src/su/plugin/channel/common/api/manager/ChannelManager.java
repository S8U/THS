package su.plugin.channel.common.api.manager;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import su.plugin.channel.common.api.object.Channel;

public class ChannelManager {
	
	@Setter
	@Getter
	private HashMap<String, Channel> channels = new HashMap<>();
	
	public void setChannel(String name, Channel channel) {
		channels.put(name.toLowerCase(), channel);
	}
	
	public Channel getChannel(String name) {
		return channels.get(name.toLowerCase());
	}
	
	public boolean existsChannel(String name) {
		return channels.containsKey(name.toLowerCase());
	}
	
	public void removeChannel(String name) {
		channels.remove(name.toLowerCase());
	}
	
	public Channel getChannelByDisplayName(String displayName) {
		for(Channel channel : channels.values()) {
			if(channel.getName().equalsIgnoreCase(displayName) || (channel.getDisplayName() != null && channel.getDisplayName().equalsIgnoreCase(displayName))) return channel;
		}
		
		return null;
	}
	
	public Channel getChannelHasPlayer(String playerName) {
		for(Channel channel : channels.values()) {
			if(channel.hasPlayer(playerName)) return channel;
		}
		
		return null;
	}
	
}