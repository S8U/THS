package su.plugin.gfriend.api;

import lombok.Getter;
import lombok.Setter;
import su.plugin.gfriend.api.manager.PlayerManager;
import su.plugin.gfriend.api.manager.SQLManager;

public class GFriendAPI {

	@Setter
	@Getter
	private static boolean useGLogin, useChannel;

	@Getter
	private static  PlayerManager playerManager;
	@Getter
	private static  SQLManager SQLManager;
	
	public void init() {
		playerManager = new PlayerManager();
		SQLManager = new SQLManager();
	}
	
}