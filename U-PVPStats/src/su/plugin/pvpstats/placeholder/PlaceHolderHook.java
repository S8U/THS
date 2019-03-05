package su.plugin.pvpstats.placeholder;

import java.util.Arrays;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.pvpstats.PVPStatsPlugin;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.PSPlayer;
import su.plugin.pvpstats.api.object.Stats;

public class PlaceHolderHook extends EZPlaceholderHook {

  public PlaceHolderHook() {
    super(PVPStatsPlugin.getInstance(), "pvpstats");
  }

  @Override
  public String onPlaceholderRequest(Player p, String identifier) {
    String lower = identifier.toLowerCase();

    PSPlayer psp = new PSPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
    if(psp == null) return null;

    int i = -1;

    for(String str : Arrays.asList("daily", "weekly", "monthly", "all")) {
      Stats stats = null;
      if(stats.equals("daily")) {
        stats = psp.getDailyStats();
      } else if(stats.equals("weekly")) {
        stats = psp.getWeeklyStats();
      } else if(stats.equals("monthly")) {
        stats = psp.getMonthlyStats();
      } else if(stats.equals("all")) {
        stats = psp.getAllStats();
      }

      if(lower.equals(str + "_kill_count")) {
        i = stats.getKillCount();
      } else if(lower.equals(str + "_death_count")) {
        i = stats.getDeathCount();
      } else if(lower.equals(str + "_assist_count")) {
        i = stats.getAssistCount();
      } else if(lower.equals(str + "_win_count")) {
        i = stats.getWinCount();
      } else if(lower.equals(str + "_quit_count")) {
        i = stats.getQuitCount();
      } else if(lower.equals(str + "_kill_streak")) {
        i = stats.getKillStreak();
      } else if(lower.equals(str + "_death_streak")) {
        i = stats.getDeathStreak();
      } else if(lower.equals(str + "_win_streak")) {
        i = stats.getWinStreak();
      } else if(lower.equals(str + "_max_kill_streak")) {
        i = stats.getMaxKillStreak();
      } else if(lower.equals(str + "_max_death_streak")) {
        i = stats.getMaxDeathStreak();
      } else if(lower.equals(str + "_max_win_streak")) {
        i = stats.getMaxWinStreak();
      } else if(lower.equals(str + "_ranking")) {
        if(stats.equals("daily")) {
          PVPStatsAPI.getRankingManager().getDailyRanking(psp.getPlayerKey());
        } else if(stats.equals("weekly")) {
          PVPStatsAPI.getRankingManager().getWeeklyRanking(psp.getPlayerKey());
        } else if(stats.equals("monthly")) {
          PVPStatsAPI.getRankingManager().getMonthlyRanking(psp.getPlayerKey());
        } else if(stats.equals("all")) {
          PVPStatsAPI.getRankingManager().getAllRanking(psp.getPlayerKey());
        }
      }
    }

    return i + "";
  }

}
