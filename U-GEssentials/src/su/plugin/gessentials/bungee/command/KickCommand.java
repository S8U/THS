package su.plugin.gessentials.bungee.command;

import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;

public class KickCommand implements UCommandListener {
	
	@CommandHandler(
			name = "kick",
			additional = "<플레이어> (<사유>)",
			minArgs = 1,
			usage="플레이어를 서버에서 강제로 퇴장시킵니다.",
			permission = "gessentials.kick"
			)
	public void kick(UCommandSender sender, String[] args, Command command) {
		UPlayer tup = Core.getUPlayerByDisplayName(args[0]);
		if(tup == null) {
			sender.wmsg("접속 중이 아닌 플레이어입니다.");
			return;
		}
		
		String reason = args.length < 2 ? "서버에서 강제 퇴장되었습니다." : String.join(" ", args).substring(args[0].length()).trim();
		
		boolean noBroadcast = reason.endsWith("$nobroadcast");
		reason = noBroadcast ? reason.substring(0, reason.length() - 15) : reason;
		
		GGEssentialsAPI.getPlayerManager().getEPlayer(tup.getPlayerKey()).kickPlayer(sender, reason);
		
		if(noBroadcast) {
			sender.nmsg(tup.getDisplayName() + "§7님을 [§f" + reason + "§7] 이유로 강제 퇴장시켰습니다.");
			return;
		}
		
		Core.bc("§c" + tup.getDisplayName() + "님께서 [" + reason + "] 이유로 강제 퇴장되었습니다. [처리자: " + sender.getDisplayName() + "]");
	}
	
}