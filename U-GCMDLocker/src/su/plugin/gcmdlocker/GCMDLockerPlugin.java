package su.plugin.gcmdlocker;

import java.util.Arrays;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.gcmdlocker.api.GCMDLockerAPI;
import su.plugin.gcmdlocker.listener.GLoginListener;
import su.plugin.gcmdlocker.listener.PlayerListener;

public class GCMDLockerPlugin extends UGPlugin {
	
	@Getter
	private static GCMDLockerPlugin instance;
	@Getter
	private static GCMDLockerAPI api = new GCMDLockerAPI();
	
	public void onUEnable() {
		instance = this;
		setPrefix("§7[ U-GCMDLocker ]");
		api.init();
		
		onConfigLoad();

		api.getSQLManager().connect(this);

		if(getProxy().getPluginManager().getPlugin("U-GLogin") != null) {
			api.setUseGLogin(true);
			getProxy().getPluginManager().registerListener(this, new GLoginListener());
			log("U-GLogin 플러그인과 연동되었습니다.");
		}

		ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener());
		if(api.isUseGLogin()) {
			ProxyServer.getInstance().getPluginManager().registerListener(this, new GLoginListener());
		}
		registerCommands();
	}

	public void onUDisable() {
		api.getSQLManager().close();
	}
	
	public void onConfigLoad() {
		getJsonConfig().addDefault("알림 사용", true);
		getJsonConfig().addDefault("로그인 유지", true);
		getJsonConfig().addDefault("비밀번호", "password123!@#");
		getJsonConfig().addDefault("금지 명령어", Arrays.asList("end", "server", "greload"));

		getJsonConfig().save();

		api.setUseNotify(getJsonConfig().getBoolean("알림 사용"));
		api.setUseKeepLogin(getJsonConfig().getBoolean("로그인 유지"));
		api.setPassword(getJsonConfig().getString("비밀번호"));
		api.setBlacklistedCommand(getJsonConfig().getStringList("금지 명령어"));
	}

}