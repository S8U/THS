package su.plugin.gessentials.bungee.command;

import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;

public class AdminChatCommand implements UCommandListener {
	
	@CommandHandler(
			name = "adminChat",
			aliases = {"ac", "관리자채팅"},
			additional = "(<메시지>)",
			usage = "관리자 채팅을 보내거나 관리자 채팅 모드로 전환하거나 해제합니다.",
			permission="gessentials.adminchat"
			)
	public void adminChat(UCommandSender sender, String[] args, Command command) {
		if(sender.isConsole() && args.length < 1) {
			command.sendUsage(sender, false);
			return;
		} else if(args.length > 0) {
			String message = String.join(" ", args);
			GGEssentialsAPI.getChatManager().sendAdminChat(sender, message);
			return;
		}
		
		int id = ((UPlayer) sender).getPlayerKey().getId();
		
		boolean toggle = GGEssentialsAPI.getChatManager().getAdminChats().add(id);
		if(!toggle) {
			GGEssentialsAPI.getChatManager().getAdminChats().remove(id);
		}
		
		sender.msg((toggle ? "§7" : "§c") + "관리자 채팅 모드가 " + (toggle ? "활성화" : "비활성화") + "되었습니다.");
	}
	
}