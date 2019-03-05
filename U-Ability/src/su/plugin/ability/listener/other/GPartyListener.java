package su.plugin.ability.listener.other;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.event.DeathEvent;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.bukkit.api.object.KPartyPlayer;

public class GPartyListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@EventHandler
	public void onDeath(DeathEvent e) {
		GamePlayer gp = e.getPlayer();
		KPartyPlayer pp = KGPartyAPI.getPlayerManager().getPartyPlayer(gp.getPlayerKey());
		if(pp == null || !pp.hasParty() || gp == null || !gp.isEliminate() || gp.isWatchMode()) return;
		
		gp.toggleWatchMode(true, false);

		Core.cbc(ChatColor.DARK_RED, gp.getDisplayName() + " §c님께서 탈락하여 관전 모드로 전환되었습니다. (관전자 수: " + api.getPlayerManager().getOnlineWatchPlayers().size() + "명)");
		
		if(api.getGameManager().finish()) {
			api.shutdown(13);
		} else if(!api.getGameManager().getGameState().equals(GameState.END) && api.getPlayerManager().getJoinedPlayers().size() < 2) {
			for(Player ap : KCore.getOnlinePlayers()) {
				KCore.teleport(ap, api.getMapManager().getSpawn());
			}
			
			api.getGameManager().stopGame();

			Core.cbc(ChatColor.RED, "§c인원 수가 부족하여 게임이 중단됩니다.");
		}
	}
	
}