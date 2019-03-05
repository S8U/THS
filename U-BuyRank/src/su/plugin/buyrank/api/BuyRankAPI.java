package su.plugin.buyrank.api;

import lombok.Getter;
import lombok.Setter;
import su.plugin.buyrank.api.manager.FileManager;
import su.plugin.buyrank.api.manager.RankManager;
import su.plugin.buyrank.api.manager.SQLManager;

public class BuyRankAPI {

  @Setter
  @Getter
  private static boolean usePVPStats, broadcastOnBuy;

  @Getter
  private static RankManager rankManager;
  @Getter
  private static FileManager fileManager;
  @Getter
  private static SQLManager SQLManager;

  public void init() {
    rankManager = new RankManager();
    fileManager = new FileManager();
    SQLManager = new SQLManager();
  }

}