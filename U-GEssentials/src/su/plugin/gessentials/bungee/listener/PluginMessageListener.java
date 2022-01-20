package su.plugin.gessentials.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.core.common.api.player.PlayerKey;

public class PluginMessageListener implements Listener {
	
	private GGEssentialsAPI api = GGEssentialsPlugin.getApi();
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if(!e.getTag().equals("ugessentials:main")) return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
		
		String task = in.readUTF();
		
		if(task.equals("SetPrefixerPrefix")) {
			PlayerKey playerKey = PlayerKey.getPlayerKey(in.readInt());
			int priority = in.readInt();
			String prefix = in.readUTF();
			
			EPlayer ep = api.getPlayerManager().getEPlayer(playerKey);
			if(ep == null || !ep.isOnline()) return;
			

			if(prefix.equalsIgnoreCase("$null")) {
				ep.deletePrefixerPrefix(prefix);
			} else {
				if(ep.hasPrefixerPrefix(prefix)) {
					ep.deletePrefixerPrefix(prefix);
				}

				ep.setPrefixerPrefix(priority, prefix);
			}
		} else if(task.equals("SetPermissionPrefix")) {
			PlayerKey playerKey = PlayerKey.getPlayerKey(in.readInt());
			String prefix = in.readUTF();
			
			EPlayer ep = api.getPlayerManager().getEPlayer(playerKey);
			if(ep == null || !ep.isOnline()) return;

			if(prefix.equalsIgnoreCase("$null")) {
				ep.setPermissionPrefix(null);
			} else {
				ep.setPermissionPrefix(prefix);
			}
		} else if(task.equals("Chat")) {
			PlayerKey playerKey = PlayerKey.getPlayerKey(in.readInt());
			String chat = in.readUTF();
			
			EPlayer ep = api.getPlayerManager().getEPlayer(playerKey);
			if(ep == null || !ep.isOnline()) return;
			
			GGEssentialsAPI.getChatManager().sendGlobalChat(ep, chat);
		}
	}
	
}