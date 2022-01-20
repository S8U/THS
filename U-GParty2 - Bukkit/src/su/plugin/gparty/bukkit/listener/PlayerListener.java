package su.plugin.gparty.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UPlayerJoinEvent;
import su.plugin.core.common.api.event.c.player.UPlayerQuitEvent;
import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class PlayerListener implements UEventListener, Listener {

  private KGPartyAPI api = KGPartyPlugin.getApi();

  @UEventHandler
  public void onJoin(UPlayerJoinEvent e) {
    if (api.getPlayerManager().getPartyPlayers().containsKey(e.getPlayer().getPlayerKey())) return;
    
    PartyPlayer pp = new PartyPlayer(e.getPlayer().getPlayerKey());

    api.getPlayerManager().getPartyPlayers().put(e.getPlayer().getPlayerKey(), pp);

    Bukkit.getScheduler().runTaskLater(KGPartyPlugin.getInstance(),() -> api.sendPartyDataRequest(pp.getPlayerKey()), 10);
  }

  @UEventHandler
  public void onQuit(UPlayerQuitEvent e) {
    api.getPlayerManager().getPartyPlayers().remove(e.getPlayer().getPlayerKey());
  }

}