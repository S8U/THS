package su.plugin.glogin.common.api.manager;

import java.util.HashMap;

import lombok.Getter;
import su.plugin.glogin.common.api.object.Account;
import su.plugin.core.common.api.player.PlayerKey;

@Getter
public class AccountManager {
	
	private HashMap<PlayerKey, Account> accounts = new HashMap<>();
	
	public void setAccount(PlayerKey playerKey, Account account) {
		accounts.put(playerKey, account);
	}
	
	public boolean hasAccount(PlayerKey playerKey) {
		return accounts.containsKey(playerKey);
	}
	
	public Account getAccount(PlayerKey playerKey) {
		return accounts.get(playerKey);
	}
	
	public void removeAccount(PlayerKey playerKey) {
		accounts.remove(playerKey);
	}
	
}