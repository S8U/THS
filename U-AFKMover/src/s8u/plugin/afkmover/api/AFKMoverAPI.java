package s8u.plugin.afkmover.api;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import s8u.plugin.afkmover.PluginChecker;
import s8u.plugin.afkmover.api.task.AFKCheckTask;
import su.plugin.core.bukkit.api.util.BungeeUtil;
import su.plugin.core.common.api.util.DebugUtil;
import su.plugin.core.common.api.util.StringUtil;

public class AFKMoverAPI {

  /* 이동될 채널 */
  @Setter
  @Getter
  private static String targetChannel;

  /* 잠수로 전환되는 시간 */
  @Setter
  @Getter
  private static int afkSwitchingTime;

  /* 다른 플레이어를 가릴지 여부 */
  @Setter
  @Getter
  private static boolean hideOtherPlayers;

  /* 활동했을 때 타겟 채널로 이동할지 여부 */
  @Setter
  @Getter
  private static boolean sendTargetChannelOnActivity;

  /* 잠수 채널, 일반 채널 여부 */
  @Setter
  @Getter
  private static boolean afkChannel, normalChannel;

  /* 마지막으로 활동한 시간 */
  @Getter
  private static HashMap<String, Long> lastActivity = new HashMap<>();

  /* 잠수 체크 Task */
  @Getter
  private static AFKCheckTask afkCheckTask;

  public void init() {
    afkCheckTask = new AFKCheckTask();
  }

  /**
   * 1초마다 잠수를 체크하는 Task를 시작합니다.
   */
  public static void startAFKCheckTask() {
    afkCheckTask.runTaskTimerAsynchronously(20, 20);
  }

  /**
   * 잠수 체크 Task 중단
   */
  public static void stopAFKCheck() {
    afkCheckTask.cancel();
  }

  /**
   * 플레이어를 타겟 채널로 이동시킵니다.
   *
   * @param player 이동 시킬 플레이어
   */
  public static void sendToTargetChannel(Player player) {
    if (PluginChecker.isUseChannel()) {
      if(targetChannel.startsWith("<channel:")) {
        String channeName = StringUtil.getValue("channel", targetChannel).get(0);
        su.plugin.channel.common.api.ChannelAPI.getChannelManager().getChannel(channeName).sendToChannel(player.getName());
      } else if(targetChannel.startsWith("<channelgroup:")) {
        String channelGroupName = StringUtil.getValue("channelgroup", targetChannel).get(0);
        su.plugin.channel.common.api.ChannelAPI.getChannelGroupManager().getChannelGroup(channelGroupName).sendToOptimizeChannel(player.getName());
      } else {
        BungeeUtil.sendPlayer(player.getName(), targetChannel);
      }
    } else {
      BungeeUtil.sendPlayer(player.getName(), targetChannel);
    }

    DebugUtil.log(player.getName() + " 님을 타겟 채널로 이동시켰습니다.");
  }

}