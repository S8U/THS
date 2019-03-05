package su.plugin.core.bungee.api.task;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import su.plugin.core.bungee.api.scheduler.UGRunnable;

public class PluginMessageTask extends UGRunnable {
	
	public PluginMessageTask(Plugin plugin, ServerInfo serverInfo, String channel, byte... bytes) {
		super(plugin);
		this.serverInfo = serverInfo;
		this.channel = channel;
		this.bytes = bytes;
	}

	private final ServerInfo serverInfo;
	private final String channel;
	private final byte[] bytes;
	
	public void run() {
		serverInfo.sendData(channel, bytes);
	}
	
}