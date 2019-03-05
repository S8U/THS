package su.plugin.pvpstats.command;

import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.pvpstats.PVPStatsPlugin;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.PSPlayer;

public class UserCommand implements UCommandListener {
	
	private PVPStatsAPI api = PVPStatsPlugin.getApi();
	
	@SubCommandHandler(
			parent = "전적",
			name = "확인",
			aliases = { "조회", "정보", "show", "check", "info" },
			additional = "(<플레이어>)",
			maxArgs = 1,
			usage = "전적을 확인합니다."
			)
	public void info(UCommandSender sender, String[] args, Command cmd) {
		if(args.length < 1 && sender.isConsole()) {
			cmd.sendUsage(sender, true);
			return;
		}
		
		String player = args.length < 1 ? sender.getName() : args[0];

		PlayerKey playerKey = PlayerKey.getPlayerKey(player);
		if(playerKey == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		PSPlayer pp = api.getPlayerManager().getPSPlayer(playerKey);
		if(pp == null) {
			api.getSQLManager().loadPlayer(playerKey);
		}

		sender.nmsg("§6§l[ " + (args.length < 1 ? "내" : playerKey.getDisplayName() + " 님의") + " 전적 ]");
		sender.nmsg("§e[일간]");
		sender.nmsg("§e킬: §f" + pp.getDailyStats().getKillCount() + " §e/ 데스: §f" + pp.getDailyStats().getDeathCount() + " §e/ 어시스트: §f" + pp.getDailyStats().getAssistCount() + " §e/ 우승: §f" + pp.getDailyStats().getWinCount() + " §e/ 도망: §f" + pp.getDailyStats().getQuitCount());
		sender.nmsg("§e현재 연속 킬: §f" + pp.getDailyStats().getKillStreak() + " §e/ 현재 연속 데스: §f" + pp.getDailyStats().getDeathStreak() + " §e/ 현재 연속 우승: §f" + pp.getDailyStats().getWinStreak());
		sender.nmsg("§e최대 연속 킬: §f" + pp.getDailyStats().getMaxWinStreak() + " §e/ 최대 연속 데스: §f" + pp.getDailyStats().getMaxDeathStreak() + " §e/ 최대 연속 우승: §f" + pp.getDailyStats().getMaxWinStreak());
		sender.nmsg("§e[주간]");
		sender.nmsg("§e킬: §f" + pp.getWeeklyStats().getKillCount() + " §e/ 데스: §f" + pp.getWeeklyStats().getDeathCount() + " §e/ 어시스트: §f" + pp.getWeeklyStats().getAssistCount() + " §e/ 우승: §f" + pp.getWeeklyStats().getWinCount() + " §e/ 도망: §f" + pp.getWeeklyStats().getQuitCount());
		sender.nmsg("§e현재 연속 킬: §f" + pp.getWeeklyStats().getKillStreak() + " §e/ 현재 연속 데스: §f" + pp.getWeeklyStats().getDeathStreak() + " §e/ 현재 연속 우승: §f" + pp.getWeeklyStats().getWinStreak());
		sender.nmsg("§e최대 연속 킬: §f" + pp.getWeeklyStats().getMaxWinStreak() + " §e/ 최대 연속 데스: §f" + pp.getWeeklyStats().getMaxDeathStreak() + " §e/ 최대 연속 우승: §f" + pp.getWeeklyStats().getMaxWinStreak());
		sender.nmsg("§e[월간]");
		sender.nmsg("§e킬: §f" + pp.getMonthlyStats().getKillCount() + " §e/ 데스: §f" + pp.getMonthlyStats().getDeathCount() + " §e/ 어시스트: §f" + pp.getMonthlyStats().getAssistCount() + " §e/ 우승: §f" + pp.getMonthlyStats().getWinCount() + " §e/ 도망: §f" + pp.getMonthlyStats().getQuitCount());
		sender.nmsg("§e현재 연속 킬: §f" + pp.getMonthlyStats().getKillStreak() + " §e/ 현재 연속 데스: §f" + pp.getMonthlyStats().getDeathStreak() + " §e/ 현재 연속 우승: §f" + pp.getMonthlyStats().getWinStreak());
		sender.nmsg("§e최대 연속 킬: §f" + pp.getMonthlyStats().getMaxWinStreak() + " §e/ 최대 연속 데스: §f" + pp.getMonthlyStats().getMaxDeathStreak() + " §e/ 최대 연속 우승: §f" + pp.getMonthlyStats().getMaxWinStreak());
		sender.nmsg("§e[전체]");
		sender.nmsg("§e킬: §f" + pp.getAllStats().getKillCount() + " §e/ 데스: §f" + pp.getAllStats().getDeathCount() + " §e/ 어시스트: §f" + pp.getAllStats().getAssistCount() + " §e/ 우승: §f" + pp.getAllStats().getWinCount() + " §e/ 도망: §f" + pp.getAllStats().getQuitCount());
		sender.nmsg("§e현재 연속 킬: §f" + pp.getAllStats().getKillStreak() + " §e/ 현재 연속 데스: §f" + pp.getAllStats().getDeathStreak() + " §e/ 현재 연속 우승: §f" + pp.getAllStats().getWinStreak());
		sender.nmsg("§e최대 연속 킬: §f" + pp.getAllStats().getMaxWinStreak() + " §e/ 최대 연속 데스: §f" + pp.getAllStats().getMaxDeathStreak() + " §e/ 최대 연속 우승: §f" + pp.getAllStats().getMaxWinStreak());

		/*sender.nmsg("§e킬: §f" + pp.getAllStats().getKillCount());
		sender.nmsg("§e데스: §f" + pp.getAllStats().getDeathCount());
		sender.nmsg("§e어시스트: §f" + pp.getAllStats().getAssistCount());
		sender.nmsg("§e우승: §f" + pp.getAllStats().getWinCount());
		sender.nmsg("§e도망: §f" + pp.getAllStats().getQuitCount());
		sender.nmsg("§e현재 연속 킬: §f" + pp.getAllStats().getKillStreak());
		sender.nmsg("§e현재 연속 데스: §f" + pp.getAllStats().getDeathStreak());
		sender.nmsg("§e현재 연속 우승: §f" + pp.getAllStats().getWinStreak());
		sender.nmsg("§e최다 연속 킬: §f" + pp.getAllStats().getMaxKillStreak());
		sender.nmsg("§e최다 연속 데스: §f" + pp.getAllStats().getMaxDeathStreak());
		sender.nmsg("§e최다 연속 우승: §f" + pp.getAllStats().getMaxWinStreak());*/
	}
	
}