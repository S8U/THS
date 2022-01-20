package s8u.plugin.minigamecore.api.player;

import java.util.HashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import s8u.plugin.minigamecore.api.config.GameConfig;
import su.plugin.core.common.api.player.PlayerKey;

@Getter
@RequiredArgsConstructor
public class GamePlayer {

  private final PlayerKey playerKey;

  private boolean dead;

  @Setter
  private boolean defeated; // 탈락 (패배)
  private boolean spectator;

  @Setter
  private long lastKillTime;
  @Setter
  private HashMap<PlayerKey, Long> lastHitTimes = new HashMap<>();

  public Player getPlayer() {
    return (Player) playerKey.getPlatformPlayer();
  }

  public boolean isOnline() {
    return playerKey.getUPlayer().isOnline();
  }

  public boolean isPlaying() {
    return isOnline() && !spectator && !defeated;
  }

  public void setSpectator(boolean toggle) { // 미완
    if (toggle) {
      spectator = true;

      if (GameConfig.isUseGameModeSpecatator()) {
        getPlayer().setGameMode(GameMode.SPECTATOR);
      }


      getPlayer().setCollidable(false);
    } else {
      spectator = false;

      if (GameConfig.isUseGameModeSpecatator()) {
        getPlayer().setGameMode(GameConfig.getDefaultGameMode());
      }

    }
  }

}