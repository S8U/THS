package su.plugin.core.common.command;

import java.util.UUID;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NumberUtil;

public class UPlayerManagerCommand implements UCommandListener {

  @SubCommandHandler(
      parent = "core",
      name = "uPlayerManager",
      aliases = {"upm", "플레이어관리"},
      permission = "core.admin",
      usage = "U-Core 기반 플레이어 관리 명령어를 확인합니다."
  )
  public void core_uPlayerManager(UCommandSender sender, String[] args) {
    sender.nmsg("§e§l[ U-Core | UPlayerManager ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("core uPlayerManager", 1)) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

  @SubCommandHandler(
      parent = "core uPlayerManager",
      name = "list",
      aliases = {"l", "목록"},
      additional = "(<페이지>)",
      permission = "core.admin",
      usage = "U-Core 기반 플레이어 목록을 확인합니다."
  )
  public void core_uPlayerManager_list(UCommandSender sender, String[] args) {
    int playerCount = Core.getSQLManager().getPlayerKeyCount();
    if(playerCount < 1) {
      sender.wmsg("아직 플레이어가 존재하지 않습니다.");
      return;
    }

    Integer page = 1;
    int maxPage = Double.valueOf(Math.ceil(playerCount / 10)).intValue() + 1;
    if(args.length > 0) {
      page = NumberUtil.getInteger(args[0]);

      if(page == null || page > maxPage) {
        sender.wmsg("페이지는 1 ~" + maxPage + "의 정수만 입력 가능합니다.");
        return;
      }
    }

    sender.nmsg("§e[ 플레이어 목록 (" + page + " / " + maxPage + ") ]");
    for(int i = 1; i <= 10; i++) {
      PlayerKey playerKey = PlayerKey.getPlayerKey((page - 1) * 10 + i);
      if(playerKey == null) break;

      String displayName = playerKey.getDisplayName();

      sender.nmsg(playerKey.getId()
          + " §e: §f" + playerKey.getName()
          + " §e/ §f" + playerKey.getUuid()
          + " §e/ §f" + playerKey.isOnlineMode()
          + (displayName.equalsIgnoreCase(playerKey.getName()) ? "" : " §e/ §f" + displayName));
    }
  }

  private void playerInfoCmd(UCommandSender sender, PlayerKey targetPlayerKey) {
    if(targetPlayerKey == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    sender.nmsg("§e[ §f" + targetPlayerKey.getName() + " §e님의 정보 ]");
    sender.nmsg("§eId: §f" + targetPlayerKey.getId());
    sender.nmsg("§eUuid: §f" + targetPlayerKey.getUuid());

    sender.nmsg("§e닉네임 표기: §f" + targetPlayerKey.getDisplayName());

    sender.nmsg("§e정품: §f" + (targetPlayerKey.isOnlineMode() ? "O" : "X"));
  }

  @SubCommandHandler(
      parent = "core uPlayerManager",
      name = "info",
      aliases = {"i", "정보"},
      additional = "<닉네임>",
      minArgs = 1,
      permission = "core.admin",
      usage = "U-Core 기반 플레이어 정보를 확인합니다."
  )
  public void core_uPlayerManager_info(UCommandSender sender, String[] args) {
    playerInfoCmd(sender, PlayerKey.getPlayerKeyByDisplayName(args[0]));
  }

  @SubCommandHandler(
      parent = "core uPlayerManager",
      name = "infoById",
      aliases = {"ibi", "정보아이디"},
      additional = "<Id>",
      minArgs = 1,
      permission = "core.admin",
      usage = "U-Core 기반 플레이어 정보를 확인합니다."
  )
  public void core_uPlayerManager_infoById(UCommandSender sender, String[] args) {
    Integer id = NumberUtil.getInteger(args[0]);
    if(id == null) {
      sender.wmsg("Id는 정수만 입력 가능합니다.");
      return;
    }

    playerInfoCmd(sender, PlayerKey.getPlayerKey(id));
  }

  private void transferCmd(UCommandSender sender, PlayerKey playerKey1, PlayerKey playerKey2) {
    if (playerKey1 == null || playerKey2 == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    Core.log("PlayerKey " + playerKey1 + " 현재 정보 (name=" + playerKey1.getName() + ", uuid=" + playerKey1.getUuid() + ", onlineMode=" + playerKey1.isOnlineMode() + ")");
    if(!sender.isConsole()) {
      sender.msg("PlayerKey " + playerKey1 + " 현재 정보 (name=" + playerKey1.getName() + ", uuid=" + playerKey1.getUuid() + ", onlineMode=" + playerKey1.isOnlineMode() + ")");
    }

    Core.log("PlayerKey " + playerKey2 + " 현재 정보 (name=" + playerKey2.getName() + ", uuid=" + playerKey2.getUuid() + ", onlineMode=" + playerKey2.isOnlineMode() + ")");
    if(!sender.isConsole()) {
      sender.msg("PlayerKey " + playerKey2 + " 현재 정보 (name=" + playerKey2.getName() + ", uuid=" + playerKey2.getUuid() + ", onlineMode=" + playerKey2.isOnlineMode() + ")");
    }

    Core.log("PlayerKey " + playerKey1 + ", " + playerKey2 + " 교환을 시작합니다.");
    if(!sender.isConsole()) {
      sender.msg("PlayerKey " + playerKey1 + ", " + playerKey2 + " 교환을 시작합니다.");
    }

    String tempName = playerKey1.getName();
    UUID tempUuid = playerKey1.getUuid();
    boolean tempOnlineMode = playerKey1.isOnlineMode();

    playerKey1.updatePlayerKey(playerKey2.getName(), playerKey2.getUuid(), playerKey2.isOnlineMode());

    Core.log("PlayerKey " + playerKey1 + " 변경 완료 (name=" + playerKey1.getName() + ", uuid=" + playerKey1.getUuid() + ", onlineMode=" + playerKey1.isOnlineMode() + ")");
    if(!sender.isConsole()) {
      sender.msg("PlayerKey " + playerKey1 + " 변경 완료 (name=" + playerKey1.getName() + ", uuid=" + playerKey1.getUuid() + ", onlineMode=" + playerKey1.isOnlineMode() + ")");
    }

    playerKey2.updatePlayerKey(tempName, tempUuid, tempOnlineMode);

    Core.log("PlayerKey " + playerKey2 + " 변경 완료 (name=" + playerKey2.getName() + ", uuid=" + playerKey2.getUuid() + ", onlineMode=" + playerKey2.isOnlineMode() + ")");
    if(!sender.isConsole()) {
      sender.msg("PlayerKey " + playerKey2 + " 변경 완료 (name=" + playerKey2.getName() + ", uuid=" + playerKey2.getUuid() + ", onlineMode=" + playerKey2.isOnlineMode() + ")");
    }

    Core.log(playerKey1 + ", " + playerKey2 + " 교환이 완료되었습니다.");
    if(!sender.isConsole()) {
      sender.msg(playerKey1 + ", " + playerKey2 + " 교환이 완료되었습니다.");
    }
  }

  @SubCommandHandler(
      parent = "core uPlayerManager",
      name = "transfer",
      aliases = {"t", "계정이전"},
      additional = "<닉네임 1> <닉네임 2>",
      minArgs = 2,
      permission = "core.admin",
      usage = "U-Core 기반 플레이어를 계정을 교환시킵니다."
  )
  public void core_uPlayerManager_transfer(UCommandSender sender, String[] args) {
    transferCmd(sender, PlayerKey.getPlayerKeyByDisplayName(args[0]), PlayerKey.getPlayerKeyByDisplayName(args[1]));
  }

  @SubCommandHandler(
      parent = "core uPlayerManager",
      name = "transferById",
      aliases = {"tbi", "계정이전아이디"},
      additional = "<Id 1> <Id 2>",
      minArgs = 2,
      permission = "core.admin",
      usage = "U-Core 기반 플레이어를 계정을 교환시킵니다."
  )
  public void core_uPlayerManager_transferById(UCommandSender sender, String[] args) {
    Integer id1 = NumberUtil.getInteger(args[0]);
    Integer id2 = NumberUtil.getInteger(args[1]);
    if(id1 == null || id2 == null) {
      sender.wmsg("Id는 정수만 입력 가능합니다.");
      return;
    }

    transferCmd(sender, PlayerKey.getPlayerKey(id1), PlayerKey.getPlayerKey(id2));
  }

}