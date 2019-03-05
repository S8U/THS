package su.plugin.gparty.bungee.api.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import su.plugin.gparty.bungee.GGPartyPlugin;
import su.plugin.gparty.bungee.api.GGPartyAPI;
import su.plugin.gparty.bungee.api.object.GParty;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;
import su.plugin.core.bungee.api.task.PluginMessageTask;
import su.plugin.core.common.api.player.PlayerKey;

public class GPartyManager {

	public GParty getParty(PlayerKey playerKey) {
		for(GPartyPlayer pp : GGPartyAPI.getPlayerManager().getPartyPlayers().values()) {
			if(pp.hasParty() && (pp.getParty().isOwner(playerKey) || pp.getParty().getPlayers().contains(playerKey))) return pp.getParty();
		}

		return null;
	}

	public void sendParty(GParty party, ServerInfo server) {
		if(party == null) return;

		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		out.writeUTF("PartyData");
		out.writeInt(party.getOwner().getId()); // Owner Id
		out.writeInt(party.getPlayers().size()); // Player Count
		for(PlayerKey players : party.getPlayers()) { // KParty Player Id
			out.writeInt(players.getId());
		}

		new PluginMessageTask(GGPartyPlugin.getInstance(), server, "U-GParty", out.toByteArray()).runAsync();
	}
	
	public void sendPartyDelete(PlayerKey player, ServerInfo server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		out.writeUTF("PartyDelete");
		out.writeInt(player.getId());

		new PluginMessageTask(GGPartyPlugin.getInstance(), server, "U-GParty", out.toByteArray()).runAsync();
	}
	
}