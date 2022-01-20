package s8u.plugin.cash.listener.lib.ability;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import s8u.plugin.cash.CashPlugin;
import s8u.plugin.cash.api.CashAPI;
import su.plugin.ability.api.event.KillEvent;
import su.plugin.ability.api.event.WinEvent;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.lib.VaultHandler;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class AbilityMoneyListener implements Listener {

  @EventHandler
  public void onKill(KillEvent e) {
    Bukkit.getScheduler().runTaskLater(CashPlugin.getInstance(), () -> {
      // 킬
      if (CashAPI.hasMoneyBoost(PlayerKey.getPlayerKeyByPlatformPlayer(e.getKiller()))) {
        double addMoney = 0;

        addMoney += e.getKillMoney();
        addMoney += e.getRegularKillMoney();
        addMoney += e.isFirstBlood() ? e.getFirstBloodMoney() : 0;

        VaultHandler.giveMoney(e.getKiller().getName(), addMoney);
        Core.cmsg(e.getKiller(), ChatColor.YELLOW, "§a+§f" + addMoney + "§a원 (킬 돈부스트)");
      }

      // 어시스트
      for (GamePlayer ap : e.getAssists()) {
        if (!CashAPI.hasMoneyBoost(ap.getPlayerKey())) continue;

        VaultHandler.giveMoney(ap.getPlayerKey().getName(), e.getAssistMoney());
        if (ap.getPlayer() == null) continue;

        Core.cmsg(ap.getPlayer(), ChatColor.YELLOW, "§a+§f" + e.getAssistMoney() + "§a원 (어시스트 돈부스트)");
      }
    }, 1L);
  }

  @EventHandler
  public void onWin(WinEvent e) {
    double money = Math.round(e.getWinMoney() / e.getPlayers().size());

    Bukkit.getScheduler().runTaskLater(CashPlugin.getInstance(), () -> {
      for (GamePlayer ap : e.getPlayers()) {
        if (!CashAPI.hasMoneyBoost(ap.getPlayerKey())) continue;

        VaultHandler.giveMoney(ap.getPlayerKey().getName(), money);
        if (ap.getPlayer() == null) continue;

        Core.cmsg(ap.getPlayer(), ChatColor.YELLOW, "§a+" + money + "원 (우승 돈부스트)");
      }
    }, 1L);
  }

}