package su.plugin.gessentials.bungee.api.manager;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.gessentials.bungee.api.object.EChannel;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.ChannelGroup;

public class ChannelManager {
	
	@Setter
	@Getter
	private String lobbyGroupName;
	
	@Setter
	@Getter
	private HashMap<String, EChannel> channels = new HashMap<>();
	
	public void setEChannel(String name, EChannel channel) {
		channels.put(name.toLowerCase(), channel);
	}
	
	public boolean existsEChannel(String name) {
		return channels.containsKey(name.toLowerCase());
	}
	
	public EChannel getEChannel(String name) {
		return channels.get(name.toLowerCase());
	}
	
	public ChannelGroup getLobbyGroup() {
		return ChannelAPI.getChannelGroupManager().getChannelGroup(lobbyGroupName);
	}
	
	public boolean isLobby(EChannel channel) {
		return getLobbyGroup().hasChannel(channel.getName());
	}
	
	public boolean canJoinToLobby() {
		return getLobbyGroup().getChannelsCanJoin().size() > 0;
	}
	
	public boolean sendOptimizedLobby(ProxiedPlayer player) {
		return getLobbyGroup().sendToOptimizeChannel(player.getName()) != null;
	}
	
}