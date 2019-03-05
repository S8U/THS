package su.plugin.ability.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.PermissionList;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GameMap;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;

public class SupplyCommand implements UCommandListener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @CommandHandler(
      name = "보급품",
      aliases = {"supply"},
      usage = "보급품 명령어 도움말을 확인합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "보급품",
      aliases = {"supply"},
      usage = "보급품 명령어 도움말을 확인합니다."
  )
  public void ability_supply(UCommandSender sender, String[] args) {
    Core.msg(sender, "§b§lU-Ability - Supply");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("능력자 보급품", 1)) {
      if(sc.getPermission() == null) {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.YELLOW);
      } else {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.BLUE);
      }
    }
  }

  @SubCommandHandler(
      parent = {"보급품", "능력자 보급품"},
      name = "기록",
      aliases = {"history"},
      usage = "보급품 생성 기록을 확인합니다."
  )
  public void ability_supply_history(UCommandSender sender, String[] args) {
    if(api.getSupplyManager().getSupplyLogs().size() < 1) {
      sender.wmsg("생성된 보급품이 없습니다.");
      return;
    }

    sender.nmsg("");
    sender.cmsg(ChatColor.AQUA, "§b보급품 기록 ( " + api.getSupplyManager().getSupplyLogs().size() + " )");

    for(int i = 0; i < api.getSupplyManager().getSupplyLogs().size(); i++) {
      Location supplyLocation = api.getSupplyManager().getSupplyLogs().get(i);

      sender.cmsg(ChatColor.AQUA, (i + 1) + " §b) X: §f" + Math.round(supplyLocation.getX()) + "§b, Y: §f" + Math.round(supplyLocation.getY()) + "§b, Z: §f" + Math.round(supplyLocation.getZ()));
    }
  }

  @SubCommandHandler(
      parent = {"보급품", "능력자 보급품"},
      name = "생성",
      aliases = {"create"},
      additional = "(<보급품>)",
      permission = PermissionList.ABILITY_ADMIN,
      usage = "보급품을 생성합니다."
  )
  public void ability_supply_create(UPlayer up, String[] args) {
    List<ItemStack> items = new ArrayList<>();

    if(args.length > 1) {
      items = api.getSupplyManager().getSupply(String.join(" ", args));
      if(items == null) {
        up.wmsg("존재하지 않는 보급품입니다.");
        return;
      }
    } else {
      items = api.getSupplyManager().getRandomSupply();
    }

    Location location = ((Player) up).getLocation();
    api.getSupplyManager().createSupply(location, items);

    Core.nbc(" ");
    Core.cbc(ChatColor.AQUA, "§b(X: §f" + Math.round(location.getX()) + "§b, Y: §f" + Math.round(location.getY()) + "§b, Z: §f" + Math.round(location.getZ()) + "§b) 에 보급품이 생성되었습니다.");
    Core.cbc(ChatColor.AQUA, "§b보급품 좌표는 §f'/보급품 기록' §b명령어로 다시 확인할 수 있습니다.");
  }

  @SubCommandHandler(
      parent = {"보급품", "능력자 보급품"},
      name = "랜덤생성",
      aliases = {"randomCreate"},
      additional = "(<보급품>)",
      permission = PermissionList.ABILITY_ADMIN,
      usage = "랜덤 위치에 보급품을 생성합니다."
  )
  public void ability_supply_randomCreate(UCommandSender sender, String[] args) {
    GameMap map = api.getMapManager().getPlayingMap();
    if(map == null) {
      sender.wmsg("아직 게임이 시작되지 않았습니다.");
      return;
    }

    List<ItemStack> items = new ArrayList<>();
    if(args.length > 2) {
      items = api.getSupplyManager().getSupply(args[2]);
      if(items == null) {
        sender.wmsg("존재하지 않는 보급품입니다.");
        return;
      }
    } else {
      items = api.getSupplyManager().getRandomSupply();
    }

    Location location = api.getSupplyManager().createSupplyAtRandomLocation(map, items, api.getGameManager().isTeleportedAll());
    location.getWorld().loadChunk(location.getWorld().getChunkAt(location));

    Core.nbc(" ");
    Core.cbc(ChatColor.AQUA, "§b(X: §f" + Math.round(location.getX()) + "§b, Y: §f" + Math.round(location.getY()) + "§b, Z: §f" + Math.round(location.getZ()) + "§b) 에 보급품이 생성되었습니다.");
    Core.cbc(ChatColor.AQUA, "§b보급품 좌표는 §f'/보급품 기록' §b명령어로 다시 확인할 수 있습니다.");
  }

  @SubCommandHandler(
      parent = {"보급품", "능력자 보급품"},
      name = "목록",
      aliases = {"list"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "보급품 목록을 확인합니다."
  )
  public void ability_supply_list(UCommandSender sender, String[] args) {
    if(api.getSupplyManager().getSupplies().size() < 1) {
      sender.wmsg("등록된 보급품이 없습니다.");
      return;
    }

    sender.nmsg("");
    sender.cmsg(ChatColor.AQUA, "§b보급품 목록 ( " + api.getSupplyManager().getSupplies().size() + " )");

    for(String supplyName : api.getSupplyManager().getSupplies().keySet()) {
      sender.cmsg(ChatColor.AQUA, supplyName);
    }
  }

}