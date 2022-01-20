package s8u.plugin.cash.api.data;

import lombok.Data;
import s8u.plugin.cash.api.CashAPI;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.UPlayer;

@Data
public class ColorDisplayNameData extends ExpirableData {

  private ChatColor color;

  @Override
  public void onHandleExpire() {
    CashAPI.deleteColorDisplayName(getPlayerKey(), color);
    CashAPI.getSQLManager().logExpire(getPlayerKey().getId(), "ColorDisplayName");

    UPlayer up = Core.getUPlayer(getPlayerKey());
    if (up == null) return;
    up.nmsg("");
    up.msg("색깔닉네임 " + color + "&" + color.getChar() + " §f사용 기간이 만료되었습니다.");
    up.nmsg("");
  }

}