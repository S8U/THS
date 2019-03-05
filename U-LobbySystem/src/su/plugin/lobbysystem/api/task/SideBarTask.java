package su.plugin.lobbysystem.api.task;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.lobbysystem.api.LobbySystemAPI;

public class SideBarTask extends UKRunnable {

  public SideBarTask(Plugin plugin) {
    super(plugin);
  }

  public void run() {
    for(Player ap : KCore.getOnlinePlayers()) {
      ap.setScoreboard(LobbySystemAPI.makeScoreBoard(ap));
    }
  }

}
