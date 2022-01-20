package s8u.plugin.cash;

import lombok.Getter;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.command.CashCommand;
import s8u.plugin.cash.listener.PlayerListener;
import s8u.plugin.cash.listener.lib.ability.AbilityMoneyListener;
import s8u.plugin.cash.listener.lib.citizen.NPCListener;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.ChatColor;

public class CashPlugin extends UKPlugin {

  @Getter
  private static CashPlugin instance;
  @Getter
  private static CashAPI api = new CashAPI();

  @Override
  public void onUEnable() {
    instance = this;

    setPrefix("§e[ U-Cash ]");
    setColor(ChatColor.YELLOW);

    api.init();

    if(!api.getSQLManager().connect(this)) {
      log("MySQL에 연결할 수 없어 비활성화됩니다.");

      disable();
      return;
    }

    registerListeners(PlayerListener.class.getPackage().getName());
    if (PluginUtil.existsPlugin("U-Ability")) {
      registerListener(new AbilityMoneyListener());
    }
    if (PluginUtil.existsPlugin("Citizens")) {
      registerListener(new NPCListener());
    }
    registerCommands(CashCommand.class.getPackage().getName());
    registerPermissions();
  }

  @Override
  public void onUDisable() {

  }

}