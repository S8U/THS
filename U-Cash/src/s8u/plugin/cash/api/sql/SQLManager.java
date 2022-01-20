package s8u.plugin.cash.api.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import lombok.Cleanup;
import lombok.SneakyThrows;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.data.ColorDisplayNameData;
import s8u.plugin.cash.api.data.DisplayNameData;
import s8u.plugin.cash.api.data.MoneyBoostData;
import s8u.plugin.cash.api.data.PlayerData;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;
import su.plugin.core.common.api.util.DebugUtil;

public class SQLManager extends SQLManagerBase {

  private SQLTable cashTable, moneyBoostTable, displayNameTable, colorDisplayNameTable; //, prefixTable;
  private SQLTable cashLogTable, benefitLogTable, expireLogTable;

  @Override
  public void createTable() {
    cashTable = new SQLTable(this, "Cash", "player_id int primary key, cash int").createTable();
    moneyBoostTable = new SQLTable(this, "Money_Boost", "player_id int primary key, expire_time datetime").createTable();
    displayNameTable = new SQLTable(this, "DisplayName", "player_id int primary key, display_name varchar(255), expire_time datetime").createTable();
    colorDisplayNameTable = new SQLTable(this, "DisplayName_Color", "player_id int, color_code varchar(1), expire_time datetime, primary key(player_id, color_code)").createTable();
    // prefixTable = new SQLTable(this, "PrefixTable", "player_id int, prefix varchar(255), expire_time datetime, primary key(player_id, expire_time)").createTable();

    cashLogTable = new SQLTable(this, "Log_Cash", "player_id int, admin_id int, type varchar(255), amount int, time datetime").createTable();
    benefitLogTable = new SQLTable(this, "Log_Benefit", "player_id int, admin_id int, type varchar(255), amount int, expire_time datetime, time datetime").createTable();
    expireLogTable = new SQLTable(this, "Log_Expire", "player_id int, type varchar(255), time datetime").createTable();
  }

  public PlayerData loadPlayerData(PlayerKey playerKey) {
    PlayerData playerData = CashAPI.getPlayerDatas().containsKey(playerKey) ? CashAPI.getPlayerDatas().get(playerKey) : new PlayerData(playerKey);

    playerData.setCash(getCash(playerKey));
    playerData.setMoneyBoostData(getMoneyBoostData(playerKey));
    playerData.setDisplayNameData(getDisplayNameData(playerKey));
    playerData.setColorDisplayNameDatas(getColorDisplayNameDatas(playerKey));
    // playerData.setPrefixDatas(getPrefixDatas(playerKey));

    if (playerData.getMoneyBoostData() != null) {
      DebugUtil.log("MoneyBoost: " + playerData.getMoneyBoostData().getFormattedExpireTime());
    }
    if (playerData.getDisplayNameData() != null) {
      DebugUtil.log("DisplayName: " + playerData.getDisplayNameData().getFormattedExpireTime());
    }
    if (playerData.getColorDisplayNameDatas().size() > 0) {
      DebugUtil.log("ColorDisplayName:");

      playerData.getColorDisplayNameDatas().forEach((color, data) -> {
        DebugUtil.log(color + "&" + color.getChar() + ": §f" + data.getFormattedExpireTime());
      });
    }
    /*if (playerData.getPrefixDatas().size() > 0) {
      DebugUtil.log("Prefix:");
      for (PrefixData storage : playerData.getPrefixDatas()) {
        DebugUtil.log(storage.getPrefix() + ": §f" + storage.getFormattedExpireTime());
      }
    }*/

    CashAPI.getPlayerDatas().put(playerKey, playerData);

    return playerData;
  }

  public void setCash(PlayerKey playerKey, int cash) {
    cashTable.insertDuplicate(playerKey.getId(), cash);
  }

  @SneakyThrows(SQLException.class)
  public int getCash(PlayerKey playerKey) {
    @Cleanup PreparedStatement state = cashTable.select("cash", "where player_id = " + playerKey.getId());
    @Cleanup ResultSet result = state.executeQuery();

    return result.next() ? result.getInt("cash") : 0;
  }

  public void setMoneyBoostData(PlayerKey playerKey, String expireTime) {
    moneyBoostTable.insertDuplicate(playerKey.getId(), expireTime);
  }

  public void setMoneyBoostData(MoneyBoostData data) {
    setMoneyBoostData(data.getPlayerKey(), data.getFormattedExpireTime());
  }

  public void deleteMoneyBoostData(PlayerKey playerKey) {
    moneyBoostTable.delete("where player_id = " + playerKey.getId());
  }

  @SneakyThrows(SQLException.class)
  public MoneyBoostData getMoneyBoostData(PlayerKey playerKey) {
    @Cleanup PreparedStatement state = moneyBoostTable.select("*", "where player_id = " + playerKey.getId());
    @Cleanup ResultSet result = state.executeQuery();

    if (!result.next()) return null;

    MoneyBoostData data = new MoneyBoostData();
    data.setPlayerKey(playerKey);
    data.setExpireTime(result.getString("expire_time"));

    return data;
  }

  public void setDisplayNameData(PlayerKey playerKey, String displayName, String expireTime) {
    displayNameTable.insertDuplicate(playerKey, displayName, expireTime);
  }

  public void setDisplayNameData(DisplayNameData data) {
    setDisplayNameData(data.getPlayerKey(), data.getDisplayName(), data.getFormattedExpireTime());
  }

  public void deleteDisplayNameData(PlayerKey playerKey) {
    displayNameTable.delete("where player_id = " + playerKey.getId());
  }

  @SneakyThrows(SQLException.class)
  public DisplayNameData getDisplayNameData(PlayerKey playerKey) {
    @Cleanup PreparedStatement state = displayNameTable.select("expire_time", "where player_id = " + playerKey.getId());
    @Cleanup ResultSet result = state.executeQuery();

    if (!result.next()) return null;

    DisplayNameData data = new DisplayNameData();
    data.setPlayerKey(playerKey);
    data.setExpireTime(result.getString("expire_time"));

    return data;
  }

  public void setColorDisplayNameData(PlayerKey playerKey, ChatColor color, String expireTime) {
    colorDisplayNameTable.insertDuplicate(playerKey, String.valueOf(color.getChar()), expireTime);
  }

  public void setColorDisplayNameData(ColorDisplayNameData data) {
    setColorDisplayNameData(data.getPlayerKey(), data.getColor(), data.getFormattedExpireTime());
  }

  public void deleteColorDisplayNameData(PlayerKey playerKey, ChatColor color) {
    colorDisplayNameTable.delete("where player_id = " + playerKey.getId() + " and color_code = '" + color.getChar() + "'");
  }

  public void deleteColorDisplayNameDatas(PlayerKey playerKey, ChatColor color) {
    colorDisplayNameTable.delete("where player_id = " + playerKey.getId());
  }

  @SneakyThrows(SQLException.class)
  public ColorDisplayNameData getColorDisplayNameData(PlayerKey playerKey, ChatColor color) {
    @Cleanup PreparedStatement state = colorDisplayNameTable.select("*", "where player_id = " + playerKey.getId() + " and color_code = '" + color.getChar() + " '");
    @Cleanup ResultSet result = state.executeQuery();

    if (!result.next()) return null;

    ColorDisplayNameData data = new ColorDisplayNameData();
    data.setPlayerKey(playerKey);
    data.setColor(ChatColor.getByChar(result.getString("color_code")));
    data.setExpireTime(result.getString("expire_time"));

    return data;
  }

  @SneakyThrows(SQLException.class)
  public HashMap<ChatColor, ColorDisplayNameData> getColorDisplayNameDatas(PlayerKey playerKey) {
    @Cleanup PreparedStatement state = colorDisplayNameTable.select("*", "where player_id = " + playerKey.getId());
    @Cleanup ResultSet result = state.executeQuery();

    HashMap<ChatColor, ColorDisplayNameData> datas = new HashMap<>();
    while (result.next()) {
      ColorDisplayNameData data = new ColorDisplayNameData();
      data.setPlayerKey(playerKey);
      data.setColor(ChatColor.getByChar(result.getString("color_code")));
      data.setExpireTime(result.getString("expire_time"));

      datas.put(data.getColor(), data);
    }

    return datas;
  }

  /*public void setPrefixData(PlayerKey playerKey, String prefix, String expireTime) {
    prefixTable.insertDuplicate(playerKey.getId(), prefix, expireTime);
  }

  public void setPrefixData(PrefixData storage) {
    setPrefixData(storage.getPlayerKey(), storage.getPrefix(), storage.getFormattedExpireTime());
  }

  @SneakyThrows(SQLException.class)
  public List<PrefixData> getPrefixDatas(PlayerKey playerKey) {
    @Cleanup PreparedStatement state = prefixTable.select("*", "where player_id = " + playerKey.getId());
    @Cleanup ResultSet result = state.executeQuery();

    List<PrefixData> datas = new ArrayList<>();
    while (result.next()) {
      PrefixData storage = new PrefixData();
      storage.setPlayerKey(playerKey);
      storage.setPrefix(result.getString("prefix"));
      storage.setExpireTime(result.getString("expire_time"));

      datas.add(storage);
    }

    return datas;
  }*/

  public void logCash(int playerId, int adminId, Type type, int amount) {
    cashLogTable.insert(playerId, adminId, type.getText(), amount, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }

  public void logBenefit(int playerId, int adminId, BenefitType type, int amount, LocalDateTime expireTime) {
    benefitLogTable.insert(playerId, adminId, type.getText(), amount, expireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }

  public void logExpire(int playerId, String type) {
    expireLogTable.insert(playerId, type, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }

}