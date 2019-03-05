package su.plugin.lobbysystem.api.task;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.lobbysystem.LobbySystemPlugin;

public class TimeLockTask extends UKRunnable {
	
	public TimeLockTask(Plugin plugin) {
		super(plugin);
	}

	public void run() {
		for(World world : Bukkit.getWorlds()) {
			world.setTime(LobbySystemPlugin.getApi().getLockTime());
		}
	}
	
}