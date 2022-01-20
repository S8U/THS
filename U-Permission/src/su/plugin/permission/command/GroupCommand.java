package su.plugin.permission.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.permission.PermissionList;
import su.plugin.permission.PermissionPlugin;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionGroup;
import su.plugin.permission.api.object.PermissionPlayer;

public class GroupCommand implements UCommandListener {
	
	private PermissionAPI api = PermissionPlugin.getApi();
	
	@SubCommandHandler(
			parent = "pm",
			name = "groups",
			additional = "(<페이지>)",
			permission = PermissionList.PERMISSION_ADMIN,
			usage = "펄미션 그룹 목록을 확인합니다."
			)
	public void pm_groups(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadAllGroup();
		}
		
		int page = 1;
		
		if(args.length > 0) {
			if(!NumberUtil.isInteger(args[0])) {
				Core.wmsg(sender, "페이지는 정수만 입력 가능합니다.");
				return;
			}
			page = Integer.parseInt(args[0]);
		}
		
		int maxPage = (int) (Math.floor(api.getGroupManager().getPermissionGroups().size() / 7) + 1);
		if(page > maxPage) {
			Core.wmsg(sender, "페이지는 1부터 " + maxPage + "까지의 정수만 입력 가능합니다.");
			return;
		}
		
		Core.nmsg(sender, "§7[ 그룹 목록 ( " + page + " / " + maxPage + " ) ]");
		for(int i = 0; i < 7; i++) {
			int num = (page -  1) * 7 + i;
			if(api.getGroupManager().getPermissionGroups().size() <= num) break;
			
			Core.nmsg(sender, api.getGroupManager().getPermissionGroupList().get(num).getName());
		}
	}
	
	@SubCommandHandler(
			parent = "pm",
			name = "group",
			permission = PermissionList.PERMISSION_ADMIN,
			usage = "펄미션 그룹 관련 명령어를 확인합니다.")
	public void pm_group(UCommandSender sender, String[] args, Command command) {
		Core.nmsg(sender, "§7§l[ U-Permission | Group ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("pm group", 1)) {
			sc.sendUsage(sender, false);
		}
	}
	
	@SubCommandHandler(
			parent = "pm group",
			name = "create",
			additional = "<그룹>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage = "그룹을 생성합니다.")
	public void pm_group_create(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadAllGroup();
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		
		if(group != null) {
			Core.wmsg(sender, "이미 존재하는 그룹입니다.");
			return;
		}
		
		group = new PermissionGroup(args[0]);
		
		api.getGroupManager().setGroup(args[0], group);
		
		api.getSQLManager().saveGroup(group);
		
		api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());
		
		Core.msg(sender, group.getName() + " §7그룹을 생성했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm group",
			name = "delete",
			additional = "<그룹>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage = "그룹을 삭제합니다.")
	public void pm_group_delete(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		
		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}
		
		args[0] = group.getName();
		
		for(PermissionPlayer pp : group.getOnlinePlayers()) {
			pp.setGroupName(null);
			
			pp.updatePermissionAttachment();
		}
		
		api.getGroupManager().removeGroup(args[0]);
		
		api.getSQLManager().deleteGroup(args[0]);
		
		api.getGroupManager().sendGroupDeleteToAllChannel(group.getName());
		
		Core.msg(sender, args[0] + " §7그룹을 삭제했습니다.");
	}

	@SubCommandHandler(
			parent = "pm group",
			name = "info",
			additional = "<그룹>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage = "그룹 정보를 확인합니다.")
	public void pm_group_info(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadConfig();
			api.getSQLManager().loadGroup(args[0]);
		}

		PermissionGroup group = api.getGroupManager().getGroup(args[0]);

		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}

		Core.nmsg(sender, "§7[ §f" + group.getName() + " §7그룹 정보 ]");
		Core.nmsg(sender, "§7접두사: §f" + (group.hasPrefix() ? group.getPrefix() : "없음"));
		Core.nmsg(sender, "§7접미사: §f" + (group.hasSuffix() ? group.getSuffix() : "없음"));
		Core.nmsg(sender, "§7기본 그룹: §f" + (group.isDefaultGroup() ? "O" : "X"));

		Core.nmsg(sender, "§7부모 그룹: §f" + (group.hasParents() ? "" : "없음"));

		if(group.hasParents()) {
			Collections.sort(group.getParents());

			for(String node : group.getParents()) {
				Core.nmsg(sender, node);
			}

			//

			List<String> n = group.getParentNodes();

			Core.nmsg(sender, "§7부모 그룹 노드: §f" + (n.size() < 1 ? "없음" : ""));

			if(n.size() > 0) {
				Collections.sort(n);

				for(String node : n) {
					Core.nmsg(sender, node);
				}
			}
		}

		Core.nmsg(sender, "§7노드: §f" + (group.getNodes().size() < 1 ? "없음" : ""));

		Collections.sort(group.getNodes());

		for(String node : group.getNodes()) {
			Core.nmsg(sender, node);
		}
	}

	@SubCommandHandler(
			parent = "pm group",
			name = "users",
			additional = "<그룹> (<페이지>)",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage = "그룹에 속한 플레이어를 확인합니다.")
	@SneakyThrows(SQLException.class)
	public void pm_group_users(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}

		PermissionGroup group = api.getGroupManager().getGroup(args[0]);

		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}

		int groupPlayerCount = api.getSQLManager().getGroupPlayerCount(group.getName());
		if (groupPlayerCount < 1) {
			Core.wmsg(sender, "그룹에 속한 플레이어가 없습니다.");

			return;
		}

		int page = 1;
		int maxPage = (int) Math.floor(groupPlayerCount / 10) + 1;

		if (args.length > 1) {
			if (!NumberUtil.isInteger(args[1]) || (page = Integer.parseInt(args[1])) > maxPage) {
				Core.wmsg(sender,"페이지는 " + page + " ~ " + maxPage + "의 정수만 입력 가능합니다.");

				return;
			}
		}

		@Cleanup PreparedStatement state
				= api.getSQLManager().getUserTable().select("player_id","where group_name ='" + group.getName() + "' limit " + ((page - 1) * 10) + ", 10");
		@Cleanup ResultSet result = state.executeQuery();

		Core.nmsg(sender, "§7[ §f" + group.getName() + " §7그룹 플레이어 목록 ( " + page + " / " + maxPage + " ) ]");
		for (int i = 1; result.next(); i++) {
			Core.nmsg(sender,"§7" + i + " ) §f" + PlayerKey.getPlayerKey(result.getInt("player_id")).getName());
		}
	}
	
	@SubCommandHandler(
			parent = "pm group",
			name = "add",
			additional = "<그룹> <노드>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage = "그룹에 권한을 추가합니다.")
	public void pm_group_add(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		
		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}
		
		group.addNode(args[1]);
		
		api.getSQLManager().addGroupNode(group.getName(), args[1]);
		
		api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());
		
		group.updatePlayerPermissionAttachments();
		
		Core.msg(sender, "§f" + group.getName() + " §7그룹에 §f" + args[1] + " §7노드를 추가했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm group",
			name = "remove",
			additional = "<그룹> <노드>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage = "그룹의 권한을 삭제합니다.")
	public void pm_group_remove(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		
		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}
		
		group.removeNode(args[1]);
		
		api.getSQLManager().removeGroupNode(group.getName(), args[1]);
		
		api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());
		
		group.updatePlayerPermissionAttachments();
		
		Core.msg(sender, "§f" + group.getName() + " §7그룹의 §f" + args[1] + " §7노드를 삭제했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm group",
			name = "setDefault",
			additional = "<그룹>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage = "그룹을 기본 그룹으로 설정합니다.")
	public void pm_group_setDefault(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		
		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}
		
		group.setDefaultGroup();
		
		api.getSQLManager().setDefaultGroup(group.getName());
		
		api.getGroupManager().sendDefaultGroupChangeToAllChannel();
		
		Core.msg(sender, "§f" + group.getName() + " §7그룹을 기본 그룹으로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm group",
			name = "setPrefix",
			additional = "<그룹> <접두사>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage = "그룹의 접두사를 설정합니다.")
	public void pm_group_setPrefix(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		
		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}
		
		String prefix = ChatColor.translateAlternateColorCodes('&', StringUtil.connectString(args, " ").substring(args[0].length() + 1));
		
		group.setPrefix(prefix);
		
		api.getSQLManager().setGroupPrefix(group.getName(), prefix);
		
		api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());
		
		Core.msg(sender, "§f" + group.getName() + " §7그룹의 접두사를 §f" + prefix + "§7으로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm group",
			name = "removePrefix",
			additional = "<그룹>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage = "그룹의 접두사를 삭제합니다.")
	public void pm_group_removePrefix(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		
		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}
		
		group.setPrefix(null);
		
		api.getSQLManager().setGroupPrefix(group.getName(), null);
		
		api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());
		
		Core.msg(sender, "§f" + group.getName() + " §7그룹의 접두사를 삭제했습니다.");
	}
	
	@SubCommandHandler(parent = "pm group",
			name = "setSuffix",
			additional = "<그룹> <접미사>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage = "그룹의 접미사를 설정합니다.")
	public void pm_group_setSuffix(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		
		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}
		
		String suffix = ChatColor.translateAlternateColorCodes('&', StringUtil.connectString(args, " ").substring(args[0].length() + 1));
		
		group.setSuffix(suffix);
		
		api.getSQLManager().setGroupSuffix(group.getName(), suffix);
		
		api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());
		
		Core.msg(sender, "§f" + group.getName() + " §7그룹의 접미사를 §f" + suffix + "§7으로 설정했습니다.");
	}
	
	@SubCommandHandler(parent = "pm group",
			name = "removeSuffix",
			additional = "<그룹>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage = "그룹의 접미사를 삭제합니다.")
	public void pm_group_removeSuffix(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		
		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}
		
		group.setSuffix(null);
		
		api.getSQLManager().setGroupSuffix(group.getName(), null);
		
		api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());
		
		Core.msg(sender, "§f" + group.getName() + " §7그룹의 접미사를 삭제했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm group",
			name = "addParent",
			additional = "<그룹> <부모 그룹>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage = "부모 그룹을 추가합니다.")
	public void pm_group_addParent(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
			api.getSQLManager().loadGroup(args[1]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		PermissionGroup parent = api.getGroupManager().getGroup(args[1]);
		
		if(group == null || parent == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		} else if(group.equals(parent)) {
			Core.wmsg(sender, "같은 그룹은 추가할 수 없습니다.");
			return;
		} else if(group.hasParent(parent.getName())) {
			Core.wmsg(sender, "이미 존재하는 그룹입니다.");
			return;
		} else if(parent.hasParent(group.getName())) {
			Core.wmsg(sender, "부모 그룹에 해당 그룹이 부모 그룹으로 존재합니다.");
			return;
		}
		
		group.addParent(parent.getName());
		
		api.getSQLManager().addGroupParent(group.getName(), parent.getName());
		
		api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());
		
		group.updatePlayerPermissionAttachments();
		
		Core.msg(sender, "§f" + group.getName() + " §7그룹에 §f" + parent.getName() + " §7부모 그룹을 추가했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm group",
			name = "removeParent",
			additional = "<그룹> <부모 그룹>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage = "부모 그룹을 삭제합니다.")
	public void pm_group_removeParent(CommandSender sender, String[] args, Command command) {
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[0]);
		}
		
		PermissionGroup group = api.getGroupManager().getGroup(args[0]);
		PermissionGroup parent = api.getGroupManager().getGroup(args[1]);
		
		if(group == null || parent == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		} else if(!group.hasParent(parent.getName())) {
			Core.wmsg(sender, "부모 그룹이 아닌 그룹입니다.");
			return;
		}
		
		group.removeParent(parent.getName());
		
		api.getSQLManager().removeGroupParent(group.getName(), parent.getName());
		
		api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());
		
		group.updatePlayerPermissionAttachments();
		
		Core.msg(sender, "§f" + group.getName() + " §7그룹에서 §f" + parent.getName() + " §7부모 그룹을 삭제했습니다.");
	}
	
}