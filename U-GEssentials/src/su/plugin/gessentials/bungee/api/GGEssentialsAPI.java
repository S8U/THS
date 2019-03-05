package su.plugin.gessentials.bungee.api;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.gessentials.bungee.api.category.ChatHandlingLocation;
import su.plugin.gessentials.bungee.api.manager.BanManager;
import su.plugin.gessentials.bungee.api.manager.ChannelManager;
import su.plugin.gessentials.bungee.api.manager.ChatManager;
import su.plugin.gessentials.bungee.api.manager.ConfigManager;
import su.plugin.gessentials.bungee.api.manager.PlayerManager;
import su.plugin.gessentials.bungee.api.manager.SQLManager;
import su.plugin.gessentials.bungee.api.manager.WarningManager;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class GGEssentialsAPI {
	
	@Setter
	@Getter
	private static String kickServerMark;
	
	@Setter
	@Getter
	private static boolean loadAllBanData, loadAllPlayerData;
	
	@Setter
	@Getter
	private static boolean useGLogin, useGParty, useGFriend, useChannel, useBOptimizeChannel;
	
	@Setter
	@Getter
	private static boolean useLobby, useChatFilter, useWarning;
	
	@Setter
	@Getter
	private static boolean sendToLobbyOnConnect, ignoreFilterWordWithNames;
	
	@Setter
	@Getter
	private static ChatHandlingLocation defaultChatHandlingLocation;
	
	@Getter
	private static ChatManager chatManager;
	@Getter
	private static WarningManager warningManager;
	@Getter
	private static PlayerManager playerManager;
	@Getter
	private static BanManager banManager;
	@Getter
	private static ChannelManager channelManager;
	@Getter
	private static ConfigManager configManager;
	@Getter
	private static su.plugin.gessentials.bungee.api.manager.SQLManager SQLManager;
	
	public void init() {
		chatManager = new ChatManager();
		warningManager = new WarningManager();
		playerManager = new PlayerManager();
		banManager = new BanManager();
		channelManager = new ChannelManager();
		configManager = new ConfigManager();
		SQLManager = new SQLManager();
	}
	
	public void registerPlugins() {
		useGLogin = ProxyServer.getInstance().getPluginManager().getPlugin("U-GLogin") != null;
		if(useGLogin) {
			Core.log("U-GLogin 플러그인과 연동되었습니다.");
		}

		useGParty = ProxyServer.getInstance().getPluginManager().getPlugin("U-GParty") != null;
		if(useGParty) {
			Core.log("U-GParty 플러그인과 연동되었습니다.");
		}

		useGFriend = ProxyServer.getInstance().getPluginManager().getPlugin("U-GFriend") != null;
		if(useGFriend) {
			Core.log("U-GFriend 플러그인과 연동되었습니다.");
		}

		useChannel = ProxyServer.getInstance().getPluginManager().getPlugin("U-Channel") != null;
		if(useChannel) {
			Core.log("U-Channel 플러그인과 연동되었습니다.");
		}
	}
	
	public EPlayer getEPlayer(PlayerKey playerKey) {
		return loadAllPlayerData || playerManager.existsEPlayer(playerKey) ? playerManager.getEPlayer(playerKey) : SQLManager.getEPlayer(playerKey);
	}
	
	public EPlayer getEPlayer(ProxiedPlayer player) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(player.getName());
		if(playerKey == null) return null;
		
		return getEPlayer(playerKey);
	}
	
	public EPlayer getEPlayerByName(String name) {
		return loadAllPlayerData || playerManager.existsEPlayer(name) ? playerManager.getEPlayer(name) : SQLManager.getEPlayerByName(name);
	}
	
}