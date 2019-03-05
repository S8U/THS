package su.plugin.prefixer.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.prefixer.PermissionList;
import su.plugin.prefixer.PrefixerPlugin;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.object.PrefixPlayer;

public class AdminCommand implements UCommandListener {
	
	private PrefixerAPI api = PrefixerPlugin.getApi();
	
	@SubCommandHandler(
			parent = "칭호",
			name = "추가",
			aliases = {"ㅊㄱ", "add"},
			additional = "<플레이어> <칭호>",
			permission = PermissionList.PREFIXER_ADMIN,
			minArgs = 2,
			usage = "칭호를 추가합니다."
			)
	public void add(CommandSender sender, String[] args, Command cmd) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(args[0]);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		String prefix = ChatColor.translateAlternateColorCodes('&', StringUtil.connectString(args, " ").substring(args[0].length()).trim());
		
		if(!api.addPrefix(playerKey, prefix)) {
			Core.wmsg(sender, "이미 가지고 있는 칭호입니다.");
			return;
		}
		
		Core.msg(sender, Core.getDisplayName(playerKey) + "§d님께 §f" + prefix + " §d칭호를 추가했습니다.");
	}
	
	@SubCommandHandler(
			parent = "칭호",
			name = "삭제",
			aliases = {"delete", "del"},
			additional = "<번호> (<플레이어>)",
			permission = PermissionList.PREFIXER_ADMIN,
			minArgs = 1,
			usage = "칭호를 삭제합니다."
			)
	public void delete(UCommandSender sender, String[] args, Command cmd) {
		if(args.length < 2 && sender.isConsole()) {
			cmd.sendUsage(sender, true);
			return;
		}
		
		int num = args.length < 1 ? 1 : NumberUtil.getInteger(args[0]);
		if(num == -1) {
			Core.wmsg(sender, "번호는 정수만 입력 가능합니다.");
			return;
		}
		
		String player = args.length < 2 ? sender.getName() : args[1];
		
		PlayerKey playerKey = PlayerKey.getPlayerKey(player);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		PrefixPlayer pp = api.getPlayerManager().getPrefixPlayer(playerKey);
		
		List<String> prefixes = pp != null ? pp.getPrefixes() : api.getSQLManager().getPrefixes(playerKey);
		if(prefixes.size() < 1) {
			Core.wmsg(sender, "칭호가 존재하지 않는 플레이어입니다.");
			return;
		}
		
		else if(num <= 0 || prefixes.size() < num) {
			Core.wmsg(sender, "해당 번호의 칭호가 존재하지 않습니다.");
			return;
		}
		
		String prefix = prefixes.get(num - 1);
		
		api.deletePrefix(playerKey, prefix);
		
		Core.msg(sender, (sender.getName().equals(player) ? "" : player + "§d님의 ") + "§f" + prefix + " §d칭호를 삭제했습니다.");
	}
	
}