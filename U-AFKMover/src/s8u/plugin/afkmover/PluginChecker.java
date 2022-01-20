package s8u.plugin.afkmover;

import lombok.Getter;
import su.plugin.core.bukkit.api.util.PluginUtil;

public class PluginChecker {

  @Getter
  private static boolean useChannel;

  public static void check() {
    useChannel = PluginUtil.existsPlugin("U-Channel");
  }

}