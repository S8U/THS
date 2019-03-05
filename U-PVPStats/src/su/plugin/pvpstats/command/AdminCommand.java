package su.plugin.pvpstats.command;

import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.util.KStringUtil;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.pvpstats.PVPStatsPlugin;
import su.plugin.pvpstats.PermissionList;
import su.plugin.pvpstats.api.PVPStatsAPI;

public class AdminCommand implements UCommandListener {
	
	private PVPStatsAPI api = PVPStatsPlugin.getApi();
	
/*
	@SubCommandHandler(
			parent = "전적",
			name = "킬설정",
			aliases = {"ㅋㅅㅈ", "setKill"},
			additional = "<플레이어> <숫자>",
			permission = PermissionList.PVPSTATS_ADMIN,
			minArgs = 2,
			usage = "킬을 설정합니다."
			)
	public void setKill(CommandSender sender, String[] args) {
		String playerKey = lib.getPlayerKey(args[0], true);
		if(playerKey == null) {
			lib.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		int num = NumberUtil.getInteger(args[0]);
		if(num == -1) {
			lib.wmsg(sender, "숫자는 정수만 입력 가능합니다.");
			return;
		}
		
		api.setKillCount(playerKey, num);
		
		lib.msg(sender, args[0] + "§c님의 킬 횟수를 §f" + num + "§c번으로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = "전적",
			name = "데스설정",
			aliases = {"ㄷㅅㅅㅈ", "setDeath"},
			additional = "<플레이어> <숫자>",
			permission = PermissionList.PVPSTATS_ADMIN,
			minArgs = 2,
			usage = "데스 횟수를 설정합니다."
			)
	public void setDeath(CommandSender sender, String[] args) {
		String playerKey = lib.getPlayerKey(args[0], true);
		if(playerKey == null) {
			lib.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		int num = NumberUtil.getInteger(args[0]);
		if(num == -1) {
			lib.wmsg(sender, "숫자는 정수만 입력 가능합니다.");
			return;
		}
		
		api.setDeathCount(playerKey, num);
		
		lib.msg(sender, args[0] + "§c님의 데스 횟수를 §f" + num + "§c번으로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = "전적",
			name = "어시스트설정",
			aliases = {"ㅇㅅㅅㅌㅅㅈ", "어시설정", "ㅇㅅㅅㅈ", "setAssist"},
			additional = "<플레이어> <숫자>",
			permission = PermissionList.PVPSTATS_ADMIN,
			minArgs = 2,
			usage = "어시스트 횟수를 설정합니다."
			)
	public void setAssist(CommandSender sender, String[] args) {
		String playerKey = lib.getPlayerKey(args[0], true);
		if(playerKey == null) {
			lib.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		int num = NumberUtil.getInteger(args[0]);
		if(num == -1) {
			lib.wmsg(sender, "숫자는 정수만 입력 가능합니다.");
			return;
		}
		
		api.setAssistCount(playerKey, num);
		
		lib.msg(sender, args[0] + "§c님의 킬 횟수을 §f" + num + "§c번으로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = "전적",
			name = "연속킬설정",
			aliases = {"ㅇㅅㅋㅅㅈ", "setKillStreak"},
			additional = "<플레이어> <숫자>",
			permission = PermissionList.PVPSTATS_ADMIN,
			minArgs = 2,
			usage = "연속킬 횟수를 설정합니다."
			)
	public void setKillStreak(CommandSender sender, String[] args) {
		String playerKey = lib.getPlayerKey(args[0], true);
		if(playerKey == null) {
			lib.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		int num = NumberUtil.getInteger(args[0]);
		if(num == -1) {
			lib.wmsg(sender, "숫자는 정수만 입력 가능합니다.");
			return;
		}
		
		api.setDeathCount(playerKey, num);
		
		lib.msg(sender, args[0] + "§c님의 연속 킬 횟수를 §f" + num + "§c번으로 설정했습니다.");
	}
*/

	@SubCommandHandler(
			parent = "전적",
			name = "랭킹업데이트",
			aliases = { "updateRanking", "ur" },
			permission = PermissionList.PVPSTATS_ADMIN,
			usage = "랭킹을 업데이트합니다."
	)
	public void ps_updateRanking(UCommandSender sender, String[] args) {
		api.getRankingManager().updateRanking(sender);
	}

	@SubCommandHandler(
			parent = "전적",
			name = "랭킹홀로그램설치",
			aliases = { "setRankingHologram", "srh" },
			permission = PermissionList.PVPSTATS_ADMIN,
			usage = "랭킹 홀로그램을 현재 위치에 설치합니다."
	)
	public void ps_setRankingHologram(UPlayer player, String[] args) {
		Player p = (Player) player.getPlatformSender();

		api.getRankingManager().setHologramLocation(p.getLocation());
		api.getRankingManager().updateRankingHologram(true);

		PVPStatsPlugin.getInstance().getJsonConfig().set("랭킹.홀로그램 위치", KStringUtil.locationToString(p.getLocation()));
		PVPStatsPlugin.getInstance().getJsonConfig().save();

		player.msg("§a랭킹 홀로그램을 현재 위치에 설치했습니다.");
	}
	
}