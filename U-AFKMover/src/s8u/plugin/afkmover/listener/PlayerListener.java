package s8u.plugin.afkmover.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import s8u.plugin.afkmover.api.AFKMoverAPI;

public class PlayerListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    if (AFKMoverAPI.isNormalChannel()) {
      AFKMoverAPI.getLastActivity().put(e.getPlayer().getName().toLowerCase(), System.currentTimeMillis());
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    if (AFKMoverAPI.isNormalChannel()) {
      AFKMoverAPI.getLastActivity().remove(e.getPlayer().getName().toLowerCase());
    }
  }

  @EventHandler
  public void onMove(PlayerMoveEvent e) {
    if (AFKMoverAPI.isNormalChannel()) {
      AFKMoverAPI.getLastActivity().put(e.getPlayer().getName().toLowerCase(), System.currentTimeMillis());
    } else if (AFKMoverAPI.isAfkChannel() && AFKMoverAPI.isSendTargetChannelOnActivity()) {
      AFKMoverAPI.sendToTargetChannel(e.getPlayer());
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (AFKMoverAPI.isNormalChannel()) {
      AFKMoverAPI.getLastActivity().put(e.getPlayer().getName().toLowerCase(), System.currentTimeMillis());
    } else if (AFKMoverAPI.isAfkChannel() && AFKMoverAPI.isSendTargetChannelOnActivity()) {
      AFKMoverAPI.sendToTargetChannel(e.getPlayer());
    }
  }

  @EventHandler
  public void onChat(AsyncPlayerChatEvent e) {
    if (AFKMoverAPI.isNormalChannel()) {
      AFKMoverAPI.getLastActivity().put(e.getPlayer().getName().toLowerCase(), System.currentTimeMillis());
    } else if (AFKMoverAPI.isAfkChannel() && AFKMoverAPI.isSendTargetChannelOnActivity()) {
      AFKMoverAPI.sendToTargetChannel(e.getPlayer());
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    if (AFKMoverAPI.isNormalChannel()) {
      AFKMoverAPI.getLastActivity().put(e.getWhoClicked().getName().toLowerCase(), System.currentTimeMillis());
    } else if (AFKMoverAPI.isAfkChannel() && AFKMoverAPI.isSendTargetChannelOnActivity()) {
      AFKMoverAPI.sendToTargetChannel((Player) e.getWhoClicked());
    }
  }

}