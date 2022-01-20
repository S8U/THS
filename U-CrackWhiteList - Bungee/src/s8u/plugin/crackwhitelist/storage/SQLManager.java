package s8u.plugin.crackwhitelist.storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;

public class SQLManager extends SQLManagerBase {

  @Getter
  private SQLTable whiteListTable, whiteListLogTable;

  @Override
  public void createTable() {
    whiteListTable = new SQLTable(this, "whitelist_players", "player_id INT PRIMARY KEY").createTable();
    whiteListLogTable = new SQLTable(this, "whitelist_log", "id INT PRIMARY KEY AUTO_INCREMENT, player_id INT, admin_id INT, type VARCHAR(6), time DATETIME").createTable();
  }

  public void addWhiteList(PlayerKey playerKey) {
    whiteListTable.insertIgnore(playerKey.getId());
  }

  public void removeWhiteList(PlayerKey playerKey) {
    whiteListTable.delete("where player_id = " + playerKey.getId());
  }

  @SneakyThrows (SQLException.class)
  public boolean existsWhiteList(PlayerKey playerKey) {
    @Cleanup PreparedStatement statement = whiteListTable.select("*", "where player_id = " + playerKey.getId());
    @Cleanup ResultSet result = statement.executeQuery();

    return result.next();
  }

  public void logWhiteList(PlayerKey playerKey, int adminKey, String type) {
    whiteListLogTable.insert(null, playerKey.getId(), adminKey, type, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }

}