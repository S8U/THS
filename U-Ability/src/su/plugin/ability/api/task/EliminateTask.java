package su.plugin.ability.api.task;

import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.PSPlayer;

public class EliminateTask extends UKRunnable {

  private final PlayerKey playerKey;

  public EliminateTask(PlayerKey playerKey) {
    super(AbilityPlugin.getInstance());

    this.playerKey = playerKey;
  }

  @Override
  public void run() {
    if(!AbilityAPI.getGameManager().isGameStarted()) return;

    GamePlayer gp = AbilityAPI.getPlayerManager().getGamePlayer(playerKey);

    gp.setEliminate(true);
    gp.setReconnectEliminate(true);
    gp.setReconnectEliminateMessage(true);

    Core.cbc(ChatColor.DARK_RED, gp.getDisplayName() + " 님께서 재접속 가능 시간을 초과하여 탈락했습니다.");

    if(AbilityAPI.isUsePVPStats()) {
      PSPlayer psp =  PVPStatsAPI.getSQLManager().loadPlayer(gp.getPlayerKey());

      psp.addQuitCount();
      psp.savePlayerAsynchronously();
    }
  }

}