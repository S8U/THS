package su.plugin.permission.api;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.Core;
import su.plugin.permission.api.manager.AttachmentManager;
import su.plugin.permission.api.manager.GroupManager;
import su.plugin.permission.api.manager.PlayerManager;
import su.plugin.permission.api.manager.SQLManager;
import su.plugin.permission.vault.VaultPermissionHandler;

public class PermissionAPI {
	
	@Setter
	@Getter
	private static boolean useBungeecord, usePrefixer;
	
	@Getter
	private static AttachmentManager attachmentManager;
	@Getter
	private static GroupManager groupManager;
	@Getter
	private static PlayerManager playerManager;
	@Getter
	private static SQLManager SQLManager;
	
	public void init() {
		attachmentManager = new AttachmentManager();
		groupManager = new GroupManager();
		playerManager = new PlayerManager();
		SQLManager = new SQLManager();
	}
	
	public void registerPlugins() {
		if(usePrefixer = PluginUtil.existsPlugin("U-Prefixer")) {
			Core.log("U-Prefixer 플러그인과 연동되었습니다.");
		}

		if(KCore.isUseVault()) {
			VaultPermissionHandler.register();

			Core.log("Vault 플러그인과 연동되었습니다.");
		}
	}
	
}