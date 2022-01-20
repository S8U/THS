package su.plugin.gessentials.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import su.plugin.gessentials.bukkit.KGEssentialsPlugin;
import su.plugin.gessentials.bukkit.api.KGEssentialsAPI;
import su.plugin.core.common.api.player.PlayerKey;

public class MessageListener implements PluginMessageListener {
	
	private KGEssentialsAPI api = KGEssentialsPlugin.getApi();
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("ugessentials:main")) return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		
		String task = in.readUTF();
		if (task.equals("SetMoveSpy")) {
			String name = in.readUTF();
			boolean toggle = in.readBoolean();
			
			PlayerKey playerKey = PlayerKey.getPlayerKey(name);
			if(playerKey == null) return;
			
			if(toggle) {
				api.getMoveSpys().add(playerKey);
			} else {
				api.getMoveSpys().remove(playerKey);
			}
		}
	}
	
}