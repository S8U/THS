package su.plugin.prefixer.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.prefixer.PrefixerPlugin;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.object.PrefixPlayer;

public class UserCommand implements UCommandListener {
	
	private PrefixerAPI api = PrefixerPlugin.getApi();
	
	@SubCommandHandler(
			parent = "칭호",
			name = "착용",
			aliases = {"ㅊㅇ", "설정", "ㅅㅈ", "wear"},
			additional = "<번호> (<우선 순위>)",
			minArgs = 1,
			usage = "칭호를 착용합니다."
			)
	public void wear(Player p, String[] args) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(p.getName());
		
		PrefixPlayer pp = api.getPlayerManager().getPrefixPlayer(playerKey);
		
		Integer num = NumberUtil.getInteger(args[0]);
		int priority = args.length < 2 ? pp.getMainPrefixes().size() + 1 : NumberUtil.getInteger(args[1]);
		if(num == null) {
			Core.wmsg(p, "번호는 정수만 입력 가능합니다.");
			return;
		} else if(priority < 1 || priority > api.getMaxMainPrefixCount()) {
			Core.wmsg(p, "우선 순위는 1부터 " + api.getMaxMainPrefixCount() + "까지의 정수만 입력 가능합니다.");
			return;
		}
		
		List<String> prefixes = pp != null ? pp.getPrefixes() : api.getSQLManager().getPrefixes(playerKey);
		if(prefixes.size() < 1) {
			Core.wmsg(p, "칭호가 존재하지 않는 플레이어입니다.");
			return;
		}
		
		if(num <= 0 || prefixes.size() < num) {
			Core.wmsg(p, "해당 번호의 칭호가 존재하지 않습니다.");
			return;
		}
		
		String prefix = prefixes.get(num - 1);
		
		if(pp.isMainPrefix(prefix)) {
			Core.wmsg(p, "이미 착용 중인 칭호입니다.");
			return;
		}
		
		api.setMainPrefix(playerKey, priority, prefix);
		
		Core.msg(p, "§f" + prefix + " §d칭호를 우선 순위 §f" + priority + "§d(으)로 착용했습니다.");
	}
	
	@SubCommandHandler(
			parent = "칭호",
			name = "해제",
			aliases = {"ㅎㅈ", "unwear"},
			additional = "<우선 순위>",
			minArgs = 1,
			usage = "칭호 착용을 해제합니다."
			)
	public void unWear(Player p, String[] args) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(p.getName());
		
		PrefixPlayer pp = api.getPlayerManager().getPrefixPlayer(playerKey);
		if(!pp.hasMainPrefix()) {
			Core.wmsg(p, "아직 칭호를 설정하지 않았습니다.");
			return;
		}
		
		Integer priority = NumberUtil.getInteger(args[0]);
		if(priority == null || priority < 1 || priority > api.getMaxMainPrefixCount()) {
			Core.wmsg(p, "우선 순위는 1부터 " + api.getMaxMainPrefixCount() + "까지의 정수만 입력 가능합니다.");
			return;
		}
		
		String prefix = pp.getMainPrefixes().get(priority);
		if(prefix == null) {
			Core.wmsg(p, "해당 우선 순위인 칭호가 존재하지 않습니다.");
			return;
		}
		
		api.removeMainPrefix(playerKey, prefix);
		
		Core.msg(p, "§f" + prefix + " §d칭호 착용를 해제했습니다.");
	}
	
	@SubCommandHandler(
			parent = "칭호",
			name = "착용목록",
			aliases = {"ㅊㅇㅁㄹ", "wearList"},
			additional = "(<페이지>) (<플레이어>)",
			usage = "사용 중인 칭호 목록을 확인합니다."
			)
	public void wearList(UCommandSender sender, String[] args, Command cmd) {
		if(args.length < 2 && sender.isConsole()) {
			cmd.sendUsage(sender, true);
			return;
		}
		
		String player = args.length < 2 ? sender.getName() : args[1];
		
		Integer page = args.length < 1 ? 1 : NumberUtil.getInteger(args[0]);
		if(page == null) {
			Core.wmsg(sender, "페이지는 정수만 입력 가능합니다.");
			return;
		}
		
		PlayerKey playerKey = PlayerKey.getPlayerKey(player);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		PrefixPlayer pp = api.getPlayerManager().getPrefixPlayer(playerKey);
		
		List<String> prefixes = api.getMainPrefixList(playerKey);
		if(prefixes.size() < 1) {
			Core.wmsg(sender, "착용 중인 칭호가 없습니다.");
			return;
		}
		
		int maxPage = (int) (Math.ceil(prefixes.size() / 7) + 1);
		if(page > maxPage) {
			Core.wmsg(sender, "페이지는 1부터 " + maxPage + "까지의 정수만 입력 가능합니다.");
			return;
		}
		
		List<Integer> pl = new ArrayList<>();
		pl.addAll(pp.getMainPrefixes().keySet());
		
		Core.nmsg(sender, "§d[ " + (sender.getName().equals(player) ? "" : player + "님의 ") +"착용 중인 칭호 목록 ( " + page + " / " + maxPage + " ) ]");
		for(int i = 0; i < 7; i++) {
			int num = (page -  1) * 7 + i;
			if(prefixes.size() <= num) break;
			
			Core.nmsg(sender, new ComponentBuilder("§d" + pl.get(i) + " ) §f" + prefixes.get(num))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/칭호 해제 " + pp.getMainPrefixPriority(prefixes.get(num))))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("클릭시 칭호를 착용 해제합니다.").create()))
					.create());
		}
	}
	
	@SubCommandHandler(
			parent = "칭호",
			name = "목록",
			aliases = {"ㅁㄹ", "list"},
			additional = "(<페이지>) (<플레이어>)",
			usage = "칭호 목록을 확인합니다."
			)
	public void list(UCommandSender sender, String[] args, Command cmd) {
		if(args.length < 2 && sender.isConsole()) {
			cmd.sendUsage(sender, true);
			return;
		}
		
		String player = args.length < 2 ? sender.getName() : args[1];
		
		Integer page = args.length < 1 ? 1 : NumberUtil.getInteger(args[0]);
		if(page == null) {
			Core.wmsg(sender, "페이지는 정수만 입력 가능합니다.");
			return;
		}
		
		PlayerKey playerKey = PlayerKey.getPlayerKey(player);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		PrefixPlayer pp = api.getPlayerManager().getPrefixPlayer(playerKey);
		
		List<String> prefixes = pp != null ? pp.getPrefixes() : api.getSQLManager().getPrefixes(playerKey);
		if(prefixes.size() < 1) {
			Core.wmsg(sender, "칭호를 가지고 있지 않습니다.");
			return;
		}
		
		int maxPage = (int) (Math.ceil(prefixes.size() / 7) + 1);
		if(page > maxPage) {
			Core.wmsg(sender, "페이지는 1부터 " + maxPage + "까지의 정수만 입력 가능합니다.");
			return;
		}
		
		Core.nmsg(sender, "§d[ " + (sender.getName().equals(player) ? "" : player + "님의 ") +"칭호 목록 ( " + page + " / " + maxPage + " ) ]");
		for(int i = 0; i < 7; i++) {
			int num = (page -  1) * 7 + i;
			if(prefixes.size() <= num) break;
			
			String pf = prefixes.get(num);
			
			if(sender.getName().equalsIgnoreCase(player)) {
				Core.nmsg(sender, new ComponentBuilder("§d" + (num + 1) + " ) §f" + pf)
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + (pp.isMainPrefix(pf) ? "칭호 해제 " + pp.getMainPrefixPriority(pf) : "칭호 착용 " + (num + 1))))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("클릭시 칭호를 착용" + (pp.isMainPrefix(pf) ? " 해제" : "") + "합니다.").create()))
						.create());
			} else {
				Core.nmsg(sender, "§d" + (num + 1) + " ) §f" + pf);
			}
		}
	}
	
	@SubCommandHandler(
			parent = "칭호",
			name = "확인",
			aliases = {"ㅎㅇ", "show"},
			additional = "(<플레이어>)",
			usage = "칭호를 확인합니다."
			)
	public void show(UCommandSender sender, String[] args, Command cmd) {
		if(args.length < 1 && !(sender instanceof Player)) {
			cmd.sendUsage(sender, true);
			return;
		}
		
		String player = args.length < 1 ? sender.getName() : args[0];
		
		PlayerKey playerKey = PlayerKey.getPlayerKey(player);
		if(playerKey == null) {
			Core.wmsg(sender, "존재하지 않는 플레이어입니다.");
			return;
		}
		
		PrefixPlayer pp = api.getPlayerManager().getPrefixPlayer(playerKey, true);
		if(pp == null || pp.getPrefixes().size() < 1 || !pp.hasMainPrefix()) {
			Core.wmsg(sender, "칭호를 설정하지 않은 플레이어입니다.");
			return;
		}
		
		Core.msg(sender, player + "§d님의 칭호: §f" + StringUtil.connectString(pp.getMainPrefixList(), ""));
	}
	
}