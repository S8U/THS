package su.plugin.pvpstats.api.object;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLTable;

@RequiredArgsConstructor
@Getter
public class Stats implements Comparable<Stats> {

  private final PlayerKey playerKey;

  @Setter
  private int killCount, deathCount, assistCount, winCount, quitCount, killStreak, deathStreak, winStreak, maxKillStreak, maxDeathStreak, maxWinStreak;

  public void init() {
    killCount = 0;
    deathCount = 0;
    assistCount = 0;
    winCount = 0;
    quitCount = 0;
    killStreak = 0;
    deathStreak = 0;
    winStreak = 0;
    maxKillStreak = 0;
    maxDeathStreak = 0;
    maxWinStreak = 0;
  }

  @SneakyThrows(SQLException.class)
  public void load(SQLTable statsTable) {
    @Cleanup PreparedStatement state = statsTable.select("*", "where player_id = " + playerKey);
    @Cleanup ResultSet result = state.executeQuery();

    if(!result.next()) return;

    load(result);
  }

  @SneakyThrows(SQLException.class)
  public void load(ResultSet result) {
    killCount = result.getInt("kill_count");
    deathCount = result.getInt("death_count");
    assistCount = result.getInt("assist_count");
    winCount = result.getInt("win_count");
    quitCount = result.getByte("quit_count");

    killStreak = result.getInt("kill_streak");
    deathStreak = result.getInt("death_streak");
    winStreak = result.getInt("win_streak");

    maxKillStreak = result.getInt("max_kill_streak");
    maxDeathStreak = result.getInt("max_death_streak");
    maxWinStreak = result.getInt("max_win_streak");
  }

  public void save(SQLTable statsTable) {
    statsTable.insertDuplicate(playerKey, killCount, deathCount, assistCount, winCount, quitCount, killStreak, deathStreak, winStreak, maxKillStreak, maxDeathStreak, maxWinStreak);
  }

  public void addKillCount() {
    killCount++;
  }

  public void addDeathCount() {
    deathCount++;
  }

  public void addAssistCount() {
    assistCount++;
  }

  public void addWinCount() {
    winCount++;
  }

  public void addQuitCount() {
    quitCount++;
  }

  public void addKillStreak() {
    killStreak++;

    maxKillStreak = killStreak > maxKillStreak ? killStreak : maxKillStreak;
  }

  public void addDeathStreak() {
    deathStreak++;

    maxDeathStreak = deathStreak > maxDeathStreak ? deathStreak : maxDeathStreak;
  }

  public void addWinStreak() {
    winStreak++;

    maxWinStreak = winStreak > maxWinStreak ? winStreak : maxWinStreak;
  }

  public int getScore() {
    return killCount * 2 + assistCount - deathCount * 2;
  }

  @Override
  public int compareTo(Stats o) {
    return new Integer(getScore()).compareTo(o.getScore());
  }
}