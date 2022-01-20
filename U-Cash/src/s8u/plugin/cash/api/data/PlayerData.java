package s8u.plugin.cash.api.data;

import java.util.HashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.player.PlayerKey;

@Data
@RequiredArgsConstructor
public class PlayerData {

  private final PlayerKey playerKey;

  private int cash;

  private MoneyBoostData moneyBoostData;
  private DisplayNameData displayNameData;
  private HashMap<ChatColor, ColorDisplayNameData> colorDisplayNameDatas;
  //private List<PrefixData> prefixDatas;

}