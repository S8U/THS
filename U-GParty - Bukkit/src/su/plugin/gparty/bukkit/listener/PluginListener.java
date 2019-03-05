package su.plugin.gparty.bukkit.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.bukkit.api.object.KParty;
import su.plugin.gparty.bukkit.api.object.KPartyPlayer;
import su.plugin.core.common.api.player.PlayerKey;

public class PluginListener implements PluginMessageListener {
	
	private KGPartyAPI api = KGPartyPlugin.getApi();

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		if(!channel.equals("U-GParty")) return;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String task = in.readUTF();

		if(task.equals("PartyData")) {
			PlayerKey ownerKey = PlayerKey.getPlayerKey(in.readInt());
			KPartyPlayer pp = KGPartyAPI.getPlayerManager().getPartyPlayer(ownerKey);
			if(pp == null) return;

			KParty party = pp.hasParty() ? pp.getParty() : new KParty();
			party.getPlayers().clear();

			party.addPlayer(ownerKey);

			int count = in.readInt();
			for(int i = 0; i < count; i++) {
				PlayerKey playerKey = PlayerKey.getPlayerKey(in.readInt());
				if(playerKey == null) continue;

				party.getPlayers().add(playerKey);

				KPartyPlayer pkp = api.getPlayerManager().getPartyPlayer(playerKey);
				if(pkp == null) {
					pkp = new KPartyPlayer(playerKey);

					api.getPlayerManager().setPartyPlayer(playerKey, pkp);
				}

				pkp.setParty(party);
			}

			pp.setParty(party);
		} else if(task.equals("PartyDelete")) {
			PlayerKey playerKey = PlayerKey.getPlayerKey(in.readInt());

			KPartyPlayer pp = KGPartyAPI.getPlayerManager().getPartyPlayer(playerKey);
			if(pp == null || !pp.hasParty()) return;

			for(PlayerKey ppk : pp.getParty().getPlayers()) {
				KPartyPlayer ppp = KGPartyAPI.getPlayerManager().getPartyPlayer(ppk);
				if(ppp == null) continue;

				ppp.setParty(null);
			}

			pp.setParty(null);
		}
	}
	
	
}