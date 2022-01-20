package s8u.plugin.fmllogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;

public class SqlManager extends SQLManagerBase {

  private SQLTable logTable;

  @Override
  public void createTable() {
    logTable = new SQLTable(this, "fml_mod_log", "player_id INT, time DATETIME, mod TEXT").createTable();
  }

  public void log(int id, String modList) {
    logTable.insert(id, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), modList);
  }

}