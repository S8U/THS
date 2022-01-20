package s8u.plugin.cash.lib.prefixer;

import java.util.ArrayList;
import java.util.List;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.prefixer.api.PrefixerAPI;

public class PrefixerHandler {

  private static String prefix = "[ Donator ]";
  private static List<String> prefixes = new ArrayList<>();

  static {
    for (int i = 0; i < 10; i++) {
      prefixes.add("§" + i + prefix);
    }

    for (char c = 'a'; c <= 'f'; c++) {
      prefixes.add("§" + c + prefix);
    }
  }

  public static String giveDonatorPrefix(PlayerKey playerKey) {
    List<String> ownPrefix = PrefixerAPI.getPrefixes(playerKey);
    if (ownPrefix.containsAll(prefixes)) return null;

    String prefix = null;
    do {
      prefix = prefixes.get(NumberUtil.random(prefixes.size()));
    } while (!PrefixerAPI.addPrefix(playerKey, prefix));

    return prefix;
  }


}