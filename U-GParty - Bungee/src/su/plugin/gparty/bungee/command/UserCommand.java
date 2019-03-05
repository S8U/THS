package su.plugin.gparty.bungee.command;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.gfriend.api.GFriendAPI;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.glogin.common.api.GLoginAPI;
import su.plugin.gparty.bungee.GGPartyPlugin;
import su.plugin.gparty.bungee.api.GGPartyAPI;
import su.plugin.gparty.bungee.api.object.GParty;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;

public class UserCommand implements UCommandListener {
	
	private GGPartyAPI api = GGPartyPlugin.getApi();
	
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
			additional = "플레이어",
			minArgs = 1,
			usage = "플레이어를 파티에 초대합니다."
			)
	public void party_invite(UPlayer up, String[] args, Command cmd) {
		if(up.getName().equalsIgnoreCase(args[0])) {
			up.wmsg("자신에게는 초대를 보낼 수 없습니다.");
			return;
		}

		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(up.getPlayerKey());
		
		if(pp.getParty().getPlayers().size() + 1 >= api.getMaxPartyCount()) {
			up.wmsg("파티가 가득 찼습니다.");
			return;
		}
		
		UPlayer target = Core.getUPlayer(args[0]);
		if(target == null) {
			up.wmsg("접속 중이 아닌 플레이어입니다.");
			return;
		}
		
		if(api.isUseGLogin() && (!GGLoginAPI.getAccountManager().hasAccount(target.getPlayerKey()) || !GLoginAPI
				.getAccountManager().getAccount(target.getPlayerKey()).isLogin())) {
			up.wmsg("아직 로그인하지 않은 플레이어입니다.");
			return;
		}

		Object option = Core.getOptionManager().getPlayerOption(target.getPlayerKey(), "gparty_allow_invite");
		if(option != null && (option.equals("block") || (option.equals("friend") && api.isUseGFriend() && !GFriendAPI.getSQLManager().isFriend(up.getPlayerKey(), target.getPlayerKey())))) {
			up.wmsg("상대가 파티 초대를 허용하지 않았습니다.");
			return;
		}

		GPartyPlayer targetPartyPlayer = api.getPlayerManager().getPartyPlayer(target.getPlayerKey());
		if(targetPartyPlayer.getParty().getPlayers().size() > 0) {
			up.wmsg("다른 파티에 참여 중인 플레이어입니다.");
			return;
		}

		targetPartyPlayer.setInvitedParty(pp.getParty());
		
		pp.getParty().bc("§f" + up.getDisplayName() + " §a님께서 §f" + target.getDisplayName() + " §a님을 파티에 초대했습니다.");

		target.msg("§f " + up.getDisplayName() + " §a님께서 파티에 초대했습니다.");
		target.msg(
				new ComponentBuilder("§a파티 수락").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/파티 수락")).event(new HoverEvent(
						Action.SHOW_TEXT, new ComponentBuilder("클릭 시 파티를 수락합니다.").create())).create(),
				" §f/ ",
				new ComponentBuilder("§c파티 거절").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/파티 거절")).event(new HoverEvent(
						Action.SHOW_TEXT, new ComponentBuilder("클릭 시 파티를 거절합니다.").create())).create());
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "수락",
			aliases = {"ㅅㄹ", "accept"},
			usage = "파티 초대를 수락합니다."
			)
	public void party_accept(ProxiedPlayer p, String[] args, Command cmd) {
		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		
		if(pp.getParty().getPlayers().size() > 0) {
			Core.wmsg(p, "이미 파티에 가입되어있습니다.");
			return;
		} else if(!pp.hasInvitedParty()) {
			Core.wmsg(p, "아직 파티에 초대받지 못했습니다.");
			return;
		}
		
		GParty party = pp.getInvitedParty();
		
		if(party.getPlayers().size() + 1 >= api.getMaxPartyCount()) {
			Core.wmsg(p, "파티가 가득 찼습니다.");
			return;
		}
		
		pp.setParty(pp.getInvitedParty());
		pp.setInvitedParty(null);
		
		party.bc("§f" + pp.getDisplayName()+ " §a님께서 파티에 참여했습니다.");
		party.addPlayer(p);

		Core.msg(p, "§a파티에 참여했습니다.");
		
		//
		
		ProxiedPlayer op = party.getOwnerPlayer();
		if(!p.getServer().getInfo().equals(op.getServer().getInfo())) {
			pp.setMoving(true);
			p.connect(op.getServer().getInfo());

			String targetChannelName = op.getServer().getInfo().getName();
			if(api.isUseChannel()) {
				targetChannelName = ChannelAPI.getChannelManager().getChannel(targetChannelName).getDisplayName();
			}

			Core.msg(p, "§a파티장을 따라 §f" + targetChannelName + "§a(으)로 이동했습니다.");
		}

		api.getPartyManager().sendParty(party, party.getOwnerPlayer().getServer().getInfo());
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "거절",
			aliases = {"ㄱㅈ", "deny"},
			usage = "파티 초대를 거절합니다."
			)
	public void party_deny(ProxiedPlayer p, String[] args, Command cmd) {
		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		
		if(pp.getParty().getPlayers().size() > 0) {
			Core.wmsg(p, "이미 파티에 가입되어있습니다.");
			return;
		} else if(!pp.hasInvitedParty()) {
			Core.wmsg(p, "아직 파티에 초대받지 못했습니다.");
			return;
		}
		
		GParty party = pp.getInvitedParty();
		
		pp.setInvitedParty(null);
		
		Core.msg(p, "§c파티 초대를 거절했습니다.");
		party.bc("§f" + pp.getDisplayName() + " §c님께서 파티 초대를 거절했습니다.");
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "추방",
			aliases = {"ㅊㅂ", "강퇴", "ㄱㅌ", "kick"},
			additional = "<플레이어>",
			minArgs = 1,
			usage = "플레이어를 파티에서 추방합니다."
			)
	public void party_kick(ProxiedPlayer p, String[] args, Command cmd) {
		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		
		if(pp.getParty().getPlayers().size() < 0) {
			Core.wmsg(p, "참여 중인 파티가 없습니다.");
			return;
		}
		
		GParty party = pp.getParty();

		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

		if(!party.isOwner(p)) {
			Core.wmsg(p, "파티장만 사용 가능합니다.");
			return;
		} else if(!party.hasPlayer(target)) {
			Core.wmsg(p, "파티에 소속되어 있지 않은 플레이어입니다.");
			return;
		}
		
		party.removePlayer(target);
		
		GPartyPlayer tp = api.getPlayerManager().getPartyPlayer(target);
		
		tp.setParty(new GParty(tp.getPlayerKey()));
		tp.setPartyChat(false);
		
		api.getPartyManager().sendParty(party, p.getServer().getInfo());
		if(!p.getServer().getInfo().equals(target.getServer().getInfo())) {
			api.getPartyManager().sendParty(party, target.getServer().getInfo());
		}
		
		Core.msg(target, "§c파티에서 추방당했습니다.");
		party.bc("§f" + pp.getDisplayName() + " 님께서 " + tp.getDisplayName() + " 님을 파티에서 추방했습니다.");
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "위임",
			aliases = {"ㅇㅇ", "giveLeader"},
			additional = "<플레이어>",
			minArgs = 1,
			usage = "플레이어에게 파티장을 위임합니다."
			)
	public void party_giveLeader(ProxiedPlayer p, String[] args, Command cmd) {
		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		
		if(pp.getParty().getPlayers().size() < 1) {
			Core.msg(p, "참여 중인 파티가 없습니다!");
			return;
		}
		
		GParty party = pp.getParty();

		PlayerKey tpk = PlayerKey.getPlayerKey(args[0]);

		if(!party.isOwner(pp.getPlayerKey())) {
			Core.wmsg(p, "파티장만 사용 가능합니다.");
			return;
		} else if(!party.hasPlayer(tpk)) {
			Core.wmsg(p, "파티에 소속되어 있지 않은 플레이어입니다.");
			return;
		}

		GPartyPlayer target = GGPartyAPI.getPlayerManager().getPartyPlayer(tpk);

		party.setOwner(tpk);
		party.addPlayer(p);
		
		api.getPartyManager().sendParty(party, target.getPlayer().getServer().getInfo());
		
		party.bc("§f" + pp.getDisplayName() + " §a님께서 파티장을 §f" + target.getDisplayName() +" §a님께 위임했습니다.");
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "탈퇴",
			aliases = {"ㅌㅌ", "leave"},
			usage = "파티에서 탈퇴합니다."
			)
	public void party_leave(ProxiedPlayer p, String[] args, Command cmd) {
		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		
		if(pp.getParty().getPlayers().size() < 1) {
			Core.wmsg(p, "참여 중인 파티가 없습니다.");
			return;
		}
		
		GParty party = pp.getParty();
		
		party.removePlayer(p);
		
		pp.setParty(new GParty(pp.getPlayerKey()));
		pp.setPartyChat(false);
		
		Core.msg(p, "§c파티를 탈퇴했습니다.");
		
		if((party.isOwner(p) && party.getPlayers().size() < 2) || (!party.isOwner(p) && party.getPlayers().size() < 1)) {
			api.getPartyManager().sendPartyDelete(pp.getPlayerKey(), p.getServer().getInfo());

			if(party.getPlayers().size() > 0) {
				Core.msg(party.getOnlinePlayers().get(0), "§c파티가 해체되었습니다.");
			}
		} else {
			api.getPartyManager().sendParty(party, party.getOwnerPlayer().getServer().getInfo());

			party.bc("§f" + pp.getDisplayName() + " §c님께서 파티에서 탈퇴했습니다.");

			if(party.isOwner(p)) {
				GPartyPlayer np = api.getPlayerManager().getPartyPlayer(party.getPlayers().get(0));
				party.setOwner(np.getPlayerKey());
				party.bc("§a파티장이 §f" + np.getDisplayName() + "§a님께 위임되었습니다.");
			}
		}
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "채팅",
			aliases = {"ㅊㅌ", "chat"},
			usage = "파티 채팅 모드로 전환하거나 해제합니다."
			)
	public void party_chat(ProxiedPlayer p, String[] args, Command cmd) {
		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		
		if(pp.getParty().getPlayers().size() < 1) {
			Core.wmsg(p, "참여 중인 파티가 없습니다.");
			return;
		}

		pp.setPartyChat(!pp.isPartyChat());

		Core.msg(p, (pp.isPartyChat() ? "§a" : "§c") + "파티 채팅 모드" + (pp.isPartyChat() ? "로 전환" : "가 해제") + "되었습니다.");
	}
	
	@SubCommandHandler(
			parent = "파티",
			name = "정보",
			aliases = {"ㅈㅂ", "info"},
			additional = "(<플레이어>)",
			usage = "파티 정보를 확인합니다."
			)
	public void party_info(ProxiedPlayer p, String[] args, Command cmd) {
		GPartyPlayer pp;
		GParty party = null;
		
		if(args.length < 1) {
			pp = api.getPlayerManager().getPartyPlayer(p);
			
			if(pp.getParty().getPlayers().size() < 1) {
				Core.wmsg(p, "참여 중인 파티가 없습니다.");
				return;
			}
			
			party = pp.getParty();
		} else {
			pp = api.getPlayerManager().getPartyPlayer(PlayerKey.getPlayerKey(args[0]));
			if(pp == null) {
				Core.wmsg(p, "접속 중이 아닌 플레이어입니다.");
				return;
			} else if(pp.getParty().getPlayers().size() < 1) {
				Core.wmsg(p, "파티에 소속되어 있지 않은 플레이어입니다.");
				return;
			}
		}
		
		Core.nmsg(p, (pp.getPlayer().equals(p) ? "§a내" : "§f" + pp.getDisplayName() + " §a님의") + " 파티 정보");
		Core.nmsg(p, "§a파티장: §f" + party.getOwner().getUPlayer().getDisplayName());
		Core.nmsg(p, "§a참여 중인 플레이어(" + (party.getPlayers().size() + 1) + "): §f" + api.buildPlayerList(party.getOnlinePlayers()));
	}
	
}