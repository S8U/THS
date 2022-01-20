package su.plugin.core.bukkit.command;

import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;

public class QuickBarCommand implements UCommandListener {

  @CommandHandler(
      name = "퀵바제거",
      aliases = { "removeQuickBar" },
      additional = "(<플레이어>)",
      usage = "플레이어의 퀵바를 제거합니다.",
      permission = "core.admin"
  )
  public void removeQuickBar(UCommandSender sender, String[] args, Command cmd) {
    UPlayer target;

    if (args.length < 1) {
      if (sender.isConsole()) {
        cmd.sendUsage(sender, true);
        return;
      }

      target = Core.getUPlayer(args[0]);

      if (target == null) {
        sender.wmsg("접속 중이 아닌 플레이어입니다.");
        return;
      }
    } else {
      target = (UPlayer) sender;
    }

    KCore.getGUIManager().clearQuickBar((Player) target.getPlatformSender());

    sender.msg((sender.equals(target) ? "" : target.getName() + "님의 ") + "퀵바를 강제로 제거했습니다.");
  }

}