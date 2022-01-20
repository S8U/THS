package s8u.plugin.afkmover;

import lombok.Getter;
import s8u.plugin.afkmover.api.AFKMoverAPI;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;

public class AFKMoverPlugin extends UKPlugin {

  @Getter
  private static AFKMoverPlugin instance;

  @Getter
  private static AFKMoverAPI api = new AFKMoverAPI();

  @Override
  public void onUEnable() {
    instance = this;

    setPrefix("[ U-AFKMover ]");
    setColor(ChatColor.GRAY);

    api.init();

    PluginChecker.check();

    registerListeners();

    loadConfig();
  }

  @Override
  public void onUDisable() {
    api.stopAFKCheck();
  }

  @Override
  public void onConfigLoad(UCommandSender sender) {
    getJsonConfig().addDefault("잠수 채널.사용", true);
    getJsonConfig().addDefault("잠수 채널.다른 플레이어 가리기", true);
    getJsonConfig().addDefault("잠수 채널.활동 시 타겟 채널로 이동", true);
    getJsonConfig().addDefault("잠수 채널.퀵바 사용", false);

    getJsonConfig().addDefault("일반 채널.사용", false);
    getJsonConfig().addDefault("일반 채널.잠수 시간(s)", 60);

    getJsonConfig().addDefault("타겟 채널", "channelName");

    getJsonConfig().saveDefaults();

    //

    api.setAfkChannel(getJsonConfig().getBoolean("잠수 채널.사용"));
    api.setHideOtherPlayers(getJsonConfig().getBoolean("잠수 채널.다른 플레이어 가리기"));
    api.setSendTargetChannelOnActivity(getJsonConfig().getBoolean("잠수 채널.활동 시 타겟 채널로 이동"));

    api.setNormalChannel(getJsonConfig().getBoolean("일반 채널.사용"));
    api.setAfkSwitchingTime(getJsonConfig().getInt("일반 채널.잠수 시간(s)"));

    api.setTargetChannel(getJsonConfig().getString("타겟 채널"));
  }

}