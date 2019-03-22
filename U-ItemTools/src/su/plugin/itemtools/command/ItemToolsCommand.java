package su.plugin.itemtools.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.itemtools.PermissionList;
import su.plugin.itemtools.api.ItemToolsAPI;

public class ItemToolsCommand implements UCommandListener {
	
	@CommandHandler(
			name = "도구",
			aliases = {"itemtools", "it"},
			additional = "<인챈트테이블 | 작업대 | 모루>",
			usage = "도구를 지급합니다."
			)
	public void itemtools(UCommandSender sender, String[] args, Command cmd) {
		Player target = sender.isConsole() ? null : (Player) sender.getPlatformSender();

		if(args.length < 1) {
			Core.nmsg(sender, "§6§l[ U-ItemTools ]");

			cmd.sendUsageIfHasPermission(sender, false);
			for(SubCommand sc : Core.getCommandManager().getSubCommands("도구", 1)) {
				sc.sendUsageIfHasPermission(sender, false);
			}
			return;
		}

		else if(args.length > 1) {
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
			Core.msg(sender, target.getName() + " 님께 " + args[0] + "(을)를 지급했습니다.");
		}
	}

	@SubCommandHandler(
			parent = "도구",
			name = "방어력",
			additional = "<방어력>",
			minArgs = 1,
			permission = "itemtools.admin",
			usage = "해당 아이템에 추가 방어력을 설정합니다."
	)
	public void itemtools_armour(Player p, String[] args) {
		ItemStack item = p.getItemInHand();
		if(item == null) {
			Core.wmsg(p, "손에 들고 있는 아이템이 없습니다.");
			return;
		}

		Integer armour = NumberUtil.getInteger(args[0]);
		if(armour == null) {
			Core.wmsg(p, "정수만 입력 가능합니다.");
			return;
		}

		String text = "§r§3§r§9추가 방어력: §f" + armour;

		List<String> lore = item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();

		boolean change = false;
		for (int i = 0; i < lore.size(); i++) {
			String line = lore.get(i);

			if(line.startsWith("§r§3§r§9추가 방어력: §f")) {
				lore.remove(i);
				lore.add(i, text);

				change = true;
			}
		}

		if(!change) {
			lore.add(text);
		}

		p.setItemInHand(new ItemBuilder(item).clearLore().lore(lore).build());

		p.updateInventory();

		Core.msg(p, "손에 들고 있는 아이템의 추가 방어력을 " + armour + "로 설정했습니다.");
	}

	@SubCommandHandler(
			parent = "도구",
			name = "공격력",
			additional = "<공격력>",
			minArgs = 1,
			permission = "itemtools.admin",
			usage = "해당 아이템의 추가 공격력을 설정합니다."
	)
	public void itemtools_attack(Player p, String[] args) {
		ItemStack item = p.getItemInHand();
		if(item == null) {
			Core.wmsg(p, "손에 들고 있는 아이템이 없습니다.");
			return;
		}

		Integer ad = NumberUtil.getInteger(args[0]);
		if(ad == null) {
			Core.wmsg(p, "정수만 입력 가능합니다.");
			return;
		}

		String text = "§r§3§r§c추가 공격력: §f" + ad;

		List<String> lore = item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();

		boolean change = false;
		for (int i = 0; i < lore.size(); i++) {
			String line = lore.get(i);

			if(line.startsWith("§r§3§r§c추가 공격력: §f")) {
				lore.remove(i);
				lore.add(i, text);

				change = true;
			}
		}

		if(!change) {
			lore.add(text);
		}

		p.setItemInHand(new ItemBuilder(item).clearLore().lore(lore).build());

		p.updateInventory();

		Core.msg(p, "손에 들고 있는 아이템의 추가 공격력을 " + ad + "로 설정했습니다.");
	}
	
}