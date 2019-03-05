package su.plugin.gfriend;

import lombok.Getter;
import net.md_5.bungee.BungeeCord;
import su.plugin.gfriend.api.GFriendAPI;
import su.plugin.gfriend.listener.LoginListener;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.bungee.api.util.PluginUtil;
import su.plugin.core.common.api.ChatColor;

public class GFriendPlugin extends UGPlugin {
	
	@Getter
	private static GFriendPlugin instance;
	@Getter
	private static GFriendAPI api = new GFriendAPI();

	public void onUEnable() {
		instance = this;
		api.init();
		
		setPrefix("§a[ U-GFriend ]");
		setColor(ChatColor.GREEN);

		api.getSQLManager().connect(this);

		registerPlugins();

		registerUEventListeners();
		if(api.isUseGLogin()) {
			BungeeCord.getInstance().getPluginManager().registerListener(this, new LoginListener());
		}
		registerCommands();
	}

	public void onUDisable() {
		api.getSQLManager().close();
	}
	
	public void registerPlugins() {
		if(PluginUtil.existsPlugin("U-GLogin")) {
			api.setUseGLogin(true);
			log("U-GLogin 플러그인과 연동되었습니다.");
		}

		if(PluginUtil.existsPlugin("U-Channel")) {
			api.setUseChannel(true);
			log("U-Channel 플러그인과 연동되었습니다.");
		}
	}
	
}