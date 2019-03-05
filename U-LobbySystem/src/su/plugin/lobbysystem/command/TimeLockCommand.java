package su.plugin.lobbysystem.command;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.lobbysystem.LobbySystemPlugin;
import su.plugin.lobbysystem.PermissionList;
import su.plugin.lobbysystem.api.LobbySystemAPI;

public class TimeLockCommand implements UCommandListener {
	
	private LobbySystemAPI api = LobbySystemPlugin.getApi();
	
	@CommandHandler(
			name = "시간고정",
			aliases = "timelock",
			additional = "<-1 | 시간>",
			minArgs = 1,
			permission = PermissionList.LOBBYSYSTEM_ADMIN,
			usage = "시간을 고정하거나 해제합니다."
			)
	public void invincibility(UCommandSender sender, String[] args, Command cmd) {
		if(!NumberUtil.isInteger(args[0])) {
			cmd.sendUsage(sender, true);
			return;
		}
		
		int time = NumberUtil.getInteger(args[0]);
		if(time == -1) {
			api.setUseTimeLock(false);
			api.getTimeLockTask().cancel();
			
			Core.msg(sender, "시간 고정이 해제되었습니다.");
			return;
		}
		
		api.setLockTime(time);
		
		api.getTimeLockTask().cancel();
		api.getTimeLockTask().runTaskTimer(0, api.getLockInterval());
		
		Core.msg(sender, "고정 시간이 " + time + "(으)로 변경되었습니다.");
	}
	
}