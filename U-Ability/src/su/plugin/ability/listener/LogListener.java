package su.plugin.ability.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.event.AbilityGameStartedEvent;
import su.plugin.ability.api.object.Ability;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class LogListener implements Listener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @EventHandler (priority = EventPriority.HIGH)
  public void onGameStarted(AbilityGameStartedEvent e) {
    Core.log(StringUtil.buildDateString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss - " + api.getPlayerManager().getOnlineJoinedPlayers().size() + "명이 참여한 게임이 시작되었습니다."));
    Core.log("능력 목록");
    for (GamePlayer gp : api.getPlayerManager().getJoinedPlayers()) {
      StringBuilder sb = new StringBuilder();
      if (gp.hasAbility()) {
        for (Ability ability : gp.getAbilities()) {
          sb.append((sb.length() < 1 ? "" : ", ") + ability.getName() + " (" + ability.getPluginName() + ")");
        }
      } else {
        sb.append("없음");
      }
      Core.log(gp.getName() + (gp.isOnline() ? "" : " (Offline)") + ": " + sb.toString());
    }
  }

}
