package su.plugin.gessentials.bungee;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.command.AdminChatCommand;
import su.plugin.gessentials.bungee.command.LobbyCommand;
import su.plugin.gessentials.bungee.listener.ChatListener;

public class GGEssentialsPlugin extends UGPlugin {
	
	@Getter
	private static GGEssentialsPlugin instance;
	
	@Getter
	private static GGEssentialsAPI api = new GGEssentialsAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		setPrefix("§7[ U-GEssentials ]");
		setColor(ChatColor.GRAY);
		
		api.init();
		
		if(!api.getSQLManager().connect(this)) {
			log("MySQL에 연결할 수 없어 비활성화됩니다.");
			return;
		}
		
		api.registerPlugins();
		
		registerListeners(new ChatListener().getClass().getPackage().getName());
		registerUEventListeners(new ChatListener().getClass().getPackage().getName());
		
		registerCommands(new AdminChatCommand().getClass().getPackage().getName());
		
		ProxyServer.getInstance().registerChannel("ugessentials:main");

		loadConfig();
		
		if(api.isLoadAllPlayerData()) {
			api.getSQLManager().loadAllEPlayer();
			
			log("모든 플레이어 정보를 불러왔습니다.");
		}
		
		if(api.isLoadAllBanData()) {
			api.getSQLManager().loadAllPlayerKeyBanData();
			api.getSQLManager().loadAllIpBanData();
			
			log("모든 차단 정보를 불러왔습니다.");
		}
	}
	
	@Override
	public void onUDisable() {
		api.getSQLManager().close();
	}

	private LobbyCommand lobbyCommand;

	@Override
	public void onConfigLoad(UCommandSender sender) {
		api.getConfigManager().loadConfig();
		api.getConfigManager().loadChannelConfig();
		api.getConfigManager().loadChatFilterConfig();
		api.getConfigManager().loadOption();

		Core.getOptionSQLManager().setServerOption("gessentials_banword", api.getChatManager().getBanWords());

		if(api.isUseLobby() && lobbyCommand == null) {
			lobbyCommand = new LobbyCommand();
			registerCommands(lobbyCommand);
		} else if(!api.isUseLobby() && lobbyCommand != null) {
			// Unregister Lobby Command
		}

		if(api.isUseWarning()) {
			api.getWarningManager().startWarningInitTask();
		}
	}
}