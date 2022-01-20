package s8u.plugin.crackwhitelist.bungee;

import s8u.plugin.crackwhitelist.api.CrackWhiteListAPI;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UPlayerLoginEvent;

public class ConnectListener implements UEventListener {

  private CrackWhiteListAPI api = GCrackWhiteListPlugin.getApi();

  @UEventHandler
  public void onLogin(UPlayerLoginEvent e) {
    if (!api.isWhiteList() || e.getPlayer().isOnlineMode()) return;
    else if (api.isWhiteListed(e.getPlayer().getPlayerKey())) return;

    e.setDisallow(true);
    e.setDisallowReason(api.getDisallowMessage());
  }

}