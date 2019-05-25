package su.plugin.channel.bukkit;

import java.util.Arrays;

import org.bukkit.Bukkit;

import lombok.Getter;
import su.plugin.channel.bukkit.api.KChannelAPI;
import su.plugin.channel.bukkit.listener.UpdateListener;
import su.plugin.channel.common.PermissionList;
import su.plugin.channel.common.command.ChannelCommand;
import su.plugin.channel.common.command.ChannelGroupCommand;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;

public class KChannelPlugin extends UKPlugin {
	
	@Getter
	private static KChannelPlugin instance;
	
	@Getter
	private static KChannelAPI api = new KChannelAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		api.init();
		setPrefix("§7[ U-Channel ]");
		setColor(ChatColor.GRAY);
		
		setPluginPackage(getClass().getPackage().getName().substring(0, getClass().getPackage().getName().lastIndexOf(".")));
		
		if(!api.getSQLManager().connect(this)) {
			wlog("MySQL에 연결할 수 없어 비활성화됩니다.");
			disable();
			return;
		}
		
		loadConfig();
		
		registerCommands(new ChannelCommand());
		registerCommands(new ChannelGroupCommand());
		
		registerPermissions(PermissionList.class.getPackage().getName());
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "U-Channel");
	}
	
	@Override
	public void onUDisable() {
		api.stopChannelLoadTask();
		
		if(api.getSQLManager().isUpload()) {
			api.updateThisChannelInfo(false, 0, 0, Arrays.asList());
		}
		
		api.getSQLManager().close();
	}
	
	@Override
	public void onConfigLoad(UCommandSender sender) {
		getJsonConfig().addDefault("공지 접두사", "&6&l[ 공지 ] &f");
		getJsonConfig().addDefault("번지코드 채널 이름", "channel");
		getJsonConfig().save();

		api.setBroadCastPrefix(ChatColor.translateAlternateColorCodes('&', getJsonConfig().getString("공지 접두사")));
		api.setChannelName(getJsonConfig().getString("번지코드 채널 이름"));
	}

	private UpdateListener updateListener;

	@Override
	public void onConfigLoaded(UCommandSender sender) {
		api.stopChannelLoadTask();

		if(api.getSQLManager().isLoad()) {
			if(KCore.getOnlinePlayers().size() > 0) {
				api.startChannelLoadTask();
			}
			api.getSQLManager().loadAllChannel();
			api.getSQLManager().loadAllChannelGroup();
		}

		if(api.getSQLManager().isUpload()) {
			if(updateListener == null) {
				Bukkit.getPluginManager().registerEvents(updateListener = new UpdateListener(), this);
			}
			api.updateThisChannelInfo(true, KCore.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), KCore.getOnlinePlayers());
		}
	}

}