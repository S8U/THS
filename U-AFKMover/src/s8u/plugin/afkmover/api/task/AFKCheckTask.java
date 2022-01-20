package s8u.plugin.afkmover.api.task;

import org.bukkit.entity.Player;
import s8u.plugin.afkmover.AFKMoverPlugin;
import s8u.plugin.afkmover.api.AFKMoverAPI;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;

public class AFKCheckTask extends UKRunnable {

  public AFKCheckTask() {
    super(AFKMoverPlugin.getInstance());
  }

  @Override
  public void run() {
    long switchingTime = AFKMoverAPI.getAfkSwitchingTime() * 1000;
    for (Player ap : KCore.getOnlinePlayers()) {
      if (System.currentTimeMillis() - AFKMoverAPI.getLastActivity().get(ap.getName().toLowerCase()) < switchingTime) continue;

      AFKMoverAPI.sendToTargetChannel(ap);
    }
  }

}