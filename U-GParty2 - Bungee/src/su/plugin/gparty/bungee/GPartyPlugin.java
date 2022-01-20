package su.plugin.gparty.bungee;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.gparty.bungee.api.GPartyAPI;
import su.plugin.gparty.bungee.command.UserCommand;
import su.plugin.gparty.bungee.listener.PlayerListener;

public class GPartyPlugin extends UGPlugin {

  @Getter
  private static GPartyPlugin instance;
  @Getter
  private static GPartyAPI api = new GPartyAPI();

  @Override
  public void onUEnable() {
    instance = this;
    setPrefix("§a[ U-Party ]");
    setPluginPackage(getClass().getPackage().getName().substring(0,getClass().getPackage().getName().lastIndexOf(".")));
    setColor(ChatColor.GREEN);

    ProxyServer.getInstance().registerChannel("ugparty:main");

    registerPlugins();

    registerListeners(PlayerListener.class.getPackage().getName());
    registerUEventListeners(PlayerListener.class.getPackage().getName());

    registerCommands(UserCommand.class.getPackage().getName());

    loadConfig();
  }

  @Override
  public void onConfigLoad(UCommandSender sender) {
    getJsonConfig().addDefault("최대 파티 인원", 3);
    getJsonConfig().saveDefaults();

    api.setMaxPartyMember(getJsonConfig().getInt("최대 파티 인원"));
  }

  private void registerPlugins() {
    if (existsPlugin("U-GLogin")) {
      api.setUseGLogin(true);

      log("U-GLogin 플러그인과 연동되었습니다.");
    }

    if (existsPlugin("U-GEssentials")) {
      api.setUseGEssentials(true);

      log("U-GEssentials 플러그인과 연동되었습니다.");
    }

    if (existsPlugin("U-GFriend")) {
      api.setUseGFriend(true);

      log("U-GFriend 플러그인과 연동되었습니다.");
    }

    if (existsPlugin("U-Channel")) {
      api.setUseChannel(true);

      log("U-Channel 플러그인과 연동되었습니다.");
    }
  }

}