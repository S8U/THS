package su.plugin.core.bukkit.api.task;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;

public class PluginMessageTask extends UKRunnable {
	
	private final Plugin plugin;
	private final Player player;
	private final String channel;
	private final byte[] bytes;
	
	public PluginMessageTask(Plugin plugin, Player player, String channel, byte... bytes) {
		super(plugin);
		
		this.plugin = plugin;
		this.player = player;
		this.channel = channel;
		this.bytes = bytes;
	}
	
	public PluginMessageTask(Plugin plugin, String channel, byte... bytes) {
		super(plugin);
		
		this.plugin = plugin;
		this.player = null;
		this.channel = channel;
		this.bytes = bytes;
	}
	
	public void run() {
		if(KCore.getOnlinePlayers().size() < 1) return;
		
		Player p = player;
		
		if(p == null) {
			for(Player ap : KCore.getOnlinePlayers()) {
				if(ap == null || !ap.isOnline()) continue;
				p = ap;
			}
		}
		
		p.sendPluginMessage(plugin, channel, bytes);
	}

}
