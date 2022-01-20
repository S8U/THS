package su.plugin.channel.common.api.object;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.channel.common.api.ChannelAPI;

@RequiredArgsConstructor
@Setter
@Getter
public class ChannelGroup {
	
	private final String name;
	
	private String displayName;
	
	public List<Channel> getChannels() {
		List<Channel> channels = new ArrayList<>();
		for(Channel channel : ChannelAPI.getChannelManager().getChannels().values()) {
			if(channel.getGroupName() == null || !channel.getGroupName().equalsIgnoreCase(name)) continue;

			channels.add(channel);
		}
		
		return channels;
	}
	
	public List<Channel> getOnlineChannels() {
		List<Channel> channels = new ArrayList<>();
		for(Channel channel : ChannelAPI.getChannelManager().getChannels().values()) {
			if(channel.getGroupName() == null || !channel.getGroupName().equalsIgnoreCase(name) || !channel.isOnline()) continue;
			
			channels.add(channel);
		}
		
		return channels;
	}
	
	public List<Channel> getChannelsCanJoin() {
		List<Channel> channels = new ArrayList<>();
		for(Channel channel : ChannelAPI.getChannelManager().getChannels().values()) {
			if(channel.getGroupName() == null || !channel.getGroupName().equalsIgnoreCase(name) || !channel.isOnline() || channel.getPlayerCount() >= channel.getMaxPlayerCount()) continue;
			
			channels.add(channel);
		}
		
		return channels;
	}
	
	public boolean hasChannel(Channel channel) {
		return getChannels().contains(channel);
	}
	
	public boolean hasChannel(String name) {
		for(Channel ch : getChannels()) {
			if(ch.getName().equalsIgnoreCase(name)) return true;
		}
		
		return false;
	}
	
	public boolean hasPlayer(String playerName) {
		for(Channel channel : getChannels()) {
			if(channel.hasPlayer(playerName)) return true;
		}
		
		return false;
	}
	
	public int getPlayerCount() {
		return getPlayerList().size();
	}
	
	public Set<String> getPlayerList() {
		Set<String> list = new HashSet<>();
		
		for(Channel ch : getChannels()) {
			if (!ch.isOnline()) continue;

			for(String player : ch.getPlayerList()) {
				list.add(player);
			}
		}
		
		return list;
	}
	
	public List<String> getScript() {
		return ChannelAPI.getScriptManager().getScripts().get(this);
	}
	
	@SneakyThrows(Exception.class)
	public Channel getOptimizeChannel(String playerName) {
		ScriptEngine se = ChannelAPI.getScriptManager().getScriptEngines().get(this);
		
		if(se == null) return null;
		
		Invocable invEngine = (Invocable) se;
		
		return (Channel) invEngine.invokeFunction("getOptimizeChannel", ChannelAPI.getChannelManager().getChannelHasPlayer(playerName), getChannels());
	}
	
	public Channel sendToOptimizeChannel(String playerName) {
		return ChannelAPI.getPlatformProvider().sendToOptimizeChannel(this, playerName);
	}
	
}