package su.plugin.core.bukkit.api.lib;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultHandler {
	
	@Getter
    private static Permission permission;
	@Getter
    private static Economy economy;
	@Getter
    private static Chat chat;
	
    public static boolean setupPermission() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
            permission = rsp.getProvider();
        }
        return rsp != null;
    }

    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
        return rsp != null;
    }

	public static boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            chat = rsp.getProvider();
        }
        return rsp != null;
    }
	
	public static double getMoney(Player p) {
		return getMoney(p.getName());
	}
	
	public static double getMoney(String name) {
		return getEconomy().hasAccount(name) ? getEconomy().getBalance(name) : 0;
	}
	
	public static boolean hasMoney(Player p, double money) {
		return hasMoney(p.getName(), money);
	}
	
	public static boolean hasMoney(String name, double money) {
		return getEconomy().hasAccount(name) && getEconomy().has(name, money);
	}
	
	public static void giveMoney(Player p, double money) {
		giveMoney(p.getName(), money);
	}
	
	public static void giveMoney(String name, double money) {
		getEconomy().depositPlayer(name, money);
	}
	
	public static boolean takeMoney(Player p, double money) {
		return takeMoney(p.getName(), money);
	}
	
	public static boolean takeMoney(String name, double money) {
		return getEconomy().withdrawPlayer(name, money).transactionSuccess();
	}

}