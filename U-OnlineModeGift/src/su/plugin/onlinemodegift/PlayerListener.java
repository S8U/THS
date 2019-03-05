package su.plugin.onlinemodegift;

import org.bukkit.Bukkit;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UNewPlayerJoinEvent;

public class PlayerListener implements UEventListener {

  @UEventHandler
  public void onNewPlayerJoin(UNewPlayerJoinEvent e) {
    if(!e.getPlayer().isOnlineMode()) return;

    OnlineModeGiftPlugin.getCommands().forEach(cmd -> Bukkit
        .dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("<플레이어>", e.getPlayer().getName())));
    OnlineModeGiftPlugin.getMessages().forEach(msg -> e.getPlayer().msg(msg));
  }

}
