package su.plugin.gparty.bungee;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.gparty.bungee.api.GGPartyAPI;
import su.plugin.gparty.bungee.command.AdminCommand;
import su.plugin.gparty.bungee.listener.ChatListener;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.common.api.ChatColor;

public class GGPartyPlugin extends UGPlugin {
	
	@Getter
	private static GGPartyPlugin instance;
	@Getter
	private static GGPartyAPI api = new GGPartyAPI();
	
	public void onUEnable() {
		instance = this;
		setPrefix("§a[ U-GParty ]");
		setColor(ChatColor.GREEN);
		api.init();
		
		loadConfig();
		
		registerCommands(new AdminCommand().getClass().getPackage().getName());
		registerListeners(new ChatListener().getClass().getPackage().getName());
		registerUEventListeners(new ChatListener().getClass().getPackage().getName());

		ProxyServer.getInstance().registerChannel("U-GParty");
	}
	
	public void loadConfig() {
		getJsonConfig().addDefault("최대 파티 인원", 3);
		getJsonConfig().save();
		
		api.setMaxPartyCount(getJsonConfig().getInt("최대 파티 인원"));
	}
	
}