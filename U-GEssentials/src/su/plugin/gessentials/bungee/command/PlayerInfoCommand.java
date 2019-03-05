package su.plugin.gessentials.bungee.command;

import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;

public class PlayerInfoCommand implements UCommandListener {
	
	@CommandHandler(
			name = "playerInfo",
			aliases = {"pi", "info", "플레이어정보", "정보"},
			additional = "<플레이어>",
			minArgs = 1,
			usage="플레이어의 정보를 확인합니다.",
			permission = "gessentials.playerinfo"
			)
	public void playerInfo(UCommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(playerKey == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		EPlayer target = GGEssentialsAPI.getPlayerManager().getEPlayer(playerKey);
		if(target == null) {
			target = GGEssentialsAPI.getSQLManager().getEPlayer(playerKey);
			
			if(target == null) {
				sender.wmsg("존재하지 않는 플레이어입니다.");
				return;
			}
		}
		
		sender.nmsg("§7[ 플레이어 정보 ]");
		sender.nmsg("§7닉네임: §f" + target.getName());
		if(!target.getDisplayName().equals(target.getName())) {
			sender.nmsg("§7닉네임 표기: §f" + target.getDisplayName());
		}
		sender.nmsg("§7상태: " + (target.isOnline() ? "§a온라인" : "§c오프라인"));
		if(target.isOnline()) {
			sender.nmsg("§7접속 중인 채널: §f" + target.getEChannel().getDisplayName());
		}
		sender.nmsg("§7정품: §f" + (playerKey.isOnlineMode() ? "O" : "X"));
		
		// Require Party
	}
	
}