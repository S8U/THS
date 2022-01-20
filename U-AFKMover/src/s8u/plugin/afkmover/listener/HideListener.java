package s8u.plugin.afkmover.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import s8u.plugin.afkmover.api.AFKMoverAPI;
import su.plugin.core.bukkit.api.KCore;

public class HideListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    if (!AFKMoverAPI.isAfkChannel() || !AFKMoverAPI.isHideOtherPlayers()) return;

    for (Player ap : KCore.getOnlinePlayers()) {
      e.getPlayer().hidePlayer(ap);
      ap.hidePlayer(e.getPlayer());
    }
  }

}