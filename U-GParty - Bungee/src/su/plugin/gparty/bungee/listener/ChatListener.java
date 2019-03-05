package su.plugin.gparty.bungee.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.gparty.bungee.GGPartyPlugin;
import su.plugin.gparty.bungee.api.GGPartyAPI;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class ChatListener implements Listener {
	
	private GGPartyAPI api = GGPartyPlugin.getApi();
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChat(ChatEvent e) {
		if(e.isCommand()) return;

		ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		if(!pp.isPartyChat()) return;

		PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

		if(api.isUseGLogin() && (!GGLoginAPI.getAccountManager().hasAccount(playerKey) || !GGLoginAPI.getAccountManager().getAccount(playerKey).isLogin())) return;


		e.setCancelled(true);

		String pf = "";
		String pm = "";
		String channel = p.getServer().getInfo().getName();

		if(api.isUseGEssentials()) {
			EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p);

			pf = ep.hasPrefixerPrefix() ? ep.getPrefixerPrefix() : "";
			pm = ep.hasPermissionPrefix() ? ep.getPermissionPrefix() : "";
		}

		if(api.isUseChannel()) {
			channel = ChannelAPI.getChannelManager().getChannel(p.getServer().getInfo().getName()).getDisplayName();
		}

		for(ProxiedPlayer app : pp.getParty().getOnlinePlayers()) {
			Core.nmsg(app, "§a[파티 채팅] " + pf + pm + Core.getUPlayerByPlatformPlayer(app).getDisplayName() + ": " + e.getMessage());
		}

		for(GPartyPlayer app : api.getPlayerManager().getPartyChatSpyPlayers()) {
			if(pp.getPlayer() == null) continue;

			Core.nmsg(app.getPlayer(), "§a<PChatSpy> [" + channel + "] " + pf + pm + pp.getDisplayName() + ": " + e.getMessage());
		}
	}
	
}