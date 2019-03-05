package su.plugin.ability.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.PermissionList;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.util.BuildUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;

public class RankItemCommand implements UCommandListener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @CommandHandler(
      name = "등급아이템",
      aliases = {"rankItem"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "등급 아이템 명령어 도움말을 확인합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "등급아이템",
      aliases = {"rankItem"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "등급 아이템 명령어 도움말을 확인합니다."
  )
  public void ability_rankItem(UCommandSender sender, String[] args) {
    Core.msg(sender, "§b§lU-Ability - RankItem");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("능력자 등급아이템", 1)) {
      if(sc.getPermission() == null) {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.YELLOW);
      } else {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.BLUE);
      }
    }
  }

  @SubCommandHandler(
      parent = {"등급아이템", "능력자 등급아이템"},
      name = "주기",
      aliases = {"give"},
      additional = "<플레이어>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "플레이어에게 등급 아이템을 지급합니다."
  )
  public void ability_rankItem_give(UCommandSender sender, String[] args) {
    Player target = Bukkit.getPlayer(args[0]);
    if(target == null) {
      sender.wmsg("접속 중이 아닌 플레이어입니다.");
      return;
    } else if(!api.getItemManager().hasRankItemGroup(target)) {
      sender.wmsg("플레이어 등급의 아이템이 존재하지 않습니다");
      return;
    }
    
    api.getItemManager().giveRankItem(target);
    
    sender.cmsg(ChatColor.BLUE, target.getDisplayName() + " §b님께 등급 아이템을 지급했습니다.");
    Core.cmsg(target, ChatColor.BLUE, sender.getDisplayName() + " §b님께서 등급 아이템을 지급했습니다.");
  }

  @SubCommandHandler(
      parent = {"등급아이템", "능력자 등급아이템"},
      name = "모두주기",
      aliases = {"giveAll"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "모든 플레이어에게 등급 아이템을 지급합니다."
  )
  public void ability_rankItem_giveAll(UCommandSender sender, String[] args) {
    api.getItemManager().giveRankItemAll();

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 모두에게 등급 아이템을 지급했습니다.");
  }

  @SubCommandHandler(
      parent = {"등급아이템", "능력자 등급아이템"},
      name = "등급목록",
      aliases = {"rankList"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "등급 목록을 확인합니다."
  )
  public void ability_rankItem_rankList(UCommandSender sender, String[] args) {
    sender.cmsg(ChatColor.BLUE, "§b등급 목록: §f" + String.join(", ", api.getItemManager().getRankItems().keySet()));
  }

  @SubCommandHandler(
      parent = {"등급아이템", "능력자 등급아이템"},
      name = "아이템목록",
      aliases = {"itemList"},
      additional = "<등급>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "등급의 아이템 목록을 확인합니다."
  )
  public void ability_rankItem_itemList(UCommandSender sender, String[] args) {
    if(!api.getItemManager().existsRankItem(args[0])) {
      sender.wmsg("존재하지 않는 등급입니다.");
      return;
    }

    sender.cmsg(ChatColor.BLUE, args[0] + " §b등급의 아이템 목록: §f" + BuildUtil.buildItemListString(api.getItemManager().getRankItemList(args[0])));
  }

}