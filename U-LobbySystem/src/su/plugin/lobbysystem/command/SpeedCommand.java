package su.plugin.lobbysystem.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.lobbysystem.LobbySystemPlugin;
import su.plugin.lobbysystem.PermissionList;
import su.plugin.lobbysystem.api.LobbySystemAPI;

public class SpeedCommand implements UCommandListener {
	
	private LobbySystemAPI api = LobbySystemPlugin.getApi();
	
	@CommandHandler(
			name = "속도",
			aliases = "speed",
			minArgs = 1,
			usage = "속도를 변경합니다."
			)
	public void invincibility(UCommandSender sender, String[] args, Command cmd) {
		Player target = sender.isConsole() ? null : (Player) sender.getPlatformSender();
		
		if(args.length > 1) {
			if(!sender.hasPermission(PermissionList.LOBBYSYSTEM_ADMIN)) {
				Core.wmsg(sender, cmd.getNoPermissionMessage());
				return;
			} else if((target = Bukkit.getPlayer(args[1])) == null) {
				Core.wmsg(sender, "접속 중이 아닌 플레이어입니다.");
			}
		} else if(target == null) {
			Core.wmsg(sender, "콘솔에서는 사용할 수 없습니다.");
			return;
		}
		
		int speed = 0;
		if(!NumberUtil.isInteger(args[0]) || (speed = NumberUtil.getInteger(args[0])) < 1 || speed > api.getMaxSpeed()) {
			cmd.sendUsage(sender, true);
			return;
		}

		api.setSpeed(target, speed);
		Core.msg(target, "속도가 " + speed + "로 변경되었습니다.");
		if(!sender.getPlatformSender().equals(target)) {
			Core.msg(sender, target.getName() + "님의 속도를 " + speed + "로 변경했습니다.");
		}
	}

}
