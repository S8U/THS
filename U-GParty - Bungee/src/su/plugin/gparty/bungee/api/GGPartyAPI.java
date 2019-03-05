package su.plugin.gparty.bungee.api;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.gparty.bungee.api.manager.GPartyManager;
import su.plugin.gparty.bungee.api.manager.GPlayerManager;
import su.plugin.core.bungee.api.util.PluginUtil;
import su.plugin.core.common.api.Core;

public class GGPartyAPI {
	
	@Getter
	@Setter
	private static int maxPartyCount;

	@Setter
	@Getter
	private static boolean useChannel, useGEssentials, useGLogin, useGFriend;
	
	@Getter
	private static GPlayerManager playerManager;
	@Getter
	private static GPartyManager partyManager;
	
	public void init() {
		playerManager = new GPlayerManager();
		partyManager = new GPartyManager();

		if(PluginUtil.existsPlugin("U-Channel")) {
			useChannel = true;
			Core.log("U-Channel 플러그인과 연동되었습니다.");
		}
		if(PluginUtil.existsPlugin("U-GEssentials")) {
			useGEssentials = true;
			Core.log("U-GEssentials 플러그인과 연동되었습니다.");
		}
		if(PluginUtil.existsPlugin("U-GLogin")) {
			useGLogin = true;
			Core.log("U-GLogin 플러그인과 연동되었습니다.");
		}
		if(PluginUtil.existsPlugin("U-GFriend")) {
			useGFriend = true;
			Core.log("U-GFriend 플러그인과 연동되었습니다.");
		}
	}
	
	public static String buildPlayerList(List<ProxiedPlayer> players) {
		if(players.size() < 1) return "없음";
		
		List<String> list = new ArrayList<>();
		players.forEach(p -> list.add(p.getName()));
		
		return String.join(", ", list);
	}
	
}