package su.plugin.gessentials.bungee.command;

import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.category.ListeningChannel;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;

public class ListeningChannelCommand implements UCommandListener{
	
	@CommandHandler(
			name = "listeningChannel",
			aliases = {"듣기채널", "듣기", "lc"},
			additional = "<전체/채널>",
			minArgs = 1,
			usage="듣기 채널을 변경합니다.",
			permission = "gessentials.listeningchannel"
			)
	public void listeningChannel(UPlayer p, String[] args, Command command) {
		EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p.getPlayerKey());
		
		ListeningChannel lc = args[0].equals("전체") || args[0].equalsIgnoreCase("global") ? ListeningChannel.GLOBAL : (args[0].equals("채널") || args[0].equalsIgnoreCase("local")) ? ListeningChannel.LOCAL : null;
		
		if(lc == null) {
			p.wmsg("존재하지 않는 듣기 채널입니다.");
			return;
		} else if(ep.getListeningChannel() == lc) {
			p.wmsg("§c이미 " + (lc == ListeningChannel.GLOBAL ? "전체 채팅을" : "채널 채팅만") + " 듣고 있습니다.");
			return;
		}
		
		ep.setListeningChannel(lc);
		
		p.msg("§a듣기 채널을 §f" + (lc == ListeningChannel.GLOBAL ? "전체" : "채널") + "§a로 변경했습니다.");
	}
	
}