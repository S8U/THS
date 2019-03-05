package su.plugin.channel.bungee.api;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.channel.bungee.GChannelPlugin;
import su.plugin.channel.bungee.api.manager.GConfigManager;
import su.plugin.channel.bungee.api.manager.GSQLManager;
import su.plugin.channel.bungee.platform.GProvider;
import su.plugin.channel.bungee.task.OfflineCheckTask;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.task.ChannelLoadTask;

public class GChannelAPI extends ChannelAPI {
	
	@Setter
	@Getter
	private static boolean useOfflineCheck;
	
	@Setter
	@Getter
	private static int offlineCheckInterval;
	
	private static int channelLoadTaskId = -1;
	
	private static OfflineCheckTask offlineCheckTask;
	
	@Getter
	private static GConfigManager configManager;
	
	public void init() {
		offlineCheckTask = new OfflineCheckTask();
		
		SQLManager = new GSQLManager();
		configManager = new GConfigManager();
		
		platformProvider = new GProvider();
	}

	public static void startChannelLoadTask() {
		if(channelLoadTaskId != -1) return;

		channelLoadTaskId = ProxyServer.getInstance().getScheduler().schedule(GChannelPlugin.getInstance(), new ChannelLoadTask(), 0, SQLManager.getLoadInterval(), TimeUnit.SECONDS).getId();
	}
	
	public static void stopChannelLoadTask() {
		if(channelLoadTaskId == -1) return;
		
		ProxyServer.getInstance().getScheduler().cancel(channelLoadTaskId);
		channelLoadTaskId = -1;
	}
	
	public static void startOfflineCheckTask() {
		if(offlineCheckTask.isRunning()) return;
		
		offlineCheckTask.schedule(0, offlineCheckInterval, TimeUnit.SECONDS);
	}
	
	public static void stopOfflineCheckTask() {
		offlineCheckTask.cancel();
	}
	
}