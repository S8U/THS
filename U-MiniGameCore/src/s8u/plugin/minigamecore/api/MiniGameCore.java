package s8u.plugin.minigamecore.api;

import lombok.Getter;
import s8u.plugin.minigamecore.api.bungee.BungeeManager;
import s8u.plugin.minigamecore.api.game.GameManager;
import s8u.plugin.minigamecore.api.map.MapManager;
import s8u.plugin.minigamecore.api.player.PlayerManager;
import s8u.plugin.minigamecore.api.spectator.SpectatorQuickBar;
import s8u.plugin.minigamecore.api.vote.map.MapVoteManager;
import s8u.plugin.minigamecore.api.vote.start.StartVoteManager;
import s8u.plugin.minigamecore.api.wait.WaitQuickBar;

public class MiniGameCore {

  @Getter
  private static WaitQuickBar waitQuickBar = new WaitQuickBar();
  @Getter
  private static SpectatorQuickBar spectatorQuickBar = new SpectatorQuickBar();

  @Getter
  private static GameManager gameManager;
  @Getter
  private static MapManager mapManager;
  @Getter
  private static PlayerManager playerManager;
  @Getter
  private static StartVoteManager startVoteManager;
  @Getter
  private static MapVoteManager mapVoteManager;
  @Getter
  private static BungeeManager bungeeManager;

  public void init() {
    gameManager = new GameManager();
    mapManager = new MapManager();
    playerManager = new PlayerManager();

    startVoteManager = new StartVoteManager();
    mapVoteManager = new MapVoteManager();

    bungeeManager = new BungeeManager();
  }

}