package su.plugin.gbroadcaster;

import java.util.Arrays;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.gbroadcaster.api.GBroadcasterAPI;
import su.plugin.gbroadcaster.api.object.BroadcastData;

public class GBroadcasterPlugin extends UGPlugin {
	
	@Getter
	private static GBroadcasterPlugin instance;
	@Getter
	private static GBroadcasterAPI api = new GBroadcasterAPI();
	
	public void onUEnable() {
		instance = this;
		setPrefix("§e[ U-GBroadcaster ]");
		setColor(ChatColor.YELLOW);
		api.init();
		
		registerCommands();

		onConfigLoad();

		api.getBroadcastManager().startAllTasks();
	}
	
	public void onUDisable() {
		api.getBroadcastManager().stopAllTasks();
	}

	public void onConfigLoad() {
		api.getBroadcastManager().getBroadCastDatas().clear();

		getJsonConfig().addDefault("접두사", "&6&l[공지] &f");
		api.setPrefix(ChatColor.translateAlternateColorCodes('&', getJsonConfig().getString("접두사")));

		loadChannelConfig("전체");
		ProxyServer.getInstance().getServers().keySet().forEach(channel -> loadChannelConfig(channel));

		getJsonConfig().save();

		Core.log("설정을 불러왔습니다.");
	}

	public void loadChannelConfig(String channel) {
		getJsonConfig().addDefault(channel + ".사용", false);
		getJsonConfig().addDefault(channel + ".간격(s)", 30);
		getJsonConfig().addDefault(channel + ".랜덤", false);
		getJsonConfig().addDefault(channel + ".메시지", Arrays.asList(channel.equals("전체") ? "서버에 오신 것을 환영합니다." : "현재 " + channel + "에 접속 중입니다."));

		BroadcastData data = new BroadcastData(channel);
		data.setUse(getJsonConfig().getBoolean(channel + ".사용"));
		data.setInterval(getJsonConfig().getInt(channel + ".간격(s)"));
		data.setRandom(getJsonConfig().getBoolean(channel + ".랜덤"));
		data.setMessages(StringUtil.translateAlternateColorCodes(getJsonConfig().getStringList(channel + ".메시지")));

		api.getBroadcastManager().setBroadCastData(channel, data);
	}
	
}