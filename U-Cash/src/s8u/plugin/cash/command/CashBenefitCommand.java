package s8u.plugin.cash.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import s8u.plugin.cash.PermissionList;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.data.ColorDisplayNameData;
import s8u.plugin.cash.api.data.DisplayNameData;
import s8u.plugin.cash.api.data.MoneyBoostData;
import s8u.plugin.cash.api.sql.BenefitType;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.NumberUtil;

public class CashBenefitCommand implements UCommandListener {

  // 돈부스트
  @SubCommandHandler(
      parent = "캐시",
      name = "돈부스트",
      aliases = { "moneyBoost" },
      permission = PermissionList.CASH_ADMIN,
      usage = "돈부스트 명령어를 확인합니다."
  )
  public void cash_moneyBoost_help(UCommandSender sender, String[] args) {
    Core.nmsg(sender, "§e§l[ U-Cash | Money Boost ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("캐시 돈부스트", 1)) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

  @SubCommandHandler(
      parent = "캐시 돈부스트",
      name = "설정",
      aliases = { "set" },
      additional = "<플레이어> <yyyy-MM-dd HH:mm:ss>",
      minArgs = 3,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 돈부스트 기간을 설정합니다."
  )
  public void cash_moneyBoost_set(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    String timeStr = args[1] + " " + args[2];
    try {
      LocalDateTime expireTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      if (expireTime.isBefore(LocalDateTime.now())) {
        sender.wmsg("기간은 과거로 설정할 수 없습니다.");
        return;
      }

      MoneyBoostData data = CashAPI.setMoneyBoost(target, expireTime);
      CashAPI.getSQLManager().logBenefit(target.getId(),
          sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
          BenefitType.MONEY_BOOST, 0, expireTime);
      sender.msg(target.getName() + " §e님의 돈 부스트 기간을 §f" + data.getFormattedExpireTime() + " §e까지로 설정했습니다.");
    } catch (DateTimeParseException e) {
      sender.wmsg("기간은 yyyy-MM-dd HH:mm:ss 포맷으로만 입력 가능합니다.");
    }
  }

  @SubCommandHandler(
      parent = "캐시 돈부스트",
      name = "추가",
      aliases = { "add", "연장", "extend" },
      additional = "<플레이어> <d:일 / h:시간 / m:분 / s:초>",
      minArgs = 2,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 돈부스트 기간을 연장합니다."
  )
  public void cash_moneyBoost_add(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    int[] timeArr = getTimeArr(args, 1);
    MoneyBoostData data = CashAPI.extendMoneyBoost(target, timeArr[0], timeArr[1], timeArr[2], timeArr[3]);
    CashAPI.getSQLManager().logBenefit(target.getId(),
        sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
        BenefitType.MONEY_BOOST, 0, data.getExpireTime());

    sender.msg(target.getName() + " §e님의 돈 부스트 기간을 §f" + data.getFormattedExpireTime() + " §e까지 연장했습니다.");
  }

  @SubCommandHandler(
      parent = "캐시 돈부스트",
      name = "빼기",
      aliases = { "sub", "subtract" },
      additional = "<플레이어> <d:일 / h:시간 / m:분 / s:초>",
      minArgs = 2,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 돈부스트 기간을 줄입니다."
  )
  public void cash_moneyBoost_sub(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    int[] timeArr = getTimeArr(args, 1);
    MoneyBoostData data = CashAPI.subtractMoneyBoost(target, timeArr[0], timeArr[1], timeArr[2], timeArr[3]);
    CashAPI.getSQLManager().logBenefit(target.getId(),
        sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
        BenefitType.MONEY_BOOST, 0, data.getExpireTime());

    sender.msg(target.getName() + " §e님의 돈 부스트 기간을 §f" + data.getFormattedExpireTime() + " §e까지 줄였습니다.");
  }

  // 기간 닉네임
  @SubCommandHandler(
      parent = "캐시",
      name = "가상닉네임",
      aliases = { "displayName" },
      permission = PermissionList.CASH_ADMIN,
      usage = "가상닉네임 명령어를 확인합니다."
  )
  public void cash_displayName_help(UCommandSender sender, String[] args) {
    Core.nmsg(sender, "§e§l[ U-Cash | DisplayName ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("캐시 가상닉네임", 1)) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

  @SubCommandHandler(
      parent = "캐시 가상닉네임",
      name = "설정",
      aliases = { "set" },
      additional = "<플레이어> <yyyy-MM-dd HH:mm:ss> <가상닉네임>",
      minArgs = 4,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 가상닉네임 기간을 연장합니다."
  )
  public void cash_displayName_set(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    String timeStr = args[1] + " " + args[2];
    try {
      LocalDateTime expireTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      if (expireTime.isBefore(LocalDateTime.now())) {
        sender.wmsg("기간은 과거로 설정할 수 없습니다.");
        return;
      }

      String displayName = String.join(" ", args);
      displayName = ChatColor.translateAlternateColorCodes('&', displayName.substring((args[0] + " " + timeStr + " ").length()));

      if (Core.getSQLManager().getPlayerKeyByDisplayName(displayName) != null) {
        sender.wmsg("이미 사용 중인 닉네임입니다.");
        return;
      }

      DisplayNameData data = CashAPI.setDisplayName(target, displayName, expireTime);
      CashAPI.getSQLManager().logBenefit(target.getId(),
          sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
          BenefitType.DISPLAY_NAME, 0, data.getExpireTime());
      sender.msg(target.getName() + " §e님의 가상닉네임 '§f" + displayName + "' §f기간을 §f" + data.getFormattedExpireTime() + " §e까지로 설정했습니다.");
    } catch (DateTimeParseException e) {
      sender.wmsg("기간은 yyyy-MM-dd HH:mm:ss 포맷으로만 입력 가능합니다.");
    }
  }

  @SubCommandHandler(
      parent = "캐시 가상닉네임",
      name = "추가",
      aliases = { "add", "연장", "extend" },
      additional = "<플레이어> <d:일 / h:시간 / m:분 / s:초>",
      minArgs = 2,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 가상닉네임 기간을 연장합니다."
  )
  public void cash_displayName_add(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    int[] timeArr = getTimeArr(args, 1);
    DisplayNameData data = CashAPI.extendDisplayName(target, timeArr[0], timeArr[1], timeArr[2], timeArr[3]);
    CashAPI.getSQLManager().logBenefit(target.getId(),
        sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
        BenefitType.DISPLAY_NAME, 0, data.getExpireTime());

    if (data.isExpired()) {
      sender.msg(target.getName() + " §e님의 가상닉네임 기간이 삭제되었습니다.");
    } else {
      sender.msg(target.getName() + " §e님의 가상닉네임 기간을 §f" + data.getFormattedExpireTime() + " §e까지 연장했습니다.");
    }
  }

  @SubCommandHandler(
      parent = "캐시 가상닉네임",
      name = "빼기",
      aliases = { "sub", "subtract" },
      additional = "<플레이어> <d:일 / h:시간 / m:분 / s:초>",
      minArgs = 2,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 가상닉네임 기간을 줄입니다."
  )
  public void cash_displayName_sub(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    int[] timeArr = getTimeArr(args, 1);
    DisplayNameData data = CashAPI.subtractDisplayName(target, timeArr[0], timeArr[1], timeArr[2], timeArr[3]);
    CashAPI.getSQLManager().logBenefit(target.getId(),
        sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
        BenefitType.DISPLAY_NAME, 0, data.getExpireTime());

    if (data.isExpired()) {
      sender.msg(target.getName() + " §e님의 가상닉네임 기간이 삭제되었습니다.");
    } else {
      sender.msg(target.getName() + " §e님의 가상닉네임 기간을 §f" + data.getFormattedExpireTime() + " §e까지 줄였습니다.");
    }
  }

  // 색깔 닉네임
  @SubCommandHandler(
      parent = "캐시",
      name = "색깔닉네임",
      aliases = { "colorDisplayName" },
      permission = PermissionList.CASH_ADMIN,
      usage = "돈부스트 명령어를 확인합니다."
  )
  public void cash_colorDisplayName_help(UCommandSender sender, String[] args) {
    Core.nmsg(sender, "§e§l[ U-Cash | Color DisplayName ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("캐시 색깔닉네임", 1)) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

  @SubCommandHandler(
      parent = "캐시 색깔닉네임",
      name = "추가",
      aliases = { "add", "연장", "extend" },
      additional = "<플레이어> <색깔코드> <d:일 / h:시간 / m:분 / s:초>",
      minArgs = 3,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 색깔닉네임 기간을 연장합니다."
  )
  public void cash_colorDisplayName_add(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    int[] timeArr = getTimeArr(args, 2);
    ChatColor color = ChatColor.getByChar(args[1].contains("&") ? args[1].charAt(1) : args[1].charAt(0));

    ColorDisplayNameData data = CashAPI.extendColorDisplayName(target, color, timeArr[0], timeArr[1], timeArr[2], timeArr[3]);
    CashAPI.getSQLManager().logBenefit(target.getId(),
        sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
        BenefitType.COLOR_DISPLAY_NAME.color(data.getColor()), 0, data.getExpireTime());

    if (data.isExpired()) {
      sender.msg(target.getName() + " §e님의 " + color + "&" + color.getChar() + " §e색깔닉네임 기간이 삭제되었습니다.");
    } else {
      sender.msg(target.getName() + " §e님의 " + color + "&" + color.getChar() + " §e색깔닉네임 기간을 §f" + data.getFormattedExpireTime() + " §e까지 연장했습니다.");
    }
  }

  @SubCommandHandler(
      parent = "캐시 색깔닉네임",
      name = "빼기",
      aliases = { "sub", "subtract" },
      additional = "<플레이어> <색깔코드> <d:일 / h:시간 / m:분 / s:초>",
      minArgs = 3,
      permission = PermissionList.CASH_ADMIN,
      usage = "플레이어의 색깔닉네임 기간을 줄입니다."
  )
  public void cash_colorDisplayName_sub(UCommandSender sender, String[] args) {
    PlayerKey target = PlayerKey.getPlayerKey(args[0]);
    if (target == null) {
      sender.wmsg("존재하지 않는 플레이어입니다.");
      return;
    }

    int[] timeArr = getTimeArr(args, 2);
    ChatColor color = ChatColor.getByChar(args[1].contains("&") ? args[1].charAt(1) : args[1].charAt(0));

    ColorDisplayNameData data = CashAPI.subtractColorDisplayName(target, color, timeArr[0], timeArr[1], timeArr[2], timeArr[3]);
    CashAPI.getSQLManager().logBenefit(target.getId(),
        sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(),
        BenefitType.COLOR_DISPLAY_NAME.color(data.getColor()), 0, data.getExpireTime());

    if (data.isExpired()) {
      sender.msg(target.getName() + " §e님의 " + color + "&" + color.getChar() + " §e색깔닉네임 기간이 삭제되었습니다.");
    } else {
      sender.msg(target.getName() + " §e님의 " + color + "&" + color.getChar() + " §e색깔닉네임 기간을 §f" + data.getFormattedExpireTime() + " §e까지 줄였습니다.");
    }
  }

  private int[] getTimeArr(String[] args, int startArgs) {
    int[] timeArr = new int[] { 0, 0, 0, 0 };
    for (int i = startArgs; i <= startArgs + 4; i++) {
      if (args.length < i + 1 || args[i].length() < 3) break;

      Integer value = NumberUtil.getInteger(args[i].substring(2));
      if (value == null) continue;

      String timeStr = args[i].substring(0, 2);
      switch (timeStr) {
        case "d:":
          timeArr[0] = value;
          break;
        case "h:":
          timeArr[1] = value;
          break;
        case "m:":
          timeArr[2] = value;
          break;
        case "s:":
          timeArr[3] = value;
          break;
      }
    }

    return timeArr;
  }

}