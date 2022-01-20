package s8u.plugin.minigamecore.api.game;

import lombok.Getter;
import org.bukkit.Bukkit;
import s8u.plugin.minigamecore.api.MiniGameCore;
import s8u.plugin.minigamecore.api.event.GameEndEvent;
import s8u.plugin.minigamecore.api.event.GameEndedEvent;
import s8u.plugin.minigamecore.api.event.GameStartEvent;
import s8u.plugin.minigamecore.api.event.GameStartedEvent;
import s8u.plugin.minigamecore.api.event.GameStopEvent;
import s8u.plugin.minigamecore.api.event.GameStoppedEvent;

@Getter
public class GameManager {

  private boolean gameStarted;

  private int numberOfPlayersOnStart;
  private int numberOfTeamsOnStart;

  private AutoStartHandler autoStartHandler = new DefaultAutoStartHandler();

  public void gameStart(boolean auto) {
    // GameStartEvent
    GameStartEvent gameStartEvent = new GameStartEvent(auto);
    if (gameStartEvent.isCancelled()) return;

    Bukkit.getPluginManager().callEvent(gameStartEvent);

    // 게임 시작 시
    gameStarted = true;

    MiniGameCore.getStartVoteManager().stopVote(null);

    // GameStartedEvent
    Bukkit.getPluginManager().callEvent(new GameStartedEvent(auto));
  }

  public void gameStop(boolean auto) {
    // GameStopEvent
    GameStopEvent gameStopEvent = new GameStopEvent(auto);
    if (gameStopEvent.isCancelled()) return;

    Bukkit.getPluginManager().callEvent(gameStopEvent);

    // 게임 종료 시
    gameStarted = false;

    // GameStoppedEvent
    Bukkit.getPluginManager().callEvent(new GameStoppedEvent(auto));
  }

  public void gameEnd(boolean auto) {
    // GameEndEvent
    GameEndEvent gameEndEvent = new GameEndEvent(auto);
    if (gameEndEvent.isCancelled()) return;

    Bukkit.getPluginManager().callEvent(gameEndEvent);

    // GameEndedEvent
    Bukkit.getPluginManager().callEvent(new GameEndedEvent(auto));
  }

}