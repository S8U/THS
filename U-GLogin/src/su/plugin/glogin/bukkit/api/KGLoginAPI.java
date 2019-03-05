package su.plugin.glogin.bukkit.api;

import su.plugin.glogin.common.api.GLoginAPI;
import su.plugin.glogin.common.api.manager.AccountManager;
import su.plugin.glogin.common.api.manager.SQLManager;

public class KGLoginAPI extends GLoginAPI {
	
	public void init() {
		accountManager = new AccountManager();
		SQLManager = new SQLManager();
	}
	
}