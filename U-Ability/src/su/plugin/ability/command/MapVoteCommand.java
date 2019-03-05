package su.plugin.ability.command;

import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GameMap;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;

public class MapVoteCommand implements UCommandListener {

  private AbilityAPI api = AbilityPlugin.getApi();

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
    if(api.getMapManager().getMaps().size() < 1) {
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

}
