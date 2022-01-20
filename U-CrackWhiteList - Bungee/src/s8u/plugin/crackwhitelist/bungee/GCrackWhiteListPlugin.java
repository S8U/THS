package s8u.plugin.crackwhitelist.bungee;

import lombok.Getter;
import s8u.plugin.crackwhitelist.api.CrackWhiteListAPI;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;

public class GCrackWhiteListPlugin extends UGPlugin {

  @Getter
  private static GCrackWhiteListPlugin instance;

  @Getter
  private static CrackWhiteListAPI api;

  @Override
  public void onUEnable() {
    instance = this;

    setPrefix("§7[ U-CrackWhiteList ]");
    setColor(ChatColor.GRAY);

    api = new CrackWhiteListAPI(this);

    if (!api.getSQLManager().connect(this)) return;

    registerUEventListeners();
    registerCommands();

    loadConfig();
  }

  @Override
  public void onConfigLoad(UCommandSender sender) {
    api.loadConfig();
  }

}