package s8u.plugin.crackwhitelist.bungee;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.Cleanup;
import lombok.SneakyThrows;
import s8u.plugin.crackwhitelist.api.CrackWhiteListAPI;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.NumberUtil;

public class WhiteListCommand implements UCommandListener {

  private CrackWhiteListAPI api = GCrackWhiteListPlugin.getApi();

  @CommandHandler(
      name = "CrackWhiteList",
      aliases = {"복돌화이트리스트", "복돌화리", "비정품화이트리스트", "비정품화리", "cwl"},
      usePlatformPrefix = true,
      usage = "비정품 화이트리스트 명령어를 확인합니다.",
      permission = "crackwhitelist.admin"
  )
  public void crackWhiteList(UCommandSender sender, String[] args, Command cmd) {
    sender.nmsg("§7§l[ U-CrackWhiteList ]");
    for(Command sc : Core.getCommandManager().getSubCommands(cmd.getName(), 1)) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

  //

  @SubCommandHandler(
      parent = "CrackWhiteList",
      name = "켜기",
      aliases = {"on"},
      usage = "비정품 화이트리스트를 켭니다.",
      permission = "crackwhitelist.admin"
  )
  public void crackWhiteList_on(UCommandSender sender, String[] args) {
    api.toggleWhiteList(true);

    sender.msg("비정품 화이트리스트를 켰습니다.");
  }

  @SubCommandHandler(
      parent = "CrackWhiteList",
      name = "끄기",
      aliases = {"off"},
      usage = "비정품 화이트리스트를 끕니다.",
      permission = "crackwhitelist.admin"
  )
  public void crackWhiteList_off(UCommandSender sender, String[] args) {
    api.toggleWhiteList(false);

    sender.msg("비정품 화이트리스트를 껐습니다.");
  }

  @SubCommandHandler(
      parent = "CrackWhiteList",
      name = "메시지설정",
      aliases = {"setMessage"},
      additional = "<메시지>",
      minArgs = 1,
      usage = "비정품 화이트리스트 차단 메시지를 변경합니다.",
      permission = "crackwhitelist.admin"
  )
  public void crackWhiteList_setMessage(UCommandSender sender, String[] args) {
    String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args).replace("\\n", "\n"));

    api.setDisallowMessage(message);

    sender.msg("비정품 화이트리스트 차단 메시지를 '" + message + "§f'로 설정했습니다.");
  }

  //

  @SubCommandHandler(
      parent = "CrackWhiteList",
      name = "추가",
      aliases = {"add"},
      additional = "<플레이어>",
      minArgs = 1,
      usage = "비정품 화이트리스트에 플레이어를 추가합니다.",
      permission = "crackwhitelist.admin"
  )
  public void crackWhiteList_add(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    if (api.addWhiteList(target)) {
      api.getSQLManager().logWhiteList(target, sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(), "ADD");
      sender.msg(target.getName() + "님을 비정품 화이트리스트에 추가했습니다.");
    } else {
      sender.wmsg("이미 비정품 화이트리스트에 등록되어 있습니다.");
    }
  }

  @SubCommandHandler(
      parent = "CrackWhiteList",
      name = "삭제",
      aliases = {"remove"},
      additional = "<플레이어>",
      minArgs = 1,
      usage = "비정품 화이트리스트에서 플레이어를 삭제합니다.",
      permission = "crackwhitelist.admin"
  )
  public void crackWhiteList_remove(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    if (api.removeWhiteList(target)) {
      api.getSQLManager().logWhiteList(target, sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(), "REMOVE");
      sender.msg(target.getName() + "님을 비정품 화이트리스트에서 삭제했습니다.");
    } else {
      sender.wmsg("비정품 화이트리스트에 등록되어 있지 않은 플레이어입니다.");
    }
  }

  @SubCommandHandler(
      parent = "CrackWhiteList",
      name = "확인",
      aliases = {"check"},
      additional = "<플레이어>",
      minArgs = 1,
      usage = "플레이어가 비정품 화이트리스트에 등록되어 있는지 확인합니다.",
      permission = "crackwhitelist.admin"
  )
  public void crackWhiteList_check(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    sender.msg(target.getName() + "님의 화이트리스트 여부: " + (api.isWhiteListed(target) ? "O" : "X"));
  }

  @SubCommandHandler(
      parent = "CrackWhiteList",
      name = "목록",
      aliases = {"list"},
      additional = "(<페이지>)",
      usage = "비정품 화이트리스트 플레이어 목록을 확인합니다.",
      permission = "crackwhitelist.admin"
  )
  @SneakyThrows (SQLException.class)
  public void crackWhiteList_list(UCommandSender sender, String[] args) {
    int maxPage = 0;

    @Cleanup PreparedStatement state = api.getSQLManager().getWhiteListTable().select("count(*)");
    @Cleanup ResultSet result = state.executeQuery();
    if (result.next()) {
      maxPage = (int) Math.ceil((double) result.getInt("count(*)") / 10);
    }

    if (maxPage < 1) {
      sender.wmsg("비정품 화이트리스트에 등록된 플레이어가 없습니다.");
      return;
    }

    Integer page = 1;
    if (args.length > 0) {
      page = NumberUtil.getInteger(args[0]);
    }

    if (page == null || page > maxPage) {
      sender.wmsg("페이지는 1~" + maxPage + "의 정수만 입력 가능합니다.");
      return;
    }

    sender.nmsg("§7[ 비정품 화이트리스트 목록 ( " + page + " / " + maxPage + " ) ]");

    @Cleanup PreparedStatement state2 = api.getSQLManager().getWhiteListTable().select("*", "limit " + ((page - 1) * 10) + ", 10");
    @Cleanup ResultSet result2 = state2.executeQuery();
    while (result2.next()) {
      sender.nmsg(PlayerKey.getPlayerKey(result2.getInt("player_id")).getName());
    }
  }

}