package su.plugin.gfriend.command;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.core.bungee.api.GCore;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.gfriend.GFriendPlugin;
import su.plugin.gfriend.api.GFriendAPI;
import su.plugin.gfriend.api.object.FriendPlayer;
import su.plugin.glogin.common.api.GLoginAPI;

public class FriendCommand implements UCommandListener {

	private GFriendAPI api = GFriendPlugin.getApi();

	@CommandHandler(
			name = "친구",
			aliases = {"ㅊㄱ", "friend", "f"},
			usage = "친구 명령어 목록을 확인합니다."
	)
	public void friend(UCommandSender sender, String[] args, Command command) {
		sender.nmsg("§a[ U-GFriend ]");
		for(Command cmd : Core.getCommandManager().getSubCommands(command.getName(), 1)) {
			cmd.sendUsage(sender, false);
		}
	}

	@SubCommandHandler(
			parent="친구",
			name = "추가",
			aliases = {"ㅊㄱ", "요청", "ㅇㅊ", "add", "a", "request", "r"},
			additional = "<플레이어>",
			minArgs = 1,
			maxArgs = 1,
			usage = "친구 요청을 보냅니다."
	)
	public void friend_add(UPlayer up, String[] args) {
		if(up.getName().equalsIgnoreCase(args[0])) {
			up.wmsg("자신에게는 친구 요청을 보낼 수 없습니다.");
			return;
		}

		PlayerKey targetKey = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(targetKey == null) {
			up.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}

		Object option = Core.getOptionManager().getPlayerOption(targetKey, "gfriend_allow_request");
		if(option != null && option.toString().equals("block")) {
			up.wmsg("상대가 친구 요청을 허용하지 않았습니다.");
			return;
		}

		FriendPlayer fp = api.getPlayerManager().getFriendPlayer(up.getPlayerKey());
		if(fp.isFriend(targetKey)) {
			up.wmsg("이미 친구인 플레이어입니다.");
			return;
		}

		FriendPlayer target = api.getPlayerManager().getFriendPlayer(targetKey);
		if(target != null) {
			if(target.hasRequestFrom(fp)) {
				up.wmsg("이미 친구 요청을 보냈습니다.");
				return;
			} else if(target.isOnline()) {
				target.addRequest(fp);

				Core.msg(target.getProxiedPlayer(), "§f " + up.getDisplayName() + " §a님께서 친구 요청을 보냈습니다.");
				Core.msg(target.getProxiedPlayer(),
						new ComponentBuilder("§a친구 수락").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/친구 수락 " + up.getName())).event(new HoverEvent(
								Action.SHOW_TEXT, new ComponentBuilder("클릭 시 친구 요청을 수락합니다.").create())).create(),
						" §f/ ",
						new ComponentBuilder("§c친구 거절").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/친구 거절 " + up.getName())).event(new HoverEvent(
								Action.SHOW_TEXT, new ComponentBuilder("클릭 시 친구 요청을 거절합니다.").create())).create());
			}
		} else if(api.getSQLManager().hasRequest(targetKey, up.getPlayerKey())) {
			up.wmsg("이미 친구 요청을 보냈습니다.");
			return;
		}

		api.getSQLManager().addRequest(targetKey, up.getPlayerKey());

		up.msg("§f" + targetKey.getDisplayName() + " §a님께 친구 요청을 보냈습니다.");
	}

	@SubCommandHandler(
			parent="친구",
			name = "삭제",
			aliases = {"ㅅㅈ", "delete", "d"},
			additional = "<플레이어>",
			minArgs = 1,
			maxArgs = 1,
			usage = "친구를 삭제합니다."
	)
	public void friend_delete(UPlayer up, String[] args) {
		PlayerKey targetKey = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(targetKey == null) {
			up.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}

		FriendPlayer fp = api.getPlayerManager().getFriendPlayer(up.getPlayerKey());
		if(!fp.isFriend(targetKey)) {
			up.wmsg("친구가 아닌 플레이어입니다.");
			return;
		}

		fp.removeFriend(targetKey);

		FriendPlayer target = api.getPlayerManager().getFriendPlayer(targetKey);
		if(target != null) {
			target.removeFriend(fp);
		}

		api.getSQLManager().removeFriend(targetKey, up.getPlayerKey());
		api.getSQLManager().removeFriend(up.getPlayerKey(), targetKey);

		up.msg("§f" + targetKey.getDisplayName() + " §c님을 친구에서 삭제했습니다.");
	}

	@SubCommandHandler(
			parent="친구",
			name = "목록",
			aliases = {"ㅁㄹ", "list", "l"},
			additional = "(<페이지>)",
			maxArgs = 1,
			usage = "친구 목록을 확인합니다."
	)
	public void friend_list(UPlayer up, String[] args) {
		FriendPlayer fp = api.getPlayerManager().getFriendPlayer(up.getPlayerKey());

		int page = 1;
		int maxPage = (int) Math.ceil(fp.getFriends().size() / 10) + 1;

		if(args.length > 0) {
			if(NumberUtil.isInteger(args[0])) {
				page = args.length < 2 ? 1 : Integer.parseInt(args[1]);
			} else {
				up.wmsg("페이지는 1 ~ " + maxPage + " 사이의 정수만 입력 가능합니다.");
				return;
			}
		}

		List<PlayerKey> friends = fp.getFriends();
		if(friends.size() < 1) {
			up.wmsg("친구가 없습니다.");
			return;
		}

		List<PlayerKey> onlineFriends = new ArrayList<>();
		List<PlayerKey> offlineFriends = new ArrayList<>();
		for(PlayerKey fpk : friends) {
			if(fpk.getUPlayer() == null || !fpk.getUPlayer().isOnline()) {
				offlineFriends.add(fpk);
				continue;
			}

			onlineFriends.add(fpk);
		}

		List<PlayerKey> sortedFriends = new ArrayList<>();
		sortedFriends.addAll(onlineFriends);
		sortedFriends.addAll(offlineFriends);

		up.nmsg("§a[ 친구 목록 ( " + page + " / " + maxPage + " ) ]");
		for(int i = (page - 1) * 10; i < page * 10; i++) {
			if(friends.size() <= i) break;
			PlayerKey friendKey = sortedFriends.get(i);
			boolean online = GCore.getProxiedPlayer(friendKey) != null;
			String color = online ? "§a" : "§7";
			String status = online ? (api.isUseChannel() ? ChannelAPI
					.getChannelManager().getChannel(GCore.getProxiedPlayer(friendKey).getServer().getInfo().getName()).getDisplayName() : "온라인") : (api.isUseGLogin() ? StringUtil.buildTimeString(
					System.currentTimeMillis() - GLoginAPI.getSQLManager().getAccount(friendKey)
							.getLastLogout()) + " 동안 " : "") + "오프라인";

			up.nmsg(color + friendKey.getDisplayName() + color + ": " + status);
		}
	}

	@SubCommandHandler(
			parent="친구",
			name = "수락",
			aliases = {"ㅅㄹ", "accept", "ac"},
			additional = "<플레이어>",
			minArgs = 1,
			maxArgs = 1,
			usage = "친구 요청을 수락합니다."
	)
	public void friend_accept(UPlayer up, String[] args) {
		PlayerKey targetKey = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(targetKey == null) {
			up.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}

		FriendPlayer fp = api.getPlayerManager().getFriendPlayer(up.getPlayerKey());
		if(!fp.hasRequestFrom(targetKey)) {
			up.wmsg("아직 친구 요청을 받지 못했습니다.");
			return;
		}

		FriendPlayer target = api.getPlayerManager().getFriendPlayer(targetKey);
		if(target != null) {
			target.addFriend(fp);
			target.removeRequest(fp);
		}

		api.getSQLManager().addFriend(targetKey, up.getPlayerKey());
		api.getSQLManager().removeRequest(targetKey, up.getPlayerKey());

		fp.removeRequest(targetKey);
		fp.addFriend(targetKey);

		api.getSQLManager().removeRequest(up.getPlayerKey(), targetKey);
		api.getSQLManager().addFriend(up.getPlayerKey(), targetKey);

		up.msg("§f" + targetKey.getDisplayName() + " §a님의 친구 요청을 수락했습니다.");
		if(target != null && target.isOnline()) {
			Core.msg(target.getProxiedPlayer(), "§f" + fp.getDisplayName() + " §a님께서 친구 요청을 수락했습니다.");
		}
	}

	@SubCommandHandler(
			parent="친구",
			name = "거절",
			aliases = {"ㄱㅈ", "deny", "d"},
			additional = "<플레이어>",
			minArgs = 1,
			maxArgs = 1,
			usage = "친구 요청을 거절합니다."
	)
	public void friend_deny(UPlayer up, String[] args) {
		PlayerKey targetKey = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(targetKey == null) {
			up.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}

		FriendPlayer fp = api.getPlayerManager().getFriendPlayer(up.getPlayerKey());
		if(!fp.hasRequestFrom(targetKey)) {
			up.wmsg("아직 친구 요청을 받지 못했습니다.");
			return;
		}

		FriendPlayer target = api.getPlayerManager().getFriendPlayer(targetKey);

		fp.removeRequest(targetKey);
		api.getSQLManager().removeRequest(up.getPlayerKey(), targetKey);

		up.msg("§f" + targetKey.getDisplayName() + " §c님의 친구 요청을 거절했습니다.");
		if(target != null && target.isOnline()) {
			Core.msg(target.getProxiedPlayer(), fp.getDisplayName() + " §c님께서 친구 요청을 거절했습니다.");
		}
	}

	@SubCommandHandler(
			parent="친구",
			name = "요청목록",
			aliases = {"ㅇㅊㅁㄹ", "requestList", "rl"},
			additional = "(<페이지>)",
			maxArgs = 1,
			usage = "친구 요청 목록을 확인합니다."
	)
	public void friend_requestList(UPlayer up, String[] args) {
		FriendPlayer fp = api.getPlayerManager().getFriendPlayer(up.getPlayerKey());

		int page = 1;
		int maxPage = (int) Math.ceil(fp.getFriends().size() / 10) + 1;

		if(args.length > 0) {
			if(NumberUtil.isInteger(args[0])) {
				page = args.length < 2 ? 1 : Integer.parseInt(args[1]);
			} else {
				up.wmsg("페이지는 1 ~ " + maxPage + " 사이의 정수만 입력 가능합니다.");
				return;
			}
		}

		List<PlayerKey> requests = fp.getRequests();
		if(requests.size() < 1) {
			up.wmsg("친구 요청이 없습니다.");
			return;
		}

		up.nmsg("§a[ 친구 요청 목록 ( " + page + " / " + maxPage + " ) ]");
		for(int i = (page - 1) * 10; i < page * 10; i++) {
			if(requests.size() <= i) break;

			PlayerKey friendKey = requests.get(i);
			boolean online = GCore.getProxiedPlayer(friendKey) != null;
			String color = online ? "§a" : "§7";
			String status = online ? "온라인" : (api.isUseGLogin() ? StringUtil.buildTimeString(System.currentTimeMillis() - GLoginAPI
					.getSQLManager().getAccount(friendKey).getLastLogout()) + " 동안 " : "") + "오프라인";

			up.nmsg(new ComponentBuilder(color + friendKey.getDisplayName() + ": " + status)
			.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("클릭 시 친구 요청을 수락합니다.").create()))
			.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/친구 수락 " + friendKey.getName())).create());
		}
	}

}