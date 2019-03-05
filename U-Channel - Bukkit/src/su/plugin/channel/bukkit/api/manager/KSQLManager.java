package su.plugin.channel.bukkit.api.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.Bukkit;
import su.plugin.channel.bukkit.KChannelPlugin;
import su.plugin.channel.bukkit.api.event.KChannelLoadedEvent;
import su.plugin.channel.common.api.manager.SQLManager;
import su.plugin.channel.common.api.object.Channel;

public class KSQLManager extends SQLManager {

	@Override
	protected void onChannelLoaded(Channel channel) {
		Bukkit.getScheduler().runTask(KChannelPlugin.getInstance(), () -> {
			Bukkit.getPluginManager().callEvent(new KChannelLoadedEvent(Arrays.asList(channel)));
		});
	}

	@Override
	protected void onAllChannelLoaded(HashMap<String, Channel> channels) {
		Bukkit.getScheduler().runTask(KChannelPlugin.getInstance(), () -> {
			Bukkit.getPluginManager().callEvent(new KChannelLoadedEvent(new ArrayList<>(channels.values())));
		});
	}
	
}