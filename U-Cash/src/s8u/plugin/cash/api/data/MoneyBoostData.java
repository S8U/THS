package s8u.plugin.cash.api.data;

import lombok.Data;
import s8u.plugin.cash.api.CashAPI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.UPlayer;

@Data
public class MoneyBoostData extends ExpirableData {

  @Override
  public void onHandleExpire() {
    CashAPI.deleteMoneyBoost(getPlayerKey());
    CashAPI.getSQLManager().logExpire(getPlayerKey().getId(), "MoneyBoost");

    UPlayer up = Core.getUPlayer(getPlayerKey());
    if (up == null) return;
    up.nmsg("");
    up.msg("돈 부스트 사용 기간이 만료되었습니다.");
    up.nmsg("");
  }
}