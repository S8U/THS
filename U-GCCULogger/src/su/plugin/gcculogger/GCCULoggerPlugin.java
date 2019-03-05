package su.plugin.gcculogger;

import lombok.Getter;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.gcculogger.api.GCCULoggerAPI;

public class GCCULoggerPlugin extends UGPlugin {
	
	@Getter
	private static GCCULoggerPlugin instance;
	
	@Getter
	private static GCCULoggerAPI api = new GCCULoggerAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		setPrefix("§7[ U-CCULogger ]");
		setColor(ChatColor.GRAY);
		
		api.init();

		if(!api.getSQLManager().connect(this)) {
			log("MySQL에 연결할 수 없어 비활성화됩니다.");
			return;
		}
		
		api.setMaxCCU(api.getSQLManager().getMaxCCU());
		
		registerListeners();
		registerUEventListeners();
	}
	
	@Override
	public void onUDisable() {
		api.getSQLManager().close();
	}
	
}