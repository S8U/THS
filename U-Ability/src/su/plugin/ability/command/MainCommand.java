package su.plugin.ability.command;

import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;

public class MainCommand implements UCommandListener {

  @CommandHandler(
      name = "능력자",
      aliases = {"ability", "ua", "va", "ha", "a"},
      usage = "능력자 명령어를 확인합니다."
  )
  public void ability(UCommandSender sender, String[] args) {
    Core.nmsg(sender, "§c§l[ U-Ability ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("능력자", 1)) {
      if(sc.getPermission() == null) {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.YELLOW);
      } else {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.BLUE);
      }
    }
  }

}