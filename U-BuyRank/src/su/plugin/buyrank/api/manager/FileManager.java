package su.plugin.buyrank.api.manager;

import java.io.File;
import su.plugin.buyrank.BuyRankPlugin;
import su.plugin.buyrank.api.BuyRankAPI;
import su.plugin.buyrank.api.object.Rank;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.config.json.JsonConfig;

public class FileManager {

  private JsonConfig rankConfig = new JsonConfig(new File(BuyRankPlugin.getInstance().getDataFolder(), "rank.json"));

  public void createRankConfig() {
    if(rankConfig.getFile().exists()) return;

    rankConfig.addDefault("판매 등급.newbie.권한", "buyrank.newbie");
    rankConfig.addDefault("판매 등급.newbie.가격", 1000);

    rankConfig.save();
  }

  public void loadRankConfig() {
    createRankConfig();

    BuyRankAPI.getRankManager().getRanks().clear();

    for(String rankName : rankConfig.getKeys("판매 등급")) {
      Rank rank = new Rank(rankName);
      rank.setPermission(rankConfig.getString("판매 등급." + rank + ".권한"));
      rank.setPrice(rankConfig.getDouble("판매 등급." + rank + ".가격"));
      rank.setKillCount(rankConfig.getInt("판매 등급." + rank + ".킬"));

      BuyRankAPI.getRankManager().setRank(rankName, rank);
    }

    Core.log(BuyRankAPI.getRankManager().getRanks().size() + "개의 등급을 불러왔습니다.");
  }

  public void saveRank(Rank rank) {
    createRankConfig();

    rankConfig.set("판매 등급." + rank.getName() + ".가격", rank.getPrice());
    rankConfig.set("판매 등급." + rank.getName() + ".권한", rank.getPermission());
    rankConfig.set("판매 등급." + rank.getName() + ".킬", rank.getKillCount() < 1 ? null : rank.getKillCount());

    rankConfig.save();
  }

  public void saveRanks() {
    for(Rank rank : BuyRankAPI.getRankManager().getRanks().values()) {
      saveRank(rank);
    }

    Core.log("모든 등급을 저장했습니다.");
  }

  public void deleteRank(String rankName) {
    rankConfig.set("판매 등급." + rankName, null);

    rankConfig.save();
  }

}