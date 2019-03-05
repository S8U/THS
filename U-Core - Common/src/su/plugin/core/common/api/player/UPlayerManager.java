package su.plugin.core.common.api.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import su.plugin.core.common.api.ChatColor;

@Getter
public class UPlayerManager {
	
	private HashMap<PlayerKey, UPlayer> players = new HashMap<>();
	
	public void setUPlayer(PlayerKey playerKey, UPlayer player) {
		players.put(playerKey, player);
	}
	
	public void removeUPlayer(PlayerKey playerKey) {
		players.remove(playerKey);
	}
	
	public boolean existsUPlayer(PlayerKey playerKey) {
		return players.containsKey(playerKey);
	}
	
	public UPlayer getUPlayer(PlayerKey playerKey) {
		return players.get(playerKey);
	}
	
	public UPlayer getUPlayer(int playerId) {
		Iterator<UPlayer> it = players.values().iterator();
		while(it.hasNext()) {
			UPlayer up = it.next();
			if(up.getPlayerKey().getId() == playerId) return up;
		}
		
		return null;
	}
	
	public UPlayer getUPlayer(String name) {
		Iterator<UPlayer> it = players.values().iterator();
		while(it.hasNext()) {
			UPlayer up = it.next();
			if(up.getPlayerKey().getName().equalsIgnoreCase(name)) return up;
		}
		
		return null;
	}
	
	public UPlayer getUPlayerByDisplayName(String displayName) {
		Iterator<UPlayer> it = players.values().iterator();
		while(it.hasNext()) {
			UPlayer up = it.next();
			if(up.getName().equalsIgnoreCase(displayName)) return up;
		}
		
		it = players.values().iterator();
		while(it.hasNext()) {
			UPlayer up = it.next();
			if(ChatColor.stripColor(up.getDisplayName()).equalsIgnoreCase(displayName)) return up;
		}
		
		return null;
	}
	
	public UPlayer getUPlayer(UUID uuid) {
		Iterator<UPlayer> it = players.values().iterator();
		while(it.hasNext()) {
			UPlayer up = it.next();
			if(up.getPlayerKey().getUuid().equals(uuid)) return up;
		}
		
		return null;
	}
	
	public List<UPlayer> getOnlineUPlayers() {
		List<UPlayer> l = new ArrayList<>();
		
		Iterator<UPlayer> it = players.values().iterator();
		while(it.hasNext()) {
			l.add(it.next());
		}
		
		return l;
	}
	
}