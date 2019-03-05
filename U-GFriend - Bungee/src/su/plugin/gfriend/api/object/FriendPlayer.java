package su.plugin.gfriend.api.object;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.bungee.api.GCore;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;

@Setter
@Getter
@RequiredArgsConstructor
public class FriendPlayer {
	
	private final PlayerKey playerKey;
	
	private List<PlayerKey> friends = new NotDuplicatedArrayList<>();
	private List<PlayerKey> requests = new NotDuplicatedArrayList<>();
	
	public ProxiedPlayer getProxiedPlayer() {
		return GCore.getProxiedPlayer(playerKey);
	}
	
	public boolean isOnline() {
		return getProxiedPlayer() != null;
	}
	
	public boolean addFriend(FriendPlayer fp) {
		return addFriend(fp.getPlayerKey());
	}
	
	public boolean addFriend(PlayerKey playerKey) {
		return friends.add(playerKey);
	}
	
	public boolean removeFriend(FriendPlayer fp) {
		return removeFriend(fp.getPlayerKey());
	}
	
	public boolean removeFriend(PlayerKey playerKey) {
		return friends.remove(playerKey);
	}
	
	public boolean isFriend(FriendPlayer fp) {
		return isFriend(fp.getPlayerKey());
	}
	
	public boolean isFriend(PlayerKey playerKey) {
		return friends.contains(playerKey);
	}
	
	public boolean addRequest(FriendPlayer fp) {
		return addRequest(fp.getPlayerKey());
	}
	
	public boolean addRequest(PlayerKey playerKey) {
		return requests.add(playerKey);
	}
	
	public boolean removeRequest(FriendPlayer fp) {
		return removeRequest(fp.getPlayerKey());
	}
	
	public boolean removeRequest(PlayerKey playerKey) {
		return requests.remove(playerKey);
	}
	
	public boolean hasRequestFrom(FriendPlayer fp) {
		return hasRequestFrom(fp.getPlayerKey());
	}
	
	public boolean hasRequestFrom(PlayerKey playerKey) {
		return requests.contains(playerKey);
	}
	
	public List<ProxiedPlayer> getOnlineFriends() {
		List<ProxiedPlayer> list = new ArrayList<>();

		for(PlayerKey friendKey : friends) {
			ProxiedPlayer p = GCore.getProxiedPlayer(friendKey);
			if(p == null) continue;

			list.add(p);
		}

		return list;
	}
	
	public List<String> getOfflineFriends() {
		List<String> list = new ArrayList<>();

		for(PlayerKey friendKey : friends) {
			ProxiedPlayer p = GCore.getProxiedPlayer(friendKey);
			if(p != null) continue;

			list.add(friendKey.getName());
		}

		return list;
	}
	
	public String getDisplayName() {
		return playerKey.getUPlayer().getDisplayName();
	}
	
}