package s8u.plugin.minigamecore.api.game;

import org.bukkit.entity.Player;
import s8u.plugin.minigamecore.api.MiniGameCore;
import s8u.plugin.minigamecore.api.config.AutoGameConfig;

public class DefaultAutoStartHandler implements AutoStartHandler {

  @Override
  public boolean onPlayerJoin(Player player) {
    return AutoGameConfig.getNumberOfAutoStart() <= MiniGameCore.getPlayerManager().getOnlineJoinPlayers().size()
        && MiniGameCore.getPlayerManager().getNumberOfTeams() > 1;
  }

}