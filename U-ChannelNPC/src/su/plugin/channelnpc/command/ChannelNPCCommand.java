package su.plugin.channelnpc.command;

import java.util.List;

import org.bukkit.entity.Player;

import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.channelnpc.ChannelNPCPlugin;
import su.plugin.channelnpc.PermissionList;
import su.plugin.channelnpc.api.ChannelNPCAPI;
import su.plugin.channelnpc.api.category.ChannelType;
import su.plugin.channelnpc.api.object.ChannelNPC;
import su.plugin.core.bukkit.api.util.KStringUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.core.common.api.util.StringUtil;

public class ChannelNPCCommand implements UCommandListener {
	
	private ChannelNPCAPI api = ChannelNPCPlugin.getApi();
	
	@CommandHandler(
			name = "channelnpc",
			aliases = {"cnpc", "채널npc", "cosjfnpc"},
			permission = PermissionList.CHANNELNPC_ADMIN,
			usage = "채널 NPC 명령어를 확인합니다."
			)
	public void channelnpc(UCommandSender sender, String[] args) {
		Core.nmsg(sender, "§e§l[ U-Channel ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("channelnpc", 1)) {
			sc.sendUsage(sender, false);
		}
	}
	
	@SubCommandHandler(
			parent = "channelnpc",
			name = "reload",
			aliases = {"리로드", "flfhem"},
			permission = PermissionList.CHANNELNPC_ADMIN,
			usage = "설정을 리로드합니다."
			)
	public void channelnpc_reload(UCommandSender sender, String[] args, Command cmd) {
		for(ChannelNPC cn : api.getNPCManager().getChannelNPCs().values()) {
			cn.removeHologram();
		}
		
		ChannelNPCPlugin.getInstance().loadConfig();
		api.getConfigManager().loadNPC();
		
		sender.msg("설정을 리로드했습니다.");
	}
	
	@SubCommandHandler(
			parent = "channelnpc",
			name = "create",
			aliases = {"생성", "todtjd"},
			additional = "<이름> <<channel:채널> | <channelgroup:채널그룹>> (<스킨 이름>)",
			minArgs = 2,
			permission = PermissionList.CHANNELNPC_ADMIN,
			usage = "채널 NPC를 생성합니다."
			)
	public void channelnpc_create(UPlayer sender, String[] args, Command cmd) {
		ChannelNPC npc = api.getNPCManager().createChannelNPC(args[0]);
		
		if(args[1].toLowerCase().startsWith("<channel:")) {
			List<String> arr = StringUtil.getValue("channel", args[1]);
			if(arr.size() < 1) {
				cmd.sendUsage(sender, false);
				return;
			}
			
			npc.setChannelType(ChannelType.CHANNEL);
			
			Channel channel = ChannelAPI.getChannelManager().getChannel(arr.get(0));
			if(channel == null) {
				sender.wmsg("존재하지 않는 채널입니다.");
				return;
			}
			
			npc.setChannel(channel);
		} else if(args[1].toLowerCase().startsWith("<channelgroup:")) {
			List<String> arr = StringUtil.getValue("channelgroup", args[1]);
			if(arr.size() < 1) {
				cmd.sendUsage(sender, false);
				return;
			}
			
			npc.setChannelType(ChannelType.CHANNEL_GROUP);

			ChannelGroup group = ChannelAPI.getChannelGroupManager().getChannelGroup(arr.get(0));
			if(group == null) {
				sender.wmsg("존재하지 않는 채널 그룹입니다.");
				return;
			}
			
			npc.setChannelGroup(group);
		} else {
			cmd.sendUsage(sender, false);
			return;
		}
		
		if(args.length > 2) {
			npc.setSkinName(args[2]);
		}
		
		npc.setLocation(((Player) sender.getPlatformSender()).getLocation());
		
		npc.spawnNPC();
		npc.updateHologram(false);
		
		npc.saveCitizens();
		api.getConfigManager().saveNPC();
		
		sender.msg(npc.getName() + " NPC를 생성했습니다.");
	}
	
	@SubCommandHandler(
			parent = "channelnpc",
			name = "delete",
			aliases = {"삭제", "tkrwp", "remove"},
			additional = "<ID>",
			minArgs = 1,
			permission = PermissionList.CHANNELNPC_ADMIN,
			usage = "채널 NPC를 삭제합니다."
			)
	public void channelnpc_delete(UCommandSender sender, String[] args, Command cmd) {
		if(!NumberUtil.isInteger(args[0])) {
			cmd.sendUsage(sender, false);
			return;
		}
		
		int id = Integer.parseInt(args[0]);
		
		ChannelNPC npc = api.getNPCManager().getChannelNPC(id);
		if(npc == null) {
			sender.wmsg("존재하지 않는 NPC입니다.");
			return;
		}
		
		npc.destroyNPC();
		npc.removeHologram();
		
		npc.saveCitizens();
		api.getNPCManager().removeChannelNPC(npc.getId());
		
		api.getConfigManager().saveNPC();
		
		sender.msg(npc.getName() + " NPC를 삭제했습니다.");
	}
	
	@SubCommandHandler(
			parent = "channelnpc",
			name = "tphere",
			aliases = {"위치설정", "dnlcltjfwjd"},
			additional = "<ID>",
			minArgs = 1,
			permission = PermissionList.CHANNELNPC_ADMIN,
			usage = "채널 NPC를 현재 위치로 불러옵니다."
			)
	public void channelnpc_tphere(UPlayer sender, String[] args, Command cmd) {
		if(!NumberUtil.isInteger(args[0])) {
			cmd.sendUsage(sender, false);
			return;
		}
		
		int id = Integer.parseInt(args[0]);
		
		ChannelNPC npc = api.getNPCManager().getChannelNPC(id);
		if(npc == null) {
			sender.wmsg("존재하지 않는 NPC입니다.");
			return;
		}
		
		npc.setLocation(((Player) sender.getPlatformSender()).getLocation());
		npc.updateHologram(true);
		
		npc.saveCitizens();
		api.getConfigManager().saveNPC();
		
		sender.msg(npc.getName() + " NPC를 현재 위치로 불러왔습니다.");
	}
	
	@SubCommandHandler(
			parent = "channelnpc",
			name = "info",
			aliases = {"정보", "wjdqh"},
			additional = "<ID>",
			minArgs = 1,
			permission = PermissionList.CHANNELNPC_ADMIN,
			usage = "채널 NPC 정보를 확인합니다."
			)
	public void channelnpc_info(UCommandSender sender, String[] args, Command cmd) {
		if(!NumberUtil.isInteger(args[0])) {
			cmd.sendUsage(sender, false);
			return;
		}
		
		int id = Integer.parseInt(args[0]);
		
		ChannelNPC npc = api.getNPCManager().getChannelNPC(id);
		if(npc == null) {
			sender.wmsg("존재하지 않는 NPC입니다.");
			return;
		}
		
		sender.nmsg("§e[ " + npc.getName() + " NPC 정보 ]");
		sender.nmsg("§eID: §f" + npc.getId());
		sender.nmsg("§e위치: §f" + KStringUtil.locationToString(npc.getLocation()));
		sender.nmsg("§e형식: §f" + npc.getChannelType().getName());
		sender.nmsg("§e우클릭: §f" + (npc.getRightCommands().size() < 1 ? "없음" : StringUtil.connectString(npc.getRightCommands(), "\n")));
		sender.nmsg("§e쉬프트 우클릭: §f" + (npc.getRightCommands().size() < 1 ? "없음" : StringUtil.connectString(npc.getShiftRightCommands(), "\n")));
	}
	
	@SubCommandHandler(
			parent = "channelnpc",
			name = "list",
			aliases = {"목록", "ahrfhr"},
			permission = PermissionList.CHANNELNPC_ADMIN,
			usage = "채널 목록을 확인합니다."
			)
	public void channelnpc_list(UCommandSender sender, String[] args) {
		if(api.getNPCManager().getChannelNPCs().size() < 1) {
			sender.wmsg("채널 NPC가 없습니다.");
			return;
		}
		
		sender.nmsg("§e[ 채널 NPC 목록 ]");
		
		for(ChannelNPC npc : api.getNPCManager().getChannelNPCs().values()) {
			sender.nmsg("§e" + npc.getId() + ": §f" + npc.getName());
		}
	}
	
}