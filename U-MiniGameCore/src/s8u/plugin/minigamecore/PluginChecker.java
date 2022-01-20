package s8u.plugin.minigamecore;

import lombok.Getter;
import su.plugin.core.bukkit.api.util.PluginUtil;

public class PluginChecker {

  @Getter
  private static boolean useGParty;
  @Getter
  private static boolean useChannel;

  public static void checkPlugins() {
    useGParty = PluginUtil.existsPlugin("U-GParty");
    useChannel = PluginUtil.existsPlugin("U-Channel");
  }

}