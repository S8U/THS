package su.plugin.gfriend.api.manager;

import java.util.HashMap;

import su.plugin.gfriend.api.object.FriendPlayer;
import su.plugin.core.common.api.player.PlayerKey;

public class PlayerManager {
	
	private HashMap<PlayerKey, FriendPlayer> friendPlayers = new HashMap<>();
	
	public void setFriendPlayer(PlayerKey playerKey, FriendPlayer fp) {
		friendPlayers.put(playerKey, fp);
	}
	
	public void removeFriendPlayer(PlayerKey playerKey) {
		friendPlayers.remove(playerKey);
	}
	
	public FriendPlayer getFriendPlayer(PlayerKey playerKey) {
		return friendPlayers.get(playerKey);
	}
	
	public boolean existsFriendPlayer(PlayerKey playerKey) {
		return friendPlayers.containsKey(playerKey);
	}
	
}