package s8u.plugin.minigamecore.task;

import lombok.Getter;
import s8u.plugin.minigamecore.MiniGameCorePlugin;
import s8u.plugin.minigamecore.api.MiniGameCore;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class StartVoteTask extends UKRunnable {

  @Getter
  private int count = 0;

  public StartVoteTask() {
    super(MiniGameCorePlugin.getInstance());
  }

  @Override
  public void run() {
    if (++count == MiniGameCore.getStartVoteManager().getVoteTimeout()) {
      Core.cbc(ChatColor.RED, "§c투표 시간이 초과하여 투표가 부결되었습니다.");

      MiniGameCore.getStartVoteManager().stopVote(null);
    } else {
      MiniGameCore.getStartVoteManager().getGui().update();
    }
  }

}