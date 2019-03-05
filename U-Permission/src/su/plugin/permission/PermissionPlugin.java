package su.plugin.permission;

import org.bukkit.permissions.PermissionAttachment;

import lombok.Getter;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.permission.api.PermissionAPI;

public class PermissionPlugin extends UKPlugin {
	
	@Getter
	private static PermissionPlugin instance;
	
	@Getter
	private static PermissionAPI api = new PermissionAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		setPrefix("§7[ U-Permission ]");
		setColor(ChatColor.GRAY);
		
		api.init();
		
		if(!api.getSQLManager().connect(this)) {
			log("MySQL에 연결할 수 없어 비활성화됩니다.");
			disable();
			
			return;
		}
		
		registerListeners();
		registerCommands();
		
		registerPermissions();
		
		api.registerPlugins();
		
		loadConfig();
		
		if(!api.isUseBungeecord() || KCore.getOnlinePlayers().size() > 0) {
			api.getSQLManager().loadConfig();
			api.getSQLManager().loadAllGroup();
		}
		
		api.getPlayerManager().registerAllPlayer();
	}
	
	@Override
	public void onUDisable() {
		api.getSQLManager().close();
		
		for(PermissionAttachment attech : api.getAttachmentManager().getPermissionAttachments().values()) {
			attech.remove();
		}
	}
	
	public void loadConfig() {
		getJsonConfig().addDefault("번지코드 채널 연동", false);
		
		getJsonConfig().save();
		
		api.setUseBungeecord(getJsonConfig().getBoolean("번지코드 채널 연동"));
		
		log("설정을 불러왔습니다.");
	}
	
}