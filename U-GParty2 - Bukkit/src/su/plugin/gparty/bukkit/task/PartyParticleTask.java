package su.plugin.gparty.bukkit.task;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.enumeration.Particle;
import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.common.api.object.Party;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class PartyParticleTask implements Runnable {
	
	private KGPartyAPI api = KGPartyPlugin.getApi();
	
	public void run() {
		Set<Party> complete = new HashSet<>();

		for(PartyPlayer kap : api.getPlayerManager().getPartyPlayers().values()) {
			if(!kap.hasParty() || complete.contains(kap.getParty())) continue;

			kap.getParty().getPlayers().stream().map(ptp -> (Player) ptp.getPlayerKey().getPlatformPlayer()).forEach(p -> {
				kap.getParty().getPlayers().stream().map(ptp -> (Player) ptp.getPlayerKey().getPlatformPlayer()).forEach(p2 -> {
					if (p.getName().equals(p2.getName()) || p == null || !p.isOnline() || p2 == null || !p2.isOnline()) return;

					Location loc = p2.getLocation();
					loc.setY(loc.getY() + 2.5);
					Bukkit.getScheduler().runTask(KGPartyPlugin.getInstance(),() -> Particle.getByName(api.getParticleName()).spawn(p, loc, 0, 1));
				});
			});

			complete.add(kap.getParty());
		}
	}
	
}