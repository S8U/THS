package su.plugin.core.bukkit.api.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.experimental.UtilityClass;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.task.PluginMessageTask;
import su.plugin.core.common.api.player.PlayerKey;

@UtilityClass
public class BungeeUtil {
	
	public static void sendMessageToBungeeCord(JavaPlugin plugin, String messageChannel, Object...objs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		writeObject(out, objs);

		new PluginMessageTask(plugin, messageChannel, out.toByteArray()).runTaskAsynchronously();
	}
	
	public static void sendPlayer(String player, String channel) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "BungeeCord", "ConnectOther", player, channel);
	}
	
	public static void requestPlayerCount(String channel) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "BungeeCord", "PlayerCount", channel);
	}
	
	public static void requestPlayerList(String channel) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "BungeeCord", "PlayerList", channel);
	}
	
	public static void requestServerList() {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "BungeeCord", "GetServers");
	}
	
	public static void sendMessage(String player, String message) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "BungeeCord", "Message", player, message);
	}
	
	public static void requestServerName() {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "BungeeCord", "GetServer");
	}
	
	public static void requestUUID(String player) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "BungeeCord", "UUIDOther", player);
	}
	
	public static void requestServerIp(String channel) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "BungeeCord", "ServerIP", channel);
	}
	
	public static void kickPlayer(String player, String message) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "BungeeCord", "KickPlayer", player, message);
	}

	public static void broadcast(String channel, String message) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "U-Core", "BroadcastChannel", channel, message);
	}

	public static void broadcast(String message) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(), "U-Core", "BroadcastAll", message);
	}

	public static void playSound(String channel, Sound sound, float volume, float pitch) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(),"U-Core","PlaySoundChannel", sound.toString(), volume, pitch);
	}

	public static void playSound(PlayerKey playerKey, Sound sound, float volume, float pitch) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(),"U-Core","PlaySoundTo", playerKey.getId(), sound.toString(), volume, pitch);
	}

	public static void playSound(Player player, Sound sound, float volume, float pitch) {
		playSound(PlayerKey.getPlayerKeyByPlatformPlayer(player), sound, volume, pitch);
	}

	public static void playSound(Sound sound, float volume, float pitch) {
		sendMessageToBungeeCord(KCorePlugin.getInstance(),"U-Core","PlaySoundAll", sound.toString(), volume, pitch);
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