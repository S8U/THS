package su.plugin.gessentials.bungee.task;

import su.plugin.core.bungee.api.scheduler.UGRunnable;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;

public class UnMuteTask extends UGRunnable {

  private PlayerKey playerKey;

  public UnMuteTask(PlayerKey playerKey) {
    super(GGEssentialsPlugin.getInstance());

    this.playerKey = playerKey;
  }

  @Override
  public void run() {
    GGEssentialsAPI.getChatManager().unMute(playerKey, null);

    if(playerKey.getUPlayer() != null && playerKey.getUPlayer().isOnline()) {
      playerKey.getUPlayer().msg("§a채팅 금지가 해제되었습니다.");
    }
  }

}