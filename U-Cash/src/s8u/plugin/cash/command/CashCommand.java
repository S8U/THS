package s8u.plugin.cash.command;

import java.text.DecimalFormat;
import s8u.plugin.cash.api.CashAPI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;

public class CashCommand implements UCommandListener {

  @CommandHandler(
      name = "캐시",
      aliases = {"cash", "캐쉬"},
      usage = "보유 중인 캐시를 확인합니다."
  )
  public void cash(UPlayer up, String[] args) {
    up.nmsg("§e보유 중인 캐시: §f" + new DecimalFormat("#,###").format(CashAPI.getPlayerDatas().get(up.getPlayerKey()).getCash()) + "§e원");
  }

  @SubCommandHandler(
      parent = "캐시",
      name = "도움말",
      aliases = {"help"},
      usage = "캐시 명령어를 확인합니다."
  )
  public void cash_help(UCommandSender sender, String[] args) {
    Core.nmsg(sender, "§e§l[ U-Cash ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("캐시")) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

}