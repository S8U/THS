package su.plugin.ability.listener.other;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionPlayer;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.object.PrefixPlayer;

public class PrefixListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChat(AsyncPlayerChatEvent e) {
    e.setFormat(makeChatFormat(AbilityAPI.getPlayerManager().getGamePlayer(e.getPlayer()), e.getFormat()));
  }

  private String makeChatFormat(GamePlayer gp, String format) {
    String prefix = "";
    String pPrefix = "";
    String pSuffix = "";

    if(AbilityAPI.isUsePrefixer()) {
      PrefixPlayer pfp = PrefixerAPI.getPlayerManager().getPrefixPlayer(gp.getPlayerKey());
      prefix = pfp.hasMainPrefix() ? StringUtil.connectString(pfp.getMainPrefixList(), "") : "";
    }

    if(AbilityAPI.isUsePermission()) {
      PermissionPlayer pp = PermissionAPI.getPlayerManager().getPermissionPlayer(gp.getPlayerKey());

      pPrefix = pp.getPrefix() == null ? "" : pp.getPrefix();
      pSuffix = pp.getSuffix() == null ? "" : pp.getSuffix();
    }

    String color =  gp.isWatchMode() ? "§7" : (AbilityAPI.getGameManager().isGameStarted() ? "§c" : "§b");
    String statePrefix = gp.isWatchMode() ? "[관전]" : (AbilityAPI.getGameManager().isGameStarted() ? "[게임 중]" : "[대기 중]");

    return color + statePrefix + " §f" + prefix + pPrefix + "%1$s" + pSuffix + color + ": §f%2$s";
  }

}
