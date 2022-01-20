package su.plugin.gparty.bungee.command;

import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.gparty.bungee.GPartyPlugin;
import su.plugin.gparty.bungee.api.GPartyAPI;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;

public class AdminCommand implements UCommandListener {
	
	private GPartyAPI api = GPartyPlugin.getApi();
	
	@SubCommandHandler(
			parent = "파티",
			name = "채팅스파이",
			aliases = {"ㅊㅌㅅㅍㅇ", "chatspy"},
			usage = "채팅 스파이 모드로 전환하거나 해제합니다.",
			permission = "gparty.admin"
			)
	public void party_chatSpy(UPlayer up, String[] args, Command command) {
		GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(up.getPlayerKey());
		
		if(pp.isChatSpy()) {
			up.msg("§c파티 채팅 스파이 모드가 비활성화되었습니다.");
		} else {
			up.msg("§a파티 채팅 스파이 모드가 활성화되었습니다.");
		}
		
		pp.setChatSpy(!pp.isChatSpy());
	}
	
}