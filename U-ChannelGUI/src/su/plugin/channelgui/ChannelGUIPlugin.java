package su.plugin.channelgui;

import java.util.Arrays;
import lombok.Getter;
import su.plugin.channelgui.api.ChannelGUIAPI;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.util.NumberUtil;

public class ChannelGUIPlugin extends UKPlugin {
	
	@Getter
	private static ChannelGUIPlugin instance;
	@Getter
	private static ChannelGUIAPI api = new ChannelGUIAPI();
	
	public void onUEnable() {
		instance = this;
		setPrefix("§e[ U-ChannelGUI ]");
		setColor(ChatColor.YELLOW);
		api.init();
		
		onConfigLoad();
		
		api.getConfigManager().createFolder();
		api.getConfigManager().loadGUIs();
		
		registerListeners();
		registerCommands();
		registerPermissions();
	}
	
	public void onConfigLoad() {
		getJsonConfig().addDefault("상태 메시지.온라인", "&a온라인");
		getJsonConfig().addDefault("상태 메시지.오프라인", "&c오프라인");
		getJsonConfig().addDefault("오프라인 아이템 코드", "351:8");

		getJsonConfig().addDefault("형식.U-Ability.아이템 코드.온라인", Arrays.asList("0 351:13", "1 351:10", "2 351:9", "3 351:13", "4 351:13", "5 351:13", "6 351:13"));
		getJsonConfig().addDefault("형식.U-Ability.아이템 코드.오프라인", "351:8");
		getJsonConfig().addDefault("형식.U-Ability.상태 메시지.온라인", Arrays.asList("0 &e상태: &7채널 준비중", "1 &e상태: &b대기 중", "2 &e상태: &6시작 준비 중", "3 &e상태: &6능력 추첨 중", "4 &e상태: &c게임 중", "5 &e상태: &c게임 중", "6 &e상태: &7게임을 끝내는 중"));
		getJsonConfig().addDefault("형식.U-Ability.상태 메시지.오프라인", "&e상태: &7오프라인");

		getJsonConfig().save();

		api.setOnlineMessage(ChatColor.translateAlternateColorCodes('&', getJsonConfig().getString("상태 메시지.온라인")));
		api.setOfflineMessage(ChatColor.translateAlternateColorCodes('&', getJsonConfig().getString("상태 메시지.오프라인")));
		api.setOfflineItemCode(getJsonConfig().getString("오프라인 아이템 코드"));

		for(String line : getJsonConfig().getStringList("형식.U-Ability.아이템 코드.온라인")) {
			api.getUabilityOnlineItemCode().put(NumberUtil.getInteger(line.substring(0, line.indexOf(" "))), line.substring(line.indexOf(" ") + 1, line.length()));
		}
		api.setUabilityOfflineItemCode(getJsonConfig().getString("형식.U-Ability.아이템 코드.오프라인"));

		for(String line : getJsonConfig().getStringList("형식.U-Ability.상태 메시지.온라인")) {
			api.getUabilityOnlineMessage().put(NumberUtil.getInteger(line.substring(0, line.indexOf(" "))), ChatColor.translateAlternateColorCodes('&', line.substring(line.indexOf(" ") + 1, line.length())));
		}
		api.setUabilityOfflineMessage(ChatColor.translateAlternateColorCodes('&', getJsonConfig().getString("형식.U-Ability.상태 메시지.오프라인")));

		log("설정을 불러왔습니다.");
	}
	
}
