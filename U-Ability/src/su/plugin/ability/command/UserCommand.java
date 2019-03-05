package su.plugin.ability.command;

import java.util.List;
import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.object.Ability;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.ability.api.util.BuildUtil;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.core.common.api.util.StringUtil;

public class UserCommand implements UCommandListener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @CommandHandler(
      name = "스폰",
      aliases = {"넴주", "spawn"},
      usage = "스폰으로 이동합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "스폰",
      aliases = {"넴주", "spawn"},
      usage = "스폰으로 이동합니다."
  )
  public void ability_spawn(UPlayer up, String[] args) {
    if(api.getGameManager().isGameStarted()) {
      up.wmsg("게임 중에는 스폰으로 이동할 수 없습니다.");
      return;
    } else if(api.getMapManager().getSpawn() == null) {
      up.wmsg("아직 스폰이 설정되지 않았습니다.");
      return;
    }

    KCore.teleport((Player) up.getPlatformSender(), api.getMapManager().getSpawn());

    up.cmsg(ChatColor.DARK_AQUA, "§e스폰으로 이동되었습니다.");
  }

  @CommandHandler(
      name = "목록",
      aliases = {"플레이어목록", "playerList", "list"},
      usage = "플레이어 목록을 확인합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "목록",
      aliases = {"플레이어목록", "playerList", "pl", "list"},
      usage = "플레이어 목록을 확인합니다."
  )
  public void ability_list(UCommandSender sender, String[] args) {
    List<GamePlayer> playing = api.getPlayerManager().getOnlineJoinedPlayers();
    List<GamePlayer> watching = api.getPlayerManager().getOnlineWatchPlayers();

    sender.nmsg("");
    sender.cmsg(ChatColor.RED, "§c게임 중인 플레이어(" + playing.size() + "): §f" + BuildUtil.buildGamePlayerList(playing));
    if(watching.size() > 0) {
      sender.cmsg(ChatColor.BLUE, "§b관전 중인 플레이어(" + watching.size() + "): §f" + BuildUtil.buildGamePlayerList(watching));
    }
  }

  @CommandHandler(
      name = "진행시간",
      aliases = {"플레이시간", "플레이타임", "playTime"},
      usage = "게임 진행 시간을 확인합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "진행시간",
      aliases = {"플레이시간", "플레이타임", "playTime"},
      usage = "게임 진행 시간을 확인합니다."
  )
  public void ability_playTime(UCommandSender sender, String[] args) {
    if(!api.getGameManager().isGameStarted()) {
      sender.wmsg("아직 게임이 시작되지 않았습니다.");
      return;
    }

    sender.cmsg(ChatColor.YELLOW, "게임 진행 시간: " + StringUtil.buildTimeString(api.getGameManager().getPlayTime()));
  }

  @CommandHandler(
      name = "능력",
      aliases = {"ability", "help"},
      usage = "자신의 능력을 확인합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "능력",
      aliases = {"ability", "help"},
      usage = "자신의 능력을 확인합니다."
  )
  public void ability_ability(UPlayer up, String[] args) {
    GamePlayer gp = api.getPlayerManager().getGamePlayer(up.getPlayerKey());
    if(gp.getAbilities().size() < 1) {
      up.wmsg("능력이 없습니다.");
      return;
    }

    for(Ability ability : gp.getAbilities()) {
      up.nmsg("");
      up.cmsg(ChatColor.DARK_GREEN, ability.getName() + " | " + ability.getRank().getText() + " | " + ability.getPluginName());
      up.cmsg(ChatColor.YELLOW, ability.getManual());

      String time = "";
      if(ability.getCoolTime() > 0) {
        time += "§2쿨타임: §f" + StringUtil.buildTimeString(ability.getCoolTime() * 1000);
      }
      if(ability.getDurationTime() > 0) {
        time += (time != null ? " / " : "") + "§e지속 시간: §f" + StringUtil.buildTimeString(ability.getDurationTime() * 1000);
      }
      if(time.length() > 0) {
        up.cmsg(ChatColor.DARK_GREEN, time);
      }
    }
  }

  @CommandHandler(
      name = "확정",
      aliases = {"yes"},
      usage = "능력을 확정합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "확정",
      aliases = {"yes"},
      usage = "능력을 확정합니다."
  )
  public void ability_yes(UPlayer up, String[] args) {
    if(!api.getGameManager().getGameState().equals(GameState.DRAWING)) {
      up.wmsg("능력 추첨 중이 아닙니다.");
      return;
    }

    GamePlayer gp = api.getPlayerManager().getGamePlayer(up.getPlayerKey());

    gp.setRedrawCount(0);

    up.cmsg(ChatColor.DARK_GREEN, "§a능력을 확정했습니다.");

    Core.cbc(ChatColor.DARK_GREEN, gp.getDisplayName() + " §a님께서 능력을 확정했습니다.  (" + api.getAbilityManager().getConfirmationCount() + " / " + api.getPlayerManager().getOnlineJoinedPlayers().size() + ")");

    if(!(api.getGameManager().isAutoMode() && api.isUseDrawTimeLimit())) {
      api.getBarManager().getBossBar().setText("능력을 추첨 중입니다.. (" + api.getAbilityManager().getConfirmationCount() + " / " + api.getPlayerManager().getOnlineJoinedPlayers().size() + ")");
      api.getBarManager().getBossBar().setProgress((float) api.getAbilityManager().getConfirmationCount() / (float) api.getPlayerManager().getJoinedPlayers().size() * 100);
    }

    if(api.getAbilityManager().getConfirmationCount() == api.getPlayerManager().getOnlineJoinedPlayers().size()) {
      api.getGameManager().setGameState(GameState.PREPARING);

      Core.nbc(" ");
      Core.cbc(ChatColor.DARK_GREEN, "§a모든 플레이어가 능력을 확정했습니다.");

      api.getTaskManager().stopDrawSkipTask();
      api.getTaskManager().runGameStartCountTask(20);
    }
  }

  @CommandHandler(
      name = "재추첨",
      aliases = {"no"},
      usage = "능력을 재추첨합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "재추첨",
      aliases = {"no"},
      usage = "능력을 재추첨합니다."
  )
  public void ability_no(UPlayer up, String[] args) {
    if(!api.getGameManager().getGameState().equals(GameState.DRAWING)) {
      up.wmsg("능력 추첨 중이 아닙니다.");
      return;
    }

    GamePlayer gp = api.getPlayerManager().getGamePlayer(up.getPlayerKey());

    if(gp.getRedrawCount() < 1) {
      up.wmsg("남은 재추첨 횟수가 없습니다.");
      return;
    }

    gp.setRedrawCount(gp.getRedrawCount() - 1);

    gp.clearAbility();

    api.getAbilityManager().giveRandomAbility(gp, api.isUseOverlap());
    api.getBarManager().updateSideBar(gp);

    up.cmsg(ChatColor.DARK_GREEN, "§a능력을 재추첨했습니다. (남은 재추첨 횟수: " + gp.getRedrawCount() + "회)");

    if(gp.getRedrawCount() < 1) {
      Core.cbc(ChatColor.DARK_GREEN, gp.getDisplayName() + " §b님께서 능력을 확정했습니다.  (" + api.getAbilityManager().getConfirmationCount() + " / " + api.getPlayerManager().getOnlineJoinedPlayers().size() + ")");

      if(!(api.getGameManager().isAutoMode() && api.isUseDrawTimeLimit())) {
        api.getBarManager().getBossBar().setText("능력을 추첨 중입니다.. (" + api.getAbilityManager().getConfirmationCount() + " / " + api.getPlayerManager().getOnlineJoinedPlayers().size() + ")");
        api.getBarManager().getBossBar().setProgress((float) api.getAbilityManager().getConfirmationCount() / (float) api.getPlayerManager().getJoinedPlayers().size() * 100);
      }

      if(api.getAbilityManager().getConfirmationCount() == api.getPlayerManager().getOnlineJoinedPlayers().size()) {
        api.getGameManager().setGameState(GameState.PREPARING);
        Core.cbc(ChatColor.DARK_GREEN, "§b모든 플레이어가 능력을 확정했습니다.");
        api.getTaskManager().stopDrawSkipTask();
        api.getTaskManager().runGameStartCountTask(20);
      }
    }
  }

  @CommandHandler(
      name = "관전모드",
      aliases = {"관전", "watchMode"},
      usage = "관전 모드로 전환하거나 해제합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "관전모드",
      aliases = {"관전", "watchMode"},
      usage = "관전 모드로 전환하거나 해제합니다."
  )
  public void ability_watchMode(UPlayer up, String[] args) {
    GamePlayer gp = api.getPlayerManager().getGamePlayer(up.getPlayerKey());
    if(gp.isWatchMode()) {
      if(api.getGameManager().isGameStarted()) {
        up.wmsg("게임 중에는 관전 모드를 해제할 수 없습니다.");
        return;
      }

      gp.toggleWatchMode(false, true);
      return;
    }

    gp.toggleWatchMode(true, true);

    if(api.getGameManager().finish()) {
      api.shutdown(13);
    } else if(api.getGameManager().isAutoMode() // 자동 모드일 경우
        && !api.getGameManager().getGameState().equals(GameState.END) && api.getPlayerManager().getTeamAmount() < 2) { // 끝나지 않았을 경우 && 팀이 2보다 적을 경우
      api.getGameManager().stopGame();

      Core.cbc(ChatColor.RED, "§c인원이 부족하여 게임이 중단됩니다.");
    }
  }

  @CommandHandler(
      name = "능력목록",
      aliases = {"abilityList", "al"},
      minArgs = 1,
      additional = "<페이지>",
      usage = "능력 목록을 확인합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "능력목록",
      aliases = {"abilityList", "al"},
      minArgs = 1,
      additional = "<페이지>",
      usage = "능력 목록을 확인합니다."
  )
  public void ability_abilityList(UCommandSender sender, String[] args) {
    int amount = api.getAbilityManager().getAbilities().size();
    int maxPage = Double.valueOf(Math.ceil(amount / 10)).intValue() + 1;
    int page = 0;

    if(!NumberUtil.isInteger(args[0]) || (page = Integer.parseInt(args[0])) < 1 || page > maxPage) {
      sender.wmsg("페이지는 1~" + maxPage + "의 숫자만 입력할 수 있습니다.");
      return;
    }

    sender.cmsg(ChatColor.DARK_GREEN, "§a능력 목록 (" + page + " / " + maxPage + ")");
    for(int i = 0; i < 10; i++) {
      int j = (page - 1) * 10 + i;
      Ability ab = api.getAbilityManager().getAbilities().get(j);
      if(ab == null) break;

      sender.cmsg(ChatColor.DARK_AQUA, j + "§a: §f" + ab.getName() + " §a| §f" + ab.getRank().getText() + " §a| §f" + ab.getPluginName());
    }
  }

}
