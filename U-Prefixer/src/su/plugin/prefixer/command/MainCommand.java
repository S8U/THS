package su.plugin.prefixer.command;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;

public class MainCommand implements UCommandListener {
	
	@CommandHandler (
			name = "칭호",
			aliases = { "ㅊㅎ", "prefix", "prefixer", "pf", },
			usage = "칭호 명령어를 확인합니다."
			)
	public void pm(UCommandSender sender, String[] args, Command command) {
		sender.nmsg("§d§l[ U-Prefixer ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("칭호", 1)) {
			sc.sendUsageIfHasPermission(sender, false);
		}
	}
	
}