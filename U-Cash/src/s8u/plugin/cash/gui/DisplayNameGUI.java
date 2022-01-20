package s8u.plugin.cash.gui;

import java.time.LocalDateTime;
import org.bukkit.entity.Player;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.data.DisplayNameData;
import s8u.plugin.cash.api.sql.BenefitType;
import s8u.plugin.cash.api.sql.Type;
import su.plugin.core.bukkit.api.gui.sign.SignGUI;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class DisplayNameGUI extends SignGUI {

  public DisplayNameGUI() {
    super("", "^^^^^^^", "사용할 닉네임을", "입력하세요!");
  }

  @Override
  public void onSignComplete(Player p, String[] lines) {
    if (lines[0].trim().isEmpty()) return;

    PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

    if (!CashAPI.subCash(playerKey, 5000)) {
      Core.wmsg(p, "캐시 잔액이 부족합니다.");
      return;
    }
    CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, 5000);


    if (!(lines[0].matches(".*[ㄱ-ㅣ가-힣]+.*") && lines[0].matches("[ㄱ-ㅣ가-힣a-zA-Z0-9_]+"))) {
      Core.wmsg(p, "닉네임은 한글 + 영어 또는 숫자만 사용 가능합니다.");
      return;
    }

    lines[0] = ChatColor.translateAlternateColorCodes('&', lines[0]);
    if (Core.getSQLManager().getPlayerKeyByDisplayName(lines[0]) != null) {
      Core.wmsg(p, "이미 사용 중인 닉네임입니다.");
      return;
    }

    DisplayNameData data = CashAPI.setDisplayName(playerKey, lines[0], LocalDateTime.now().plusDays(90));
    CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.DISPLAY_NAME.displayName(lines[0]), 5000, data.getExpireTime());

    Core.msg(p, "§f닉네임 " + lines[0] + "§f을(를) 구입했습니다.");
  }

}
