package su.plugin.core.common.command;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;

public class MainCommand implements UCommandListener {

  @CommandHandler(
      name = "core",
      aliases = {"c"},
      usePlatformPrefix = true,
      permission = "core.admin",
      usage = "U-Core 명령어를 확인합니다."
  )
  public void core(UCommandSender sender, String[] args) {
    Core.nmsg(sender, "§e§l[ U-Core ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("core", 1)) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

}