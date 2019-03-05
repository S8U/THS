package su.plugin.ability.command;

import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.PermissionList;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GameMap;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;

public class MapCommand implements UCommandListener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @CommandHandler(
      name = "맵",
      aliases = {"map"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "맵 명령어 도움말을 확인합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "맵",
      aliases = {"map"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "맵 명령어 도움말을 확인합니다."
  )
  public void ability_map(UCommandSender sender, String[] args) {
    Core.msg(sender, "§b§lU-Ability - Map");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("능력자 맵", 1)) {
      if(sc.getPermission() == null) {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.YELLOW);
      } else {
        AbilityAPI.sendUsageIfHasPermission(sc, sender, ChatColor.BLUE);
      }
    }
  }

  @SubCommandHandler(
      parent = {"맵", "능력자 맵"},
      name = "스폰설정",
      aliases = {"setSpawn"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "현재 위치를 스폰으로 설정합니다."
  )
  public void ability_map_setSpawn(UPlayer up, String[] args) {
    api.getMapManager().setSpawn(((Player) up.getPlatformSender()).getLocation());
    api.getConfigManager().saveSpawn();

    up.cmsg(ChatColor.BLUE, "§b스폰으로 설정되었습니다.");
  }

  @SubCommandHandler(
      parent = {"맵", "능력자 맵"},
      name = "위치설정",
      aliases = {"setLocation"},
      additional = "<맵>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "맵의 첫 텔레포트 위치를 설정합니다."
  )
  public void ability_map_setLocation(UPlayer up, String[] args) {
    GameMap map = new GameMap();
    boolean create = true;

    if(api.getMapManager().existsMap(args[0])) {
      map = api.getMapManager().getMap(args[0]);

      create = false;
    }

    map.setName(args[0]);
    map.setMapLocation(((Player) up.getPlatformSender()).getLocation());

    api.getMapManager().setMap(args[0], map);
    api.getConfigManager().saveMap(map);

    if(api.isUseMapVote() && create) {
      api.getGUIManager().updateMapVoteGUI(true);

      if(api.isUseWaitingQuickBar()) {
        api.getBarManager().initQuickBar();
      }
    }

    up.cmsg(ChatColor.BLUE, map.getName() + " §b맵의 위치가 설정되었습니다.");
  }

  @SubCommandHandler(
      parent = {"맵", "능력자 맵"},
      name = "티피올위치설정",
      aliases = {"setTpAllLocation"},
      additional = "<맵>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "맵의 티피올 위치를 설정합니다."
  )
  public void ability_map_setTpAllLocation(UPlayer up, String[] args) {
    if(!api.getMapManager().existsMap(args[0])) {
      up.wmsg("존재하지 않는 맵입니다.");
      return;
    }

    GameMap map = api.getMapManager().getMap(args[0]);
    map.setTPAllLocation(((Player) up.getPlatformSender()).getLocation());
    
    api.getConfigManager().saveMap(map);

    up.cmsg(ChatColor.BLUE, map.getName() + " §b맵의 텔레포트 위치가 설정되었습니다.");
  }

  @SubCommandHandler(
      parent = {"맵", "능력자 맵"},
      name = "범위설정모드",
      aliases = {"rangeSettingMode"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "범위 설정 모드로 전환합니다."
  )
  public void ability_map_rangeSettingMode(UPlayer up, String[] args) {
    GamePlayer gp = api.getPlayerManager().getGamePlayer((Player) up.getPlatformSender());
    gp.setRangeSelectMode(!gp.isRangeSelectMode());

    up.cmsg(ChatColor.BLUE, (gp.isRangeSelectMode() ? "§a" : "§c") + "범위 설정 모드가 " + (gp.isRangeSelectMode() ? "활성화" : "비활성화") + "되었습니다.");
  }

  @SubCommandHandler(
      parent = {"맵", "능력자 맵"},
      name = "범위설정",
      aliases = {"setRange"},
      additional = "<맵>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "맵의 범위를 설정합니다."
  )
  public void ability_map_setRange(UPlayer up, String[] args) {
    if(!api.getMapManager().existsMap(args[0])) {
      up.wmsg("존재하지 않는 맵입니다.");
      return;
    } else if(!api.getMapManager().existsLeftLocation((Player) up.getPlatformSender())|| !api.getMapManager().existsRightLocation((Player) up.getPlatformSender())) {
      up.wmsg("아직 범위를 설정하지 않았습니다.");
      return;
    }

    GameMap map = api.getMapManager().getMap(args[0]);
    map.setMapLimitLocation(api.getMapManager().getLeftLocation((Player) up.getPlatformSender()), api.getMapManager().getRightLocation((Player) up.getPlatformSender()));

    api.getConfigManager().saveMap(map);

    up.cmsg(ChatColor.BLUE, map.getName() + " §b맵의 범위가 설정되었습니다.");
  }

  @SubCommandHandler(
      parent = {"맵", "능력자 맵"},
      name = "티피올범위설정",
      aliases = {"setTpAllRange"},
      additional = "<맵>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "맵의 티피올 장소 범위를 설정합니다."
  )
  public void ability_map_setTpAllRange(UPlayer up, String[] args) {
    if(!api.getMapManager().existsMap(args[0])) {
      up.wmsg("존재하지 않는 맵입니다.");
      return;
    } else if(!api.getMapManager().existsLeftLocation((Player) up.getPlatformSender())|| !api.getMapManager().existsRightLocation((Player) up.getPlatformSender())) {
      up.wmsg("아직 범위를 설정하지 않았습니다.");
      return;
    }

    GameMap map = api.getMapManager().getMap(args[0]);
    map.setTpAllLimitLocation(api.getMapManager().getLeftLocation((Player) up.getPlatformSender()), api.getMapManager().getRightLocation((Player) up.getPlatformSender()));

    api.getConfigManager().saveMap(map);

    up.cmsg(ChatColor.BLUE, map.getName() + " §b맵의 티피올 장소 범위가 설정되었습니다.");
  }

  @SubCommandHandler(
      parent = {"맵", "능력자 맵"},
      name = "목록",
      aliases = {"list"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "맵 목록을 확인합니다."
  )
  public void ability_map_list(UCommandSender sender, String[] args) {
    sender.cmsg(ChatColor.BLUE, "§b맵 목록(" + api.getMapManager().getMaps().size() + "): §f", String.join(", ", api.getMapManager().getMaps().keySet()));
  }

  @SubCommandHandler(
      parent = {"맵", "능력자 맵"},
      name = "게임맵설정",
      aliases = {"setGameMap"},
      additional = "<맵>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "플레이할 맵을 설정합니다."
  )
  public void ability_map_setGameMap(UCommandSender sender, String[] args) {
    if(!api.getMapManager().existsMap(args[0])) {
      sender.wmsg("존재하지 않는 맵입니다.");
      return;
    }

    api.getMapManager().setPlayingMap(api.getMapManager().getMap(args[0]));

    Core.cbc(ChatColor.BLUE, "§b관리자에 의해 맵이 §f" + api.getMapManager().getPlayingMap().getName() + " §b맵으로 설정되었습니다.");
  }

  @SubCommandHandler(
      parent = {"맵", "능력자 맵"},
      name = "이동",
      aliases = {"teleport"},
      additional = "<맵>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "맵으로 텔레포트합니다."
  )
  public void ability_map_teleport(UPlayer up, String[] args) {
    GameMap map = api.getMapManager().getMap(args[0]);
    if(map == null) {
      up.wmsg("존재하지 않는 맵입니다.");
      return;
    }

    KCore.teleport((Player) up.getPlatformSender(), map.getMapLocation());

    up.cmsg(ChatColor.BLUE, map.getName() + " §b맵으로 텔레포트되었습니다.");
  }

}