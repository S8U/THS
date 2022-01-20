package s8u.plugin.cash.api;

import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import s8u.plugin.cash.CashPlugin;
import s8u.plugin.cash.api.data.ColorDisplayNameData;
import s8u.plugin.cash.api.data.DisplayNameData;
import s8u.plugin.cash.api.data.ExpirableData;
import s8u.plugin.cash.api.data.MoneyBoostData;
import s8u.plugin.cash.api.data.PlayerData;
import s8u.plugin.cash.api.sql.SQLManager;
import s8u.plugin.cash.gui.ColorSelectGUI;
import s8u.plugin.cash.gui.DisplayNameGUI;
import s8u.plugin.cash.gui.ShopColorGUI;
import s8u.plugin.cash.gui.ShopDonationGUI;
import s8u.plugin.cash.gui.ShopMainGUI;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class CashAPI {

  @Getter
  private static ShopMainGUI shopMainGUI;
  @Getter
  private static ShopColorGUI shopColorGUI;
  @Getter
  private static ShopDonationGUI shopDonationGUI;
  @Getter
  private static ColorSelectGUI colorSelectGUI;
  @Getter
  private static DisplayNameGUI displayNameGUI;

  @Getter
  private static HashMap<PlayerKey, PlayerData> playerDatas = new HashMap<>();

  @Getter
  private static SQLManager SQLManager;

  public void init() {
    shopMainGUI = new ShopMainGUI();
    shopColorGUI = new ShopColorGUI();
    shopDonationGUI = new ShopDonationGUI();

    colorSelectGUI = new ColorSelectGUI();

    displayNameGUI = new DisplayNameGUI();

    SQLManager = new SQLManager();
  }

  // Cash
  public static void setCash(PlayerKey playerKey, int cash) {
    PlayerData data = playerDatas.get(playerKey);
    if (data != null) {
      data.setCash(cash);
    }

    SQLManager.setCash(playerKey, cash);
  }

  public static void addCash(PlayerKey playerKey, int cash) {
    setCash(playerKey, getCash(playerKey) + cash);
  }

  public static boolean subCash(PlayerKey playerKey, int cash) {
    int afterCash = getCash(playerKey) - cash;
    if (afterCash < 0) return false;

    setCash(playerKey, afterCash);
    return true;
  }

  public static boolean hasCash(PlayerKey playerKey, int cash) {
    return getCash(playerKey) >= cash;
  }

  public static int getCash(PlayerKey playerKey) {
    PlayerData data = playerDatas.get(playerKey);

    return data == null ? SQLManager.getCash(playerKey) : data.getCash();
  }

  // Money Boost
  public static MoneyBoostData setMoneyBoost(PlayerKey playerKey, LocalDateTime expireTime) {
    MoneyBoostData data = new MoneyBoostData();
    data.setPlayerKey(playerKey);
    data.setExpireTime(expireTime);

    PlayerData playerData = playerDatas.get(playerKey);
    if (playerData != null) {
      playerData.setMoneyBoostData(data);

      data.stopExpireTask();
      data.startExpireTask();
    }

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.setMoneyBoostData(data));

    return data;
  }

  public static void deleteMoneyBoost(PlayerKey playerKey) {
    PlayerData playerData = playerDatas.get(playerKey);
    if (playerData != null && playerData.getMoneyBoostData() != null) {
      playerData.getMoneyBoostData().stopExpireTask();
      playerData.setMoneyBoostData(null);
    }

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.deleteMoneyBoostData(playerKey));
  }

  public static MoneyBoostData extendMoneyBoost(PlayerKey playerKey, int day, int hour, int minute, int second) {
    PlayerData playerData = playerDatas.get(playerKey);
    MoneyBoostData data = playerData == null ? SQLManager.getMoneyBoostData(playerKey) : playerData.getMoneyBoostData();
    if (data == null) {
      data = new MoneyBoostData();
      data.setPlayerKey(playerKey);
      data.setExpireTime(LocalDateTime.now());

      if (playerData != null) {
        playerData.setMoneyBoostData(data);
      }
    }

    data.setExpireTime(data.getExpireTime().plusDays(day).plusHours(hour).plusMinutes(minute).plusSeconds(second));

    if (playerData != null) {
      data.stopExpireTask();
      data.startExpireTask();
    }

    MoneyBoostData temp = data;
    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.setMoneyBoostData(temp));

    return data;
  }

  public static MoneyBoostData subtractMoneyBoost(PlayerKey playerKey, int day, int hour, int minute, int second) {
    PlayerData playerData = playerDatas.get(playerKey);
    MoneyBoostData data = playerData == null ? SQLManager.getMoneyBoostData(playerKey) : playerData.getMoneyBoostData();
    if (data == null) return null;

    data.setExpireTime(data.getExpireTime().minusDays(day).minusHours(hour).minusMinutes(minute).minusSeconds(second));
    if (data.isExpired()) {
      data.handleExpire();
      return data;
    }

    if (playerData != null) {
      data.stopExpireTask();
      data.startExpireTask();
    }

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.setMoneyBoostData(data));

    return data;
  }

  public static boolean hasMoneyBoost(PlayerKey playerKey) {
    ExpirableData data = playerDatas.containsKey(playerKey) ? playerDatas.get(playerKey).getMoneyBoostData() : SQLManager.getMoneyBoostData(playerKey);

    return data != null && !data.isExpired();
  }

  public static MoneyBoostData getMoneyBoost(PlayerKey playerKey) {
    return playerDatas.containsKey(playerKey) ? playerDatas.get(playerKey).getMoneyBoostData() : SQLManager.getMoneyBoostData(playerKey);
  }

  // DisplayName
  public static DisplayNameData setDisplayName(PlayerKey playerKey, String displayName, LocalDateTime expireTime) {
    DisplayNameData data = new DisplayNameData();
    data.setPlayerKey(playerKey);
    data.setDisplayName(displayName);
    data.setExpireTime(expireTime);

    PlayerData playerData = playerDatas.get(playerKey);
    if (playerData != null) {
      playerData.setDisplayNameData(data);

      data.stopExpireTask();
      data.startExpireTask();
    }

    Core.setDisplayName(playerKey, data.getDisplayName());

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.setDisplayNameData(data));

    return data;
  }

  public static void deleteDisplayName(PlayerKey playerKey) {
    PlayerData playerData = playerDatas.get(playerKey);
    if (playerData != null && playerData.getDisplayNameData() != null) {
      playerData.getDisplayNameData().stopExpireTask();
      playerData.setDisplayNameData(null);
    }

    Core.setDisplayName(playerKey, playerKey.getName());

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.deleteDisplayNameData(playerKey));
  }

  public static DisplayNameData extendDisplayName(PlayerKey playerKey, int day, int hour, int minute, int second) {
    PlayerData playerData = playerDatas.get(playerKey);
    DisplayNameData data = playerData == null ? SQLManager.getDisplayNameData(playerKey) : playerData.getDisplayNameData();
    if (data == null) return null;

    data.setExpireTime(data.getExpireTime().plusDays(day).plusHours(hour).plusMinutes(minute).plusSeconds(second));

    if (playerData != null) {
      data.stopExpireTask();
      data.startExpireTask();
    }

    Core.setDisplayName(playerKey, data.getDisplayName());

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.setDisplayNameData(data));

    return data;
  }

  public static DisplayNameData subtractDisplayName(PlayerKey playerKey, int day, int hour, int minute, int second) {
    PlayerData playerData = playerDatas.get(playerKey);
    DisplayNameData data = playerData == null ? SQLManager.getDisplayNameData(playerKey) : playerData.getDisplayNameData();
    if (data == null) return null;

    data.setExpireTime(data.getExpireTime().minusDays(day).minusHours(hour).minusMinutes(minute).minusSeconds(second));
    if (data.isExpired()) {
      data.handleExpire();
      return data;
    }

    if (playerData != null) {
      data.stopExpireTask();
      data.startExpireTask();
    }

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.setDisplayNameData(data));

    return data;
  }

  public static boolean hasDisplayName(PlayerKey playerKey) {
    ExpirableData data = playerDatas.containsKey(playerKey) ? playerDatas.get(playerKey).getDisplayNameData() : SQLManager.getDisplayNameData(playerKey);

    return data != null && !data.isExpired();
  }

  public static DisplayNameData getDisplayName(PlayerKey playerKey) {
    return playerDatas.containsKey(playerKey) ? playerDatas.get(playerKey).getDisplayNameData() : SQLManager.getDisplayNameData(playerKey);
  }

  // Color DisplayName
  public static ColorDisplayNameData setColorDisplayName(PlayerKey playerKey, ChatColor color, LocalDateTime expireTime) {
    ColorDisplayNameData data = new ColorDisplayNameData();
    data.setPlayerKey(playerKey);
    data.setColor(color);
    data.setExpireTime(expireTime);

    PlayerData playerData = playerDatas.get(playerKey);
    if (playerData != null) {
      playerData.getColorDisplayNameDatas().put(color, data);

      data.stopExpireTask();
      data.startExpireTask();
    }

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.setColorDisplayNameData(data));

    return data;
  }

  public static void deleteColorDisplayName(PlayerKey playerKey, ChatColor color) {
    PlayerData playerData = playerDatas.get(playerKey);
    if (playerData != null && playerData.getColorDisplayNameDatas().containsKey(color)) {
      playerData.getColorDisplayNameDatas().get(color).stopExpireTask();
      playerData.getColorDisplayNameDatas().remove(color);
    }

    if (playerKey.getDisplayName().contains("§" + color.getChar())) {
      Core.setDisplayName(playerKey, playerKey.getName());
    }

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.deleteColorDisplayNameData(playerKey, color));
  }

  public static ColorDisplayNameData extendColorDisplayName(PlayerKey playerKey, ChatColor color, int day, int hour, int minute, int second) {
    PlayerData playerData = playerDatas.get(playerKey);
    ColorDisplayNameData data = playerData == null ? SQLManager.getColorDisplayNameData(playerKey, color) : playerData.getColorDisplayNameDatas().get(color);
    if (data == null) {
      data = new ColorDisplayNameData();
      data.setPlayerKey(playerKey);
      data.setColor(color);
      data.setExpireTime(LocalDateTime.now());

      if (playerData != null) {
        playerData.getColorDisplayNameDatas().put(color, data);
      }
    }

    data.setExpireTime(data.getExpireTime().plusDays(day).plusHours(hour).plusMinutes(minute).plusSeconds(second));

    if (playerData != null) {
      data.stopExpireTask();
      data.startExpireTask();
    }

    ColorDisplayNameData temp = data;
    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.setColorDisplayNameData(temp));

    return data;
  }

  public static ColorDisplayNameData subtractColorDisplayName(PlayerKey playerKey, ChatColor color, int day, int hour, int minute, int second) {
    PlayerData playerData = playerDatas.get(playerKey);
    ColorDisplayNameData data = playerData == null ? SQLManager.getColorDisplayNameData(playerKey, color) : playerData.getColorDisplayNameDatas().get(color);
    if (data == null) return null;

    data.setExpireTime(data.getExpireTime().minusDays(day).minusHours(hour).minusMinutes(minute).minusSeconds(second));
    if (data.isExpired()) {
      data.handleExpire();
      return data;
    }

    if (playerData != null) {
      data.stopExpireTask();
      data.startExpireTask();
    }

    Bukkit.getScheduler().runTaskAsynchronously(CashPlugin.getInstance(), () -> SQLManager.setColorDisplayNameData(data));

    return data;
  }

  public static boolean hasColorDisplayName(PlayerKey playerKey, ChatColor color) {
    ExpirableData data = playerDatas.containsKey(playerKey) ? playerDatas.get(playerKey).getColorDisplayNameDatas().get(color) : SQLManager.getColorDisplayNameData(playerKey, color);

    return data != null && !data.isExpired();
  }

  public static ColorDisplayNameData getColorDisplayName(PlayerKey playerKey, ChatColor color) {
    return playerDatas.containsKey(playerKey) ? playerDatas.get(playerKey).getColorDisplayNameDatas().get(color) : SQLManager.getColorDisplayNameData(playerKey, color);
  }

}