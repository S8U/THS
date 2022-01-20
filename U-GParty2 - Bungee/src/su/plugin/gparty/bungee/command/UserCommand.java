package su.plugin.gparty.bungee.command;

import java.util.stream.Collectors;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.gparty.bungee.GPartyPlugin;
import su.plugin.gparty.bungee.api.GPartyAPI;
import su.plugin.gparty.bungee.api.object.GParty;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;

public class UserCommand implements UCommandListener {
	
	private GPartyAPI api = GPartyPlugin.getApi();
	
	@CommandHandler(
			name = "파티",
			aliases = {"ㅍㅌ", "party", "p"}
			)
	public void party(UCommandSender sender, String[] args, Command cmd) {
		sender.nmsg("§a§l[ U-Party ]");
		for(Command sc : Core.getCommandManager().getSubCommands(cmd.getName(), 1)) {
			sc.sendUsageIfHasPermission(sender, false);
		}
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "초대",
			aliases = {"ㅊㄷ", "invite"},
			additional = "<플레이어>",
			minArgs = 1,
			usage = "플레이어를 파티에 초대합니다."
			)
	public void party_invite(UPlayer up, String[] args) {
		if(up.getName().equalsIgnoreCase(args[0])) {
			up.wmsg("자신에게는 초대를 보낼 수 없습니다.");
			return;
		}

		UPlayer tup = Core.getUPlayer(args[0]);
		if(tup == null) {
			up.wmsg("접속 중이 아닌 플레이어입니다.");

			return;
		}

		GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(up.getPlayerKey());
		GPartyPlayer tp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(tup.getPlayerKey());

		api.inviteParty(pp, tp);
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "수락",
			aliases = {"ㅅㄹ", "accept"},
			usage = "파티 초대를 수락합니다."
			)
	public void party_accept(UPlayer up, String[] args) {
		GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(up.getPlayerKey());

		api.acceptParty(pp);
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "거절",
			aliases = {"ㄱㅈ", "deny"},
			usage = "파티 초대를 거절합니다."
			)
	public void party_deny(UPlayer up, String[] args) {
		GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(up.getPlayerKey());

		api.denyParty(pp);
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "추방",
			aliases = {"ㅊㅂ", "강퇴", "ㄱㅌ", "kick"},
			additional = "<플레이어>",
			minArgs = 1,
			usage = "플레이어를 파티에서 추방합니다."
			)
	public void party_kick(UPlayer up, String[] args) {
		UPlayer tup = Core.getUPlayer(args[0]);
		if(tup == null) {
			up.wmsg("접속 중이 아닌 플레이어입니다.");

			return;
		}

		GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(up.getPlayerKey());
		GPartyPlayer tp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(tup.getPlayerKey());

		api.kickParty(pp, tp);
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "위임",
			aliases = {"ㅇㅇ", "giveLeader"},
			additional = "<플레이어>",
			minArgs = 1,
			usage = "플레이어에게 파티장을 위임합니다."
			)
	public void party_giveLeader(UPlayer up, String[] args) {
		UPlayer tup = Core.getUPlayer(args[0]);
		if(tup == null) {
			up.wmsg("접속 중이 아닌 플레이어입니다.");

			return;
		}

		GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(up.getPlayerKey());
		GPartyPlayer tp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(tup.getPlayerKey());

		api.giveLeader(pp, tp);
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "탈퇴",
			aliases = {"ㅌㅌ", "leave"},
			usage = "파티에서 탈퇴합니다."
			)
	public void party_leave(UPlayer up, String[] args) {
		GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(up.getPlayerKey());

		api.leaveParty(pp);
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "채팅",
			aliases = {"ㅊㅌ", "chat"},
			additional = "(<메시지>)",
			usage = "파티 채팅 모드로 전환하거나 파티 채팅을 보냅니다."
			)
	public void party_chat(UPlayer up, String[] args) {
		GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(up.getPlayerKey());

		if (args.length < 1) {
			api.togglePartyChat(pp);
		} else {
			boolean partyChat = pp.isPartyChat();

			pp.setPartyChat(true);
			((ProxiedPlayer) pp.getPlayerKey().getPlatformPlayer()).chat(String.join(" ", args));
			pp.setPartyChat(partyChat);
		}
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "정보",
			aliases = {"ㅈㅂ", "info"},
			additional = "(<플레이어>)",
			usage = "파티 정보를 확인합니다."
			)
	public void party_info(UPlayer up, String[] args) {
		GPartyPlayer pp;
		GParty party = null;
		
		if (args.length < 1) {
			pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(up.getPlayerKey());
			
			if (!pp.hasParty() || pp.getParty().getPlayers().size() < 2) {
				Core.wmsg(up, "참여 중인 파티가 없습니다.");

				return;
			}
		} else {
			pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(PlayerKey.getPlayerKey(args[0]));
			if (pp == null) {
				Core.wmsg(up, "접속 중이 아닌 플레이어입니다.");

				return;
			} else if (!pp.hasParty() || pp.getParty().getPlayers().size() < 2) {
				Core.wmsg(up, "파티에 소속되어 있지 않은 플레이어입니다.");

				return;
			}
		}

		party = (GParty) pp.getParty();
		
		Core.nmsg(up, (pp.getPlayerKey().equals(up.getPlayerKey()) ? "§a내" : "§f" + pp.getPlayerKey().getDisplayName() + " §a님의") + " 파티 정보");
		Core.nmsg(up, "§a파티장: §f" + party.getLeader().getDisplayName());
		Core.nmsg(up, "§a참여 중인 플레이어(" + party.getPlayers().size() + "): §f"
				+ party.getPlayers().stream().map(ptp -> ptp.getPlayerKey().getDisplayName()).collect(Collectors.joining(", ")));
	}
	
}