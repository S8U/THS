package su.plugin.channel.bungee.api.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.md_5.bungee.api.ProxyServer;
import su.plugin.channel.bungee.api.event.GChannelLoadedEvent;
import su.plugin.channel.common.api.manager.SQLManager;
import su.plugin.channel.common.api.object.Channel;

public class GSQLManager extends SQLManager {

	@Override
	protected void onChannelLoaded(Channel channel) {
		ProxyServer.getInstance().getPluginManager().callEvent(new GChannelLoadedEvent(Arrays.asList(channel)));
	}

	@Override
	protected void onAllChannelLoaded(HashMap<String, Channel> channels) {
		ProxyServer.getInstance().getPluginManager().callEvent(new GChannelLoadedEvent(new ArrayList<>(channels.values())));
	}
	
}