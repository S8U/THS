package su.plugin.lobbysystem.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.lobbysystem.LobbySystemPlugin;
import su.plugin.lobbysystem.PermissionList;
import su.plugin.lobbysystem.api.LobbySystemAPI;

public class InvincibilityCommand implements UCommandListener {
	
	private LobbySystemAPI api = LobbySystemPlugin.getApi();
	
	@CommandHandler(
			name = "무적",
			aliases = "inv",
			additional = "<켜기/끄기>",
			minArgs = 1,
			usage = "무적 상태로 전환하거나 해제합니다."
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
		} else if(!(args[0].equals("켜기") || args[0].equals("끄기"))) {
			cmd.sendUsage(sender, true);
			return;
		}
		
		api.setInvincibility(target, args[0].equals("켜기") ? true : false);
		
		Core.msg(target, "무적 모드가 " + (api.isInvincibility(target) ? "활성화" : "비활성화") + "되었습니다.");
		if(!sender.getPlatformSender().equals(target)) {
			Core.msg(sender, target.getName() + "님의 무적 모드가 " + (api.isInvincibility(target) ? "활성화" : "비활성화") + "되었습니다.");
		}
	}

}