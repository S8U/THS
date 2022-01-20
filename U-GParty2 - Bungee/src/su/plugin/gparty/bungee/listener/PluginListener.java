package su.plugin.gparty.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gparty.bungee.GPartyPlugin;
import su.plugin.gparty.bungee.api.GPartyAPI;
import su.plugin.gparty.bungee.api.object.GParty;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;

public class PluginListener implements Listener {
	
	private GPartyAPI api = GPartyPlugin.getApi();
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if(!e.getTag().equals("ugparty:main")) return;

		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

		String task = in.readUTF();

		if (task.equals("RequestParty")) {
			PlayerKey pk = PlayerKey.getPlayerKey(in.readInt());

			GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(pk);
			if (!pp.hasParty() || !pp.getParty().getLeader().equals(pk)) return;

			((GParty) pp.getParty()).sendInfo();
		}
	}
	
}
