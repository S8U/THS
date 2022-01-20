package su.plugin.ability.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GameMap;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;

public class VoteCommand implements UCommandListener {

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
    if (!api.isUseGameStartVote()) {
      up.wmsg("시작 투표가 금지되어 사용이 불가능합니다.");
      return;
    } else if (api.getGameManager().isGameStarted()) {
      up.wmsg("이미 게임이 시작되었습니다.");
      return;
    } else if (api.getPlayerManager().getGamePlayer(up.getPlayerKey()).isWatchMode()) {
      up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
      return;
    } else if (api.getVoteManager().isGameStartVoting()) {
      if(api.isUseWaitingQuickBar()) {
        api.getGUIManager().getGameStartVoteGUI().open((Player) up.getPlatformSender());
        return;
      }

      up.wmsg("이미 투표를 진행 중입니다.");
      return;
    } else if (api.getPlayerManager().getTeamAmount() < 2) {
      up.wmsg("인원이 적어 투표를 진행할 수 없습니다.");
      return;
    } else if ((System.currentTimeMillis() - api.getVoteManager().getLastGameStartVote()) < api.getRevotePeriod() * 1000) {
      up.wmsg("아직 시작 투표를 진행할 수 없습니다.");
      return;
    } else if (!api.canStartVote()) {
      up.wmsg(api.getVoteStartingConditionMessage());
      return;
    }
    
    api.getVoteManager().startGameStartVote(api.getVoteTimeoutCount());

    Core.nbc("");
    Core.cbc(ChatColor.DARK_AQUA, "§b게임 시작 투표가 시작되었습니다.");
    Core.cbc(ChatColor.YELLOW, new ComponentBuilder("§a'/찬성'")
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/찬성"))
            .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("클릭 시 투표에 찬성합니다.").create()))
            .create(),
        " §e또는 ",
        new ComponentBuilder("§c'/반대'")
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/반대"))
            .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("클릭 시 투표에 반대합니다.").create()))
            .create(),
        " §e명령어를 사용하여 투표에 참여하세요!");
    Core.nbc("");
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
    if(api.getPlayerManager().getGamePlayer(up.getPlayerKey()).isWatchMode()) {
      up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
      return;
    }

    else if(!api.getGameManager().isGameStarted() && api.getVoteManager().isGameStartVoting()) {
      if(api.getGameManager().isGameStarted()) {
        up.wmsg("이미 게임이 시작되었습니다.");
        return;
      } else if(api.getVoteManager().isGameStartVoted(up.getPlayerKey())) {
        up.wmsg("이미 투표에 참여했습니다.");
        return;
      } else if(api.getPlayerManager().getTeamAmount() < 2) {
        Core.cbc(ChatColor.RED, "§c인원이 부족하여 투표가 중단되었습니다.");

        api.getVoteManager().stopGameStartVote();
        return;
      }

      up.cmsg(ChatColor.DARK_AQUA, "§a게임 시작 투표에 찬성했습니다.");

      api.getVoteManager().joinGameStartVote(up.getPlayerKey(), true);

      return;
    } else if (api.getVoteManager().isInvSkipVoting() && api.isInvincibilityTime()) {
      if (api.getVoteManager().getInvSkipVoteAgree().contains(up.getPlayerKey())) {
        up.wmsg("이미 투표에 참여했습니다.");

        return;
      }

      api.getVoteManager().getInvSkipVoteAgree().add(up.getPlayerKey());

      up.cmsg(ChatColor.YELLOW, "§e무적 해제 투표에 찬성했습니다.");

      int agreeOnline = (int) api.getVoteManager().getInvSkipVoteAgree()
          .stream()
          .filter(vpk -> vpk.getUPlayer() != null && vpk.getUPlayer().isOnline())
          .count();

      Core.cbc(ChatColor.YELLOW, agreeOnline + "§e명이 무적 해제 투표에 찬성했습니다. (§f" + agreeOnline + " §e/ §f" + api.getPlayerManager().getOnlineJoinedPlayers().size() + "§e)");

      if (agreeOnline >= api.getPlayerManager().getOnlineJoinedPlayers().size()) {
        api.getTaskManager().stopInvincbilityTask();

        if(api.getGameManager().isAutoMode() && api.isUseAutoTeleport()) {
          api.getTaskManager().runTeleportAllTask(20 * 3, api.getAutoTeleportCount());
        }

        api.setInvincibilityTime(false);

        Core.cbc(ChatColor.YELLOW, "투표에 전원이 찬성하여 무적이 해제되었습니다.");
      }

      return;
    }

    up.wmsg("투표 중이 아닙니다.");
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
    if(api.getPlayerManager().getGamePlayer(up.getPlayerKey()).isWatchMode()) {
      up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
      return;
    }

    if (api.getVoteManager().isGameStartVoting()) {
      if(api.getGameManager().isGameStarted()) {
        up.wmsg("이미 게임이 시작되었습니다.");
        return;
      } else if(api.getVoteManager().isGameStartVoted(up.getPlayerKey())) {
        up.wmsg("이미 투표에 참여했습니다.");
        return;
      } else if(api.getPlayerManager().getTeamAmount() < 2) {
        Core.cbc(ChatColor.RED, "§c인원이 부족하여 투표가 중단되었습니다.");

        api.getVoteManager().stopGameStartVote();
        return;
      }

      up.cmsg(ChatColor.DARK_AQUA, "§c게임 시작 투표에 반대했습니다.");

      api.getVoteManager().joinGameStartVote(up.getPlayerKey(), false);

      return;
    } else if (api.getVoteManager().isInvSkipVoting() && api.isInvincibilityTime()) {
      if (api.getVoteManager().getInvSkipVoteAgree().contains(up.getPlayerKey())) {
        up.wmsg("이미 투표에 참여했습니다.");

        return;
      }

      api.getVoteManager().setInvSkipVoting(false);
      api.getVoteManager().getInvSkipVoteAgree().clear();

      Core.cbc(ChatColor.RED,"§c무적 해제에 전원이 찬성하지 않아 투표가 부결되었습니다.");

      return;
    }

    up.wmsg("투표 중이 아닙니다.");
  }

  //

  @CommandHandler(
      name = "맵투표",
      aliases = {"mapVote"},
      additional = "<맵>",
      usage = "맵 투표를 진행합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "맵투표",
      additional = "<맵>",
      aliases = {"mapVote"},
      usage = "맵 투표를 진행합니다."
  )
  public void ability_mapVote(UPlayer up, String[] args) {
    if (!api.isUseMapVote()) {
      up.wmsg("맵 투표가 금지되어 사용이 불가능합니다.");
      return;
    } else if(api.getMapManager().getMaps().size() < 1) {
      up.wmsg("아직 맵이 생성되지 않았습니다.");
      return;
    } else if(api.getGameManager().isGameStarted()) {
      up.wmsg("이미 게임이 시작되었습니다.");
      return;
    } else if(api.getPlayerManager().getGamePlayer(up.getPlayerKey()).isWatchMode()) {
      up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
      return;
    }

    if(args.length > 0) {
      String mapName = String.join(" ", args);
      if(mapName.equals("랜덤") || mapName.equalsIgnoreCase("random")) {
        api.getVoteManager().getMapVote().remove(up.getPlayerKey());

        up.cmsg(ChatColor.DARK_AQUA, "랜덤§b에 투표했습니다.");
      } else {
        GameMap map = api.getMapManager().getMap(mapName);
        if(map == null) {
          up.wmsg("존재하지 않는 맵입니다.");
          return;
        }

        api.getVoteManager().getMapVote().put(up.getPlayerKey(), map);

        up.cmsg(ChatColor.DARK_AQUA, map.getName() + " §b맵에 투표했습니다.");
      }

      if(api.isUseWaitingQuickBar()) {
        api.getBarManager().getWaitingQuickBar().update();
      }
      api.getGUIManager().updateMapVoteGUI();
    } else {
      api.getGUIManager().getMapVoteGUI().open((Player) up.getPlatformSender());
    }
  }

  //

  @CommandHandler(
      name = "무적해제투표",
      aliases = {"invSkipVote"},
      usage = "무적 해제 투표를 진행합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "무적해제투표",
      aliases = {"invSkipVote"},
      usage = "무적 해제 투표를 진행합니다."
  )
  public void ability_invSkipVote(UPlayer up, String[] args) {
    if (!api.isUseInvSkipVote()) {
      up.wmsg("무적 해제 투표가 금지되어 사용이 불가능합니다.");

      return;
    } else if (!api.isInvincibilityTime()) {
      up.wmsg("무적 시간이 아닙니다.");

      return;
    } else if (api.getVoteManager().isInvSkipVoting()) {
      up.wmsg("이미 무적 해제 투표를 진행 중입니다.");

      return;
    }

    api.getVoteManager().setInvSkipVoting(true);

    Core.nbc("");
    Core.cbc(ChatColor.YELLOW, "§e무적 해제 투표가 시작되었습니다.");
    Core.cbc(ChatColor.YELLOW,"§e전원이 투표에 찬성할 경우 무적이 해제됩니다.");

    Core.cbc(ChatColor.YELLOW, new ComponentBuilder("§a'/찬성'")
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/찬성"))
            .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("클릭 시 투표에 찬성합니다.").create()))
            .create(),
        " §e또는 ",
        new ComponentBuilder("§c'/반대'")
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/반대"))
            .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("클릭 시 투표에 반대합니다.").create()))
            .create(),
        " §e명령어를 사용하여 투표에 참여하세요!");
    Core.nbc("");
  }

}
