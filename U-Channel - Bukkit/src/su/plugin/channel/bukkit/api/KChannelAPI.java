package su.plugin.channel.bukkit.api;

import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.channel.bukkit.KChannelPlugin;
import su.plugin.channel.bukkit.api.event.KCurrentChannelUpdatedEvent;
import su.plugin.channel.bukkit.api.manager.KSQLManager;
import su.plugin.channel.bukkit.platform.KProvider;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.task.ChannelLoadTask;
import su.plugin.core.bukkit.api.KCore;

public class KChannelAPI extends ChannelAPI {
	
	@Setter
	@Getter
	private static String channelName;
	
	private static int channelLoadTaskId = -1;
	
	public void init() {
		SQLManager = new KSQLManager();
		
		platformProvider = new KProvider();
	}

	public static Channel getCurrentChannel() {
		return channelManager.getChannel(channelName);
	}

	public static void startChannelLoadTask() {
		if(channelLoadTaskId != -1) return;
		
		channelLoadTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(KChannelPlugin.getInstance(), new ChannelLoadTask(), 0, getSQLManager().getLoadInterval() * 20).getTaskId();
	}
	
	public static void stopChannelLoadTask() {
		if(channelLoadTaskId == -1) return;
		
		Bukkit.getScheduler().cancelTask(channelLoadTaskId);
		channelLoadTaskId = -1;
	}

	public static void updateThisChannelInfo() {
		updateThisChannelInfo(true, KCore.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), KCore.getOnlinePlayers());
	}

	public static void updateThisChannelInfo(boolean online, int playerCount, int maxPlayerCount, List<Player> players) {
		Channel channel = getChannelManager().getChannel(channelName);
		if(channel == null) {
			channel = new Channel(channelName);
		}
		
		channel.setOnline(online);
		channel.setPlayerCount(playerCount);
		channel.setMaxPlayerCount(maxPlayerCount);
		
		List<String> playerList = new ArrayList<>();
		players.forEach(p -> playerList.add(p.getName()));
		
		channel.setPlayerList(playerList);
		
		if(!online) {
			channel.setETCs(new LinkedTreeMap<>());
		}

		if(getSQLManager().isUpload()) {
			getSQLManager().saveChannel(channel);
		}

		Bukkit.getScheduler().runTask(KChannelPlugin.getInstance(), () -> {
			Bukkit.getPluginManager().callEvent(new KCurrentChannelUpdatedEvent());
		});
	}
	
}