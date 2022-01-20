package s8u.plugin.cash.command;

import s8u.plugin.cash.PermissionList;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.sql.Type;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.NumberUtil;

public class CashAdminCommand implements UCommandListener {

  @SubCommandHandler(
      parent = "캐시",
      name = "설정",
      aliases = {"set"},
      additional = "<플레이어> <캐시>",
      minArgs = 2,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 캐시를 설정합니다."
  )
  public void cash_set(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    if (!NumberUtil.isInteger(args[1])) {
      sender.wmsg("캐시는 정수만 입력 가능합니다.");
      return;
    }

    int cash = NumberUtil.getInteger(args[1]);
    CashAPI.setCash(target, cash);
    CashAPI.getSQLManager().logCash(target.getId(),
        sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
        Type.SET, cash);
    sender.msg(target.getName() + " §e님의 캐시 잔액을 §f" + cash + "§e원으로 설정했습니다.");
  }

  @SubCommandHandler(
      parent = "캐시",
      name = "추가",
      aliases = {"add"},
      additional = "<플레이어> <캐시>",
      minArgs = 2,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 캐시를 추가합니다."
  )
  public void cash_add(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    if (!NumberUtil.isInteger(args[1])) {
      sender.wmsg("캐시는 정수만 입력 가능합니다.");
      return;
    }

    int cash = NumberUtil.getInteger(args[1]);
    CashAPI.addCash(target, cash);
    CashAPI.getSQLManager().logCash(target.getId(),
        sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
        Type.ADD, cash);
    sender.msg(target.getName() + " §e님의 캐시 잔액에 §f" + cash + "§e원을 추가했습니다.");
  }

  @SubCommandHandler(
      parent = "캐시",
      name = "빼기",
      aliases = {"sub", "subtract"},
      additional = "<플레이어> <캐시>",
      minArgs = 2,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 캐시를 차감합니다."
  )
  public void cash_sub(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    if (!NumberUtil.isInteger(args[1])) {
      sender.wmsg("캐시는 정수만 입력 가능합니다.");
      return;
    }

    int cash = NumberUtil.getInteger(args[1]);
    CashAPI.subCash(target, cash);
    CashAPI.getSQLManager().logCash(target.getId(),
        sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
        Type.SUBTRACT, cash);
    sender.msg(target.getName() + " §e님의 캐시 잔액에 §f" + cash + "§e원을 차감했습니다.");
  }

  @SubCommandHandler(
      parent = "캐시",
      name = "확인",
      aliases = {"check"},
      additional = "<플레이어>",
      minArgs = 1,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 캐시를 확인합니다."
  )
  public void cash_check(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    sender.msg(target.getName() + "§e님의 캐시 잔액: §f" + CashAPI.getCash(target) + "§e원");
  }

}