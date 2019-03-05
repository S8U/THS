package su.plugin.itemtools;

import lombok.Getter;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.itemtools.api.ItemToolsAPI;

public class ItemToolsPlugin extends UKPlugin {
	
	@Getter
	private static ItemToolsPlugin instance;
	@Getter
	private static ItemToolsAPI api = new ItemToolsAPI();
	
	public void onUEnable() {
		instance = this;
		
		setPrefix(api.getPluginPrefix());
		
		registerListeners();
		registerCommands();
		
		registerPermissions();
		
		api.makeItem();
	}
	
}