package su.plugin.channel.bungee.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import lombok.Cleanup;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.channel.bungee.GChannelPlugin;
import su.plugin.channel.bungee.api.GChannelAPI;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;

public class MessageListener implements Listener {
	
	private GChannelAPI api = GChannelPlugin.getApi();
	
	@SneakyThrows(IOException.class)
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if(!e.getTag().equals("U-Channel")) return;
		
		@Cleanup ByteArrayInputStream bis = new ByteArrayInputStream(e.getData());
		@Cleanup DataInputStream dis = new DataInputStream(bis);
		
		String task = dis.readUTF();
		
		if(task.equals("SendToChannel")) {
			String channelName = dis.readUTF();
			String playerName = dis.readUTF();
			
			Channel channel = api.getChannelManager().getChannel(channelName);
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
			if(channel == null || player == null) return;
			
			channel.sendToChannel(playerName);
		} else if(task.equals("SendToOptimizeChannel")) {
			String groupName = dis.readUTF();
			String playerName = dis.readUTF();

			ChannelGroup group = api.getChannelGroupManager().getChannelGroup(groupName);
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
			if(group == null || player == null) return;

			group.sendToOptimizeChannel(playerName);
		} else if(task.equals("BroadCastChannel")) {
			String channelName = dis.readUTF();
			String message = dis.readUTF();

			Channel channel = api.getChannelManager().getChannel(channelName);
			if(channel == null) return;

			ChannelAPI.getPlatformProvider().broadCast(channel, message);
		} else if(task.equals("BroadCastChannelGroup")) {
			String groupName = dis.readUTF();
			String message = dis.readUTF();

			ChannelGroup group = api.getChannelGroupManager().getChannelGroup(groupName);
			if(group == null) return;

			ChannelAPI.getPlatformProvider().broadCast(group, message);
		}
	}
	
}