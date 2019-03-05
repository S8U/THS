package su.plugin.core.common.command;

import java.util.HashMap;

import com.google.gson.Gson;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.StringUtil;

public class PlayerOptionCommand implements UCommandListener {

	@CommandHandler(
			name = "playerOption",
			aliases = {"플레이어옵션", "vmffpdldjdhqtus", "플옵", "vmfdhq", "po"},
			usePlatformPrefix = true,
			permission = "core.admin",
			usage = "플레이어 옵션 명령어를 확인합니다."
	)
	@SubCommandHandler(
			parent = "core",
			name = "playerOption",
			aliases = {"플레이어옵션", "vmffpdldjdhqtus", "플옵", "vmfdhq", "po"},
			permission = "core.admin",
			usage = "플레이어 옵션 명령어를 확인합니다."
	)
	public void playerOption(UCommandSender sender, String[] args) {
		Core.nmsg(sender, "§e§l[ U-Core | Player Option ]");
		for(SubCommand sc : Core.getCommandManager().getSubCommands("playerOption", 1)) {
			sc.sendUsage(sender, false);
		}
	}

	@SubCommandHandler(
			parent = {"playerOption", "core playerOption"},
			name = "set",
			aliases = {"설정", "tjfwjd"},
			additional = "<플레이어> <옵션 이름> <값 JSON>",
			minArgs = 3,
			permission = "core.admin",
			usage = "플레이어의 옵션을 설정합니다."
	)
	public void playerOption_set(UCommandSender sender, String[] args) {
		PlayerKey target = PlayerKey.getPlayerKey(args[0]);
		if(target == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		String vargs = StringUtil.connectString(args, " ").substring((args[0] + " " + args[1] + " ").length());
		
		Object obj = null;
		try {
			obj = new Gson().fromJson(vargs, Object.class);
		} catch(Exception e) {
			Core.wmsg(sender, "잘못된 JSON 입니다.");
			return;
		}

		if(target.getUPlayer() != null) {
			Core.getOptionManager().setPlayerOption(target, args[1], obj);
		}
		Core.getOptionSQLManager().setPlayerOption(target, args[1], obj);
		
		Core.msg(sender, target.getName() + " 님의 " + args[1] + " 옵션을 " + vargs + "(으)로 설정했습니다.");
	}
	
	@SubCommandHandler(
			parent = {"playerOption", "core playerOption"},
			name = "delete",
			aliases = {"삭제", "tkrwp"},
			additional = "<플레이어> <옵션 이름>",
			minArgs = 2,
			permission = "core.admin",
			usage = "플레이어의 옵션을 삭제합니다."
			)
	public void playerOption_delete(UCommandSender sender, String[] args) {
		PlayerKey target = PlayerKey.getPlayerKey(args[0]);
		if(target == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		} else if(!Core.getOptionSQLManager().existsPlayerOption(target, args[1])) {
			Core.wmsg(sender, "존재하지 않는 옵션입니다.");
			return;
		}

		if(target.getUPlayer() != null) {
			Core.getOptionManager().deletePlayerOption(target, args[1]);
		}
		Core.getOptionSQLManager().deletePlayerOption(target, args[1]);
		
		Core.msg(sender, target.getName() + " 님의 " + args[1] + " 옵션을 삭제했습니다.");
	}
	
	@SubCommandHandler(
			parent = {"playerOption", "core playerOption"},
			name = "get",
			aliases = {"확인", "ghkrdls"},
			additional = "<플레이어> <옵션 이름>",
			minArgs = 2,
			permission = "core.admin",
			usage = "플레이어의 옵션 값을 확인합니다."
			)
	public void playerOption_get(UCommandSender sender, String[] args) {
		PlayerKey target = PlayerKey.getPlayerKey(args[0]);
		if(target == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		Object obj = Core.getOptionSQLManager().getPlayerOption(target, args[1]);
		
		Core.msg(sender, target.getName() + " 님의 " + args[1] + " 옵션: " + new Gson().toJson(obj));
	}
	
	@SubCommandHandler(
			parent = {"playerOption", "core playerOption"},
			name = "list",
			aliases = {"목록", "ahrfhr"},
			additional = "<플레이어>",
			minArgs = 1,
			permission = "core.admin",
			usage = "플레이어의 옵션 목록을 확인합니다."
			)
	public void playerOption_list(UCommandSender sender, String[] args) {
		PlayerKey target = PlayerKey.getPlayerKey(args[0]);
		if(target == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		Core.nmsg(sender, "§e[ " + target.getName() + " 님의 옵션 ]");
		
		HashMap<String, Object> objs = Core.getOptionSQLManager().getPlayerOptions(target);
		objs.forEach((name, obj) -> {
			Core.nmsg(sender, "§e" + name + ": §f" + new Gson().toJson(obj));
		});
	}
	
}