package s8u.plugin.crackwhitelist.api;

import lombok.Getter;
import lombok.Setter;
import s8u.plugin.crackwhitelist.storage.SQLManager;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.plugin.UPlugin;

public class CrackWhiteListAPI {

  private static UPlugin plugin;

  @Getter
  private static boolean whiteList;

  @Setter
  @Getter
  private static String disallowMessage;

  @Getter
  private static SQLManager SQLManager = new SQLManager();

  public CrackWhiteListAPI(UPlugin plugin) {
    this.plugin = plugin;
  }

  public void loadConfig() {
    plugin.getJsonConfig().addDefault("화이트리스트 사용", true);
    plugin.getJsonConfig().addDefault("화이트리스트 차단 메시지", "화이트리스트에 등록되지 않았습니다.");

    plugin.getJsonConfig().saveDefaults();

    whiteList = plugin.getJsonConfig().getBoolean("화이트리스트 사용");
    disallowMessage = ChatColor.translateAlternateColorCodes('&', plugin.getJsonConfig().getString("화이트리스트 차단 메시지"));
  }

  public static void toggleWhiteList(boolean toggle) {
    whiteList = toggle;

    plugin.getJsonConfig().set("화이트리스트 사용", toggle);
    plugin.getJsonConfig().save();
  }

  public static void setDisallowMessage(String message) {
    disallowMessage = message;

    plugin.getJsonConfig().set("화이트리스트 차단 메시지", message);
    plugin.getJsonConfig().save();
  }

  public static boolean addWhiteList(PlayerKey playerKey) {
    if (isWhiteListed(playerKey)) return false;

    SQLManager.addWhiteList(playerKey); return true;
  }

  public static boolean removeWhiteList(PlayerKey playerKey) {
    if (!isWhiteListed(playerKey)) return false;

    SQLManager.removeWhiteList(playerKey); return true;
  }

  public static boolean isWhiteListed(PlayerKey playerKey) {
    return SQLManager.existsWhiteList(playerKey);
  }

}