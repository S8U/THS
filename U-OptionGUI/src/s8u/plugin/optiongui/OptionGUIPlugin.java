package s8u.plugin.optiongui;

import su.plugin.core.bukkit.api.plugin.UKPlugin;

public class OptionGUIPlugin extends UKPlugin {

  @Override
  public void onUEnable() {
    setPrefix("§7[ U-OptionGUI ]");

    registerCommands();
  }

}
