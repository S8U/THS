package su.plugin.channelgui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import su.plugin.channel.bukkit.api.event.KChannelLoadedEvent;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channelgui.api.ChannelGUIAPI;
import su.plugin.channelgui.api.object.ChannelGUI;
import su.plugin.channelgui.api.object.ChannelIcon;
import su.plugin.core.bukkit.api.gui.Icon;

public class UpdateListener implements Listener {

  @EventHandler
  public void onChannelLoaded(KChannelLoadedEvent e) {
    a: for(ChannelGUI gui : ChannelGUIAPI.getGUIManager().getGUIs().values()) {
      for(Icon icon : gui.getIcons().values()) {
        ChannelIcon ci = (ChannelIcon) icon;

        if(ci.getChannel() != null) {
          for(Channel updated : e.getLoadedChannel()) {
            if(ci.getChannel().equals(updated)) {
              gui.updateAsynchronously();

              continue a;
            }
          }
        } else if(ci.getChannelGroup() != null) {
          for(Channel updated : e.getLoadedChannel()) {
            if(updated.getGroupName() != null && updated.getGroup().equals(ci.getChannelGroup())) {
              gui.updateAsynchronously();

              continue a;
            }
          }
        }
      }
    }
  }

}