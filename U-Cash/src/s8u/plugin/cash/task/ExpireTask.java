package s8u.plugin.cash.task;

import s8u.plugin.cash.CashPlugin;
import s8u.plugin.cash.api.data.ExpirableData;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;

public class ExpireTask extends UKRunnable {

  private final ExpirableData data;

  public ExpireTask(ExpirableData data) {
    super(CashPlugin.getInstance());

    this.data = data;
  }

  @Override
  public void run() {
    data.handleExpire();
  }

}