package su.plugin.gparty.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.gparty.bungee.GGPartyPlugin;
import su.plugin.gparty.bungee.api.GGPartyAPI;
import su.plugin.gparty.bungee.api.object.GParty;
import su.plugin.core.common.api.player.PlayerKey;

public class PluginListener implements Listener {
	
	private GGPartyAPI api = GGPartyPlugin.getApi();
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if(!e.getTag().equals("U-GParty")) return;

		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

		String task = in.readUTF();

		if(task.equals("RequestPartyData")) {
			PlayerKey playerKey = PlayerKey.getPlayerKey(in.readInt());
			if(playerKey == null) return;

			GParty party = api.getPartyManager().getParty(playerKey);
			if(party == null) return;

			api.getPartyManager().sendParty(party, ProxyServer.getInstance().getPlayer(playerKey.getName()).getServer().getInfo());
		}
	}
	
}
