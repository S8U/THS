package su.plugin.worldreset;

import lombok.Getter;
import su.plugin.core.bukkit.plugin.UBukkitPlugin;
import su.plugin.worldreset.api.WorldResetAPI;

public class WorldResetPlugin extends UBukkitPlugin {
	
	@Getter
	private static WorldResetPlugin instance;
	
	@Getter
	private static WorldResetAPI api;
	
	@Override
	public void onLoad() {
		instance = this;
		api = new WorldResetAPI();
		
		api.makeBackupFolder();
		api.copyWorlds();
	}
	
	@Override
	public void onUEnable() {
		
	}
	
}