package su.plugin.gcculogger.listener;

import java.util.Calendar;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UNewPlayerLoginEvent;
import su.plugin.core.common.api.event.c.player.UPlayerJoinEvent;
import su.plugin.gcculogger.GCCULoggerPlugin;
import su.plugin.gcculogger.api.GCCULoggerAPI;

public class ConnectListener implements UEventListener, Listener {
	
	private GCCULoggerAPI api = GCCULoggerPlugin.getApi();

	private Integer lastMaxCCULog;

	@UEventHandler
	public void onNewPlayerLogin(UNewPlayerLoginEvent e) {
		api.getSQLManager().addNewPlayerCount();
	}

	@UEventHandler
	public void onJoin(UPlayerJoinEvent e) {
		int CCU = ProxyServer.getInstance().getPlayers().size();
		
		api.getSQLManager().writeCCULog(CCU);

		int date = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

		if(lastMaxCCULog == null || lastMaxCCULog != date || CCU > api.getMaxCCU()) {
			api.setMaxCCU(CCU);
			api.getSQLManager().writeMaxCCULog(CCU);

			lastMaxCCULog = date;
		}
	}
	
	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		api.getSQLManager().writeCCULog(ProxyServer.getInstance().getPlayers().size() - 1);
	}
	
}