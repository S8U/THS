package s8u.plugin.minigamecore.api.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;

public class GameConfig {

  @Setter
  @Getter
  private static boolean useInvincibility;

  @Setter
  @Getter
  private static boolean useGameModeSpecatator;

  @Setter
  @Getter
  private static GameMode defaultGameMode;

}