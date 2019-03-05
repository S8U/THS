package su.plugin.ability.command;

import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;

public class GameStartVoteCommand implements UCommandListener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @CommandHandler(
      name = "시작투표",
      aliases = {"gameStartVote", "startVote"},
      usage = "게임 시작 투표를 진행합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "시작투표",
      aliases = {"gameStartVote", "startVote"},
      usage = "게임 시작 투표를 진행합니다."
  )
  public void ability_gameStartVote(UPlayer up, String[] args) {
    if(api.getGameManager().isGameStarted()) {
      up.wmsg("이미 게임이 시작되었습니다.");
      return;
    } else if(api.getPlayerManager().getGamePlayer(up.getPlayerKey()).isWatchMode()) {
      up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
      return;
    } else if(api.getVoteManager().isGameStartVoting()) {
      if(api.isUseWaitingQuickBar()) {
        api.getGUIManager().getGameStartVoteGUI().open((Player) up.getPlatformSender());
        return;
      }

      up.wmsg("이미 투표를 진행 중입니다.");
      return;
    } else if(api.getPlayerManager().getTeamAmount() < 2) {
      up.wmsg("인원이 적어 투표를 진행할 수 없습니다.");
      return;
    } else if((System.currentTimeMillis() - api.getVoteManager().getLastGameStartVote()) < api.getRevotePeriod() * 1000) {
      up.wmsg("아직 시작 투표를 진행할 수 없습니다.");
      return;
    }
    
    api.getVoteManager().startGameStartVote(api.getVoteTimeoutCount());

    Core.cbc(ChatColor.DARK_AQUA, "§b게임 시작 투표가 시작되었습니다.");
    Core.cbc(ChatColor.DARK_AQUA, "'/찬성' §b또는 §f'/반대' §b명령어를 사용하여 투표에 참여하세요!");
  }

  @CommandHandler(
      name = "찬성",
      aliases = {"찬성", "agree"},
      usage = "게임 시작 투표에 찬성합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "찬성",
      aliases = {"찬성", "agree"},
      usage = "게임 시작 투표에 찬성합니다."
  )
  public void ability_agree(UPlayer up, String[] args) {
    if(api.getGameManager().isGameStarted()) {
      up.wmsg("이미 게임이 시작되었습니다.");
      return;
    } else if(api.getPlayerManager().getGamePlayer(up.getPlayerKey()).isWatchMode()) {
      up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
      return;
    } else if(!api.getVoteManager().isGameStartVoting()) {
      up.wmsg("투표 중이 아닙니다.");
      return;
    } else if(api.getVoteManager().isGameStartVoted(up.getPlayerKey())) {
      up.wmsg("이미 투표에 참여했습니다.");
      return;
    } else if(api.getPlayerManager().getTeamAmount() < 2) {
      Core.cbc(ChatColor.RED, "§c인원이 부족하여 투표가 중단되었습니다.");

      api.getVoteManager().stopVote();
      return;
    }

    api.getVoteManager().joinGameStartVote(up.getPlayerKey(), true);

    up.cmsg(ChatColor.DARK_AQUA, "§a투표에 찬성했습니다.");
  }

  @CommandHandler(
      name = "반대",
      aliases = {"disagree"},
      usage = "게임 시작 투표에 반대합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "반대",
      aliases = {"disagree"},
      usage = "게임 시작 투표에 반대합니다."
  )
  public void ability_disagree(UPlayer up, String[] args) {
    if(api.getGameManager().isGameStarted()) {
      up.wmsg("이미 게임이 시작되었습니다.");
      return;
    } else if(api.getPlayerManager().getGamePlayer(up.getPlayerKey()).isWatchMode()) {
      up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
      return;
    } else if(!api.getVoteManager().isGameStartVoting()) {
      up.wmsg("투표 중이 아닙니다.");
      return;
    } else if(api.getVoteManager().isGameStartVoted(up.getPlayerKey())) {
      up.wmsg("이미 투표에 참여했습니다.");
      return;
    } else if(api.getPlayerManager().getTeamAmount() < 2) {
      Core.cbc(ChatColor.RED, "§c인원이 부족하여 투표가 중단되었습니다.");

      api.getVoteManager().stopVote();
      return;
    }

    api.getVoteManager().joinGameStartVote(up.getPlayerKey(), false);

    up.cmsg(ChatColor.DARK_AQUA, "§c투표에 반대했습니다.");
  }

}
