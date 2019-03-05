package su.plugin.worldreset.api;

import java.io.File;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.util.FileUtil;
import su.plugin.worldreset.WorldResetPlugin;

public class WorldResetAPI {
	
	@Setter
	@Getter
	private static boolean deletePlayerData;
	
	@Getter
	private static File backupFolder;
	
	public WorldResetAPI() {
		backupFolder = new File(WorldResetPlugin.getInstance().getDataFolder(), "backup");
	}
	
	public static void makeBackupFolder() {
		if(backupFolder.exists()) return;
		
		backupFolder.mkdirs();
	}
	
	public static void copyWorlds() {
		for(File worldFolder : backupFolder.listFiles()) {
			if(!worldFolder.isDirectory()) continue;
			
			deleteWorld(new File(Bukkit.getWorldContainer(), worldFolder.getName()), deletePlayerData);
			
			FileUtil.copy(worldFolder, Bukkit.getWorldContainer());
		}
	}
	
	public static void deleteWorld(File folder, boolean deletePlayerData) {
		for(File files : folder.listFiles()) {
			if(files.isDirectory() && files.getName().equals("playerdata") && !deletePlayerData) continue;
			
			FileUtil.delete(files);
		}
		
		if(folder.listFiles().length < 1) {
			folder.delete();
		}
	}
	
}