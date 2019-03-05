package su.plugin.gessentials.bungee.api.object;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gessentials.bungee.task.UnMuteTask;

@Getter
@RequiredArgsConstructor
public class EMute {

  private final PlayerKey playerKey;

  private final int adminId;

  private final long time, duration;

  private final String reason;

  private UnMuteTask unMuteTask;

  public boolean isTimeMute() {
    return duration > 0;
  }

  public boolean isEffective() {
    return !isTimeMute() || getRemainingMuteTime() > 0;
  }

  public long getRemainingMuteTime() {
    return getUnMuteTime() - System.currentTimeMillis();
  }

  public long getUnMuteTime() {
    return time + duration;
  }

  public void startUnMuteTask() {
    if(unMuteTask != null  && unMuteTask.isRunning()) return;

    unMuteTask = unMuteTask == null ? new UnMuteTask(playerKey) : unMuteTask;
    unMuteTask.schedule(getRemainingMuteTime(), TimeUnit.MILLISECONDS);
  }

  public void stopUnMuteTask() {
    if(unMuteTask == null) return;

    unMuteTask.cancel();
  }

}