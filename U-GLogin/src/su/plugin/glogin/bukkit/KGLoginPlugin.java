package su.plugin.glogin.bukkit;

import org.bukkit.Bukkit;

import lombok.Getter;
import su.plugin.glogin.bukkit.api.KGLoginAPI;
import su.plugin.glogin.bukkit.listener.ControlListener;
import su.plugin.glogin.bukkit.listener.MessageListener;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;

public class KGLoginPlugin extends UKPlugin {
	
	@Getter
	private static KGLoginPlugin instance;
	
	@Getter
	private static KGLoginAPI api = new KGLoginAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		setPrefix("§e[ U-GLogin ]");
		setColor(ChatColor.YELLOW);
		
		api.init();
		
		if(!api.getSQLManager().connect(this)) {
			log("MySQL에 연결할 수 없어 비활성화됩니다.");
			disable();
			return;
		}
		
		registerListeners(new ControlListener().getClass().getPackage().getName());
		registerUEventListeners(new ControlListener().getClass().getPackage().getName());
		
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "U-GLogin", new MessageListener());
	}
	
}