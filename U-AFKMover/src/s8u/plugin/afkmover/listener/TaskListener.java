package s8u.plugin.afkmover.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import s8u.plugin.afkmover.api.AFKMoverAPI;
import su.plugin.core.bukkit.api.event.player.FirstPlayerJoinEvent;
import su.plugin.core.bukkit.api.event.player.LastPlayerQuitEvent;

public class TaskListener implements Listener {

  @EventHandler
  public void onFirstPlayerJoin(FirstPlayerJoinEvent e) {
    if (AFKMoverAPI.isAfkChannel()) return;

    AFKMoverAPI.startAFKCheckTask();
  }

  @EventHandler
  public void onLastPlayerQuit(LastPlayerQuitEvent e) {
    if (AFKMoverAPI.isAfkChannel()) return;

    AFKMoverAPI.stopAFKCheck();
  }

}