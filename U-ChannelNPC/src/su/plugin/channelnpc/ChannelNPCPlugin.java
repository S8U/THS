package su.plugin.channelnpc;

import java.util.Arrays;

import org.bukkit.Bukkit;

import lombok.Getter;
import su.plugin.channelnpc.api.ChannelNPCAPI;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.util.StringUtil;

public class ChannelNPCPlugin extends UKPlugin {
	
	@Getter
	private static ChannelNPCPlugin instance;
	
	@Getter
	private static ChannelNPCAPI api = new ChannelNPCAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		setPrefix("§e[ U-ChannelNPC ]");
		setColor(ChatColor.YELLOW);
		api.init();
		
		registerListeners();
		registerCommands();
		registerPermissions();
		
		loadConfig();
		Bukkit.getScheduler().runTaskLater(this, () -> api.getConfigManager().loadNPC(), 2);
	}
	
	public void loadConfig() {
		getJsonConfig().addDefault("NPC 기본 텍스트", Arrays.asList("<npc_name>", "<player_count>&e명 플레이 중"));
		getJsonConfig().save();
		
		api.setNPCTexts(StringUtil.translateAlternateColorCodes(getJsonConfig().getStringList("NPC 기본 텍스트")));
		
		log("설정을 불러왔습니다.");
	}
	
}