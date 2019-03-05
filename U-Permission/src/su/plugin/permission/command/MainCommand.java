package su.plugin.permission.command;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.permission.PermissionList;

public class MainCommand implements UCommandListener {
	
	@CommandHandler(
			name="pm",
			permission=PermissionList.PERMISSION_ADMIN,
			usage="펄미션 명령어를 확인합니다."
			)
	public void pm(UCommandSender sender, String[] args, Command command) {
		Core.nmsg(sender, "§7§l[ U-Permission ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("pm", 1)) {
			sc.sendUsage(sender, false);
		}
	}
	
}