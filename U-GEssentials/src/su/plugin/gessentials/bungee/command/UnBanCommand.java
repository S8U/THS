package su.plugin.gessentials.bungee.command;

import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.StringUtil;

public class UnBanCommand implements UCommandListener {
	
	@CommandHandler(
			name = "unBan",
			additional = "<플레이어>",
			minArgs = 1,
			usage = "플레이어 차단을 해제합니다.",
			permission = "gessentials.unban"
			)
	public void unBan(UCommandSender sender, String[] args, Command command) {
		PlayerKey tpk = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(tpk == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		UPlayer tup = Core.getUPlayer(tpk);
		String targetDisplayName = tup == null ? Core.getDisplayName(tpk) : tup.getDisplayName();
		
		String reason = args.length < 2 ? "서버에서 차단되었습니다." : StringUtil.connectString(args, " ").substring(args[0].length()).trim();
		
		boolean noBroadcast = reason.endsWith("$nobroadcast");
		reason = noBroadcast ? reason.substring(0, reason.length() - 15) : reason;
		
		if(GGEssentialsAPI.getBanManager().unBanPlayerKey(tpk, sender)) {
			if(noBroadcast) {
				sender.msg(targetDisplayName + " §7님의 차단을 해제했습니다.");
				return;
			}
			
			Core.bc("§a" + targetDisplayName + " 님의 차단이 해제되었습니다. [처리자: " + sender.getDisplayName() + "]");
			return;
		}
		
		sender.wmsg("차단되지 않은 플레이어입니다.");
	}
	
	@CommandHandler(
			name = "unBanIp",
			aliases = "unIpBan",
			additional = "<플레이어 | IP>",
			minArgs = 1,
			usage="IP 차단을 해제합니다.",
			permission = "gessentials.unipban"
			)
	public void unBanIp(UCommandSender sender, String[] args, Command command) {
		boolean isIp = args[0].contains(".");
		
		String ip = args[0];
		
		PlayerKey tpk = null;
		
		String target = ip;
		String targetDisplayName = ip;
		
		if(!isIp) {
			tpk = PlayerKey.getPlayerKeyByDisplayName(args[0]);
			if(tpk == null) {
				sender.wmsg("존재하지 않는 플레이어입니다.");
				return;
			}
			
			target = tpk.getName();
			targetDisplayName = Core.getDisplayName(tpk);
			
			ip = GGEssentialsAPI.getSQLManager().getEPlayerIp(tpk);
		}
		
		boolean noBroadcast = args.length > 1 && args[1].endsWith("$nobroadcast");
		
		boolean unBan = isIp ? GGEssentialsAPI.getBanManager().unBanIp(ip, sender) : GGEssentialsAPI.getBanManager().unBanPlayerIp(target, sender);
		
		if(unBan) {
			if(noBroadcast) {
				sender.msg(targetDisplayName + "§7" + (isIp ? "" : " 님의") + " IP 차단을 해제했습니다.");
				return;
			}
			
			Core.bc("§a" + targetDisplayName + (isIp ? "" : " 님의") + " IP 차단이 해제되었습니다. [처리자: " + sender.getDisplayName() + "]");
			return;
		}
		
		sender.wmsg("차단되지 않은 IP입니다.");
	}
	
}