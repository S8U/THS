package su.plugin.pvpstats.command;

import java.util.List;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.command.UnregisterableCommandListener;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.pvpstats.PVPStatsPlugin;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.Stats;

public class RankingCommand implements UCommandListener, UnregisterableCommandListener {
	
	private PVPStatsAPI api = PVPStatsPlugin.getApi();

	private void ranking(UCommandSender sender, String[] args, List<Stats> rankings, String period) {
		int page = args.length < 1 ? 1 : NumberUtil.getInteger(args[0]);
		if(page == -1) {
			sender.wmsg("페이지는 정수만 입력 가능합니다.");
			return;
		}

		int maxPage = (int) (Math.ceil(rankings.size() / 7) + 1);
		if(page > maxPage) {
			sender.wmsg("페이지는 1부터 " + maxPage + "까지의 정수만 입력 가능합니다.");
			return;
		}

		if(rankings.size() < 1) {
			sender.wmsg("아직 전적이 기록된 플레이어가 없습니다.");
			return;
		}

		sender.nmsg("§6§l[ " + period + " 랭킹 ( " + page + " / " + maxPage + " ) ]");
		for(int i = 0; i < 7; i++) {
			int num = (page -  1) * 7 + i;
			if(rankings.size() < num + 1) break;

			Stats s = rankings.get(i);

			sender.nmsg("§e" + (num + 1) + ": §f" + s.getPlayerKey().getDisplayName() + " §e- §f" + s.getKillCount() + " §e킬 §f" + s.getDeathCount() + " §e데스 §f" + s.getAssistCount() + " §e어시스트 §f" + s.getWinStreak() + " §e우승");
		}
	}

	@SubCommandHandler(
			parent = "전적",
			name = "일간랭킹",
			aliases = {"dailyRanking", "dr", "dailyTop", "dt"},
			additional = "(<페이지>)",
			usage = "일간 랭킹를 확인합니다."
	)
	public void dailyRanking(UCommandSender sender, String[] args) {
		ranking(sender, args, api.getRankingManager().getDailyRankings(), "일간");
	}

	@SubCommandHandler(
			parent = "전적",
			name = "주간랭킹",
			aliases = {"weeklyRanking", "wr", "weeklyTop", "wt"},
			additional = "(<페이지>)",
			usage = "주간 랭킹를 확인합니다."
	)
	public void weeklyRanking(UCommandSender sender, String[] args) {
		ranking(sender, args, api.getRankingManager().getWeeklyRankings(), "주간");
	}

	@SubCommandHandler(
			parent = "전적",
			name = "월간랭킹",
			aliases = {"monthlyRanking", "mr", "monthlyTop", "mt"},
			additional = "(<페이지>)",
			usage = "월간 랭킹를 확인합니다."
	)
	public void monthlyRanking(UCommandSender sender, String[] args) {
		ranking(sender, args, api.getRankingManager().getMonthlyRankings(), "월간");
	}

	@SubCommandHandler(
			parent = "전적",
			name = "전체랭킹",
			aliases = {"allRanking", "ar", "allTop", "at"},
			additional = "(<페이지>)",
			usage = "전체 랭킹를 확인합니다."
	)
	public void allRanking(UCommandSender sender, String[] args) {
		ranking(sender, args, api.getRankingManager().getAllRankings(), "전체");
	}
	
}