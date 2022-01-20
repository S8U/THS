package s8u.plugin.minigamecore.api.vote.start;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import s8u.plugin.minigamecore.api.MiniGameCore;
import s8u.plugin.minigamecore.task.StartVoteTask;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;

@Getter
public class StartVoteManager {

  private int minNumberOfPlayer;
  private int voteTimeout; // 투표 제한 시간 (s)

  private boolean voting;

  private List<PlayerKey> agreePlayers = new NotDuplicatedArrayList<>();
  private List<PlayerKey> disagreePlayers = new NotDuplicatedArrayList<>();

  private StartVoteTask startVoteTask = new StartVoteTask();

  private StartVoteGUI gui = new StartVoteGUI();

  @Setter
  @Getter
  private StartVoteHandler startVoteHandler = new DefaultStartVoteHandler();

  public int getNumberOfVotes() {
    return agreePlayers.size() + disagreePlayers.size();
  }

  public boolean isVoted(PlayerKey playerKey) {
    return agreePlayers.contains(playerKey) || disagreePlayers.contains(playerKey);
  }

  public void startVote(UPlayer up) {
    if (up != null) {
      if (voting) { // 투표 중일 경우
        up.wmsg("이미 투표를 진행 중입니다.");
        return;
      } else if (MiniGameCore.getGameManager().isGameStarted()) { // 게임 중일 경우
        up.wmsg("이미 게임이 시작되었습니다.");
        return;
      }
    }

    voting = true;
    startVoteTask.runTaskTimerAsynchronously(20, 20);

    gui.update();

    Core.nbc("");
    Core.cbc(ChatColor.DARK_AQUA, "§b게임 시작 투표가 시작되었습니다.");
    Core.cbc(ChatColor.YELLOW, new ComponentBuilder("§a'/찬성'")
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/찬성"))
            .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("클릭 시 투표에 찬성합니다.").create()))
            .create(),
        " §e또는 ",
        new ComponentBuilder("§c'/반대'")
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/반대"))
            .event(
                new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("클릭 시 투표에 반대합니다.").create()))
            .create(),
        " §e명령어를 사용하여 투표에 참여하세요!");
    Core.nbc("");
  }

  public void stopVote(UPlayer up) {
    if (up != null) {
      if (!voting) { // 투표 중이 아닐 경우
        up.wmsg("투표 중이 아닙니다.");
        return;
      } else if (MiniGameCore.getGameManager().isGameStarted()) { // 게임 중일 경우
        up.wmsg("이미 게임이 시작되었습니다.");
        return;
      }
    }

    voting = false;
    startVoteTask.cancel();

    if (up != null) {
      Core.cbc(ChatColor.DARK_AQUA, up.getDisplayName() + " 님께서 투표를 중단시켰습니다.");
    }
  }

  public void joinVote(UPlayer up, boolean agree) {
    if (!voting) { // 투표 중이 아닐 경우
      up.wmsg("투표 중이 아닙니다.");
    }
    if (MiniGameCore.getGameManager().isGameStarted()) { // 게임 중일 경우
      up.wmsg("이미 게임이 시작되었습니다.");
    } else if (MiniGameCore.getPlayerManager().getGamePlayer(up.getPlayerKey())
        .isSpectator()) { // 관전 중일 경우
      up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
    } else if (MiniGameCore.getPlayerManager().getNumberOfTeams()
        < minNumberOfPlayer) { // 인원이 부족할 경우
      Core.cbc(ChatColor.RED, "§c인원이 부족하여 투표가 중단되었습니다.");

      stopVote(null);
    } else if (agree) { // 찬성
      disagreePlayers.remove(up.getPlayerKey());
      agreePlayers.add(up.getPlayerKey());

      up.cmsg(ChatColor.DARK_AQUA, "§a게임 시작 투표에 찬성했습니다.");
    } else { // 반대
      agreePlayers.remove(up.getPlayerKey());
      disagreePlayers.add(up.getPlayerKey());

      up.cmsg(ChatColor.DARK_AQUA, "§c게임 시작 투표에 반대했습니다.");
    }

    Core.cbc(ChatColor.DARK_AQUA, agreePlayers.size() + disagreePlayers.size() + "§b명이 게임 시작 투표에 참여했습니다. (찬성: §f" + agreePlayers.size() + " §b/ 반대: §f" + disagreePlayers.size() + "§b)");

    startVoteHandler.onVote(up, agree, agreePlayers, disagreePlayers);

    gui.updateAsynchronously();
    // * Update QuickBar
  }

}