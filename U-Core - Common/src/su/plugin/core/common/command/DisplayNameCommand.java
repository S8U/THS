package su.plugin.core.common.command;

import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.StringUtil;

public class DisplayNameCommand implements UCommandListener {

	@CommandHandler(
			name = "displayname",
			aliases = {"닉네임", "slrspdla", "nick", "dn"},
			additional = "<플레이어> <닉네임>",
			minArgs = 2,
			usePlatformPrefix = true,
			permission = "core.admin",
			usage = "플레이어의 닉네임 표기를 변경합니다."
	)
	@SubCommandHandler(
			parent = "core",
			name = "displayname",
			aliases = {"닉네임", "slrspdla", "nick", "dn"},
			additional = "<플레이어> <닉네임>",
			minArgs = 2,
			permission = "core.admin",
			usage = "플레이어의 닉네임 표기를 변경합니다."
	)
	public void displayName(UCommandSender sender, String[] args) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		String displayName = ChatColor.translateAlternateColorCodes('&', StringUtil.connectString(args, " ").substring(args[0].length() + 1));
		
		UPlayer up = Core.getUPlayer(playerKey);
		if(up != null) {
			up.setDisplayName(displayName);
		}
		
		sender.msg(args[0] + " 님의 닉네임 표기를 " + displayName + "§f(으)로 변경했습니다.");
	}
	
}