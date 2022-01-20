package su.plugin.gparty.bukkit.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;
import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.common.api.object.Party;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class PluginListener implements PluginMessageListener {
	
	private KGPartyAPI api = KGPartyPlugin.getApi();

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		if(!channel.equals("ugparty:main")) return;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String task = in.readUTF();

		if (task.equals("PartyCreate")) {
			PlayerKey leaderKey = PlayerKey.getPlayerKey(in.readInt());
			PartyPlayer pp = api.getPlayerManager().getPartyPlayers().get(leaderKey);
			if (pp == null) return;

			Party party = new Party();
			party.getPlayers().add(pp);
			party.setLeader(leaderKey);

			pp.setParty(party);
		} else if (task.equals("PartyDelete")) {
			PlayerKey leaderKey = PlayerKey.getPlayerKey(in.readInt());
			PartyPlayer pp = api.getPlayerManager().getPartyPlayers().get(leaderKey);
			if (pp == null) return;

			Party party = pp.getParty();
			if (party == null) return;

			for (PartyPlayer ptp : party.getPlayers()) {
				ptp.setParty(null);
			}
		} else if (task.equals("PartyInfo")) {
			PlayerKey leaderKey = PlayerKey.getPlayerKey(in.readInt());
			PartyPlayer pp = api.getPlayerManager().getPartyPlayers().get(leaderKey);
			if (pp == null) return;

			Party party = pp.getParty();
			if (party == null) {
				party = new Party();
				party.setLeader(leaderKey);
			}

			List<PartyPlayer> list = new NotDuplicatedArrayList<>();

			int size = in.readInt();
			for (int i = 0; i < size; i++) {
				PlayerKey ppk = PlayerKey.getPlayerKey(in.readInt());

				PartyPlayer ptp = api.getPlayerManager().getPartyPlayers().get(ppk);
				if (ptp == null) {
					ptp = new PartyPlayer(ppk);
					api.getPlayerManager().getPartyPlayers().put(ppk, ptp);
				}

				ptp.setParty(party);

				list.add(ptp);
			}

			party.setPlayers(list);
		}
	}

}