package su.plugin.prefixer.api.manager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.util.HashMap;
import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.prefixer.PrefixerPlugin;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.object.PrefixPlayer;

public class PlayerManager {
	
	@Setter
	@Getter
	private HashMap<PlayerKey, PrefixPlayer> prefixPlayers = new HashMap<>();
	
	public void setPrefixPlayer(PlayerKey playerKey, PrefixPlayer pp) {
		prefixPlayers.put(playerKey, pp);
	}
	
	public void removePrefixPlayer(PlayerKey playerKey) {
		prefixPlayers.remove(playerKey);
	}
	
	public boolean existsPrefixPlayer(PlayerKey playerKey) {
		return prefixPlayers.containsKey(playerKey);
	}
	
	public PrefixPlayer getPrefixPlayer(PlayerKey playerKey) {
		return prefixPlayers.get(playerKey);
	}
	
	public PrefixPlayer getPrefixPlayer(PlayerKey playerKey, boolean sql) {
		return existsPrefixPlayer(playerKey) || !sql ? prefixPlayers.get(playerKey) : PrefixerAPI.getSQLManager().getPrefixPlayer(playerKey);
	}
	
	public PrefixPlayer registerPlayer(Player p) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(p.getName());
		
		PrefixPlayer pp = PrefixerAPI.getSQLManager().getPrefixPlayer(playerKey);
		if(pp == null) {
			pp = new PrefixPlayer(playerKey);
		} else {
			Iterator<Integer> it = pp.getMainPrefixes().keySet().iterator();
			while(it.hasNext()) {
				String prefix = pp.getMainPrefixes().get(it.next());
				if(pp.hasPrefix(prefix)) continue;

				pp.removeMainPrefix(prefix);
				PrefixerAPI.getSQLManager().removeMainPrefix(playerKey, prefix);
			}
		}

		setPrefixPlayer(playerKey, pp);
		
		if(PrefixerAPI.isUseHologram() && pp.getMainPrefixes().size() > 0) {
			Hologram holo = HologramsAPI.createHologram(PrefixerPlugin.getInstance(), pp.getMainPrefixLocation());
			
			for(String pf : pp.getMainPrefixList()) {
				holo.appendTextLine(pf);
			}
			
			holo.getVisibilityManager().setVisibleByDefault(false);
			holo.getVisibilityManager().setVisibleByDefault(true);
			
			pp.setHologram(holo);
		}
		
		return pp;
	}
	
	public void registerAllPlayer() {
		for(Player player : KCore.getOnlinePlayers()) {
			registerPlayer(player);
		}
	}
	
}