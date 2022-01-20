package s8u.plugin.cash.api.data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import s8u.plugin.cash.task.ExpireTask;
import su.plugin.core.common.api.player.PlayerKey;

@Data
public abstract class ExpirableData {

  private PlayerKey playerKey;

  private LocalDateTime expireTime;

  private ExpireTask expireTask = new ExpireTask(this);

  public void setExpireTime(LocalDateTime expireTime) {
    this.expireTime = expireTime;
  }

  public void setExpireTime(String date) {
    expireTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
  }

  public String getFormattedExpireTime() {
    return expireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  public boolean isExpired() {
    return expireTime.isBefore(LocalDateTime.now());
  }

  public boolean handleExpire() {
    if (!isExpired()) return false;

    onHandleExpire();

    stopExpireTask();

    return true;
  }

  public abstract void onHandleExpire();

  public void startExpireTask() {
    if (isExpired()) return;

    expireTask.runTaskLaterAsynchronously((expireTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis()) / 50 + 100);
  }

  public void stopExpireTask() {
    expireTask.cancel();
  }

}