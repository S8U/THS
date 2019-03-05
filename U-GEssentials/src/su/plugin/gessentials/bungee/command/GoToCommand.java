package su.plugin.gessentials.bungee.command;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;

public class GoToCommand implements UCommandListener {
	
	@CommandHandler(
			name = "goto",
			aliases = {"찾아가기"},
			additional = "<플레이어>",
			minArgs = 1,
			usage = "플레이어가 접속 중인 채널로 이동합니다.",
			permission = "gessentials.goto"
			)
	public void goTo(UPlayer p, String[] args, Command command) {
		UPlayer target = Core.getUPlayerByDisplayName(args[0]);
		if(target == null) {
			p.wmsg("접속 중이 아닌 플레이어입니다.");
			return;
		}
		
		ProxiedPlayer pp = (ProxiedPlayer) p.getPlatformSender();
		
		ProxiedPlayer ptarget = (ProxiedPlayer) target.getPlatformSender();
		if(((ProxiedPlayer) p.getPlatformSender()).getServer().equals(ptarget.getServer())) {
			p.wmsg("이미 같은 채널에 접속 중입니다.");
			return;
		}
		
		pp.connect(ptarget.getServer().getInfo());
		
		p.msg(target.getDisplayName() + " §e님이 접속 중인 채널로 이동했습니다.");
	}
}