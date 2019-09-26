package su.plugin.core.bukkit.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.ChannelMessageEvent;
import su.plugin.core.bukkit.api.player.KPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.c.player.UNewPlayerJoinEvent;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.DebugUtil;

public class BungeeMessageListener implements PluginMessageListener {
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(!channel.equals("U-Core")) return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		
		String task = in.readUTF();
		
		DebugUtil.log("PluginMessage: " + task);
		
		if(task.equals("ChannelMessage")) {
			String key = in.readUTF();
			String ctask = in.readUTF();
			
			Bukkit.getPluginManager().callEvent(new ChannelMessageEvent(key, ctask, in));
		} else if(task.equals("SetDisplayName")) {
			String name = in.readUTF();
			String displayName = in.readUTF();
			
			UPlayer up = Core.getUPlayer(name);
			if(up == null) return;
			
			up.setDisplayName(displayName, false);
		} else if(task.equalsIgnoreCase("NewPlayerJoin")) {
			String playerName = in.readUTF();

			UPlayer up = Core.getUPlayer(playerName);
			if(up == null) return;

			DebugUtil.log(playerName + ": UNewPlayerJoinEvent 시작");
			long now = System.currentTimeMillis();

			Core.getUEventManager().callEvent(new UNewPlayerJoinEvent(up));

			Core.getSQLManager().deleteNewPlayerHandle(up.getPlayerKey());

			DebugUtil.log(playerName + ": UNewPlayerJoinEvent 종료 (" + (System.currentTimeMillis() - now) + "ms)");
		} else if (task.equals("PlaySoundAll")) {
			String soundName = in.readUTF();

			Sound sound = Sound.valueOf(soundName);
			float volume = in.readFloat();
			float pitch = in.readFloat();
			if (sound == null) return;

			KCore.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), sound, volume, pitch));
		} else if (task.equals("PlaySoundTo")) {
			int playerId = in.readInt();
			KPlayer kp = (KPlayer) KCore.getUPlayer(playerId);
			if (kp == null) return;

			String soundName = in.readUTF();

			Sound sound = Sound.valueOf(soundName);
			float volume = in.readFloat();
			float pitch = in.readFloat();
			if (sound == null) return;

			kp.getPlatformSender().playSound(kp.getPlatformSender().getLocation(),sound,volume,pitch);
		}
	}
	
}