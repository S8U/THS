package su.plugin.gbroadcaster.task;

import java.util.Collection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.bungee.api.scheduler.UGRunnable;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.gbroadcaster.GBroadcasterPlugin;
import su.plugin.gbroadcaster.api.GBroadcasterAPI;
import su.plugin.gbroadcaster.api.object.BroadcastData;

public class BroadcastTask extends UGRunnable {

	private int count = -1;

	private BroadcastData data;

	public BroadcastTask(BroadcastData data) {
		super(GBroadcasterPlugin.getInstance());
		this.data = data;
	}
	
	@Override
	public void run() {
		Collection<ProxiedPlayer> players = data.getChannelName().equals("전체") ? ProxyServer.getInstance().getPlayers() : ProxyServer.getInstance().getServers().get(data.getChannelName()).getPlayers();
		if(players.size() < 1) return;

		count = data.isRandom() ? NumberUtil.random(0, data.getMessages().size() - 1) : (count + 1 == data.getMessages().size() ? 0 : count + 1);

		players.stream()
				.filter(ap -> !Core.getOptionManager().existsPlayerOption(PlayerKey.getPlayerKeyByPlatformPlayer(ap), "gbroadcaster_hide"))
				.forEach(ap -> Core.nmsg(ap, GBroadcasterAPI.getPrefix() + data.getMessages().get(count)));
	}

}