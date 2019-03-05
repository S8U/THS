package su.plugin.pvpstats.api.manager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.pvpstats.PVPStatsPlugin;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.Stats;
import su.plugin.pvpstats.task.RankingUpdateTask;

@Setter
@Getter
public class RankingManager {

  private Calendar updateTime;

  private RankingUpdateTask rankingUpdateTask = new RankingUpdateTask();

  private List<byte[]> rankingUpdateTimes = new ArrayList<>();

  private List<Stats> dailyRankings = new ArrayList<>(),
      weeklyRankings = new ArrayList<>(),
      monthlyRankings = new ArrayList<>(),
      allRankings = new ArrayList<>();

  //

  private Location hologramLocation;

  private Hologram dailyRankingHologram, weeklyRankingHologram, monthlyRankingHologram, allRankingHologram;

  public int getDailyRanking(PlayerKey playerKey) {
    for(int i = 0; i < dailyRankings.size(); i++) {
      if(dailyRankings.get(i).getPlayerKey().equals(playerKey)) return i + 1;
    }

    return -1;
  }

  public PlayerKey getDailyRankingPlayer(int ranking) {
    return dailyRankings.contains(ranking - 1) ? dailyRankings.get(ranking - 1).getPlayerKey() : null;
  }

  public int getWeeklyRanking(PlayerKey playerKey) {
    for(int i = 0; i < weeklyRankings.size(); i++) {
      if(weeklyRankings.get(i).getPlayerKey().equals(playerKey)) return i + 1;
    }

    return -1;
  }

  public PlayerKey getWeeklyRankingPlayer(int ranking) {
    return weeklyRankings.contains(ranking - 1) ? weeklyRankings.get(ranking - 1).getPlayerKey() : null;
  }

  public int getMonthlyRanking(PlayerKey playerKey) {
    for(int i = 0; i < monthlyRankings.size(); i++) {
      if(monthlyRankings.get(i).getPlayerKey().equals(playerKey)) return i + 1;
    }

    return -1;
  }

  public PlayerKey getMonthlyRankingPlayer(int ranking) {
    return monthlyRankings.contains(ranking - 1) ? monthlyRankings.get(ranking - 1).getPlayerKey() : null;
  }

  public int getAllRanking(PlayerKey playerKey) {
    for(int i = 0; i < allRankings.size(); i++) {
      if(allRankings.get(i).getPlayerKey().equals(playerKey)) return i + 1;
    }

    return -1;
  }

  public PlayerKey getAllRankingPlayer(int ranking) {
    return allRankings.contains(ranking - 1) ? allRankings.get(ranking - 1).getPlayerKey() : null;
  }

  //

  public void updateDailyRanking() {
    dailyRankings = PVPStatsAPI.getSQLManager().getDailyRanking();
  }

  public void updateWeeklyRanking() {
    weeklyRankings = PVPStatsAPI.getSQLManager().getWeeklyRanking();
  }

  public void updateMonthlyRanking() {
    monthlyRankings = PVPStatsAPI.getSQLManager().getMonthlyRanking();
  }

  public void updateAllRanking() {
    allRankings = PVPStatsAPI.getSQLManager().getAllRanking();
  }

  private void msg(UCommandSender sender, String msg) {
    if(!sender.isConsole()) {
      sender.msg(msg);
    }

    msg = ChatColor.stripColor(msg);
    Core.log(msg);
  }

  public void updateRanking(UCommandSender sender) {
    long now = System.currentTimeMillis();
    msg(sender, "§c일간 랭킹 업데이트를 시작합니다.");

    PVPStatsAPI.getRankingManager().updateDailyRanking();

    msg(sender, "§a일간 랭킹 업데이트가 완료되었습니다. (" + (System.currentTimeMillis() - now) + "ms)");

    if(hologramLocation != null) {
      now = System.currentTimeMillis();
      msg(sender, "§c홀로그램 업데이트를 시작합니다.");

      updateRankingHologram(true);

      msg(sender, "§a홀로그램 업데이트가 완료되었습니다. (" + (System.currentTimeMillis() - now) + "ms)");
    }

    now = System.currentTimeMillis();
    msg(sender, "§c주간 랭킹 업데이트를 시작합니다.");

    PVPStatsAPI.getRankingManager().updateWeeklyRanking();

    msg(sender, "§a주간 랭킹 업데이트가 완료되었습니다. (" + (System.currentTimeMillis() - now) + "ms)");

    now = System.currentTimeMillis();
    msg(sender, "§c월간 랭킹 업데이트를 시작합니다.");

    PVPStatsAPI.getRankingManager().updateMonthlyRanking();

    msg(sender, "§a월간 랭킹 업데이트가 완료되었습니다. (" + (System.currentTimeMillis() - now) + "ms)");

    now = System.currentTimeMillis();
    msg(sender, "§c전체 랭킹 업데이트를 시작합니다.");

    PVPStatsAPI.getRankingManager().updateAllRanking();

    msg(sender, "§a전체 랭킹 업데이트가 완료되었습니다. (" + (System.currentTimeMillis() - now) + "ms)");

    if(PVPStatsAPI.isUsePrefixer() && PVPStatsAPI.isGiveDailyRankingPrefix()) {
      now = System.currentTimeMillis();
      msg(sender, "§c일간 랭킹 칭호 업데이트를 시작합니다.");

      PVPStatsAPI.getRankingPrefixManager().updateDailyRankingPrefix();

      msg(sender, "§a일간 랭킹 칭호 업데이트가 완료되었습니다. (" + (System.currentTimeMillis() - now) + "ms)");
    } else if(PVPStatsAPI.isUsePrefixer() && PVPStatsAPI.isGiveWeeklyRankingPrefix()) {
      now = System.currentTimeMillis();
      msg(sender, "§c주간 랭킹 칭호 업데이트를 시작합니다.");

      PVPStatsAPI.getRankingPrefixManager().updateWeeklyRankingPrefix();

      msg(sender, "§a주간 랭킹 칭호 업데이트가 완료되었습니다. (" + (System.currentTimeMillis() - now) + "ms)");
    } else if(PVPStatsAPI.isUsePrefixer() && PVPStatsAPI.isGiveMonthlyRankingPrefix()) {
      now = System.currentTimeMillis();
      msg(sender, "§c월간 랭킹 칭호 업데이트를 시작합니다.");

      PVPStatsAPI.getRankingPrefixManager().updateMonthlyRankingPrefix();

      msg(sender, "§a월간 랭킹 칭호 업데이트가 완료되었습니다. (" + (System.currentTimeMillis() - now) + "ms)");
    } else if(PVPStatsAPI.isUsePrefixer() && PVPStatsAPI.isGiveAllRankingPrefix()) {
      now = System.currentTimeMillis();
      msg(sender, "§c전체 랭킹 칭호 업데이트를 시작합니다.");

      PVPStatsAPI.getRankingPrefixManager().updateAllRankingPrefix();

      msg(sender, "§a전체 랭킹 칭호 업데이트가 완료되었습니다. (" + (System.currentTimeMillis() - now) + "ms)");
    }
  }

  //

  private byte[] getTime(int hour, int minute, int second) {
    return new byte[]{(byte) hour, (byte) minute, (byte) second};
  }

  private byte[] getCurrentTime() {
    Calendar c = Calendar.getInstance();
    return getTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
  }

  private byte[] getEarliestUpdateTime() {
    byte[] currentTime = getCurrentTime();
    byte[] tempTime = null; // Temp time
    int currentTimei = currentTime[0] * 10000 + currentTime[1] * 100 + currentTime[2];
    int tempTimei = 0;
    for(byte[] time : rankingUpdateTimes) {
      int timei = time[0] * 10000 + time[1] * 100 + time[2];
      if(tempTime == null || (tempTimei - currentTimei < 0 && (timei - currentTimei > 0 || timei < tempTimei)) || (tempTimei - currentTimei > 0 && timei < tempTimei)) {
        tempTime = time;
        tempTimei = timei;
      }
    }
    return tempTime;
  }

  public String getRemainingUpdateTimeText() {
    return updateTime == null ? "" : StringUtil.buildTimeString(updateTime.getTimeInMillis() - System.currentTimeMillis());
  }

  public void runRankingUpdateTask() {
    if(rankingUpdateTask.isRunning()) {
      stopRankingUpdateTask();
    }

    updateTime = Calendar.getInstance();
    byte[] ec = getEarliestUpdateTime();
    int ct = getCurrentTime()[0] * 10000 + getCurrentTime()[1] * 100 + getCurrentTime()[2];
    if(ec[0] * 10000 + ec[1] * 100 + ec[2] <= ct) {
      updateTime.set(updateTime.DATE, updateTime.get(updateTime.DATE) + 1);
    }

    updateTime.set(updateTime.HOUR_OF_DAY, ec[0]);
    updateTime.set(updateTime.MINUTE, ec[1]);
    updateTime.set(updateTime.SECOND, ec[2]);
    updateTime.set(updateTime.MILLISECOND, 0);

    rankingUpdateTask.setCount(new Double((updateTime.getTimeInMillis() - System.currentTimeMillis()) / 1000).intValue());
    rankingUpdateTask.runTaskTimerAsynchronously(20, 20);

    Core.log(getRemainingUpdateTimeText() + " 후 랭킹 업데이트가 예약되었습니다.");
  }

  public void stopRankingUpdateTask() {
    rankingUpdateTask.cancel();
  }

  //

  public void setHologramLocation(Location location) {
    this.hologramLocation = location;

    if(dailyRankingHologram != null) {
      dailyRankingHologram.teleport(hologramLocation.clone().add(0, dailyRankingHologram.getHeight(), 0));
      weeklyRankingHologram.teleport(hologramLocation.clone().add(0, weeklyRankingHologram.getHeight(), 0));
      monthlyRankingHologram.teleport(hologramLocation.clone().add(0, monthlyRankingHologram.getHeight(), 0));
      allRankingHologram.teleport(hologramLocation.clone().add(0, allRankingHologram.getHeight(), 0));
    }
  }

  private void updateRankingHologram(List<Stats> rankings, Hologram holo) {
    int size = holo.size();
    for(int i = 4; i < size - 2; i++) {
      holo.removeLine(3);
    }

    if(rankings.size() < 1) {
      holo.insertTextLine(3,"전적이 기록된 플레이어가 없습니다.");
      return;
    }

    for(int i = 1; i <= 10; i++) {
      if(rankings.size() < i) break;
      Stats s = rankings.get(i - 1);
      holo.insertTextLine(holo.size() - 3, "§e" + i + ": §f" + s.getPlayerKey().getDisplayName() + " §e- §f" + s.getKillCount() + " §e킬 §f" + s.getDeathCount() + " §e데스 §f" + s.getAssistCount() + " §e어시스트 §f" + s.getWinStreak() + " §e우승");
    }
  }

  public void updateRankingHologram(boolean updateRanking) {
    if(hologramLocation == null) return;

    String text = "§e다음 업데이트까지 §f" + PVPStatsAPI.getRankingManager().getRemainingUpdateTimeText();

    Bukkit.getScheduler().runTask(PVPStatsPlugin.getInstance(), () -> {
      if(dailyRankingHologram == null) {
        dailyRankingHologram = HologramsAPI.createHologram(PVPStatsPlugin.getInstance(), hologramLocation);
        weeklyRankingHologram = HologramsAPI.createHologram(PVPStatsPlugin.getInstance(), hologramLocation);
        monthlyRankingHologram = HologramsAPI.createHologram(PVPStatsPlugin.getInstance(), hologramLocation);
        allRankingHologram = HologramsAPI.createHologram(PVPStatsPlugin.getInstance(), hologramLocation);

        dailyRankingHologram.appendItemLine(new ItemStack(Material.IRON_SWORD));
        dailyRankingHologram.appendTextLine("§f§l랭킹");
        dailyRankingHologram.appendTextLine(null);
        dailyRankingHologram.appendTextLine(null);

        weeklyRankingHologram.appendItemLine(new ItemStack(Material.IRON_SWORD));
        weeklyRankingHologram.appendTextLine("§f§l랭킹");
        weeklyRankingHologram.appendTextLine(null);
        weeklyRankingHologram.appendTextLine(null);

        monthlyRankingHologram.appendItemLine(new ItemStack(Material.IRON_SWORD));
        monthlyRankingHologram.appendTextLine("§f§l랭킹");
        monthlyRankingHologram.appendTextLine(null);
        monthlyRankingHologram.appendTextLine(null);

        allRankingHologram.appendItemLine(new ItemStack(Material.IRON_SWORD));
        allRankingHologram.appendTextLine("§f§l랭킹");
        allRankingHologram.appendTextLine(null);
        allRankingHologram.appendTextLine(null);

        dailyRankingHologram.getVisibilityManager().setVisibleByDefault(false);
        weeklyRankingHologram.getVisibilityManager().setVisibleByDefault(false);
        monthlyRankingHologram.getVisibilityManager().setVisibleByDefault(false);
        allRankingHologram.getVisibilityManager().setVisibleByDefault(false);

        dailyRankingHologram.appendTextLine(text);
        weeklyRankingHologram.appendTextLine(text);
        monthlyRankingHologram.appendTextLine(text);
        allRankingHologram.appendTextLine(text);

        ((TouchableLine) dailyRankingHologram.appendTextLine("§e§l일간 §f주간 월간 전체")).setTouchHandler(
            new TouchHandler() {
              @Override
              public void onTouch(Player player) {
                dailyRankingHologram.getVisibilityManager().hideTo(player);
                weeklyRankingHologram.getVisibilityManager().showTo(player);

                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 0.7F, 1);

                Core.msg(player, "§e랭킹 표기를 §f주간§e으로 변경했습니다.");
              }
            });

        ((TouchableLine) weeklyRankingHologram.appendTextLine("일간 §e§l주간 §f월간 전체")).setTouchHandler(
            new TouchHandler() {
              @Override
              public void onTouch(Player player) {
                weeklyRankingHologram.getVisibilityManager().hideTo(player);
                monthlyRankingHologram.getVisibilityManager().showTo(player);

                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 0.7F, 1);

                Core.msg(player, "§e랭킹 표기를 §f월간§e으로 변경했습니다.");
              }
            });

        ((TouchableLine) monthlyRankingHologram.appendTextLine("일간 주간 §e§l월간 §f전체")).setTouchHandler(
            new TouchHandler() {
              @Override
              public void onTouch(Player player) {
                monthlyRankingHologram.getVisibilityManager().hideTo(player);
                allRankingHologram.getVisibilityManager().showTo(player);

                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 0.7F, 1);

                Core.msg(player, "§e랭킹 표기를 §f전체§e로 변경했습니다.");
              }
            });

        ((TouchableLine) allRankingHologram.appendTextLine("일간 주간 월간 §e§l전체")).setTouchHandler(
            new TouchHandler() {
              @Override
              public void onTouch(Player player) {
                allRankingHologram.getVisibilityManager().hideTo(player);
                dailyRankingHologram.getVisibilityManager().showTo(player);

                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 0.7F, 1);

                Core.msg(player, "§e랭킹 표기를 §f일간§e으로 변경했습니다.");
              }
            });
      }

      dailyRankingHologram.removeLine(dailyRankingHologram.size() - 2);
      weeklyRankingHologram.removeLine(weeklyRankingHologram.size() - 2);
      monthlyRankingHologram.removeLine(monthlyRankingHologram.size() - 2);
      allRankingHologram.removeLine(allRankingHologram.size() - 2);

      dailyRankingHologram.insertTextLine(dailyRankingHologram.size() - 1, text);
      weeklyRankingHologram.insertTextLine(weeklyRankingHologram.size() - 1, text);
      monthlyRankingHologram.insertTextLine(monthlyRankingHologram.size() - 1, text);
      allRankingHologram.insertTextLine(allRankingHologram.size() - 1, text);

      if(updateRanking) {
        updateRankingHologram(dailyRankings, dailyRankingHologram);
        updateRankingHologram(weeklyRankings, weeklyRankingHologram);
        updateRankingHologram(monthlyRankings, monthlyRankingHologram);
        updateRankingHologram(allRankings, allRankingHologram);
      }

      dailyRankingHologram.teleport(hologramLocation.clone().add(0, dailyRankingHologram.getHeight(), 0));
      weeklyRankingHologram.teleport(hologramLocation.clone().add(0, weeklyRankingHologram.getHeight(), 0));
      monthlyRankingHologram.teleport(hologramLocation.clone().add(0, monthlyRankingHologram.getHeight(), 0));
      allRankingHologram.teleport(hologramLocation.clone().add(0, allRankingHologram.getHeight(), 0));
    });
  }

}
