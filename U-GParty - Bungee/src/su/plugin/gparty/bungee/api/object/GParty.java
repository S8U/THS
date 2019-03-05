package su.plugin.gparty.bungee.api.object;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.bungee.api.GCore;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class GParty {
	
	@Getter
	private PlayerKey owner;
	
	@Setter
	@Getter
	private List<PlayerKey> players = new ArrayList<>();
	
	public GParty(PlayerKey owner) {
		this.owner = owner;
	}
	
	public void bc(String msg) {
		if(owner.getUPlayer().getPlatformSender() != null) {
			owner.getUPlayer().msg(msg);
		}

		for(ProxiedPlayer ap : getOnlinePlayers()) {
			Core.msg(ap, msg);
		}
	}

	public void setOwner(PlayerKey playerKey) {
		owner = playerKey;
		players.remove(playerKey);
	}

	public ProxiedPlayer getOwnerPlayer() {
		return owner == null ? null : GCore.getProxiedPlayer(owner);
	}
	
	public boolean addPlayer(ProxiedPlayer p) {
		return addPlayer(PlayerKey.getPlayerKey(p.getName()));
	}
	
	public boolean addPlayer(PlayerKey playerKey) {
		if(players.contains(playerKey)) return false;

		return players.add(playerKey);
	}
	
	public boolean removePlayer(ProxiedPlayer p) {
		return removePlayer(PlayerKey.getPlayerKey(p.getName()));
	}
	
	public boolean removePlayer(PlayerKey playerKey) {
		return players.remove(playerKey);
	}
	
	public boolean hasPlayer(ProxiedPlayer p) {
		return hasPlayer(PlayerKey.getPlayerKey(p.getName()));
	}
	
	public boolean hasPlayer(PlayerKey playerKey) {
		return players.contains(playerKey);
	}
	
	public boolean isOwner(ProxiedPlayer p) {
		return isOwner(PlayerKey.getPlayerKey(p.getName()));
	}
	
	public boolean isOwner(PlayerKey playerKey) {
		return owner.equals(playerKey);
	}
	
	public List<ProxiedPlayer> getOnlinePlayers() {
		List<ProxiedPlayer> players = new ArrayList<>();
		
		for(PlayerKey playerKey : this.players) {
			ProxiedPlayer player = GCore.getProxiedPlayer(playerKey);
			if(player == null) continue;
			
			players.add(player);
		}
		
		return players;
	}
	
}