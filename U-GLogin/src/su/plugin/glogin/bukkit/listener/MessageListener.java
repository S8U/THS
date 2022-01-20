package su.plugin.glogin.bukkit.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import su.plugin.glogin.bukkit.KGLoginPlugin;
import su.plugin.glogin.bukkit.api.KGLoginAPI;
import su.plugin.glogin.bukkit.api.event.LoginEvent;
import su.plugin.glogin.bukkit.api.event.LogoutEvent;
import su.plugin.glogin.common.api.object.Account;
import su.plugin.core.common.api.player.PlayerKey;

public class MessageListener implements PluginMessageListener {
	
	private KGLoginAPI api = KGLoginPlugin.getApi();
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(!channel.equals("uglogin:main")) return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String task = in.readUTF();
		
		if(task.equals("Login")) {
			PlayerKey playerKey = PlayerKey.getPlayerKey(in.readInt());
			boolean login = in.readBoolean();
			
			if(playerKey == null) return;
			
			Bukkit.getScheduler().runTaskAsynchronously(KGLoginPlugin.getInstance(), () -> {
				Account account = api.getAccountManager().getAccount(playerKey);
				if(account == null) {
					account = api.getSQLManager().getAccount(playerKey);
				}
				
				account.setLogin(login);
				
				api.getAccountManager().setAccount(playerKey, account);
				
				if(login) {
					Bukkit.getPluginManager().callEvent(new LoginEvent(Bukkit.getPlayer(playerKey.getName())));
				} else {
					Bukkit.getPluginManager().callEvent(new LogoutEvent(Bukkit.getPlayer(playerKey.getName())));
				}
			});
		}
	}

}