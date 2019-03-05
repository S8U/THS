package su.plugin.gessentials.bungee.api.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.core.common.api.player.PlayerKey;

public class PlayerManager {
	
	@Setter
	@Getter
	private HashMap<PlayerKey, EPlayer> ePlayers = new HashMap<>();
	
	public void setEPlayer(PlayerKey playerKey, EPlayer ep) {
		ePlayers.put(playerKey, ep);
	}
	
	public void setEPlayer(EPlayer ep) {
		setEPlayer(ep.getPlayerKey(), ep);
	}
	
	public boolean existsEPlayer(PlayerKey playerKey) {
		return ePlayers.containsKey(playerKey);
	}
	
	public boolean existsEPlayer(String name) {
		for(EPlayer ep : ePlayers.values()) {
			if(!ep.getName().equalsIgnoreCase(name)) continue;
			return true;
		}
		
		return false;
	}
	
	public EPlayer getEPlayer(PlayerKey playerKey) {
		return ePlayers.get(playerKey);
	}
	
	public EPlayer getEPlayer(String name) {
		return getEPlayer(PlayerKey.getPlayerKey(name));
	}
	
	public EPlayer getEPlayer(ProxiedPlayer player) {
		return getEPlayer(player.getName());
	}
	
	public List<EPlayer> getOnlineEPlayers() {
		List<EPlayer> players = new ArrayList<>();
		
		ProxyServer.getInstance().getPlayers().forEach(pp -> players.add(getEPlayer(pp)));
		
		return players;
	}
	
	public List<EPlayer> getOnlineAdminChats() {
		List<EPlayer> players = new ArrayList<>();
		
		getOnlineEPlayers().forEach(ep -> {
			if(!ep.isAdminChat()) return;
			players.add(ep);
		});
		
		return players;
	}
	
	public List<EPlayer> getOnlineChatSpys() {
		List<EPlayer> players = new ArrayList<>();
		
		getOnlineEPlayers().forEach(ep -> {
			if(!ep.isChatSpy()) return;
			players.add(ep);
		});
		
		return players;
	}
	
	public List<EPlayer> getOnlineMoveSpys() {
		List<EPlayer> players = new ArrayList<>();
		
		getOnlineEPlayers().forEach(ep -> {
			if(!ep.isMoveSpy()) return;
			players.add(ep);
		});
		
		return players;
	}
	
	public String getPlayerIp(String name) {
		for(EPlayer ep : ePlayers.values()) {
			if(!ep.getName().equalsIgnoreCase(name)) continue;
			return ep.getIp();
		}
		
		return null;
	}
	
}