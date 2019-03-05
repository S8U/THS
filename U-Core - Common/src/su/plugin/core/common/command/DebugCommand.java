package su.plugin.core.common.command;

import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.DebugUtil;

public class DebugCommand implements UCommandListener {
	
	@CommandHandler(
			name = "debugMode",
			aliases = {"디버그모드", "elqjrmahem"},
			usePlatformPrefix = true,
			permission = "core.admin",
			usage = "디버그 모드로 전환하거나 해제합니다."
			)
	@SubCommandHandler(
			parent = "core",
			name = "debugMode",
			aliases = {"디버그모드", "elqjrmahem"},
			permission = "core.admin",
			usage = "디버그 모드로 전환하거나 해제합니다."
	)
	public void debugMode(UCommandSender sender, String[] args) {
		int id = sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId();

		DebugUtil.setDebugMode(id, !DebugUtil.isDebugMode(id));
		
		sender.msg("디버그 모드" + (DebugUtil.isDebugMode(id) ? "로 전환" : "가 해제") + "되었습니다.");
	}
	
}