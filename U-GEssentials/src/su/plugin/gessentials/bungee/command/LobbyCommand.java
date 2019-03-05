package su.plugin.gessentials.bungee.command;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UnregisterableCommandListener;
import su.plugin.core.common.api.player.UPlayer;

public class LobbyCommand implements UCommandListener, UnregisterableCommandListener {
	
	@CommandHandler(
			name = "lobby",
			aliases = {"로비", "fhql"},
			usage = "로비로 이동합니다.",
			permission = "gessentials.lobby"
			)
	public void mute(UPlayer p, String[] args, Command command) {
		p.msg(GGEssentialsAPI.getChannelManager().sendOptimizedLobby((ProxiedPlayer) p.getPlatformSender()) ? "§a로비로 이동합니다." : "§c이동 가능한 로비가 없습니다.");
	}
	
}