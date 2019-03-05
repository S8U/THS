package su.plugin.gessentials.bungee.command;

import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;

public class SpyCommand implements UCommandListener {
	
	@CommandHandler(
			name = "chatspy",
			aliases = {"spyChat", "채팅스파이"},
			permission = "gessentials.chatspy",
			usage = "채팅 스파이 모드로 전환하거나 해제합니다.")
	public void chatSpy(UPlayer p, String[] args, Command command) {
		EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p.getPlayerKey());
		
		ep.setChatSpy(!ep.isChatSpy());

		if(ep.isChatSpy()) {
			Core.getOptionManager().setPlayerOption(ep.getPlayerKey(), "gessentials_chat_spy", true);
			Core.getOptionSQLManager().setPlayerOption(ep.getPlayerKey(), "gessentials_chat_spy", true);
		} else {
			Core.getOptionManager().deletePlayerOption(ep.getPlayerKey(), "gessentials_chat_spy");
			Core.getOptionSQLManager().deletePlayerOption(ep.getPlayerKey(), "gessentials_chat_spy");
		}

		p.msg((ep.isChatSpy() ? "§a" : "§c") + "채팅 스파이 모드가 " + (ep.isChatSpy() ? "활성화" : "비활성화") + "되었습니다.");
	}
	
	@CommandHandler(
			name = "moveSpy",
			aliases = {"spyMove", "이동스파이"},
			permission = "gessentials.movespy",
			usage = "이동 스파이 모드로 전환하거나 해제합니다.")
	public void moveSpy(UPlayer p, String[] args, Command command) {
		EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p.getPlayerKey());
		
		ep.setMoveSpy(!ep.isMoveSpy());
		ep.sendMoveSpyToServer();


		if(ep.isMoveSpy()) {
			Core.getOptionManager().setPlayerOption(ep.getPlayerKey(), "gessentials_move_spy", true);
			Core.getOptionSQLManager().setPlayerOption(ep.getPlayerKey(), "gessentials_move_spy", true);
		} else {
			Core.getOptionManager().deletePlayerOption(ep.getPlayerKey(), "gessentials_move_spy");
			Core.getOptionSQLManager().deletePlayerOption(ep.getPlayerKey(), "gessentials_move_spy");
		}

		p.msg((ep.isMoveSpy() ? "§a" : "§c") + "이동 스파이 모드가 " + (ep.isMoveSpy() ? "활성화" : "비활성화") + "되었습니다.");
	}
	
}