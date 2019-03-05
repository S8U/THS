package su.plugin.ability.command;

import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.PermissionList;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;

public class KitCommand implements UCommandListener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @CommandHandler(
      name = "킷",
      aliases = {"kit"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "킷 명령어 도움말을 확인합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "킷",
      aliases = {"kit"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "킷 명령어 도움말을 확인합니다."
  )
  public void ability_kit(UCommandSender sender, String[] args) {
    Core.msg(sender, "§b§lU-Ability - Kit");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("능력자 킷", 1)) {
      if(sc.getPermission() == null) {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.YELLOW);
      } else {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.BLUE);
      }
    }
  }

  @SubCommandHandler(
      parent = {"킷", "능력자 킷"},
      name = "생성",
      aliases = {"create"},
      additional = "<이름>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "킷을 생성합니다."
  )
  public void ability_kit_create(UCommandSender sender, String[] args) {
    String kitName = String.join(" ", args);

    if(api.getKitManager().existsKit(kitName)) {
      sender.wmsg("이미 존재하는 킷입니다.");
      return;
    }

    api.getKitManager().createKit(kitName);

    sender.cmsg(ChatColor.BLUE, kitName + " §b킷을 생성했습니다.");
  }

  @SubCommandHandler(
      parent = {"킷", "능력자 킷"},
      name = "수정",
      aliases = {"edit"},
      additional = "<이름>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "킷을 수정합니다."
  )
  public void ability_kit_edit(UPlayer up, String[] args) {
    String kitName = String.join(" ", args);

    if(!api.getKitManager().existsKit(kitName)) {
      up.wmsg("존재하지 않는 킷입니다.");
      return;
    }

    ((Player) up.getPlatformSender()).openInventory(api.getKitManager().getKit(kitName));
  }

  @SubCommandHandler(
      parent = {"킷", "능력자 킷"},
      name = "목록",
      aliases = {"list"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "킷을 목록을 확인합니다."
  )
  public void ability_kit_list(UCommandSender sender, String[] args) {
    if(api.getKitManager().getKits().size() < 1) {
      sender.wmsg("아직 생성된 킷이 없습니다.");
      return;
    }

    sender.cmsg(ChatColor.BLUE, String.join(", ", api.getKitManager().getKits().keySet()));
  }

}