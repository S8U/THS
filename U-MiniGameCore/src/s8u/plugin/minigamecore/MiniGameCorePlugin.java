package s8u.plugin.minigamecore;

import lombok.Getter;
import s8u.plugin.minigamecore.api.MiniGameCore;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;

public class MiniGameCorePlugin extends UKPlugin {

  @Getter
  private static MiniGameCorePlugin instance;

  @Getter
  private static MiniGameCore api = new MiniGameCore();

  @Override
  public void onUEnable() {
    // Init UPlugin
    instance = this;

    setPrefix("§c[ U-MiniGameCore ]");
    setColor(ChatColor.RED);

    // Init API
    api.init();

    //
    PluginChecker.checkPlugins();
  }

  @Override
  public void onUDisable() {

  }

  @Override
  public void onConfigLoad(UCommandSender sender) {

  }

}
