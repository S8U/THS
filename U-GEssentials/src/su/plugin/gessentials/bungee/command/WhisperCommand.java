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

public class WhisperCommand implements UCommandListener {
	
	@CommandHandler(
			name = "whisper",
			aliases = {"w", "message", "msg",  "m", "tell", "t", "귓속말"},
			additional = "<플레이어> <메시지>",
			minArgs = 2,
			usage = "플레이어에게 귓속말을 보냅니다.",
			permission = "gessentials.whisper"
			)
	public void whisper(UCommandSender sender, String[] args, Command command) {
		UPlayer target = Core.getUPlayerByDisplayName(args[0]);
		if(target == null) {
			sender.wmsg("접속 중이 아닌 플레이어입니다.");
			return;
		}
		
		GGEssentialsAPI
				.getChatManager().sendWhisper(sender, target, StringUtil.connectString(args, " ").substring(args[0].length()).trim());
	}
	
	@CommandHandler(
			name = "reply",
			aliases = {"r", "답장"},
			additional = "<메시지>",
			minArgs = 1,
			usage = "플레이어에게 답장을 보냅니다.",
			permission = "gessentials.reply"
			)
	public void reply(UCommandSender sender, String[] args, Command command) {
		int targetId = GGEssentialsAPI.getChatManager().getLastWhispers().get(sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId());
		UCommandSender target = targetId == -1 ? Core.getUConsoleCommandSender() : Core.getUPlayer(PlayerKey.getPlayerKey(targetId));
		if(target == null) {
			sender.wmsg("상대방이 접속 중이 아닙니다.");
			return;
		}
		
		GGEssentialsAPI
				.getChatManager().sendWhisper(sender, target, StringUtil.connectString(args, " ").trim());
	}
	
}