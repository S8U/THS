package su.plugin.gparty.bungee.api.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;
import su.plugin.core.common.api.player.PlayerKey;

public class GPlayerManager {

	@Getter
	@Setter
	public HashMap<PlayerKey, GPartyPlayer> partyPlayers = new HashMap<>();
	
	public void setPartyPlayer(ProxiedPlayer p, GPartyPlayer pp) {
		setPartyPlayer(PlayerKey.getPlayerKey(p.getName()), pp);
	}
	
	public void setPartyPlayer(PlayerKey playerKey, GPartyPlayer pp) {
		partyPlayers.put(playerKey, pp);
	}
	
	public void removePartyPlayer(ProxiedPlayer p) {
		removePartyPlayer(PlayerKey.getPlayerKey(p.getName()));
	}
	
	public void removePartyPlayer(PlayerKey playerKey) {
		partyPlayers.remove(playerKey);
	}
	
	public boolean existPartyPlayer(ProxiedPlayer p) {
		return existPartyPlayer(PlayerKey.getPlayerKey(p.getName()));
	}
	
	public boolean existPartyPlayer(PlayerKey playerKey) {
		return partyPlayers.containsKey(playerKey);
	}
	
	public GPartyPlayer getPartyPlayer(ProxiedPlayer p) {
		return getPartyPlayer(PlayerKey.getPlayerKey(p.getName()));
	}
	
	public GPartyPlayer getPartyPlayer(PlayerKey playerKey) {
		return partyPlayers.get(playerKey);
	}
	
	public List<GPartyPlayer> getPartyChatSpyPlayers() {
		List<GPartyPlayer> spyPlayer = new ArrayList<>();
		
		for(ProxiedPlayer ap : ProxyServer.getInstance().getPlayers()) {
			GPartyPlayer pp = getPartyPlayer(ap);
			
			if(!pp.isChatSpy()) continue;
			spyPlayer.add(pp);
		}
		
		return spyPlayer;
	}
	
}