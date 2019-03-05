package su.plugin.pvpstats.listener.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import su.plugin.ability.api.event.DeathEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.pvpstats.PVPStatsPlugin;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.PSPlayer;

public class AbilityListener implements Listener {

  private PVPStatsAPI api = PVPStatsPlugin.getApi();

  @EventHandler
  public void onDeath(DeathEvent e) {
    if(!api.isWritePVPStats()) return;

    Player p = e.getPlayer().getPlayer();
    if(p.getKiller() == null && !api.isIncludeNatureDeath()) return;

    PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

    PSPlayer pp = api.getPlayerManager().getPSPlayer(playerKey);
    pp.addDeathCount();
    pp.setKillStreak(0);
    pp.addDeathStreak();

    pp.savePlayerAsynchronously();

    PlayerKey killerKey = p.getKiller() == null ? null : PlayerKey.getPlayerKeyByPlatformPlayer(p.getKiller());

    if(p.getKiller() != null) {
      PSPlayer kp = api.getPlayerManager().getPSPlayer(killerKey);
      kp.addKillCount();
      kp.addKillStreak();
      kp.setDeathStreak(0);

      kp.savePlayerAsynchronously();

      for(PlayerKey pk : pp.getLastHitTimes().keySet()) {
        if(killerKey.equals(pk) || playerKey.equals(pk) || System.currentTimeMillis() - pp.getLastHitTime(pk) > PVPStatsAPI.getAssistEffectiveTime() * 1000) continue;

        PSPlayer ap = api.getPlayerManager().getPSPlayer(playerKey);
        ap.addAssistCount();
        ap.setDeathStreak(0);

        ap.savePlayerAsynchronously();
      }
    }

    Bukkit.getScheduler().runTaskAsynchronously(PVPStatsPlugin.getInstance(), () -> {
      api.getSQLManager().writePVPLog(p.getKiller() == null ? null : killerKey, pp.getPlayerKey());
    });
  }

}
