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
import su.plugin.core.bungee.GCorePlugin;
import su.plugin.core.bungee.api.GCore;
import su.plugin.core.bungee.api.player.GPlayer;
import su.plugin.core.bungee.api.util.ChannelMessageUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.DebugUtil;

public class PluginMessageListener implements Listener {
	
	@SneakyThrows(IOException.class)
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if(!e.getTag().equals("ucore:main")) return;
		
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

			ChannelMessageUtil.sendToAllChannelExistsPlayers("ucore:main", "PlayerOptionChange", player, option);
		} else if(task.equals("PlayerOptionDelete")) {
			String player = dis.readUTF();
			String option = dis.readUTF();

			Core.getOptionManager().deletePlayerOption(PlayerKey.getPlayerKey(player), option, false);
			
			ChannelMessageUtil.sendToAllChannelExistsPlayers("ucore:main", "PlayerOptionDelete", player, option);
		} else if(task.equals("ServerOptionChange")) {
			String option = dis.readUTF();

			Core.getOptionSQLManager().loadServerOption(option);
			
			ChannelMessageUtil.sendToAllChannelExistsPlayers("ucore:main", "ServerOptionChange", option);
		} else if(task.equals("ServerOptionDelete")) {
			String option = dis.readUTF();

			Core.getOptionManager().deleteServerOption(option, false);

			ChannelMessageUtil.sendToAllChannelExistsPlayers("ucore:main", "ServerOptionDelete", option);
		}
		
		//
		
		else if(task.equals("SetDisplayName")) {
			String playerName = dis.readUTF();
			String displayName = dis.readUTF();
			
			UPlayer up = Core.getUPlayer(playerName);
			if(up == null) return;
			
			up.setDisplayName(displayName, false);
		}

		//

		else if (task.equals("BroadcastChannel")) {
			String channel = dis.readUTF();
			String message = dis.readUTF();

			ServerInfo si = ProxyServer.getInstance().getServerInfo(channel);
			if (si == null) return;

			for (ProxiedPlayer p : si.getPlayers()) {
				p.sendMessage(message);
			}
		} else if (task.equals("BroadcastAll")) {
			String message = dis.readUTF();

			ProxyServer.getInstance().broadcast(message);
		}

		//

		else if (task.equals("PlaySoundTo")) {
			int playerId = dis.readInt();
			GPlayer gp = (GPlayer) GCore.getUPlayer(playerId);
			if (gp == null) return;

			String soundName = dis.readUTF();
			float volume = dis.readFloat();
			float pitch = dis.readFloat();

			ChannelMessageUtil.sendToChannel(GCorePlugin.getInstance(), gp.getPlatformSender().getServer().getInfo(),"ucore:main", "PlaySoundTo", playerId, soundName, volume, pitch);
		} else if (task.equals("PlaySoundChannel")) {
			String channelName = dis.readUTF();
			ServerInfo si = ProxyServer.getInstance().getServerInfo(channelName);
			if (si == null || si.getPlayers().size() < 1) return;

			String soundName = dis.readUTF();
			float volume = dis.readFloat();
			float pitch = dis.readFloat();

			ChannelMessageUtil.sendToChannel(GCorePlugin.getInstance(), si, "ucore:main", "PlaySoundAll", soundName, volume, pitch);
		} else if (task.equals("PlaySoundAll")) {
			String soundName = dis.readUTF();
			float volume = dis.readFloat();
			float pitch = dis.readFloat();

			ProxyServer.getInstance().getServers().values().stream()
					.filter(si -> si.getPlayers().size() > 0)
					.forEach(si -> ChannelMessageUtil.sendToChannel(GCorePlugin.getInstance(), si, "ucore:main", "PlaySoundAll", soundName, volume, pitch));
		}
	}
	
	@SneakyThrows(IOException.class)
	private byte[] cutBytes(DataInputStream dis, byte[] originalByteArray) {
		byte[] newByteArray = new byte[dis.available()];
		
		System.arraycopy(originalByteArray, originalByteArray.length - dis.available(), newByteArray, 0, newByteArray.length);
		
		return newByteArray;
	}
	
}