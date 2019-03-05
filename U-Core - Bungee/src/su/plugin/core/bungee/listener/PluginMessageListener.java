package su.plugin.core.bungee.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import lombok.Cleanup;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.core.bungee.api.util.ChannelMessageUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.DebugUtil;

public class PluginMessageListener implements Listener {
	
	@SneakyThrows(IOException.class)
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if(!e.getTag().equals("U-Core")) return;
		
		@Cleanup ByteArrayInputStream bis = new ByteArrayInputStream(e.getData());
		@Cleanup DataInputStream dis = new DataInputStream(bis);
		
		String task = dis.readUTF();
		
		DebugUtil.log("PluginMessage: " + task);
		
		if(task.equals("SendToChannel")) {
			String channelName = dis.readUTF();
			ServerInfo si = ProxyServer.getInstance().getServerInfo(channelName);
			if(si == null) return;
			
			ChannelMessageUtil.sendToChannel(si, cutBytes(dis, e.getData()));
		} else if(task.equals("SendToAllChannel")) {
			ChannelMessageUtil.sendToAllChannel(((ProxiedPlayer) e.getReceiver()).getServer().getInfo(), cutBytes(dis, e.getData()));
		} else if(task.equals("SendToChannelExistsPlayers")) {
			String channelName = dis.readUTF();
			ServerInfo si = ProxyServer.getInstance().getServerInfo(channelName);
			if(si == null) return;
			
			ChannelMessageUtil.sendToChannelExistsPlayers(si, cutBytes(dis, e.getData()));
		} else if(task.equals("SendToAllChannelExistsPlayers")) {
			ChannelMessageUtil.sendToChannelExistsPlayers(((ProxiedPlayer) e.getReceiver()).getServer().getInfo(), cutBytes(dis, e.getData()));
		} else if(task.equals("SendToAllChannelHasPlayer")) {
			ChannelMessageUtil.sendToChannelHasPlayer(dis.readUTF(), cutBytes(dis, e.getData()));
		}
		
		//
		
		else if(task.equals("PlayerOptionChange")) {
			String player = dis.readUTF();
			String option = dis.readUTF();

			Core.getOptionSQLManager().loadPlayerOption(PlayerKey.getPlayerKey(player), option);

			ChannelMessageUtil.sendToAllChannelExistsPlayers("U-Core", "PlayerOptionChange", player, option);
		} else if(task.equals("PlayerOptionDelete")) {
			String player = dis.readUTF();
			String option = dis.readUTF();

			Core.getOptionManager().deletePlayerOption(PlayerKey.getPlayerKey(player), option, false);
			
			ChannelMessageUtil.sendToAllChannelExistsPlayers("U-Core", "PlayerOptionDelete", player, option);
		} else if(task.equals("ServerOptionChange")) {
			String option = dis.readUTF();

			Core.getOptionSQLManager().loadServerOption(option);
			
			ChannelMessageUtil.sendToAllChannelExistsPlayers("U-Core", "ServerOptionChange", option);
		} else if(task.equals("ServerOptionDelete")) {
			String option = dis.readUTF();

			Core.getOptionManager().deleteServerOption(option, false);

			ChannelMessageUtil.sendToAllChannelExistsPlayers("U-Core", "ServerOptionDelete", option);
		}
		
		//
		
		else if(task.equals("SetDisplayName")) {
			String playerName = dis.readUTF();
			String displayName = dis.readUTF();
			
			UPlayer up = Core.getUPlayer(playerName);
			if(up == null) return;
			
			up.setDisplayName(displayName, false);
		}
	}
	
	@SneakyThrows(IOException.class)
	private byte[] cutBytes(DataInputStream dis, byte[] originalByteArray) {
		byte[] newByteArray = new byte[dis.available()];
		
		System.arraycopy(originalByteArray, originalByteArray.length - dis.available(), newByteArray, 0, newByteArray.length);
		
		return newByteArray;
	}
	
}