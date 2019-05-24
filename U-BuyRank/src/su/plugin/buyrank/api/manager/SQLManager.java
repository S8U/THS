package su.plugin.buyrank.api.manager;

import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;

public class SQLManager extends SQLManagerBase {

  private SQLTable buyLogTable;

  public SQLManager() {
    setUseUseOption(true);
  }

  @Override
  public void createTable() {
    buyLogTable = new SQLTable(this, "BuyLog", "player_id int, rank_name varchar(255), money double, time bigint").createTable();
  }

  public void writeBuyLog(PlayerKey playerKey, String rank, double money) {
    if(!isUse()) return;

    buyLogTable.insert(playerKey, rank, money, System.currentTimeMillis());
  }

}