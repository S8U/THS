package su.plugin.gparty.bungee.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.gparty.bungee.GGPartyPlugin;
import su.plugin.gparty.bungee.api.GGPartyAPI;
import su.plugin.gparty.bungee.api.object.GParty;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UPlayerLoginEvent;
import su.plugin.core.common.api.event.c.player.UPlayerQuitEvent;
import su.plugin.core.common.api.player.PlayerKey;

public class ConnectListener implements UEventListener, Listener {
	
	private GGPartyAPI api = GGPartyPlugin.getApi();
	
	@UEventHandler
	public void onLogin(UPlayerLoginEvent e) {
		PlayerKey pk = e.getPlayer().getPlayerKey();
		if(api.getPlayerManager().existPartyPlayer(pk)) return;

		api.getPlayerManager().setPartyPlayer(pk, new GPartyPlayer(pk));
	}
	
	@EventHandler
	public void onConnect(ServerConnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		if(p.getServer() == null) return;

		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(p);
		GParty party = pp.getParty();
		if(party == null || party.getPlayers().size() < 1) return;
		else if(p.getServer().getInfo().equals(e.getTarget())) return;
		else if(pp.isMoving()) {
			pp.setMoving(false);
			return;
		} else if(party.isOwner(pp.getPlayerKey()) && party.getPlayers().size() > 0) {
			String targetName = e.getTarget().getName();
			if(api.isUseChannel()) {
				targetName = ChannelAPI.getChannelManager().getChannel(e.getTarget().getName()).getDisplayName();
			} else if(party.getPlayers().size() <  2) return;
			for(ProxiedPlayer ap : party.getOnlinePlayers()) {
				if(p.equals(ap)) continue;
				GPartyPlayer app = api.getPlayerManager().getPartyPlayer(ap);
				app.setMoving(true);

				ap.connect(e.getTarget());

				Core.msg(ap, "파티장을 따라 " + targetName + " 으로 이동했습니다.");
			}

			api.getPartyManager().sendParty(pp.getParty(), e.getTarget());

			Core.msg(p, "파티원들과 함께 " + targetName + " 으로 이동했습니다.");
			return;
		}

		e.setCancelled(true);

		Core.wmsg(p, "파티장만 채널을 이동할 수 있습니다.");
		Core.wmsg(p, "파티를 탈퇴하려면 /파티 탈퇴 명령어를 사용하세요.");
	}

	@UEventHandler
	public void onQuit(UPlayerQuitEvent e) {
		GPartyPlayer pp = api.getPlayerManager().getPartyPlayer(e.getPlayer().getPlayerKey());
		GParty party = pp.getParty();
		if(party == null) return;

		party.removePlayer(pp.getPlayerKey());

		party.bc("§f" + pp.getDisplayName() + " §c님께서 서버에서 퇴장하여 파티에서 탈퇴되었습니다.");

		if(party.isOwner(pp.getPlayerKey())) {
			if(party.getPlayers().size() < 2) {
				if(party.getPlayers().size() > 0) {
					Core.msg(party.getOnlinePlayers().get(0), "§c파티가 해체되었습니다.");

					party.setOwner(party.getPlayers().get(0));

					api.getPartyManager().sendPartyDelete(party.getOwner(), party.getOwnerPlayer().getServer().getInfo());
				}
			} else {
				GPartyPlayer np = api.getPlayerManager().getPartyPlayer(party.getPlayers().get(0));

				party.setOwner(np.getPlayerKey());

				party.bc("§a파티장이 " + np.getDisplayName() + " §a님께 위임되었습니다.");
			}
		} else if(party.getPlayers().size() < 1) {
			api.getPartyManager().sendPartyDelete(party.getOwner(), party.getOwnerPlayer().getServer().getInfo());
			Core.msg(party.getOwnerPlayer(), "§c파티가 해체되었습니다.");
		}

		api.getPlayerManager().removePartyPlayer(pp.getPlayerKey());
	}
	
}