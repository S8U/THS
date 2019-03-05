package su.plugin.lobbysystem.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import su.plugin.channel.bukkit.api.event.KChannelLoadedEvent;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.event.UnregisterableListener;
import su.plugin.lobbysystem.api.LobbySystemAPI;

public class ChannelListener implements Listener, UnregisterableListener {

  @EventHandler
  public void onChannelLoaded(KChannelLoadedEvent e) {
    for(Player ap : KCore.getOnlinePlayers()) {
      ap.setScoreboard(LobbySystemAPI.makeScoreBoard(ap));
    }
  }

}
