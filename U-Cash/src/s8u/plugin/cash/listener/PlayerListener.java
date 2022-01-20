package s8u.plugin.cash.listener;

import java.util.Iterator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.data.ColorDisplayNameData;
import s8u.plugin.cash.api.data.PlayerData;
import su.plugin.core.common.api.player.PlayerKey;

public class PlayerListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    PlayerData playerData = CashAPI.getSQLManager().loadPlayerData(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));

    if (playerData.getMoneyBoostData() != null && !playerData.getMoneyBoostData().handleExpire()) {
        playerData.getMoneyBoostData().startExpireTask();
    }

    if (playerData.getDisplayNameData() != null && !playerData.getDisplayNameData().handleExpire()) {
      playerData.getDisplayNameData().startExpireTask();
    }

    if (playerData.getColorDisplayNameDatas().size() > 0) {
      Iterator<ColorDisplayNameData> it = playerData.getColorDisplayNameDatas().values().iterator();
      while (it.hasNext()) {
        ColorDisplayNameData data = it.next();
        if (data.handleExpire()) continue;

        data.startExpireTask();
      }
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    PlayerData playerData = CashAPI.getPlayerDatas().get(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));

    if (playerData.getMoneyBoostData() != null) {
      playerData.getMoneyBoostData().stopExpireTask();
    }

    if (playerData.getDisplayNameData() != null) {
      playerData.getDisplayNameData().stopExpireTask();
    }

    if (playerData.getColorDisplayNameDatas().size() > 0) {
      playerData.getColorDisplayNameDatas().forEach((c, data) -> {
        data.stopExpireTask();
      });
    }

    CashAPI.getPlayerDatas().remove(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));
  }

}