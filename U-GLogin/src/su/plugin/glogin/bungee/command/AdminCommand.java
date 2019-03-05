package su.plugin.glogin.bungee.command;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.glogin.bungee.GGLoginPlugin;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.glogin.common.api.category.Type;
import su.plugin.glogin.common.api.object.Account;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.StringUtil;

public class AdminCommand implements UCommandListener {
	
	private GGLoginAPI api = GGLoginPlugin.getApi();
	
	@CommandHandler(
			name="로그인관리",
			aliases={"loginManager", "lm"},
			permission="glogin.admin",
			usage="로그인 관리 명령어 목록을 확인합니다."
			)
	public void loginManager(UCommandSender sender, String[] args, Command command) {
		sender.nmsg("§6[ 로그인 관리 ]");
		for(Command cmd : Core.getCommandManager().getSubCommands(command.getName(), 1)) {
			cmd.sendUsage(sender, false);
		}
	}
	
	/*@SubCommandHandler(parent="로그인관리", name="회원가입예약", aliases= "registerReserve", permiglogin"glogin
	.admin",
			usage="/로그인관리 회원가입예약 <플레이어> <비밀번호> §6- 플레이어 가입을 예약시킵니다.")
	public void loginManager_register(CommandSender sender, String[] args, Command command) {
		if(args.length < 2) {
			command.sendUsage(sender, true);
			return;
		}
		
	}*/
	
	@SubCommandHandler(
			parent="로그인관리",
			name="회원탈퇴",
			aliases="unregister",
			additional="<플레이어>",
			minArgs=1,
			permission="glogin.admin",
			usage="플레이어를 탈퇴시킵니다."
			)
	public void loginManager_unregister(UCommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		Account account = api.getSQLManager().getAccount(playerKey);
		if(account == null) {
			sender.wmsg("아직 가입하지 않은 플레이어입니다.");
			return;
		}
		
		
		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(playerKey.getName());
		
		ProxyServer.getInstance().getScheduler().runAsync(GGLoginPlugin.getInstance(), () -> {
			api.getSQLManager().deleteAccount(account);
			api.getSQLManager().writeManageLog(playerKey, sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(), sender.isConsole() ? "console" : ((UPlayer) sender).getIp(), Type.UNREGISTER);
		});
		
		if(target != null) {
			api.sendLoginToServer(account.getPlayerKey(), false);
			
			api.getTitleManager().sendRegisterTitle(target);
			api.getTaskManager().startLoginTimeoutTask(target);
			
			Core.msg(target, "§c관리자에 의해 회원탈퇴 처리되었습니다.");
		}
		
		api.getAccountManager().removeAccount(playerKey);

		sender.msg(playerKey.getName() + " §6님을 탈퇴시켰습니다.");
	}
	
	@SubCommandHandler(
			parent="로그인관리",
			name="비밀번호변경",
			aliases="passwordChange",
			additional="<플레이어> <비밀번호>",
			minArgs=2,
			permission="glogin.admin",
			usage="플레이어의 비밀번호를 변경시킵니다."
			)
	public void loginManager_passwordChange(UCommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		Account account = api.getSQLManager().getAccount(playerKey);
		if(account == null) {
			sender.wmsg("아직 가입하지 않은 플레이어입니다.");
			return;
		}
		
		args[0] = account.getName();
		
		account.setPassword(api.getSHA256(args[1]));
		
		ProxyServer.getInstance().getScheduler().runAsync(GGLoginPlugin.getInstance(), () -> {
			api.getSQLManager().saveAccount(account);
			api.getSQLManager().writeManageLog(playerKey, sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(), sender.isConsole() ? "console" : ((UPlayer) sender).getIp(), Type.PASSWORD_CHANGE);
		});
	}
	
	@SubCommandHandler(
			parent="로그인관리",
			name="계정",
			aliases="account",
			additional="<플레이어>",
			minArgs=1,
			permission="glogin.admin",
			usage="플레이어의 계정 정보를 확인합니다."
			)
	public void loginManager_lastLogin(UCommandSender sender, String[] args, Command command) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		Account account = api.getSQLManager().getAccount(playerKey);
		if(account == null) {
			sender.wmsg("아직 가입하지 않은 플레이어입니다.");
			return;
		}
		
		args[0] = account.getName();
		
		sender.nmsg("§6[ §f" + account.getName() + "§6 계정 정보 ]");
		sender.nmsg("§6가입일: §f" + StringUtil.buildDateString(account.getRegisterTime(), "yyyy-MM-dd HH:mm:ss"));
		sender.nmsg("§6마지막 로그인: §f" + (account.getLastLogin() == 0 ? "기록 없음" : StringUtil.buildTimeString(System.currentTimeMillis() - account.getLastLogin()) + " 전"));
	}
	
	@SubCommandHandler(
			parent="로그인관리",
			name="계정",
			aliases="accountList",
			additional="<플레이어 | IP>",
			minArgs=1,
			permission="glogin.admin",
			usage="플레이어의 계정 목록을 확인합니다."
			)
	public void loginManager_account(UCommandSender sender, String[] args, Command command) {
		List<Account> accounts = new ArrayList<>();
		
		if(args[0].contains(".")) {
			accounts = api.getSQLManager().getAccounts(args[0]);
		} else {
			PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
			if(playerKey == null) {
				sender.wmsg("존재하지 않는 플레이어입니다.");
				return;
			}
			
			Account account = api.getSQLManager().getAccount(playerKey);
			if(account != null) {
				accounts.add(account);
			}
		}
		
		if(accounts.size() < 1) {
			sender.wmsg("계정을 찾을 수 없습니다.");
			return;
		}
		
		sender.nmsg("§6[ §f" + args[0] + "§6 계정 목록 ]");
		for(Account account : accounts) {
			sender.nmsg(account.getName() + " §6- 가입일: §f" + StringUtil.buildDateString(account.getRegisterTime(), "yyyy-MM-dd HH:mm:ss") + " / §6마지막 로그인: §f" + (account.getLastLogin() == 0 ? "기록 없음" : StringUtil.buildTimeString(System.currentTimeMillis() - account.getLastLogin()) + " 전"));
		}
	}
	
}