package su.plugin.ability.listener;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.DeathReason;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.category.KillType;
import su.plugin.ability.api.event.DeathEvent;
import su.plugin.ability.api.event.JoinEvent;
import su.plugin.ability.api.event.KillEvent;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.lib.VaultHandler;
import su.plugin.core.bukkit.api.util.TitleUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class PlayerListener implements Listener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

    GamePlayer gp = api.getPlayerManager().getGamePlayer(playerKey);
    if(gp == null) { // 처음 접속 했을 경우
      gp = new GamePlayer(playerKey);
      api.getPlayerManager().setGamePlayer(playerKey, gp);
    }

    gp.setOnline(true);
    gp.getKPlayer().showPlayer();

    api.getBarManager().getBossBar().addPlayers(p);

    for(PotionEffect effect : p.getActivePotionEffects()) {
      if(effect.getType().equals(PotionEffectType.INVISIBILITY) && effect.getAmplifier() == 1) {
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
      }
    }

    api.getTaskManager().stopEliminateTask(playerKey);

    if(api.getGameManager().getGameState().getProgress() < GameState.DRAWING.getProgress()) { // 게임 중이 아닐 경우
      if(!gp.isWatchMode()) {
        gp.setJoin(true);
      }

      gp.getPlayer().setGameMode(GameMode.ADVENTURE);

      if(api.getMapManager().getSpawn() != null) {
        KCore.teleport(p, api.getMapManager().getSpawn());
      }
    } else { // 게임이 시작 됐을 경우
      if(KCore.getGUIManager().hasQuickBar(p)) {
        KCore.getGUIManager().clearQuickBar(p);
      }

      if(gp.isEliminate() || !gp.isJoin()) { // 탈락 or 참여하지 않았을 경우
        gp.toggleWatchMode(true, false);

        if(gp.isReconnectEliminate() && !gp.isReconnectEliminateMessage()) { // 재접속 탈락 && 메시지 보내지 않았을 경우
          gp.setReconnectEliminateMessage(true);

          gp.getKPlayer().wmsg("재접속 시간이 지나 탈락했습니다.");
        }

        if(!api.getMapManager().getPlayingMap().isInMap(p.getLocation(), api.getGameManager().isTeleportedAll())) { // 맵 안에 없을 경우
          KCore.teleport(p, api.getMapManager().getProgressLocation());
        }

        if(api.isUseWatchModeQuickBar()) { // 관전 퀵바 사용 중일 경우
          api.getBarManager().getWatchModeQuickBar().update();
          api.getGUIManager().updateTeleportGUI();
        }
      }
    }

    api.getBarManager().updateSideBarAllPlayer();

    // 접속 메시지 {

    String msg = gp.getDisplayName() + " §e님께서 접속했습니다.";

    if(api.isUseAutoStart() && api.getGameManager().getGameState().getProgress() <= GameState.PREPARING.getProgress()) { // 자동 시작 사용 && 시작 전일 경우
      String count = "( " + api.getPlayerManager().getOnlineJoinedPlayers().size() + " / " + api.getAutoStartCount()+ " )";
      if(!api.getGameManager().isGameStarted()) {
        msg += " " + count;
      }

      api.getBarManager().getBossBar().setText("다른 플레이어를 기다리는 중입니다.. " + count);
      api.getBarManager().getBossBar().setProgress((float) api.getPlayerManager().getOnlineJoinedPlayers().size() / (float) api.getAutoStartCount() * 100);
    } else if(gp.isJoin() && !gp.isEliminate()) {
      msg = gp.getDisplayName() + " §e님께서 재접속했습니다.";
    }

    if(gp.isWatchMode()) {
      msg = gp.getDisplayName() + " §b님께서 관전 모드로 접속하셨습니다. (관전자 수: " + api.getPlayerManager().getOnlineWatchPlayers().size() + "명)";
    }

    e.setJoinMessage(getFormatMessage(ChatColor.YELLOW, msg));

    // } 접속 메시지

    if(!api.getGameManager().isGameStarted() && api.isUseAutoStart()) { // 게임이 시작되지 않았을 경우 && 자동 시작이 켜져있을 경우
      if(api.getAutoStartCount() <= api.getPlayerManager().getOnlineJoinedPlayers().size() && api.getPlayerManager().getTeamAmount() > 1) { // 시작 인원 충족 && 팀이 1보다 많으면
        api.getGameManager().startGame(true); // 자동 게임 시작
      }
    }

    // 이벤트 {
    JoinEvent event = new JoinEvent(gp, gp.isJoin() && !gp.isEliminate(), e);
    Bukkit.getPluginManager().callEvent(event);
    // } 이벤트
  }

  //

  //

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

    GamePlayer gp = api.getPlayerManager().getGamePlayer(playerKey);

    gp.setOnline(false);
    gp.toggleWatchMode(false, false);

    api.getBarManager().getBossBar().removePlayers(p); // 보스바 제거
    api.getBarManager().updateSideBarAllPlayer();

    if(api.getVoteManager().isGameStartVoting()) { // 시작 투표 중일 경우
      api.getVoteManager().getGameStartVoteAgree().remove(gp.getPlayerKey());
      api.getVoteManager().getGameStartVoteDisagree().remove(gp.getPlayerKey());

      api.getGUIManager().updateGameStartVoteGUI();
      if(api.isUseWaitingQuickBar()) { // 대기 퀵바 사용 중일 경우
        api.getBarManager().getWaitingQuickBar().update();
      }
    }
    if(api.isUseMapVote()) { // 맵 투표를 사용할 경우
      api.getVoteManager().getMapVote().remove(gp.getPlayerKey());

      api.getGUIManager().updateMapVoteGUI();
      if(api.isUseWaitingQuickBar()) { // 대기 퀵바 사용 중일 경우
        api.getBarManager().getWaitingQuickBar().update();
      }
    }

    if(gp.isJoin() && !gp.isEliminate() // 게임 참여 && 탈락하지 않았을 경우
        && api.getGameManager().getGameState().getProgress() > GameState.PREPARING.getProgress()) { // 게임 시작됐을 경우

      if(api.isAllowReconnect() && api.isUseReconnectTimeLimit()) { // 재접속 허용 && 재접속 시간 제한 사용 중일 경우
        api.getTaskManager().runEliminateTask(gp.getPlayerKey()); // 탈락 Task 시작
      }

      if(p.getHealth() < api.getQuitDeathHealth()) { // 퇴장 사망 체력보다 낮을 경우
        p.setHealth(0);

        Core.cbc(ChatColor.RED, gp.getDisplayName() + " §c님께서 낮은 체력으로 퇴장하여 사망 처리되었습니다.");
      }

      if(api.isUseWatchModeQuickBar()) { // 관전 퀵바 사용 중일 경우
        api.getBarManager().getWatchModeQuickBar().update();
        api.getGUIManager().updateTeleportGUI();
      }
    }

    // 퇴장 메시지 {

    String msg = gp.getDisplayName() + " §e님께서 퇴장하셨습니다.";

    if(api.isUseAutoStart() && api.getGameManager().getGameState().getProgress() <= GameState.PREPARING.getProgress()) { // 자동 시작 사용 && 시작 전일 경우
      String count = "( " + api.getPlayerManager().getOnlineJoinedPlayers().size() + " / " + api.getAutoStartCount()+ " )";
      if(!api.getGameManager().isGameStarted()) {
        msg += " " + count;
      }

      api.getBarManager().getBossBar().setText("다른 플레이어를 기다리는 중입니다.. " + count);
      api.getBarManager().getBossBar().setProgress((float) api.getPlayerManager().getOnlineJoinedPlayers().size() / (float) api.getAutoStartCount() * 100);
    }

    e.setQuitMessage(getFormatMessage(ChatColor.YELLOW, msg));

    // } 퇴장 메시지

    if(api.getGameManager().isGameStarted()) { // 게임이 시작되었을 경우
      if(api.getGameManager().finish()) { // 끝낼 수 있을 경우
        api.shutdown(13); // 서버 종료
      } else if(api.getGameManager().isAutoMode() // 자동 모드일 경우
      && !api.getGameManager().getGameState().equals(GameState.END) && api.getPlayerManager().getTeamAmount() < 2) { // 끝나지 않았을 경우 && 팀이 2보다 적을 경우
        api.getGameManager().stopGame();

        Core.cbc(ChatColor.RED, "§c인원이 부족하여 게임이 중단됩니다.");
      }
    } else if(api.isUseAutoStart()) { // 자동 시작이 켜져있을 경우
      if(api.getAutoStartCount() <= api.getPlayerManager().getOnlineJoinedPlayers().size() - 1 && api.getPlayerManager().getTeamAmount() > 1) { // 시작 인원 충족 && 팀이 1보다 많으면
        api.getGameManager().startGame(true); // 자동 게임 시작
      }
    }
  }

  //

  //

  @EventHandler
  public void onDeath(PlayerDeathEvent e) {
    Player p = e.getEntity();
    PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

    GamePlayer gp = api.getPlayerManager().getGamePlayer(playerKey);

    if(api.isKickOnDeath()) {
      p.kickPlayer("사망하여 강제 퇴장되었습니다.");
    }

    if(api.isBanOnDeath()) {
      Bukkit.getBanList(Type.NAME).addBan(p.getName(), "사망하여 서버에서 차단되었습니다.", null, "시스템");
    }

    Player killer = p.getKiller();
    PlayerKey killerPlayerKey;

    GamePlayer kp = null;

    if(killer != null) {
      killerPlayerKey = PlayerKey.getPlayerKeyByPlatformPlayer(killer);
      kp = api.getPlayerManager().getGamePlayer(killerPlayerKey);
    }

    String deathMessage = gp.getDisplayName() + " §c님께서 사망했습니다.";

    if(api.getGameManager().getGameState().getProgress() > GameState.PREPARING.getProgress()) { // 게임이 시작됐을 경우
      if(gp.isJoin() && !gp.isEliminate() && !gp.isWatchMode()) { // 참여 중일 경우
        if(api.isEliminateOnDeath()) { // 사망 탈락 사용 중일 경우
          if(killer == null) { // killer가 없을 경우
            if(api.isEliminateOnNatureDeath()) { // 자연사 탈락 사용 중일 경우
              gp.setEliminate(true);
              gp.setWatchMode(true);
              gp.setJoin(false);

              TitleUtil.sendTitle(p, "탈락했습니다.", 1, 1, 2);

              api.getBarManager().updateSideBarAllPlayer();

              if(api.isUseWatchModeQuickBar()) { // 관전 퀵바 사용 중일 경우
                api.getBarManager().getWatchModeQuickBar().update();
                api.getGUIManager().updateTeleportGUI();
              }

              deathMessage = gp.getDisplayName() + " §c님께서 자연사했습니다.";

              try {
                Bukkit.getPluginManager().callEvent(new DeathEvent(gp, null, DeathReason.NATURE, e));
              } catch(Exception ex) {
                ex.printStackTrace();
              }
            } else {
              deathMessage = gp.getDisplayName() + " §c님께서 자연사하여 다시 부활했습니다.";
            }
          } else if(kp.isJoin() && !kp.isEliminate() && !kp.isWatchMode()){ // killer가 있을 경우
            gp.setEliminate(true);
            gp.setWatchMode(true);
            gp.setJoin(false);

            TitleUtil.sendTitle(p, "탈락했습니다.", 1, 1, 2);

            api.getBarManager().updateSideBarAllPlayer();

            if(api.isUseWatchModeQuickBar()) { // 관전 퀵바 사용 중일 경우
              api.getBarManager().getWatchModeQuickBar().update();
              api.getGUIManager().updateTeleportGUI();
            }

            deathMessage = kp.getDisplayName() + " §c님께서 §f" + gp.getDisplayName() + " §c님을 죽였습니다.";

            try {
              Bukkit.getPluginManager().callEvent(new DeathEvent(gp, kp, DeathReason.PLAYER, e));
            } catch(Exception ex) {
              ex.printStackTrace();
            }

            //

            KillType lastKillType = kp.getLastKillType();
            KillType killType = KillType.NORMAL;
            double killMoney = api.getKillMoney();
            double regularKillMoney = 0;

            if (lastKillType != null && !lastKillType.equals(KillType.PENTA)) {
              killType = KillType.getKillType(lastKillType.getNumber() + 1);
              regularKillMoney = killType.getKillMoney();
            }

            List<GamePlayer> assists = new ArrayList<>();
            if (api.isUseAssist()) {
              for (PlayerKey lpk : gp.getLastHitTimes().keySet()) {
                if (System.currentTimeMillis() - gp.getLastHitTimes().get(lpk) > api.getAssistCount() * 1000) continue;
                assists.add(api.getPlayerManager().getGamePlayer(lpk));
              }
            }

            KillEvent event = new KillEvent(killer, p, assists, api.isFirstBlood(), killMoney, regularKillMoney, api.getFirstBloodMoney(), api.getAssistMoney(), killType, e);
            try {
              Bukkit.getPluginManager().callEvent(event);
            } catch(Exception ex) {
              ex.printStackTrace();
            }
            if(!event.isCancelled()) {
              if(event.getKillMoney() != 0) {
                VaultHandler.giveMoney(killer, event.getKillMoney());
                Core.cmsg(killer, ChatColor.GREEN, "§a+" + event.getKillMoney() + "원 (킬)");
              }

              if(event.isFirstBlood()) {
                Core.cbc(ChatColor.DARK_RED, kp.getDisplayName() + " §c님께서 퍼스트 블러드!");

                api.setFirstBlood(false);
                if(event.getFirstBloodMoney() != 0) {
                  VaultHandler.giveMoney(killer, event.getFirstBloodMoney());
                  Core.cmsg(killer, ChatColor.GREEN, "§a+" + event.getFirstBloodMoney() + "원 (퍼스트 블러드)");
                }
              }

              if(event.getAssistMoney() != 0) {
                for (GamePlayer ap : assists) {
                  VaultHandler.giveMoney(ap.getPlayerKey().getName(), event.getAssistMoney());
                  if (ap.getPlayer() == null) continue;
                  Core.cmsg(killer, ChatColor.GREEN, "§a+" + event.getAssistMoney() + "원 (어시스트)");
                }
              }

              kp.setLastKillTime(System.currentTimeMillis());
              kp.setLastKillType(event.getKillType());

              if(!event.getKillType().equals(KillType.NORMAL)) {
                Core.cbc(ChatColor.DARK_RED, kp.getDisplayName() + " §c님께서 §e" + event.getKillType().getText() + "!");

                if(event.getRegularKillMoney() != 0) {
                  VaultHandler.giveMoney(killer, event.getKillType().getKillMoney());
                  Core.cmsg(killer, ChatColor.GREEN, "§a+" + event.getKillMoney() + "원 (연속킬)");
                }
              }
            }
          }
        }
      }

      e.setDeathMessage(getFormatMessage(ChatColor.DARK_RED, deathMessage));

      if(api.getGameManager().finish()) { // 끝낼 수 있을 경우
        api.shutdown(13); // 서버 종료
      } else if(api.getGameManager().isAutoMode() // 자동 모드일 경우
          && !api.getGameManager().getGameState().equals(GameState.END) && api.getPlayerManager().getTeamAmount() < 2) { // 끝나지 않았을 경우 && 팀이 2보다 적을 경우
        api.getGameManager().stopGame();

        Core.cbc(ChatColor.RED, "§c인원이 부족하여 게임이 중단됩니다.");
      }
    }

    p.spigot().respawn();
  }

  //

  //

  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    Player p = e.getPlayer();
    PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

    GamePlayer gp = api.getPlayerManager().getGamePlayer(playerKey);

    if(gp.isWatchMode()) {
      gp.toggleWatchMode(true, false);
    }

    if(api.getGameManager().getGameState().getProgress() > GameState.PREPARING.getProgress()) { // 게임 시작 후
      if(gp.isEliminate() || gp.isWatchMode()) { // 탈락 or 관전 모드
        if(api.getMapManager().getPlayingMap().isInMap(e.getRespawnLocation(), api.getGameManager().isTeleportedAll())) { // 맵 안에 있을 경우
          e.setRespawnLocation(e.getPlayer().getLocation());
        } else { // 맵 밖에 있을 경우
          e.setRespawnLocation(api.getMapManager().getProgressLocation());
        }
      } else {
        e.setRespawnLocation(api.getMapManager().getProgressLocation());
      }
    } else {
      if(api.getMapManager().getSpawn() != null) {
        e.setRespawnLocation(api.getMapManager().getSpawn());
      }
    }
  }

  //

  //

  @EventHandler
  public void onPing(ServerListPingEvent e) {
    e.setMotd(api.getGameManager().getGameState().getProgress() > GameState.PREPARING.getProgress() ? api.getPlayingMOTD() : api.getWaitingMOTD());
  }

  //

  //

  //

  private String getFormatMessage(ChatColor color, String message) {
    String format = AbilityPlugin.getInstance().getColorBroadcastFormat();
    format = String.format(format, "%1$s", AbilityPlugin.getInstance().getPrefix(), color);

    boolean useColor = AbilityPlugin.getInstance().isUseColorBroadcastColor();

    BaseComponent bc = (BaseComponent) Core.makeComponent(true, Core.makeComponentArr(useColor, format, message));

    return bc.toLegacyText();
  }

}