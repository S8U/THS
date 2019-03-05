package su.plugin.ability.listener.other;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.event.DeathEvent;
import su.plugin.ability.api.event.WinEvent;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.PSPlayer;

public class PVPStatsListener implements Listener {

  @EventHandler
  public void onWin(WinEvent e) {
    if(AbilityAPI.isUsePVPStats()) {
      for(GamePlayer gp : e.getPlayers()) {
        PSPlayer psp = PVPStatsAPI.getPlayerManager().getPSPlayer(gp.getPlayerKey());

        psp.addWinCount();
        psp.addWinStreak();

        psp.savePlayerAsynchronously();
      }
    }
  }

  @EventHandler
  public void onDeath(DeathEvent e) {
    PSPlayer psp = PVPStatsAPI.getPlayerManager().getPSPlayer(e.getPlayer().getPlayerKey());

    psp.setWinStreak(0);

    psp.savePlayerAsynchronously();
  }

}