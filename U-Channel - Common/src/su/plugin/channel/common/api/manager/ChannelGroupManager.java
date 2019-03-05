package su.plugin.channel.common.api.manager;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import su.plugin.channel.common.api.object.ChannelGroup;

public class ChannelGroupManager {
	
	@Setter
	@Getter
	private HashMap<String, ChannelGroup> channelGroups = new HashMap<>();
	
	public void setChannelGroup(String name, ChannelGroup channelGroup) {
		channelGroups.put(name.toLowerCase(), channelGroup);
	}
	
	public ChannelGroup getChannelGroup(String name) {
		return channelGroups.get(name.toLowerCase());
	}
	
	public boolean existsChannelGroup(String name) {
		return channelGroups.containsKey(name.toLowerCase());
	}
	
	public void removeChannelGroup(String name) {
		channelGroups.remove(name.toLowerCase());
	}
	
	public ChannelGroup getChannelGroupByDisplayName(String displayName) {
		for(ChannelGroup group : channelGroups.values()) {
			if(group.getName().equalsIgnoreCase(displayName) || (group.getDisplayName() != null && group.getDisplayName().equalsIgnoreCase(displayName))) return group;
		}
		
		return null;
	}
	
	public ChannelGroup getChannelGroupHasPlayer(String playerName) {
		for(ChannelGroup group : channelGroups.values()) {
			if(group.hasPlayer(playerName)) return group;
		}
		
		return null;
	}
	
}