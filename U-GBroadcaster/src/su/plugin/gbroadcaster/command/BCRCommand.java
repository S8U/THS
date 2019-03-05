package su.plugin.gbroadcaster.command;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.gbroadcaster.GBroadcasterPlugin;
import su.plugin.gbroadcaster.api.GBroadcasterAPI;

public class BCRCommand implements UCommandListener {
	
	private GBroadcasterAPI api = GBroadcasterPlugin.getApi();
	
	@CommandHandler(
			name = "broadcaster",
			aliases = {"bcr", "자동공지"},
			usage = "자동 공지 명령어를 확인합니다.",
			permission="gbroadcaster.admin"
	)
	public void broadCaster(UCommandSender sender, String[] args, Command cmd) {
		sender.nmsg("§e§l[ U-GBroadcaster ]");
		for(Command sc : Core.getCommandManager().getSubCommands(cmd.getName(), 1)) {
			sc.sendUsageIfHasPermission(sender, false);
		}
	}

	@SubCommandHandler(
			parent = "broadcaster",
			name = "reload",
			aliases = {"리로드"},
			usage = "공지를 다시 불러옵니다.",
			permission = "gbroadcaster.admin"
	)
	public void broadCaster_reload(UCommandSender sender, String[] args, Command cmd) {
		api.getBroadcastManager().stopAllTasks();

		GBroadcasterPlugin.getInstance().onConfigLoad();
		api.getBroadcastManager().startAllTasks();

		sender.msg("설정을 다시 불러왔습니다.");
	}
	
}
