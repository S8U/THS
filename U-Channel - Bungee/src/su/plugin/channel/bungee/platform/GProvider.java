package su.plugin.channel.bungee.platform;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.channel.bungee.GChannelPlugin;
import su.plugin.channel.bungee.api.GChannelAPI;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.channel.common.platform.PlatformProvider;
import su.plugin.core.common.api.Core;

public class GProvider implements PlatformProvider {

	private GChannelAPI api = GChannelPlugin.getApi();
	
	@Override
	public boolean sendToChannel(Channel channel, String playerName) {
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
		if(player == null) return false;
		
		ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(channel.getName());
		if(serverInfo == null) return false;
		else if(player.getServer().getInfo().equals(serverInfo)) {
			Core.wmsg(player, "이미 접속 중인 채널입니다.");
			
			return false;
		} else if(!channel.isOnline()) {
			Core.wmsg(player, "오프라인 상태인 채널입니다.");
			
			return false;
		}
		
		player.connect(serverInfo);
		
		return true;
	}
	
	@SneakyThrows(Exception.class)
	@Override
	public Channel sendToOptimizeChannel(ChannelGroup group, String playerName) {
		ScriptEngine se = api.getScriptManager().getScriptEngines().get(group);
		
		if(se == null) return null;
		
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
		if(player == null) return null;
		
		Invocable invEngine = (Invocable) se;

		Channel channel = (Channel) invEngine.invokeFunction("getOptimizeChannel", api.getChannelManager().getChannelHasPlayer(playerName), group.getChannels());

		if(channel == null) {
			Core.wmsg(player, "접속 가능한 채널이 없습니다.");
			
			return null;
		}
		
		ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(channel.getName());
		
		player.connect(serverInfo);

		return channel;
	}

	@Override
	public void broadCast(Channel channel, String message) {
		for(String playerName : channel.getPlayerList()) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerName);
			if(p == null) continue;

			Core.nmsg(p, ChannelAPI.getBroadCastPrefix() + message);
		}
	}

	@Override
	public void broadCast(ChannelGroup channelGroup, String message) {
		channelGroup.getChannels().forEach(channel -> broadCast(channel, message));
	}
}