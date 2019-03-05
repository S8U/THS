package su.plugin.pvpstats.command;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;

public class MainCommand implements UCommandListener {
	
	@CommandHandler(
			name = "전적",
			aliases = { "킬뎃", "pvpstats", "ps", "killstate", "ks" },
			usage = "전적 명령어를 확인합니다."
			)
	public void ps(UCommandSender sender, String[] args, Command command) {
		sender.nmsg("§c§l[ U-PVPStats ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("전적", 1)) {
			sc.sendUsageIfHasPermission(sender, false);
		}
	}
	
}