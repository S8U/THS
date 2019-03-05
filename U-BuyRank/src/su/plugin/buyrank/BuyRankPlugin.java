package su.plugin.buyrank;

import lombok.Getter;
import su.plugin.buyrank.api.BuyRankAPI;
import su.plugin.buyrank.command.BuyRankCommand;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;

public class BuyRankPlugin extends UKPlugin {

  @Getter
  private static BuyRankPlugin instance;

  @Getter
  private static BuyRankAPI api = new BuyRankAPI();

  @Override
  public void onUEnable() {
    instance = this;
    setPrefix("§9[ U-BuyRank ]");
    setColor(ChatColor.BLUE);

    api.init();

    if(PluginUtil.existsPlugin("U-PVPStats")) {
      api.setUsePVPStats(true);
      log("U-PVPStats 플러그인과 연동되었습니다.");
    }

    api.getSQLManager().connect(this);

    registerCommands(new BuyRankCommand());
    registerPermissions();

    loadConfig();
  }

  @Override
  public void onUDisable() {
    api.getSQLManager().close();
  }

  @Override
  public void onConfigLoad(UCommandSender sender) {
    getJsonConfig().addDefault("등급 구매 시 공지", true);
    getJsonConfig().saveDefaults();

    api.setBroadcastOnBuy(getJsonConfig().getBoolean("등급 구매 시 공지"));

    api.getFileManager().loadRankConfig();
  }

}