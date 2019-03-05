package su.plugin.channel.bungee.listener;

import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.core.common.api.Core;

public class ConnectListener implements Listener {

  @EventHandler
  public void onServerConnected(ServerConnectedEvent e) {
    Core.msg(e.getPlayer(), ChannelAPI.getChannelManager().getChannel(e.getServer().getInfo().getName()).getDisplayName() + " 채널로 이동했습니다.");
  }

}