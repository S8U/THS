package su.plugin.onlinemodegift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.util.StringUtil;

public class OnlineModeGiftPlugin extends UKPlugin {

  @Getter
  private static OnlineModeGiftPlugin instance;

  @Setter
  @Getter
  private static List<String> messages = new ArrayList<>();

  @Setter
  @Getter
  private static List<String> commands = new ArrayList<>();

  @Override
  public void onUEnable() {
    instance = this;

    setPrefix("§e[ U-OnlineModeGift ]");
    setColor(ChatColor.YELLOW);

    onConfigLoad();

    registerUEventListeners();
  }

  public void onConfigLoad() {
    getJsonConfig().addDefault("메시지", Arrays.asList("정품 보상을 받았습니다."));
    getJsonConfig().addDefault("명령어", Arrays.asList("돈 추가 <플레이어> 5000", "pf 추가 <플레이어> &7[정품]"));

    getJsonConfig().save();

    messages = StringUtil.translateAlternateColorCodes(getJsonConfig().getStringList("메시지"));
    commands = StringUtil.translateAlternateColorCodes(getJsonConfig().getStringList("명령어"));

    log("설정을 불러왔습니다.");
  }

}
