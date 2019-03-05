package su.plugin.gparty.bukkit.api.task;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.bukkit.api.object.KParty;
import su.plugin.gparty.bukkit.api.object.KPartyPlayer;
import su.plugin.core.bukkit.api.enumeration.Particle;

public class PartyParticleTask implements Runnable {
	
	private KGPartyAPI api = KGPartyPlugin.getApi();
	
	public void run() {
		Set<KParty> complete = new HashSet<>();

		for(KPartyPlayer kap : api.getPlayerManager().getPlayers().values()) {
			if(!kap.hasParty() || complete.contains(kap.getParty())) continue;

			for(Player p : kap.getParty().getOnlinePlayers()) {
				for(Player pp : kap.getParty().getOnlinePlayers()) {
					if(p.getName().equals(pp.getName())) continue;

					Location l = pp.getLocation();
					l.setY(l.getY() + 2.5);
					Particle.getByName(api.getParticle()).spawn(p, l, 0, 1);
				}
			}

			complete.add(kap.getParty());
		}
	}
	
}