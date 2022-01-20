package su.plugin.ability.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.event.GameStartedEvent;
import su.plugin.ability.api.event.GameStoppedEvent;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;

public class GUIListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(api.isUseWaitingQuickBar() && (api.getGameManager().getGameState() == GameState.WAITING || api.getGameManager().getGameState() == GameState.PREPARING)) {
			api.getBarManager().getWaitingQuickBar().setTo(e.getPlayer());
		} else if(api.isUseWatchModeQuickBar() && api.getGameManager().getGameState() != GameState.WAITING && api.getPlayerManager().getOnlineWatchPlayers().size() > 0) {
			GamePlayer gp = api.getPlayerManager().getGamePlayer(e.getPlayer());
			if(gp.isEliminate() || gp.isWatchMode()) return;
			
			api.getGUIManager().updateTeleportGUI();
			
			KCore.getGUIManager().clearQuickBar(gp.getPlayer());
		}
	}
	
	@EventHandler
	public void onGameStarted(GameStartedEvent e) {
		if(!api.isUseWaitingQuickBar()) return;
		
		for(Player ap : KCore.getGUIManager().getOnlinePlayers(api.getBarManager().getWaitingQuickBar())) {
			KCore.getGUIManager().clearQuickBar(ap);
		}
	}
	
	@EventHandler
	public void onGameStopped(GameStoppedEvent e) {
		if(!api.isUseWaitingQuickBar()) return;
		
		for(GamePlayer ap : api.getPlayerManager().getAllPlayers()) {
			if(!ap.isOnline()) continue;
			api.getBarManager().getWaitingQuickBar().setTo(ap.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(api.getPlayerManager().getOnlineWatchPlayers().size() < 1) return;
		
		api.getGUIManager().updateTeleportGUI();
	}
	
}