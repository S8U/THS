package su.plugin.ability.listener;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.event.GameStoppedEvent;
import su.plugin.ability.api.event.WinEvent;
import su.plugin.core.bukkit.api.util.BungeeUtil;

public class EndBroadcastListener implements Listener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @EventHandler
  public void onWin(WinEvent e) {
      if(!api.isUseEndBroadcastMessage()) return;

      broadcast();
  }

  @EventHandler
  public void onGameStopped(GameStoppedEvent e) {
    if(!api.isUseEndBroadcastMessage()) return;

    broadcast();
  }

  private void broadcast() {
    for(String msg : api.getEndBroadcastMessages()) {
      if (api.isUseChannel()) {
        msg = msg.replace("<channel_name>", su.plugin.channel.bukkit.api.KChannelAPI.getCurrentChannel().getDisplayName());
      }
      BungeeUtil.broadcast(msg);

      try {
        Sound sound = Sound.valueOf(api.getEndBroadcastSound());
        BungeeUtil.playSound(sound,1,1);
      } catch (NullPointerException | IllegalArgumentException e) { }
    }
  }

}