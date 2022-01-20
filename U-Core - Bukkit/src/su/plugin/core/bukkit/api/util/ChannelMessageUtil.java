package su.plugin.core.bukkit.api.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.experimental.UtilityClass;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.task.PluginMessageTask;

@UtilityClass
public class ChannelMessageUtil {
	
	private void sendToChannelDefault(String bungeeTask, String channelName, String key, String task, Object...objs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		out.writeUTF("ChannelMessage");
		out.writeUTF(bungeeTask);
		out.writeUTF(channelName);
		out.writeUTF(key);
		out.writeUTF(task);
		
		writeObject(out, objs);
		
		new PluginMessageTask(KCorePlugin.getInstance(), "ucore:main", out.toByteArray()).runTaskAsynchronously();
	}
	
	private void sendToAllChannelDefault(String bungeeTask, String key, String task, Object...objs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		out.writeUTF(bungeeTask);
		out.writeUTF(key);
		out.writeUTF(task);
		
		writeObject(out, objs);
		
		new PluginMessageTask(KCorePlugin.getInstance(), "ucore:main", out.toByteArray()).runTaskAsynchronously();
	}
	
	public static void sendToChannel(String channelName, String key, String task, Object...objs) {
		sendToChannelDefault("SendToChannel", channelName, key, task, objs);
	}
	
	public static void sendToAllChannel(String key, String task, Object...objs) {
		sendToAllChannelDefault("SendToAllChannel", key, task, objs);
	}
	
	public static void sendToChannelExistsPlayers(String channelName, String key, String task, Object...objs) {
		sendToChannelDefault("SendToChannelExistsPlayers", channelName, key, task, objs);
	}
	public static void sendToAllChannelExistsPlayers(String key, String task, Object...objs) {
		sendToAllChannelDefault("SendToAllChannelExistsPlayers", key, task, objs);
	}
	
	public static void sendToChannelHasPlayer(String player, String key, String task, Object...objs) {
		sendToChannelDefault("sendToChannelHasPlayer", key, task, player, objs);
	}
	
	private void writeObject(ByteArrayDataOutput out, Object...objs) {
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