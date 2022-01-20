package su.plugin.channel.bungee;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.channel.bungee.api.GChannelAPI;
import su.plugin.channel.bungee.listener.MessageListener;
import su.plugin.channel.common.command.ChannelCommand;
import su.plugin.channel.common.command.ChannelGroupCommand;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;

public class GChannelPlugin extends UGPlugin {
	
	@Getter
	private static GChannelPlugin instance;
	
	@Getter
	private static GChannelAPI api = new GChannelAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		api.init();
		setPrefix("§7[ U-Channel ]");
		setColor(ChatColor.GRAY);
		
		setPluginPackage(getClass().getPackage().getName().substring(0, getClass().getPackage().getName().lastIndexOf(".")));
		
		if(!api.getSQLManager().connect(this)) {
			wlog("MySQL에 연결할 수 없어 비활성화됩니다.");
			return;
		}
		
		registerCommands(new ChannelCommand());
		registerCommands(new ChannelGroupCommand());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new MessageListener());
		
		ProxyServer.getInstance().registerChannel("uchannel:main");

		loadConfig();
	}
	
	@Override
	public void onUDisable() {
		api.getSQLManager().close();

		api.stopChannelLoadTask();
	}
	
	@Override
	public void onConfigLoad(UCommandSender sender) {
		getJsonConfig().addDefault("공지 접두사", "&6&l[ 공지 ] &f");
		getJsonConfig().addDefault("오프라인 확인.사용", true);
		getJsonConfig().addDefault("오프라인 확인.간격(s)", 10);
		getJsonConfig().addDefault("오프라인 확인.타임아웃", 1500);
		getJsonConfig().save();

		api.setBroadCastPrefix(ChatColor.translateAlternateColorCodes('&', getJsonConfig().getString("공지 접두사")));
		api.setUseOfflineCheck(getJsonConfig().getBoolean("오프라인 확인.사용"));
		api.setOfflineCheckInterval(getJsonConfig().getInt("오프라인 확인.간격(s)"));
		api.setOfflineCheckTimeout(getJsonConfig().getInt("오프라인 확인.타임아웃"));

		api.getConfigManager().loadChannelConfig();
		api.getConfigManager().loadGroupConfig();
	}

	@Override
	public void onConfigLoaded(UCommandSender sender) {
		api.stopChannelLoadTask();
		api.stopOfflineCheckTask();

		if(api.getSQLManager().isLoad()) {
			api.startChannelLoadTask();
			api.getSQLManager().loadAllChannel();
		}

		if(api.getSQLManager().isUpload()) {
			api.getSQLManager().saveAllChannelGroup();
			api.getSQLManager().saveAllChannel();
		}

		if(api.isUseOfflineCheck()) {
			api.startOfflineCheckTask();
		}
	}
}