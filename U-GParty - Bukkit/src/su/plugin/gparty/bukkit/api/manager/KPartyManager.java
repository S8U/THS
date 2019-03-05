package su.plugin.gparty.bukkit.api.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.bukkit.api.object.KParty;
import su.plugin.gparty.bukkit.api.object.KPartyPlayer;
import su.plugin.gparty.bukkit.api.task.PartyParticleTask;
import su.plugin.core.bukkit.api.task.PluginMessageTask;
import su.plugin.core.common.api.player.PlayerKey;

public class KPartyManager {
	
	@Setter
	@Getter
	private int particleTaskId;

	public KParty getParty(PlayerKey playerKey) {
		for(KPartyPlayer pp : KGPartyAPI.getPlayerManager().getPlayers().values()) {
			if(pp.hasParty() && pp.getParty().getPlayers().contains(playerKey)) return pp.getParty();
		}

		return null;
	}

	public void sendPartyDataRequest(PlayerKey playerKey) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		out.writeUTF("RequestPartyData");
		out.writeInt(playerKey.getId());

		new PluginMessageTask(KGPartyPlugin.getInstance(), "U-GParty", out.toByteArray());
	}

	public void runPartyParticleTask() {
		if(getParticleTaskId() != 0) return;
		setParticleTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(KGPartyPlugin.getInstance(), new PartyParticleTask(), 0, 20));
	}
	
	public void stopPartyParticleTask() {
		if(getParticleTaskId() == 0) return;
		Bukkit.getScheduler().cancelTask(getParticleTaskId());
		setParticleTaskId(0);
	}
	
}