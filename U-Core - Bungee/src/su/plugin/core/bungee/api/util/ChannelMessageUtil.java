package su.plugin.core.bungee.api.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import su.plugin.core.bungee.GCorePlugin;
import su.plugin.core.bungee.api.task.PluginMessageTask;

public class ChannelMessageUtil {
	
	public static void sendToChannel(ServerInfo si, byte[] byteArray) {
		new PluginMessageTask(GCorePlugin.getInstance(), si, "ucore:main", byteArray).runAsync();
	}
	
	public static void sendToChannel(Plugin plugin, ServerInfo si, String messageChannel, Object...objs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		for (int i = 0; i < objs.length; i++) {
			writeObject(out, objs);
		}
		
		new PluginMessageTask(plugin, si, messageChannel, out.toByteArray()).runAsync();
	}
	
	public static void sendToChannel(ServerInfo si, String key, String task, Object...objs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		out.writeUTF(key);
		out.writeUTF(task);
		
		for (int i = 0; i < objs.length; i++) {
			writeObject(out, objs);
		}
		
		sendToChannel(si, out.toByteArray());
	}
	
	public static void sendToAllChannel(byte[] byteArray) {
		for(ServerInfo si : ProxyServer.getInstance().getServers().values()) {
			sendToChannel(si, byteArray);
		}
	}
	
	public static void sendToAllChannel(ServerInfo currentServerInfo, byte[] byteArray) {
		for(ServerInfo si : ProxyServer.getInstance().getServers().values()) {
			if(si.equals(currentServerInfo)) continue;
			sendToChannel(si, byteArray);
		}
	}
	
	public static void sendToAllChannel(String key, String task, Object...objs) {
		for(ServerInfo si : ProxyServer.getInstance().getServers().values()) {
			sendToChannel(si, key, task, objs);
		}
	}
	
	public static void sendToAllChannel(ServerInfo currentServerInfo, String key, String task, Object...objs) {
		for(ServerInfo si : ProxyServer.getInstance().getServers().values()) {
			if(si.equals(currentServerInfo)) continue;
			sendToChannel(si, key, task, objs);
		}
	}
	
	public static void sendToChannelExistsPlayers(ServerInfo si, byte[] byteArray) {
		if(si.getPlayers().size() < 1) return;
		
		sendToChannel(si, byteArray);
	}
	
	public static void sendToChannelExistsPlayers(ServerInfo si, String key, String task, Object...objs) {
		if(si.getPlayers().size() < 1) return;
		
		sendToChannel(si, key, task, objs);
	}
	
	public static void sendToAllChannelExistsPlayers(byte[] byteArray) {
		for(ServerInfo si : ProxyServer.getInstance().getServers().values()) {
			sendToChannelExistsPlayers(si, byteArray);
		}
	}
	
	public static void sendToAllChannelExistsPlayers(ServerInfo currentServerInfo, byte[] byteArray) {
		for(ServerInfo si : ProxyServer.getInstance().getServers().values()) {
			if(si.equals(currentServerInfo)) continue;
			sendToChannelExistsPlayers(si, byteArray);
		}
	}
	
	public static void sendToAllChannelExistsPlayers(String key, String task, Object...objs) {
		for(ServerInfo si : ProxyServer.getInstance().getServers().values()) {
			sendToChannelExistsPlayers(si, key, task, objs);
		}
	}
	
	public static void sendToAllChannelExistsPlayers(ServerInfo currentServerInfo, String key, String task, Object...objs) {
		for(ServerInfo si : ProxyServer.getInstance().getServers().values()) {
			if(si.equals(currentServerInfo)) continue;
			sendToChannelExistsPlayers(si, key, task, objs);
		}
	}
	
	public static void sendToChannelHasPlayer(String key, String player, String task, Object...objs) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);
		if(p == null) return;
		
		sendToChannel(p.getServer().getInfo(), key, task, objs);
	}
	
	public static void sendToChannelHasPlayer(String player, byte[] byteArray) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);
		if(p == null) return;
		
		sendToChannel(p.getServer().getInfo(), byteArray);
	}
	
	private static void writeObject(ByteArrayDataOutput out, Object...objs) {
		for (int i = 0; i < objs.length; i++) {
			Object obj = objs[i];
			if(obj instanceof String) {
				out.writeUTF((String) obj);
			} else if(obj instanceof Integer) {
				out.writeInt((Integer) obj);
			} else if(obj instanceof Short) {
				out.writeShort((Short) obj);
			} else if(obj instanceof Long) {
				out.writeLong((Long) obj);
			} else if(obj instanceof Float) {
				out.writeFloat((Float) obj);
			} else if(obj instanceof Double) {
				out.writeDouble((Double) obj);
			} else if(obj instanceof Boolean) {
				out.writeBoolean((Boolean) obj);
			}
		}
	}
	
}