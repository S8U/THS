package su.plugin.ability.api.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.common.api.object.Party;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class PlayerManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private HashMap<PlayerKey, GamePlayer> players = new HashMap<>();
	
	public void setGamePlayer(Player p, GamePlayer gp) {
		setGamePlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p), gp);
	}
	
	public void setGamePlayer(PlayerKey playerKey, GamePlayer gp) {
		players.put(playerKey, gp);
	}
	
	public boolean existGamePlayer(Player p) {
		return existGamePlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public boolean existGamePlayer(PlayerKey playerKey) {
		return players.containsKey(playerKey);
	}
	
	public GamePlayer getGamePlayer(Player p) {
		return getGamePlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public GamePlayer getGamePlayer(PlayerKey playerKey) {
		return players.get(playerKey);
	}
	
	public void removeGamePlayer(Player p) {
		removeGamePlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public void removeGamePlayer(PlayerKey playerKey) {
		players.remove(playerKey);
	}
	
	public List<GamePlayer> getAllPlayers() {
		return new ArrayList<>(players.values());
	}
	
	public List<GamePlayer> getOnlineJoinedPlayers() {
		List<GamePlayer> list = new ArrayList<>();
		for(GamePlayer gp : players.values()) {
			if(!gp.isOnline() || gp.isEliminate() || gp.isWatchMode()) continue;
			list.add(gp);
		}
		return list;
	}
	
	public List<GamePlayer> getJoinedPlayers() {
		List<GamePlayer> list = new ArrayList<>();
		for(GamePlayer gp : players.values()) {
			if(gp.isEliminate() || gp.isWatchMode()) continue;
			list.add(gp);
		}
		return list;
	}
	
	public List<GamePlayer> getEliminatedPlayers() {
		List<GamePlayer> list = new ArrayList<>();
		for(GamePlayer gp : players.values()) {
			if(!gp.isEliminate()) continue;
			list.add(gp);
		}
		return list;
	}
	
	public List<GamePlayer> getOnlineWatchPlayers() {
		List<GamePlayer> list = new ArrayList<>();
		for(GamePlayer gp : players.values()) {
			if(!gp.isOnline() || !gp.isWatchMode()) continue;
			list.add(gp);
		}
		return list;
	}
	
	public List<GamePlayer> getWatchPlayers() {
		List<GamePlayer> list = new ArrayList<>();
		for(GamePlayer gp : players.values()) {
			if(!gp.isWatchMode()) continue;
			list.add(gp);
		}
		return list;
	}
	
	public List<GamePlayer> getOnlinePlayers() {
		List<GamePlayer> list = new ArrayList<>();
		for(GamePlayer gp : players.values()) {
			if(!gp.isOnline()) continue;
			list.add(gp);
		}
		return list;
	}
	
	public int getTeamAmount() {
		if(api.isUseGParty()) {
		  int individual = 0;
      List<Party> parties = new NotDuplicatedArrayList<>();

			for(GamePlayer gp : getOnlineJoinedPlayers()) {
				PartyPlayer pp = KGPartyAPI.getPlayerManager().getPartyPlayers().get(gp.getPlayerKey());
				if(pp.hasParty()) {
          parties.add(pp.getParty());
        } else {
          individual++;
        }
			}
			
			return parties.size() + individual;
		} else {
			return getOnlineJoinedPlayers().size();
		}
	}
	
}