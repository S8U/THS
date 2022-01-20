package su.plugin.gessentials.bukkit.api;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;
import su.plugin.gessentials.bukkit.KGEssentialsPlugin;
import su.plugin.gessentials.bukkit.api.manager.ChatManager;
import su.plugin.gessentials.bukkit.listener.PermissionListener;
import su.plugin.gessentials.bukkit.listener.PrefixerListener;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class KGEssentialsAPI {
	
	@Setter
	@Getter
	private static boolean sendChat;
	
	@Getter
	private static boolean usePermission, usePrefixer;
	
	@Getter
	private static Set<PlayerKey> moveSpys = new HashSet<>();

	@Getter
	private static ChatManager chatManager;
	
	public void init() {
		chatManager = new ChatManager();
		
		if(usePermission = PluginUtil.existsPlugin("U-Permission")) {
			Bukkit.getPluginManager().registerEvents(new PermissionListener(), KGEssentialsPlugin.getInstance());
			Core.log("U-Permission 플러그인과 연동되었습니다.");
		}
		
		if(usePrefixer = PluginUtil.existsPlugin("U-Prefixer")) {
			Bukkit.getPluginManager().registerEvents(new PrefixerListener(), KGEssentialsPlugin.getInstance());
			Core.log("U-Prefixer 플러그인과 연동되었습니다.");
		}
	}
	
	public boolean isMoveSpy(PlayerKey playerKey) {
		return Core.getOptionManager().existsPlayerOption(playerKey, "gessentials_move_spy");
	}
	
}