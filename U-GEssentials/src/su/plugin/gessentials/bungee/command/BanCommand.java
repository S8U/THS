package su.plugin.gessentials.bungee.command;

import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.core.common.api.util.StringUtil;

public class BanCommand implements UCommandListener {
	
	@CommandHandler(
			name = "ban",
			additional = "<플레이어> (<사유>)",
			permission = "gessentials.ban",
			minArgs = 1,
			usage = "플레이어를 서버에서 차단시킵니다."
			)
	public void ban(UCommandSender sender, String[] args) {
		PlayerKey tpk = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(tpk == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		UPlayer tup = Core.getUPlayer(tpk);
		String targetDisplayName = tup == null ? Core.getDisplayName(tpk) : tup.getDisplayName();
		
		String reason = args.length < 2 ? "서버에서 차단되었습니다." : String.join(" ", args).substring(args[0].length()).trim();
		
		boolean noBroadcast = reason.endsWith("$nobroadcast");
		reason = noBroadcast ? reason.substring(0, reason.length() - 15) : reason;
		
		boolean ban = tup == null ? GGEssentialsAPI.getBanManager().banPlayerKey(tpk, sender, reason) : GGEssentialsAPI
				.getPlayerManager().getEPlayer(tpk).banPlayerKey(sender, reason);
		if(ban) {
			if(noBroadcast) {
				sender.msg(targetDisplayName+ " §7님을 [§f" + reason + "§7] 사유로 차단했습니다.");
				return;
			}
			
			Core.bc("§c" + targetDisplayName + " 님께서 [" + reason + "] 사유로 차단되었습니다. [처리자: " + sender.getDisplayName() + "]");
			return;
		}
		
		sender.wmsg("이미 차단된 플레이어입니다.");
	}
	
	@CommandHandler(
			name = "ipBan",
			aliases = "banIp",
			additional = "<플레이어 | IP> (<사유>)",
			minArgs = 1,
			usage = "/ipBan <플레이어 | IP> (<사유>) §7- IP를 서버에서 차단시킵니다.",
			permission = "gessentials.ipban"
			)
	public void ipBan(UCommandSender sender, String[] args) {
		boolean isIp = args[0].contains(".");
		
		String ip = args[0];
		
		PlayerKey tpk = null;
		UPlayer tup = null;
		String targetDisplayName = ip;
		
		if(!isIp) {
			tpk = PlayerKey.getPlayerKeyByDisplayName(args[0]);
			if(tpk == null) {
				sender.wmsg("존재하지 않는 플레이어입니다.");
				return;
			}
			
			tup = Core.getUPlayer(tpk);
			
			targetDisplayName = tup == null ? Core.getDisplayName(tpk) : tup.getDisplayName();
			
			ip = GGEssentialsAPI.getSQLManager().getEPlayerIp(tpk);
		}
		
		String reason = args.length < 2 ? "서버에서 차단되었습니다." : String.join(" ", args).substring(args[0].length()).trim();
		
		boolean noBroadcast = reason.endsWith("$nobroadcast");
		reason = noBroadcast ? reason.substring(0, reason.length() - 15) : reason;
		
		boolean ban = isIp ? GGEssentialsAPI.getBanManager().banIp(ip, sender, reason) : (tup == null ? GGEssentialsAPI
				.getBanManager().banPlayerIp(ip, sender, reason) : GGEssentialsAPI.getPlayerManager().getEPlayer(tpk).banIp(sender, reason));
		if(ban) {
			for(UPlayer aup : Core.getOnlineUPlayers()) {
				if(!aup.getIp().equals(ip)) continue;
				
				GGEssentialsAPI.getPlayerManager().getEPlayer(aup.getPlayerKey()).kickPlayer(sender, reason);
			}
			
			if(noBroadcast) {
				sender.msg(targetDisplayName + " §7" + (isIp ? "" : " 님의") + " IP를 [§f" + reason + "§7] 사유로 차단했습니다.");
				return;
			}
			
			Core.bc("§c" + targetDisplayName + (isIp ? "" : " 님의") + " IP가 [" + reason + "] 사유로 차단되었습니다. [처리자: " + sender.getDisplayName() + "]");
			return;
		}
		
		sender.wmsg("이미 차단된 IP입니다.");
	}
	
	@CommandHandler(
			name = "timeBan",
			aliases = "tempBan",
			additional = "<플레이어> <d:일 / h:시간 / m:분 / s:초> (<사유>)",
			minArgs = 2,
			usage = "플레이어를 서버에서 일정 기간 동안 차단시킵니다.",
			permission = "gessentials.timeban"
			)
	public void timeBan(UCommandSender sender, String[] args, Command command) {
		PlayerKey tpk = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(tpk == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		UPlayer tup = Core.getUPlayer(tpk);
		String targetDisplayName = tup == null ? Core.getDisplayName(tpk) : tup.getDisplayName();
		
		String[] arr = getTimeReason(args);
		
		long time = Long.parseLong(arr[0]), cTime = System.currentTimeMillis() + time;
		String reason = arr[1], timeStr = StringUtil.buildDateString(cTime, "yyyy년 MM월 dd일 a h시 mm분 ss초");
		
		boolean noBroadcast = reason.endsWith("$nobroadcast");
		reason = noBroadcast ? reason.substring(0, reason.length() - 15) : reason;
		
		boolean ban = tup == null ? GGEssentialsAPI
				.getBanManager().banPlayerKey(tpk, sender, reason, time) : GGEssentialsAPI.getPlayerManager().getEPlayer(tpk).banPlayerKey(sender, reason, time);
		if(ban) {
			if(noBroadcast) {
				sender.msg(targetDisplayName + " §7님을 [§f" + reason + "§7] 사유로 [§f" + timeStr + "§7] 까지 차단했습니다.");
				return;
			}
			
			Core.bc("§c" + targetDisplayName + " 님께서 [" + reason + "] 사유로 [" + timeStr + "] 까지 차단되었습니다. [처리자: " + sender.getDisplayName() + "]");
			return;
		}
		
		sender.wmsg("이미 차단된 플레이어입니다.");
	}
	
	@CommandHandler(
			name = "timeIpBan",
			aliases = {"timeBanIp", "timeIpBan", "tempBanIp", "tempIpBan"},
			additional = "<플레이어 | IP> <d:일 / h:시간 / m:분 / s:초> (<사유>)",
			minArgs = 2,
			usage = "아이피를 서버에서 일정 기간 동안 차단시킵니다.",
			permission = "gessentials.timeipban"
			)
	public void tempIpBan(UCommandSender sender, String[] args, Command command) {
		boolean isIp = args[0].contains(".");
		
		String ip = args[0];
		
		PlayerKey tpk = null;
		UPlayer tup = null;
		
		String target = ip;
		String targetDisplayName = ip;
		
		if(!isIp) {
			tpk = PlayerKey.getPlayerKeyByDisplayName(args[0]);
			if(tpk == null) {
				sender.wmsg("존재하지 않는 플레이어입니다.");
				return;
			}
			
			tup = Core.getUPlayer(tpk);
			
			target = tpk.getName();
			targetDisplayName = tup == null ? Core.getDisplayName(tpk) : tup.getDisplayName();
			
			ip = GGEssentialsAPI.getSQLManager().getEPlayerIp(tpk);
		}
		
		String[] arr = getTimeReason(args);
		
		long time = Long.parseLong(arr[0]), cTime = System.currentTimeMillis() + time;
		String reason = arr[1], timeStr = StringUtil.buildDateString(cTime, "yyyy년 MM월 dd일 a h시 mm분 ss초");
		
		boolean noBroadcast = reason.endsWith("$nobroadcast");
		reason = noBroadcast ? reason.substring(0, reason.length() - 15) : reason;
		
		boolean ban = isIp ? GGEssentialsAPI.getBanManager().banIp(ip, sender, reason, time) : (tup == null ? GGEssentialsAPI
				.getBanManager().banPlayerIp(target, sender, reason, time) : GGEssentialsAPI.getPlayerManager().getEPlayer(tpk).banIp(sender, reason, time));
		if(ban) {
			String kickReason = reason += "\n(차단 해제 시간: " + StringUtil.buildDateString(
					GGEssentialsAPI.getBanManager().getBanData(ip).getUnBanTime(), "yyyy년 MM월 dd일 a h시 mm분 ss초") + ")";
			for(UPlayer aup : Core.getOnlineUPlayers()) {
				if(aup.getPlatformSender() == null || !aup.getIp().equals(ip)) continue;
				
				GGEssentialsAPI
						.getPlayerManager().getEPlayer(aup.getPlayerKey()).kickPlayer(sender, kickReason, 3);
			}
			
			if(noBroadcast) {
				sender.msg(targetDisplayName + "§7" + (isIp ? "" : " 님의") + " IP를 [§f" + reason + "§7] 사유로 [§f" + timeStr + "§7] 까지 차단했습니다.");
				return;
			}
			
			Core.bc("§c" + targetDisplayName + (isIp ? "" : " 님의") + " IP가 [" + reason + "] 사유로 [" + timeStr + "] 까지 차단되었습니다. [처리자: " + sender.getDisplayName() + "]");
			return;
		}
		
		sender.wmsg("이미 차단된 IP입니다.");
	}
	
	private String[] getTimeReason(String[] args) {
		String[] arr = new String[2];
		
		long time = 0;
		int length = 0;
		
		for(int i = 0; i < 4; i++) {
			if(args.length < i + 1 || args[i].length() < 3) break;
			
			String ts = args[i].substring(0, 2);
			Integer num = NumberUtil.getInteger(args[i].substring(2, args[i].length()));
			
			if(num == null) continue;
			
			time += num * (ts.equalsIgnoreCase("d:") ? 86400000 : (ts.equalsIgnoreCase("h:") ? 3600000 : (ts.equalsIgnoreCase("m:") ? 60000 : (ts.equalsIgnoreCase("s:") ? 1000 : 0))));
			length++;
		}
		
		String reason = args.length > length + 1 ? StringUtil.connectString(args, length + 1, " ") : "서버에서 차단되었습니다.";
		
		arr[0] = String.valueOf(time);
		arr[1] = reason;
		
		return arr;
	}
	
}