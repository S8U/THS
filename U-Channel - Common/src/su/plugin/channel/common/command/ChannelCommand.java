package su.plugin.channel.common.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import su.plugin.channel.common.PermissionList;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.util.StringUtil;

public class ChannelCommand implements UCommandListener {
	
	private ChannelAPI api = new ChannelAPI();
	
	@CommandHandler(
			name = "channel",
			aliases = {"채널", "cosjf"},
			usePlatformPrefix = true,
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "채널 명령어를 확인합니다."
			)
	public void channel(UCommandSender sender, String[] args) {
		Core.nmsg(sender, "§7§l[ U-Channel ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("channel", 1)) {
			sc.sendUsage(sender, false);
		}
	}
	
	@SubCommandHandler(
			parent = "channel",
			name = "info",
			aliases = {"정보", "wjdqh"},
			additional = "<채널>",
			minArgs = 1,
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "채널 정보를 확인합니다."
			)
	public void channel_info(UCommandSender sender, String[] args) {
		Channel channel = api.getChannelManager().getChannel(args[0]);
		if(channel == null) {
			sender.wmsg("존재하지 않는 채널입니다.");
			return;
		}
		
		sender.nmsg("§7[ §f" + channel.getName() + " §7채널 정보 ]");
		sender.nmsg("§7표기: §f" + (channel.getDisplayName() == null ? "없음" : channel.getDisplayName()));
		sender.nmsg("§7그룹: §f" + (channel.getGroupName() == null ? "없음" : channel.getGroupName()));
		sender.nmsg("§7상태: §f" + (channel.isOnline() ? "온라인" : "오프라인"));
		sender.nmsg("§7인원: §f" + channel.getPlayerCount() + " / " + channel.getMaxPlayerCount());
		sender.nmsg("§7플레이어 목록: §f" + (channel.getPlayerList().size() < 1 ? "없음" : StringUtil.connectString(channel.getPlayerList(), ", ")));
		sender.nmsg("§7기타: §f" + channel.getETCs());
	}
	
	@SubCommandHandler(
			parent = "channel",
			name = "list",
			aliases = {"목록", "ahrfhr"},
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "채널 목록을 확인합니다."
			)
	public void channel_list(UCommandSender sender, String[] args) {
		sender.nmsg("§7[ 채널 목록 ]");
		for(Channel channel : api.getChannelManager().getChannels().values()) {
			String text = channel.getName() + ": §f" + channel.getPlayerCount() + " / " + channel.getMaxPlayerCount();
			sender.nmsg(channel.isOnline() ? new ComponentBuilder("§a" + text).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.join("§f, ", channel.getPlayerList()) + "\n§6클릭 시 이동합니다.").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/channel 이동 " + channel.getName() + " " + sender.getName())).create() : "§7" + text);
		}
	}
	
	@SubCommandHandler(
			parent = "channel",
			name = "join",
			aliases = {"이동", "dlehd", "접속", "dlehd"},
			additional = "<채널> (<플레이어 | 현재 | 모두>)",
			minArgs = 1,
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "채널에 접속합니다."
			)
	public void channel_join(UCommandSender sender, String[] args, Command cmd) {
		if(args.length < 2 && sender.isConsole()) {
			cmd.sendUsage(sender, false);
			return;
		}
		
		Channel channel = api.getChannelManager().getChannelByDisplayName(args[0]);
		if(channel == null) {
			sender.wmsg("존재하지 않는 채널입니다.");
			return;
		}
		
		if(args.length > 1) {
			if(args[1].equals("현재") || args[1].equalsIgnoreCase("current")) {
				if(sender.isConsole()) {
					cmd.sendUsage(sender, false);
					return;
				}
				
				int count = 0;
				
				Channel current = api.getChannelManager().getChannelHasPlayer(sender.getName());
				for(String pn : current.getPlayerList()) {
					if(channel.sendToChannel(pn)) count++;
				}
				
				sender.msg(count + "명의 플레이어를 " + channel.getDisplayName() + " 채널로 이동시켰습니다.");
				return;
			} else if(args[1].equals("모두") || args[1].equalsIgnoreCase("all")) {
				int count = 0;
				
				for(Channel ac : api.getChannelManager().getChannels().values()) {
					for(String pn : ac.getPlayerList()) {
						if(channel.sendToChannel(pn)) count++;
					}
				}
				
				sender.msg(count + "명의 플레이어를 " + channel.getDisplayName() + " 채널로 이동시켰습니다.");
				return;
			} else if(args[1].contains(",")) {
				String[] pls = args[1].split(",");
				
				int count = 0;
				
				for(String pn : pls) {
					String pnt = pn.trim();
					if(!api.existsPlayer(pnt)) continue;
					
					if(channel.sendToChannel(pn)) count++;
				}
				
				sender.msg(count + "명의 플레이어를 " + channel.getDisplayName() + " 채널로 이동시켰습니다.");
				return;
			}
		}
		
		String player = args.length > 1 ? (api.existsPlayer(args[1]) ? args[1] : null) : sender.getName();
		if(player == null) {
			sender.wmsg("접속 중이 아닌 플레이어입니다.");
			return;
		}
		
		if(channel.sendToChannel(player) && !player.equals(sender.getName())) {
			sender.msg(player + " 님을 " + channel.getDisplayName() + " 채널로 이동시켰습니다.");
		}
	}

	@SubCommandHandler(
			parent = "channel",
			name = "broadCast",
			aliases = {"bc", "공지", "rhdwl", "ㄱㅈ"},
			additional = "<채널> <메시지>",
			minArgs = 2,
			permission = PermissionList.CHANNEL_ADMIN,
			usage = "채널에 공지합니다."
	)
	public void channel_broadCast(UCommandSender sender, String[] args) {
		String message = ChatColor.translateAlternateColorCodes('&', StringUtil.connectString(args, 1, " "));
		if(args[0].equals("전체") || args[0].equalsIgnoreCase("all")) {
			api.getChannelManager().getChannels().values().forEach(channel -> ChannelAPI.getPlatformProvider().broadCast(channel, message));
			return;
		}

		Channel channel = api.getChannelManager().getChannelByDisplayName(args[0]);
		if(channel == null) {
			sender.wmsg("존재하지 않는 채널입니다.");
			return;
		}

		ChannelAPI.getPlatformProvider().broadCast(channel, message);
	}

}