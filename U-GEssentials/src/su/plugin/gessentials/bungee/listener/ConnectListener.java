package su.plugin.gessentials.bungee.listener;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.category.ListeningChannel;
import su.plugin.gessentials.bungee.api.object.EChannel;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.gessentials.bungee.api.object.ban.EBan;
import su.plugin.gessentials.bungee.api.object.ban.EIpBan;
import su.plugin.gessentials.bungee.api.object.ban.EPlayerKeyBan;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UPlayerLoginEvent;
import su.plugin.core.common.api.event.c.player.UPlayerQuitEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.StringUtil;

public class ConnectListener implements UEventListener, Listener {
	
	private GGEssentialsAPI api = GGEssentialsPlugin.getApi();
	
	@UEventHandler
	public void onUPlayerLogin(UPlayerLoginEvent e) {
		String name = e.getPlayer().getName();
		
		String ip = e.getPlayer().getIp();
		
		PlayerKey playerKey = PlayerKey.getPlayerKey(name);
		
		EPlayer ep = api.getEPlayer(playerKey);
		if(ep == null) {
			ep = new EPlayer(playerKey);
		}
		api.getPlayerManager().setEPlayer(ep);
		
		ep.setName(name);
		ep.setIp(ip);
		ep.setLastLogin(System.currentTimeMillis());
		
		api.getSQLManager().savePlayer(api.getPlayerManager().getEPlayer(playerKey));
		
		//
		
		EBan ban = null;
		if(api.isLoadAllBanData()) {
			ban = api.getBanManager().getBanData(api.getBanManager().hasBanData(ep.getPlayerKey() + "") ? ep.getPlayerKey() + "" : ip);
		} else {
			ban = api.getSQLManager().getPlayerKeyBanData(playerKey);
			if(ban == null) {
				ban = api.getSQLManager().getIpBanData(ip);
			}
		}
		
		if(ban != null) {
			if(ban.isTimeBan() && !ban.isEffective()) {
				if(ban instanceof EPlayerKeyBan) {
					api.getBanManager().deleteBanData(playerKey.getId() + "");
					api.getSQLManager().deletePlayerKeyBanData(playerKey);
				} else {
					String bip = ((EIpBan) ban).getIp();
					api.getBanManager().deleteBanData(bip);
					api.getSQLManager().deleteIpBanData(bip);
				}
			} else {
				String km = "\n\n\n\n\n\n" + api.getKickServerMark();
				String reason = ban.getReason() + "\n[처리자: " + (ban.getAdminId() == -2 ? api.getWarningManager().getWarningDisplayName() : (ban.getAdminId() == -1 ? "콘솔" : Core.getDisplayName(PlayerKey.getPlayerKey(ban.getAdminId())))) +"]";
				
				if(ban.isTimeBan()) {
					km = km.substring(2, km.length());
					reason += "\n(차단 해제 시간: " + StringUtil.buildDateString(ban.getUnBanTime(), "yyyy년 MM월 dd일 a h시 mm분 ss초") + ")";
				}
				
				reason += km;
				e.setDisallow(true);
				e.setDisallowReason(reason);
				return;
			}
		}
		
		//
		
		if(api.isUseLobby() && !api.getChannelManager().canJoinToLobby()) {
			e.setDisallow(true);
			e.setDisallowReason("접속 가능한 로비가 없습니다. 잠시 후에 다시 시도해주세요.");
		}
		
		//

		if(ep.isMuted() && ep.getMute().isTimeMute()) {
			ep.getMute().startUnMuteTask();
		} else if(ep.getMute() != null && !ep.getMute().isEffective()) {
			ep.unMute(null);
		}
		
		Core.log(e.getPlayer().getDisplayName() + (e.getPlayer().hasDisplayName() ? "(" + e.getPlayer().getName() + ")" : "") + " 님께서 접속했습니다. (" + ip + ")");
	}
	
	@UEventHandler
	public void onQuit(UPlayerQuitEvent e) {
		EPlayer ep = api.getPlayerManager().getEPlayer(e.getPlayer().getPlayerKey());
		
		ep.setLastLogout(System.currentTimeMillis());
		ep.setConnected(false);

		if(ep.isMuted()) {
			ep.getMute().stopUnMuteTask();
		}
		
		api.getSQLManager().savePlayer(ep);
	}
	
	@EventHandler
	public void onServerConnect(ServerConnectEvent e) { // Lobby
		ProxiedPlayer p = e.getPlayer();
		EPlayer ep = api.getPlayerManager().getEPlayer(p);

		if(ep.isConnected()) return;

		ep.setConnected(true);

		Channel channel = api.getChannelManager().getLobbyGroup().getOptimizeChannel(p.getName());
		if(channel == null || e.getTarget().getName().equals(channel.getName())) return;


		e.setTarget(ProxyServer.getInstance().getServerInfo(channel.getName()));
	}
	
	@EventHandler
	public void onServerConnected(ServerConnectedEvent e) {
		ProxiedPlayer p = e.getPlayer();
		EPlayer ep = api.getPlayerManager().getEPlayer(p);
		
		ep.getPrefixerPrefixes().clear();
		ep.setPermissionPrefix(null);
		
		ProxyServer.getInstance().getScheduler().schedule(GGEssentialsPlugin.getInstance(), () -> {
			EChannel channel = api.getChannelManager().getEChannel(e.getServer().getInfo().getName());
			if(channel == null || ep.getListeningChannel() == channel.getListeningChannel()) return;

			ep.setListeningChannel(channel.getListeningChannel());

			Core.nmsg(p, "§b듣기 채널이 " + (ep.getListeningChannel() == ListeningChannel.GLOBAL ? "전체" : "채널") + "로 변경되었습니다.");
		}, 500, TimeUnit.MILLISECONDS);
	}
	
}