package su.plugin.core.common.api.player;

import java.io.Serializable;
import java.util.Iterator;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

@AllArgsConstructor
public class PlayerKey implements Serializable, Comparable<PlayerKey> {

	@Getter
	private final int id;

	private String name;

	private UUID uuid;

	private Boolean onlineMode;

	//

	public String getName() {
		if(name == null) {
			reload();
		}

		return name;
	}

	public UUID getUuid() {
		if(uuid == null) {
			reload();
		}

		return uuid;
	}

	public boolean isOnlineMode() {
		if(onlineMode == null) {
			reload();
		}

		return onlineMode;
	}

	public boolean reload() {
		PlayerKey newPlayerKey = Core.getSQLManager().getPlayerKey(id);
		if(newPlayerKey == null) return false;

		this.name = newPlayerKey.getName();
		this.uuid = newPlayerKey.getUuid();
		this.onlineMode = newPlayerKey.isOnlineMode();

		return true;
	}

	public void updatePlayerKey(String name, UUID uuid, boolean onlineMode) {
		this.name = name;
		this.uuid = uuid;
		this.onlineMode = onlineMode;

		Core.getSQLManager().savePlayerKey(this);
	}

	public static PlayerKey getPlayerKey(int id) {
		Iterator<UPlayer> it = Core.getUPlayerManager().getPlayers().values().iterator();
		while(it.hasNext()) {
			PlayerKey playerKey = it.next().getPlayerKey();
			if(playerKey.getId() == id) return playerKey;
		}

		return Core.getSQLManager().getPlayerKey(id);
	}

	public static PlayerKey getPlayerKey(String name) {
		Iterator<UPlayer> it = Core.getUPlayerManager().getPlayers().values().iterator();
		while(it.hasNext()) {
			PlayerKey playerKey = it.next().getPlayerKey();
			if(playerKey.getName().equalsIgnoreCase(name)) return playerKey;
		}

		return Core.getSQLManager().getPlayerKey(name);
	}

	public static PlayerKey getPlayerKeyByPlatformPlayer(Object platformPlayer) {
		String name = Core.getPlatformPlayerName(platformPlayer);
		if(name == null) return null;

		return PlayerKey.getPlayerKey(name);
	}

	public static PlayerKey getPlayerKeyByDisplayName(String displayName) {
		PlayerKey playerKey = getPlayerKey(displayName);
		if(playerKey != null) return playerKey;

		Iterator<UPlayer> it = Core.getUPlayerManager().getPlayers().values().iterator();
		while(it.hasNext()) {
			UPlayer up = it.next();
			if(ChatColor.stripColor(up.getDisplayName()).equalsIgnoreCase(displayName)) return up.getPlayerKey();
		}

		return Core.getSQLManager().getPlayerKeyByDisplayName(displayName);
	}

	public static PlayerKey getPlayerKey(UUID uuid) {
		Iterator<UPlayer> it = Core.getUPlayerManager().getPlayers().values().iterator();
		while(it.hasNext()) {
			PlayerKey playerKey = it.next().getPlayerKey();
			if(playerKey.getUuid().equals(uuid)) return playerKey;
		}

		return Core.getSQLManager().getPlayerKey(uuid);
	}

	public static PlayerKey getDummy(int id) {
		return new PlayerKey(id, null, null, null);
	}

	public UPlayer getUPlayer() {
		return Core.getUPlayer(id);
	}

	public Object getPlatformPlayer() {
		return Core.getUPlayer(id) == null ? null : Core.getUPlayer(id).getPlatformSender();
	}

	public String getDisplayName() {
		return Core.getDisplayName(this);
	}

	//

	@Override
	public boolean equals(Object obj) {
		return id == ((PlayerKey) obj).getId();
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return id + "";
	}

	@Override
	public int compareTo(PlayerKey obj) {
		return new Integer(id).compareTo(obj.getId());
	}

}