package su.plugin.gfriend.api.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.Cleanup;
import lombok.SneakyThrows;
import su.plugin.gfriend.api.object.FriendPlayer;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;

public class SQLManager extends SQLManagerBase {

	private SQLTable friendTable, requestTable;

	@Override
	public void createTable() {
		friendTable = new SQLTable(this, "Friend", "player_id int, friend_id int, primary key(player_id, friend_id)").createTable();
		requestTable = new SQLTable(this, "Request", "requester_id int, target_id int, primary key(requester_id, target_id)").createTable();
	}
	
	public void loadPlayer(FriendPlayer fp) {
		fp.setFriends(getFriends(fp.getPlayerKey()));
		fp.setRequests(getRequests(fp.getPlayerKey()));
	}
	
	public void addFriend(PlayerKey playerKey, PlayerKey friendKey) {
		friendTable.insert(playerKey, friendKey);
	}
	
	public void removeFriend(PlayerKey playerKey, PlayerKey friendKey) {
		friendTable.delete("where player_id = " + playerKey + " and friend_id = " + friendKey);
	}

	@SneakyThrows(SQLException.class)
	public boolean isFriend(PlayerKey playerKey, PlayerKey friendKey) {
		@Cleanup PreparedStatement state = friendTable.select("player_id", "where player_id = " + playerKey + " and friend_id = " + friendKey);
		@Cleanup ResultSet result = state.executeQuery();

		return result.next();
	}

	@SneakyThrows(SQLException.class)
	public List<PlayerKey> getFriends(PlayerKey playerKey) {
		List<PlayerKey> friends = new NotDuplicatedArrayList<>();

		@Cleanup PreparedStatement state = friendTable.select("friend_id", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		while(result.next()) {
			friends.add(PlayerKey.getPlayerKey(result.getInt("friend_id")));
		}

		return friends;
	}

	public void addRequest(PlayerKey targetKey, PlayerKey requesterKey) {
		requestTable.insert(requesterKey, targetKey);
	}
	
	public void removeRequest(PlayerKey targetKey, PlayerKey requesterKey) {
		requestTable.delete("where requester_id = " + requesterKey + " and target_id = " + targetKey);
	}

	@SneakyThrows(SQLException.class)
	public boolean hasRequest(PlayerKey targetKey, PlayerKey requesterKey) {
		@Cleanup PreparedStatement state = requestTable.select("target_id", "where requester_id = " + requesterKey + " and target_id = " + targetKey);
		@Cleanup ResultSet result = state.executeQuery();

		return result.next();
	}

	@SneakyThrows(SQLException.class)
	public List<PlayerKey> getRequests(PlayerKey playerKey) {
		List<PlayerKey> requests = new NotDuplicatedArrayList<>();

		@Cleanup PreparedStatement state = requestTable.select("requester_id", "where target_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();

		while(result.next()) {
			requests.add(PlayerKey.getPlayerKey(result.getInt("requester_id")));
		}

		return requests;
	}

}