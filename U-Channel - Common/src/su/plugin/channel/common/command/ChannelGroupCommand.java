package su.plugin.channel.common.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import su.plugin.channel.common.PermissionList;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.util.StringUtil;

public class ChannelGroupCommand implements UCommandListener {
	
	private ChannelAPI api = new ChannelAPI();
	
	@SubCommandHandler(
			parent = "channel",
			name = "group",
			aliases = {"그룹", "rmfnq"},
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "채널 그룹 명령어 목록을 확인합니다."
			)
	public void channel_group(UCommandSender sender, String[] args) {
		Core.nmsg(sender, "§7§l[ U-Channel - Group ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("channel group", 1)) {
			sc.sendUsage(sender, false);
		}
	}
	
	@SubCommandHandler(
			parent = "channel group",
			name = "info",
			aliases = {"정보", "wjdqh"},
			additional = "<그룹>",
			minArgs = 1,
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "그룹 정보를 확인합니다."
			)
	public void channel_group_info(UCommandSender sender, String[] args) {
		ChannelGroup group = api.getChannelGroupManager().getChannelGroup(args[0]);
		if(group == null) {
			sender.wmsg("존재하지 않는 그룹입니다.");
			return;
		}
		
		sender.nmsg("§7[ §f" + group.getName() + " §7그룹 정보 ]");
		sender.nmsg("§7표기: §f" + (group.getDisplayName() == null ? "없음" : group.getDisplayName()));
		sender.nmsg("§7인원: §f" + group.getPlayerCount() + "명");
		sender.nmsg("§7플레이어 목록: §f" + (group.getPlayerList().size() < 1 ? "없음" : String.join(", ", group.getPlayerList())));
	}
	
	@SubCommandHandler(
			parent = "channel group",
			name = "list",
			aliases = {"목록", "ahrfhr"},
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "그룹 목록을 확인합니다."
			)
	public void channel_group_list(UCommandSender sender, String[] args) {
		sender.nmsg("§7[ 그룹 목록 ]");
		for(ChannelGroup group : api.getChannelGroupManager().getChannelGroups().values()) {
			sender.nmsg(new ComponentBuilder("§a" + group.getName() + ": §f" + group.getPlayerCount() + "명").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder((group.getPlayerCount() < 1 ? "" : String.join("§f, ", group.getPlayerList()) + "\n") + "§6클릭 시 이동합니다.").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/channel group 이동 " + group.getName() + " " + sender.getName())).create());
		}
	}
	
	@SubCommandHandler(
			parent = "channel group",
			name = "join",
			aliases = {"이동", "dlehd", "접속", "wjqthr"},
			additional = "<그룹> (<플레이어 | 현재 | 모두>)",
			minArgs = 1,
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "그룹의 최적 채널로 접속합니다."
			)
	public void channel_group_join(UCommandSender sender, String[] args, Command cmd) {
		if(args.length < 2 && sender.isConsole()) {
			cmd.sendUsage(sender, false);
			return;
		}
		
		ChannelGroup group = api.getChannelGroupManager().getChannelGroupByDisplayName(args[0]);
		if(group == null) {
			sender.wmsg("존재하지 않는 그룹입니다.");
			return;
		}
		
		if(args.length > 1) {
			if(args[1].equals("현재") || args[1].equalsIgnoreCase("current")) {
				if(sender.isConsole()) {
					cmd.sendUsage(sender, false);
					return;
				}
				
				Channel current = api.getChannelManager().getChannelHasPlayer(sender.getName());
				
				int count = current.getPlayerCount();
				
				for(String pn : current.getPlayerList()) {
					group.sendToOptimizeChannel(pn);
				}
				
				sender.msg(count + "명의 플레이어를 " + group.getDisplayName() + " 그룹의 채널로 이동시켰습니다.");
				return;
			} else if(args[1].equals("모두") || args[1].equalsIgnoreCase("all")) {
				int count = 0;
				
				for(Channel ac : api.getChannelManager().getChannels().values()) {
					for(String pn : ac.getPlayerList()) {
						group.sendToOptimizeChannel(pn);
						
						count++;
					}
				}
				
				sender.msg(count + "명의 플레이어를 " + group.getDisplayName() + " 그룹의 채널로 이동시켰습니다.");
				return;
			} else if(args[1].contains(",")) {
				String[] pls = args[1].split(",");
				
				int count = 0;
				
				for(String pn : pls) {
					String pnt = pn.trim();
					if(!api.existsPlayer(pnt)) continue;
					
					group.sendToOptimizeChannel(pn);
					
					count++;
				}
				
				sender.msg(count + "명의 플레이어를 " + group.getDisplayName() + " 채널로 이동시켰습니다.");
				return;
			}
		}
		
		String player = args.length > 1 ? (api.existsPlayer(args[1]) ? args[1] : null) : sender.getName();
		if(player == null) {
			sender.wmsg("접속 중이 아닌 플레이어입니다.");
			return;
		}
		
		group.sendToOptimizeChannel(player);
		
		if(!player.equals(sender.getName())) {
			sender.msg(player + " 님을 " + group.getDisplayName() + " 채널로 이동시켰습니다.");
		}
	}

	@SubCommandHandler(
			parent = "channel group",
			name = "broadCast",
			aliases = {"bc", "공지", "rhdwl", "ㄱㅈ"},
			additional = "<채널> <메시지>",
			minArgs = 2,
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "채널에 공지합니다."
	)
	public void channel_broadCast(UCommandSender sender, String[] args) {
		ChannelGroup group = api.getChannelGroupManager().getChannelGroup(args[0]);
		if(group == null) {
			sender.wmsg("존재하지 않는 그룹입니다.");
			return;
		}

		ChannelAPI.getPlatformProvider().broadCast(group, ChatColor
				.translateAlternateColorCodes('&', StringUtil.connectString(args, 1, " ")));
	}
	
}