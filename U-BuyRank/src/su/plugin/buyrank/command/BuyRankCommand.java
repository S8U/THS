package su.plugin.buyrank.command;

import java.util.HashMap;
import su.plugin.buyrank.BuyRankPlugin;
import su.plugin.buyrank.PermissionList;
import su.plugin.buyrank.api.BuyRankAPI;
import su.plugin.buyrank.api.object.Rank;
import su.plugin.core.bukkit.api.lib.VaultHandler;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionPlayer;
import su.plugin.pvpstats.api.PVPStatsAPI;

public class BuyRankCommand implements UCommandListener {

  private BuyRankAPI api = BuyRankPlugin.getApi();

  private HashMap<PlayerKey, Long> cool = new HashMap<>();

  @CommandHandler(
      name = "등급구매",
      aliases = {"등급구입", "buyRank", "br"},
      usage = "등급 구매 명령어를 확인합니다."
  )
  public void buyRank(UCommandSender sender, String[] args) {
    Core.nmsg(sender, "§9§l[ U-BuyRank ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("등급구매", 1)) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

  @SubCommandHandler(
      parent = "등급구매",
      name = "구매",
      aliases = {"구입", "buy"},
      additional = "<등급>",
      minArgs = 1,
      usage = "등급을 구매합니다."
  )
  public void buyRank_buy(UPlayer up, String[] args) {
    if(cool.containsKey(up.getPlayerKey()) && System.currentTimeMillis() - cool.get(up.getPlayerKey()) < 500) return;
    cool.put(up.getPlayerKey(), System.currentTimeMillis());

    Rank rank = api.getRankManager().getRank(args[0]);
    if(rank == null) {
      up.wmsg("판매하지 않는 등급입니다.");
      return;
    }

    if(rank.getPermission() != null && !up.hasPermission(rank.getPermission())) {
      up.wmsg("등급을 구입할 권한이 없습니다.");
      return;
    } else if(api.isUsePVPStats() && PVPStatsAPI.getPlayerManager().getPSPlayer(up.getPlayerKey()).getAllStats().getKillCount() < rank.getKillCount()) {
      up.wmsg("킬이 부족합니다.");
      return;
    } else if(VaultHandler.getMoney(up.getName()) < rank.getPrice()) {
      up.wmsg("돈이 부족합니다.");
      return;
    }

    PermissionPlayer pp = PermissionAPI.getPlayerManager().getPermissionPlayer(up.getPlayerKey());

    pp.setGroupName(rank.getName());
    pp.updatePermissionAttachment();

    PermissionAPI.getSQLManager().setPlayerGroup(up.getPlayerKey(), rank.getName());

    PermissionAPI.getPlayerManager().sendPlayerChange(up.getPlayerKey());

    VaultHandler.getEconomy().withdrawPlayer(up.getName(), rank.getPrice());

    up.msg(rank.getName() + " §9등급을 구입했습니다.");

    if(api.isBroadcastOnBuy()) {
      Core.bc(up.getDisplayName() + " §9님께서 §f" + rank.getName() + " §9등급을 구입했습니다.");
    }

    api.getSQLManager().writeBuyLog(up.getPlayerKey(), rank.getName(), rank.getPrice());
  }

  @SubCommandHandler(
      parent = "등급구매",
      name = "목록",
      aliases = {"list"},
      usage = "등급 목록을 확인합니다.",
      permission = PermissionList.BUYRANK_LIST
  )
  public void buyRank_list(UCommandSender sender, String[] args) {
    if(api.getRankManager().getRanks().isEmpty()) {
      sender.wmsg("판매 중인 등급이 없습니다.");
      return;
    }

    sender.nmsg("§9[ 등급 목록 (" + api.getRankManager().getRanks().size() + "개) ]");
    for(Rank rank : api.getRankManager().getRanks().values()) {
      sender.nmsg(rank.getName() + "§9) 가격: §f" + rank.getPrice() + " §9/ 권한: §f" + rank.getPermission() + " §9/ 킬: §f" + rank.getKillCount());
    }
  }

  @SubCommandHandler(
      parent = "등급구매",
      name = "설정",
      aliases = {"set"},
      additional = "<등급> <권한> <가격> (<킬>)",
      minArgs = 3,
      usage = "등급을 설정합니다.",
      permission = PermissionList.BUYRANK_ADMIN
  )
  public void buyRank_set(UCommandSender sender, String[] args) {
    Rank rank = api.getRankManager().getRank(args[0]);
    if(rank == null) {
      rank = new Rank(args[0]);
      api.getRankManager().setRank(rank.getName(), rank);
    }

    rank.setPermission(args[1].equalsIgnoreCase("null") ? null : args[1]);
    rank.setPrice(Double.parseDouble(args[2]));
    if(args.length > 3) {
      rank.setKillCount(Integer.parseInt(args[3]));
    }

    api.getFileManager().saveRank(rank);

    sender.msg(rank.getName() + " §9등급이 설정되었습니다. (가격: §f" + rank.getPrice() + "§9, 권한: §f" + rank.getPermission() + "§9, 킬: §f" + rank.getKillCount() + "§9)");
  }

  @SubCommandHandler(
      parent = "등급구매",
      name = "삭제",
      aliases = {"delete"},
      additional = "<등급>",
      minArgs = 1,
      usage = "등급을 삭제합니다.",
      permission = PermissionList.BUYRANK_ADMIN
  )
  public void buyRank_delete(UCommandSender sender, String[] args) {
    if(!api.getRankManager().existRank(args[0])) {
      sender.wmsg("존재하지 않는 등급입니다!");
      return;
    }

    api.getRankManager().deleteRank(args[0]);
    api.getFileManager().deleteRank(args[0]);

    sender.msg(args[0] + " §c등급을 삭제했습니다.");
  }

}