package su.plugin.itemtools.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.itemtools.PermissionList;
import su.plugin.itemtools.api.ItemToolsAPI;

public class ItemToolsCommand implements UCommandListener {
	
	@CommandHandler(
			name = "도구",
			aliases = {"itemtools", "it"},
			additional = "<인챈트테이블 | 작업대 | 모루>",
			minArgs = 1,
			usage = "도구를 지급합니다."
			)
	public void itemtools(UCommandSender sender, String[] args, Command cmd) {
		Player target = sender.isConsole() ? null : (Player) sender.getPlatformSender();
		
		if(args.length > 1) {
			if(!sender.hasPermission(PermissionList.ITEMTOOLS_ADMIN)) {
				Core.wmsg(sender, cmd.getNoPermissionMessage());
				return;
			} else if((target = Bukkit.getPlayer(args[1])) == null) {
				Core.wmsg(sender, "접속 중이 아닌 플레이어입니다.");
			}
		} else if(target == null) {
			Core.wmsg(sender, "콘솔에서는 사용할 수 없습니다.");
			return;
		} else if(!args[0].equals("인챈트테이블") && !args[0].endsWith("작업대") && !args[0].equals("모루")) {
			cmd.sendUsage(sender, true);
			return;
		}
		
		target.getInventory().addItem(args[0].equals("인챈트테이블") ? ItemToolsAPI.getEnchantTool() : (args[0].equals("작업대") ? ItemToolsAPI.getWorkbenchTool() : ItemToolsAPI.getAnvilTool()));
		
		Core.msg(target, args[0] + "(이)가 지급되었습니다.");
		if(!sender.getPlatformSender().equals(target)) {
			Core.msg(sender, target.getName() + "님께 " + args[0] + "(을)를 지급했습니다.");
		}
	}

	@SubCommandHandler(
			parent = "도구",
			name = "방어력",
			additional = "<방어력>",
			minArgs = 1,
			usage = "해당 아이템에 방어력을 부여합니다."
	)
	public void itemtools_armour(Player p, String[] args) {
		ItemStack item = p.getItemOnCursor();
		if(item == null) {
			Core.wmsg(p, "손에 들고 있는 아이템이 없습니다.");
			return;
		}

		List<String> lore = item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();
		for(String line : lore) {
			if(line.startsWith(""))
		}

		p.updateInventory();


	}
	
}