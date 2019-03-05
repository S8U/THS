package su.plugin.glogin.bungee.command;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.glogin.bungee.GGLoginPlugin;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.glogin.bungee.api.event.LoginEvent;
import su.plugin.glogin.bungee.api.event.LogoutEvent;
import su.plugin.glogin.bungee.api.event.PasswordChangeEvent;
import su.plugin.glogin.bungee.api.event.RegisterEvent;
import su.plugin.glogin.bungee.api.event.UnRegisterEvent;
import su.plugin.glogin.common.api.category.Type;
import su.plugin.glogin.common.api.object.Account;
import su.plugin.core.bungee.api.GCore;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;

public class UserCommand implements UCommandListener {
	
	private GGLoginAPI api = GGLoginPlugin.getApi();
	
	@CommandHandler(
			name="회원가입",
			additional = "<비밀번호> <비밀번호 재입력>",
			minArgs = 2,
			aliases="register",
			usage="서버에 회원가입합니다."
			)
	public void register(UPlayer up, String[] args, Command command) {
		ProxiedPlayer p = (ProxiedPlayer) up.getPlatformSender();
		if(args.length < 1) {
			command.sendUsage(up, true);
			return;
		} else if(api.getAccountManager().hasAccount(up.getPlayerKey()) || (api.isForceLoginOnConnect() || (api.isExcludeLoginIfOnlineMode() && GCore.getOnlineMode(p)))) {
			up.wmsg("이미 가입되어있습니다.");
			return;
		} else if(!args[0].equals(args[1])) {
			up.wmsg("비밀번호가 일치하지 않습니다.");
			return;
		}
		
		String ip = p.getAddress().getAddress().getHostAddress();
		if(api.getSQLManager().getAccounts(ip).size() >= api.getMaxAccountPerIp()) {
			up.wmsg("계정은 한 아이피당 " + api.getMaxAccountPerIp() + "개만 생성 가능합니다.");
			return;
		}
		
		Account account = api.getAccountManager().getAccount(up.getPlayerKey());
		if(account == null) {
			account = new Account(up.getPlayerKey());
		}

		account.setName(up.getName());
		account.setIp(ip);
		account.setPassword(api.getSHA256(args[0]));
		account.setLastLogin(System.currentTimeMillis());
		account.setRegisterTime(System.currentTimeMillis());
		
		account.setLogin(true);
		
		api.getAccountManager().setAccount(up.getPlayerKey(), account);
		
		ProxyServer.getInstance().getScheduler().runAsync(GGLoginPlugin.getInstance(), () -> {
			Account accountl = api.getAccountManager().getAccount(up.getPlayerKey());

			api.getSQLManager().saveAccount(accountl);
			
			api.sendLoginToServer(accountl.getPlayerKey(), true);
			
			api.getSQLManager().writeLoginLog(up.getPlayerKey(), accountl.getIp(), Type.REGISTER);
		});
		
		api.getTitleManager().sendLoginCompleteTitle(p);
		
		ProxyServer.getInstance().getPluginManager().callEvent(new RegisterEvent(p));
		
		Core.log(p.getName() + "님께서 가입했습니다. (" + account.getIp() + " / " + p.getUniqueId() + ")");
	}
	
	@CommandHandler(
			name="회원탈퇴",
			aliases="unregister",
			usage="서버에서 탈퇴합니다."
			)
	public void unregister(UPlayer up, String[] args, Command command) {
		ProxiedPlayer p = (ProxiedPlayer) up.getPlatformSender();
		Account account = api.getAccountManager().getAccount(up.getPlayerKey());
		if(account == null) {
			up.wmsg("아직 가입하지 않았습니다.");
			return;
		} else if(!account.isLogin()) {
			up.wmsg("아직 로그인하지 않았습니다.");
			return;
		}
		
		account.setLogin(false);
		api.sendLoginToServer(account.getPlayerKey(), false);
		
		ProxyServer.getInstance().getScheduler().runAsync(GGLoginPlugin.getInstance(), () -> {
			api.getSQLManager().deleteAccount(account);
			api.getSQLManager().writeLoginLog(up.getPlayerKey(), account.getIp(), Type.UNREGISTER);
		});
		
		api.getAccountManager().removeAccount(up.getPlayerKey());
		
		api.getTitleManager().sendRegisterTitle(p);
		api.getTaskManager().startLoginTimeoutTask(p);
		
		ProxyServer.getInstance().getPluginManager().callEvent(new UnRegisterEvent(p));
		
		up.msg("§c탈퇴되었습니다.");
	}
	
	@CommandHandler(
			name="로그인",
			aliases="login",
			additional="<비밀번호>",
			minArgs=1,
			usage="서버에 로그인합니다."
			)
	public void login(UPlayer up, String[] args, Command command) {
		ProxiedPlayer p = (ProxiedPlayer) up.getPlatformSender();
		Account account = api.getAccountManager().getAccount(up.getPlayerKey());
		if(account == null) {
			up.wmsg("아직 가입하지 않았습니다.");
			return;
		} else if(account.isLogin()) {
			up.wmsg("이미 로그인했습니다.");
			return;
		}
		
		String hash = api.getSHA256(args[0]);
		if(!hash.equals(account.getPassword())) {
			up.wmsg("잘못된 비밀번호입니다.");
			return;
		}
		
		api.sendLoginToServer(account.getPlayerKey(), true);
		
		account.setLogin(true);
		account.setName(p.getName());
		account.setIp(p.getAddress().getAddress().getHostAddress());
		account.setLastLogin(System.currentTimeMillis());
		
		ProxyServer.getInstance().getScheduler().runAsync(GGLoginPlugin.getInstance(), () -> {
			api.getSQLManager().saveAccount(account);
			
			api.getSQLManager().writeLoginLog(up.getPlayerKey(), account.getIp(), Type.LOGIN);
		});
		
		api.getTaskManager().stopLoginTimeoutTask(p);
		
		api.getTitleManager().sendLoginCompleteTitle(p);
		
		ProxyServer.getInstance().getPluginManager().callEvent(new LoginEvent(p));
		
		Core.log(p.getName() + "님께서 로그인했습니다. (" + account.getIp() + " / " + p.getUniqueId() + ")");
	}
	
	@CommandHandler(
			name="로그아웃",
			aliases="logout",
			usage="서버에서 로그아웃합니다."
			)
	public void logout(UPlayer up, String[] args, Command command) {
		ProxiedPlayer p = (ProxiedPlayer) up.getPlatformSender();
		Account account = api.getAccountManager().getAccount(up.getPlayerKey());
		if(account == null) {
			up.wmsg("아직 가입하지 않았습니다.");
			return;
		} else if(!account.isLogin()) {
			up.wmsg("아직 로그인하지 않았습니다.");
			return;
		}

		account.setLastLogout(System.currentTimeMillis());

		ProxyServer.getInstance().getScheduler().runAsync(GGLoginPlugin.getInstance(), () -> {
			api.getSQLManager().saveAccount(account);
			
			api.getSQLManager().writeLoginLog(up.getPlayerKey(), account.getIp(), Type.LOGOUT);
		});
		
		account.setLogin(false);
		api.sendLoginToServer(account.getPlayerKey(), false);
		
		api.getTitleManager().sendLoginTitle(p);
		
		ProxyServer.getInstance().getPluginManager().callEvent(new LogoutEvent(p));
		
		up.msg("§e로그아웃되었습니다.");
	}
	
	@CommandHandler(
			name="비밀번호변경",
			aliases="passwordChange",
			additional="<비밀번호>",
			minArgs=1,
			usage="비밀번호를 변경합니다."
			)
	public void passwordChange(UPlayer up, String[] args, Command command) {
		ProxiedPlayer p = (ProxiedPlayer) up.getPlatformSender();
		Account account = api.getAccountManager().getAccount(up.getPlayerKey());
		if(account == null) {
			Core.wmsg(p, "아직 가입하지 않았습니다.");
			return;
		} else if(!account.isLogin()) {
			Core.wmsg(p, "아직 로그인하지 않았습니다.");
			return;
		}
		
		account.setPassword(api.getSHA256(args[0]));
		
		ProxyServer.getInstance().getScheduler().runAsync(GGLoginPlugin.getInstance(), () -> {
			api.getSQLManager().saveAccount(account);
			
			api.getSQLManager().writeLoginLog(up.getPlayerKey(), account.getIp(), Type.PASSWORD_CHANGE);
		});
		
		ProxyServer.getInstance().getPluginManager().callEvent(new PasswordChangeEvent(p));
		
		up.msg("§e비밀번호가 변경되었습니다.");
	}
	
}