package su.plugin.lobbysystem.listener;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.lobbysystem.LobbyQuickBar;

public class QuickBarListener {

  private HashMap<String, LobbyQuickBar> quickBars = new HashMap<>();

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    // Lobby Quick Bar
    LobbyQuickBar bar = new LobbyQuickBar(e.getPlayer());
    bar.setTo(e.getPlayer());

    quickBars.put(e.getPlayer().getName().toLowerCase(), bar);


    // Hide Player
    if (Core.getOptionManager().existsPlayerOption(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()), "lobby_hide_player")) {
      KCore.getOnlinePlayers().forEach(ap -> e.getPlayer().hidePlayer(ap));
    }

    KCore.getOnlineUPlayers().forEach(up -> {
      if (!up.existsOption("lobby_hide_player")) return;

      ((Player) up.getPlatformSender()).hidePlayer(e.getPlayer());
    });
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    quickBars.remove(e.getPlayer().getName().toLowerCase());
  }

}