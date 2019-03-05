package su.plugin.core.common.command;

import java.util.HashMap;

import com.google.gson.Gson;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.util.StringUtil;

public class ServerOptionCommand implements UCommandListener {

	@CommandHandler(
			name = "serverOption",
			aliases = {"서버옵션", "tjqjdhqtus", "서옵", "tjdhq", "so"},
			usePlatformPrefix = true,
			permission = "core.admin",
			usage = "서버 옵션 명령어를 확인합니다."
	)
	@SubCommandHandler(
			parent = "core",
			name = "serverOption",
			aliases = {"서버옵션", "tjqjdhqtus", "서옵", "tjdhq", "so"},
			permission = "core.admin",
			usage = "서버 옵션 명령어를 확인합니다."
	)
	public void serverOption(UCommandSender sender, String[] args) {
		Core.nmsg(sender, "§e§l[ U-Core | Server Option ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("serverOption", 1)) {
			sc.sendUsage(sender, false);
		}
	}
	
	@SubCommandHandler(
			parent = {"serverOption", "core serverOption"},
			name = "set",
			aliases = {"설정", "tjfwjd"},
			additional = "<옵션 이름> <값 JSON>",
			minArgs = 2,
			permission = "core.admin",
			usage = "서버의 옵션을 설정합니다."
			)
	public void serverOption_set(UCommandSender sender, String[] args) {
		String vargs = StringUtil.connectString(args, " ").substring((args[0] + " ").length());
		
		Object obj = null;
		try {
			obj = new Gson().fromJson(vargs, Object.class);
		} catch(Exception e) {
			Core.wmsg(sender, "잘못된 JSON 입니다.");
			return;
		}
		
		Core.getOptionManager().setServerOption(args[0], obj);
		Core.getOptionSQLManager().setServerOption(args[0], obj);
		
		Core.msg(sender, "서버의 " + args[0] + " 옵션을 " + vargs + "(으)로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = {"serverOption", "core serverOption"},
			name = "delete",
			aliases = {"삭제", "tkrwp"},
			additional = "<옵션 이름>",
			minArgs = 1,
			permission = "core.admin",
			usage = "서버의 옵션을 삭제합니다."
			)
	public void serverOption_delete(UCommandSender sender, String[] args) {
		if(!Core.getOptionSQLManager().existsServerOption(args[1])) {
			Core.wmsg(sender, "존재하지 않는 옵션입니다.");
			return;
		}
		
		Core.getOptionSQLManager().deleteServerOption(args[1]);
		
		Core.msg(sender, "서버의 " + args[1] + " 옵션을 삭제했습니다.");
	}
	
	@SubCommandHandler(
			parent = {"serverOption", "core serverOption"},
			name = "get",
			aliases = {"확인", "ghkrdls"},
			additional = "<옵션 이름>",
			minArgs = 1,
			permission = "core.admin",
			usage = "서버의 옵션을 확인합니다."
			)
	public void serverOption_get(UCommandSender sender, String[] args) {
		Object obj = Core.getOptionSQLManager().getServerOption(args[0]);
		
		Core.nmsg(sender, "§e서버의 " + args[1] + " 옵션: §f" + new Gson().toJson(obj));
	}
	
	@SubCommandHandler(
			parent = {"serverOption", "core serverOption"},
			name = "list",
			aliases = {"목록", "ahrfhr"},
			permission = "core.admin",
			usage = "서버의 옵션 목록을 확인합니다."
			)
	public void serverOption_list(UCommandSender sender, String[] args) {
		Core.nmsg(sender, "§e[ 서버 옵션 ]");
		
		Core.getOptionSQLManager().loadServerOptions();
		
		HashMap<String, Object> objs = Core.getOptionManager().getServerOptions();
		objs.forEach((name, obj) -> {
			Core.nmsg(sender, "§e" + name + ": §f" + new Gson().toJson(obj));
		});
	}
	
}