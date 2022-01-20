package su.plugin.permission.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import lombok.Cleanup;
import lombok.SneakyThrows;
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

public class UserCommand implements UCommandListener {
	
	private PermissionAPI api = PermissionPlugin.getApi();
	
	@SneakyThrows(SQLException.class)
	@SubCommandHandler(
			parent = "pm",
			name = "users",
			additional = "(<페이지>)",
			permission = PermissionList.PERMISSION_ADMIN,
			usage="펄미션 플레이어 목록을 확인합니다."
			)
	public void pm_users(CommandSender sender, String[] args, Command command) {
		int page = 1;
		
		if(args.length > 0) {
			if(!NumberUtil.isInteger(args[0])) {
				Core.wmsg(sender, "페이지는 정수만 입력 가능합니다.");
				return;
			}
			page = Integer.parseInt(args[0]);
		}
		
		int maxPage = (int) (Math.floor(api.getSQLManager().getPlayerCount() / 7) + 1);
		if(page > maxPage) {
			Core.wmsg(sender, "페이지는 1부터 " + maxPage + "까지의 정수만 입력 가능합니다.");
			return;
		}
		
		@Cleanup PreparedStatement state = api.getSQLManager().getUserTable().select("player_id", "limit " + ((page - 1) * 10) + ", " + (page * 10 - 1));
		@Cleanup ResultSet result = state.executeQuery();
		
		Core.nmsg(sender, "§7[ 플레이어 목록 ( " + page + " / " + maxPage + " ) ]");
		while(result.next()) {
			Core.nmsg(sender, PlayerKey.getPlayerKey(result.getInt("player_id")).getName());
		}
	}
	
	@SubCommandHandler(
			parent = "pm",
			name = "user",
			permission = PermissionList.PERMISSION_ADMIN,
			usage="펄미션 플레이어 명령어를 확인합니다."
			)
	public void pm_user(UCommandSender sender, String[] args, Command command) {
		Core.nmsg(sender, "§7§l[ U-Permission | User ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("pm user", 1)) {
			sc.sendUsage(sender, false);
		}
	}
	
	//
	
	@SubCommandHandler(
			parent = "pm user",
			name = "delete",
			additional = "<플레이어>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage="§7플레이어의 펄미션 데이터를 삭제합니다."
			)
	public void pm_user_delete(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		api.getPlayerManager().removePermissionPlayer(playerKey);
		
		api.getSQLManager().deletePlayer(playerKey);
		
		api.getPlayerManager().sendPlayerChange(playerKey);
		
		Core.nmsg(sender, "§f" + args[0] + "§7님의 펄미션 데이터를 삭제했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm user",
			name = "info",
			additional = "<플레이어>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage="플레이어의 정보를 확인합니다."
			)
	public void pm_user_info(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null || !api.getSQLManager().loadPermissionPlayer(playerKey)) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
		
		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(pp.getGroupName());
		}
		
		Core.nmsg(sender, "§7[ §f" + pp.getName() + " §7정보 ]");
		Core.nmsg(sender, "§7접두사: §f" + (pp.hasPrefix() ? pp.getPrefix() : "없음"));
		Core.nmsg(sender, "§7접미사: §f" + (pp.hasSuffix() ? pp.getSuffix() : "없음"));
		
		Core.nmsg(sender, "§7그룹: §f" + (pp.hasGroup() ? pp.getGroupName() : "없음"));
		if(pp.hasGroup()) {
			List<String> n = pp.getGroup().getAllNodes();
			
			Core.nmsg(sender, "§7그룹 노드: §f" + (n.size() < 1 ? "없음" : ""));
			
			if(n.size() > 0) {
				Collections.sort(n);
				
				for(String node : n) {
					Core.nmsg(sender, node);
				}
			}
		}
		
		Core.nmsg(sender, "§7노드: §f" + (pp.getNodes().size() < 1 ? "없음" : ""));
		
		Collections.sort(pp.getNodes());
		
		for(String node : pp.getNodes()) {
			Core.nmsg(sender, node);
		}
	}
	
	@SubCommandHandler(
			parent = "pm user",
			name = "add",
			additional = "<플레이어> <노드>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage="플레이어에게 권한을 추가합니다."
			)
	public void pm_user_add(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		} else if((Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).hasPermission(args[1])) || api.getSQLManager().hasPlayerNode(playerKey, args[1])) {
			Core.wmsg(sender, "이미 존재하는 권한입니다.");
			return;
		}
		
		if(api.getPlayerManager().existsPermissionPlayer(playerKey)) {
			PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
			
			pp.addNode(args[1]);
			pp.addPermission(args[1]);
			
			args[0] = pp.getName();
		}
		
		api.getSQLManager().addPlayerNode(playerKey, args[1]);
		
		api.getPlayerManager().sendPlayerChange(playerKey);
		
		Core.msg(sender, "§f" + args[0] + "§7님께 §f" + args[1] + " §7노드를 추가했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm user",
			name = "remove",
			additional = "<플레이어> <노드>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage="플레이어의 권한을 삭제합니다."
			)
	public void pm_user_remove(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		} else if((Bukkit.getPlayer(args[0]) != null && !Bukkit.getPlayer(args[0]).hasPermission(args[1])) || !api.getSQLManager().hasPlayerNode(playerKey, args[1])) {
			Core.wmsg(sender, "권한이 없는 플레이어입니다.");
			return;
		}
		
		if(api.getPlayerManager().existsPermissionPlayer(playerKey)) {
			PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
			
			pp.removeNode(args[1]);
			pp.removePermission(args[1]);
			
			args[0] = pp.getName();
		}
		
		api.getSQLManager().removePlayerNode(playerKey, args[1]);
		
		api.getPlayerManager().sendPlayerChange(playerKey);
		
		Core.msg(sender, "§f" + args[0] + "§7님의 §f" + args[1] + " §7노드를 삭제했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm user",
			name = "setPrefix",
			additional = "<플레이어> <접두사>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage="플레이어의 접두사를 설정합니다."
			)
	public void pm_user_setPrefix(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		String prefix = ChatColor.translateAlternateColorCodes('&', StringUtil.connectString(args, " ").substring(args[0].length() + 1));
		
		if(api.getPlayerManager().existsPermissionPlayer(playerKey)) {
			PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
			
			pp.setPrefix(prefix);
			
			args[0] = pp.getName();
		}
		
		api.getSQLManager().setPlayerPrefix(playerKey, prefix);
		
		Core.msg(sender, "§f" + args[0] + "§7님의 접두사를 §f" + prefix + "§7으로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm user",
			name = "removePrefix",
			additional = "<플레이어>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage="플레이어의 접두사를 삭제합니다."
			)
	public void pm_user_removePrefix(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		if(api.getPlayerManager().existsPermissionPlayer(playerKey)) {
			PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
			
			pp.setPrefix(null);
			
			args[0] = pp.getName();
		}
		
		api.getSQLManager().setPlayerPrefix(playerKey, null);
		
		api.getPlayerManager().sendPlayerChange(playerKey);
		
		Core.msg(sender, "§f" + args[0] + "§7님의 접두사를 삭제했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm user",
			name = "setSuffix",
			additional = "<플레이어> <접미사>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage="플레이어의 접미사를 설정합니다."
			)
	public void pm_user_setSuffix(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		String suffix = ChatColor.translateAlternateColorCodes('&', StringUtil.connectString(args, " ").substring(args[0].length() + 1));
		
		if(api.getPlayerManager().existsPermissionPlayer(playerKey)) {
			PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
			
			pp.setSuffix(suffix);
			
			args[0] = pp.getName();
		}
		
		api.getSQLManager().setPlayerSuffix(playerKey, suffix);
		
		api.getPlayerManager().sendPlayerChange(playerKey);
		
		Core.msg(sender, "§f" + args[0] + "§7님의 접미사를 §f" + suffix + "§7으로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm user",
			name = "removeSuffix",
			additional = "<플레이어>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage="플레이어의 접미사를 삭제합니다."
			)
	public void pm_user_removeSuffix(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		if(api.getPlayerManager().existsPermissionPlayer(playerKey)) {
			PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
			
			pp.setSuffix(null);
			
			args[0] = pp.getName();
		}
		
		api.getSQLManager().setPlayerSuffix(playerKey, null);
		
		api.getPlayerManager().sendPlayerChange(playerKey);
		
		Core.msg(sender, "§f" + args[0] + "§7님의 접미사를 삭제했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm user",
			name = "setGroup",
			additional = "<플레이어> <그룹>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 2,
			usage="플레이어의 그룹을 설정합니다."
			)
	public void pm_user_setGroup(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}

		if(api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
			api.getSQLManager().loadGroup(args[1]);
		}

		PermissionGroup group = api.getGroupManager().getGroup(args[1]);
		if(group == null) {
			Core.wmsg(sender, "존재하지 않는 그룹입니다.");
			return;
		}
		
		if(api.getPlayerManager().existsPermissionPlayer(playerKey)) {
			PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
			
			pp.setGroupName(group.getName());
			pp.updatePermissionAttachment();
			
			args[0] = pp.getName();
		}
		
		api.getSQLManager().setPlayerGroup(playerKey, group.getName());
		
		api.getPlayerManager().sendPlayerChange(playerKey);
		
		Core.msg(sender, "§f" + args[0] + "§7님의 그룹을 §f" + group.getName() + "§7으로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = "pm user",
			name = "removeGroup",
			additional = "<플레이어>",
			permission = PermissionList.PERMISSION_ADMIN,
			minArgs = 1,
			usage="플레이어의 그룹을 삭제합니다."
			)
	public void pm_user_removeGroup(CommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		if(api.getPlayerManager().existsPermissionPlayer(playerKey)) {
			PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
			
			pp.setGroupName(null);
			pp.updatePermissionAttachment();
			
			args[0] = pp.getName();
		}
		
		api.getSQLManager().setPlayerGroup(playerKey, null);
		
		api.getPlayerManager().sendPlayerChange(playerKey);
		
		Core.msg(sender, "§f" + args[0] + "§7님의 그룹을 삭제했습니다.");
	}
	
}