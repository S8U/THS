package su.plugin.gessentials.bungee.api.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.category.ChatHandlingLocation;
import su.plugin.gessentials.bungee.api.category.ListeningChannel;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.core.bungee.api.util.ChannelMessageUtil;

@RequiredArgsConstructor
public class EChannel {
	
	@Getter
	private final String name;
	
	@Setter
	@Getter
	private String chatForm;
	
	@Setter
	@Getter
	private ListeningChannel listeningChannel = ListeningChannel.LOCAL;
	
	@Setter
	private ChatHandlingLocation chatHandlingLocation = ChatHandlingLocation.BUNGEECORD;
	
	public String getDisplayName() {
		return getChannel().getDisplayName();
	}
	
	public boolean isLobby() {
		return GGEssentialsAPI.getChannelManager().isLobby(this);
	}
	
	public ServerInfo getServerInfo() {
		return ProxyServer.getInstance().getServerInfo(name);
	}
	
	public Channel getChannel() {
		return ChannelAPI.getChannelManager().getChannel(name);
	}
	
	public ChatHandlingLocation getChatHandlingLocation() {
		return chatHandlingLocation == null ?  GGEssentialsAPI.getDefaultChatHandlingLocation() : chatHandlingLocation;
	}
	
	public void sendSetChatHandling(boolean toggle) {
		ChannelMessageUtil.sendToChannel(GGEssentialsPlugin.getInstance(), getServerInfo(), "ChatHandling", toggle);
	}
	
}