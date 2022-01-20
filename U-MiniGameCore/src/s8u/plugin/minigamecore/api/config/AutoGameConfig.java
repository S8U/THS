package s8u.plugin.minigamecore.api.config;

import lombok.Getter;
import lombok.Setter;

public class AutoGameConfig {

  @Setter
  @Getter
  private static boolean useAutoMode;

  // Vote
  @Setter
  @Getter
  private static boolean
      useStartVote,
      useMapVote,
      useInvincibilitySkipVote;

  @Setter
  @Getter
  private static int numberOfAutoStart;

}