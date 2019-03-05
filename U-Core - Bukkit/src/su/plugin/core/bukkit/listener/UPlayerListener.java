package su.plugin.core.bukkit.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.enumeration.NMSVersion;
import su.plugin.core.bukkit.api.player.KPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.c.player.UNewPlayerJoinEvent;
import su.plugin.core.common.api.event.c.player.UNewPlayerLoginEvent;
import su.plugin.core.common.api.event.c.player.UPlayerChatEvent;
import su.plugin.core.common.api.event.c.player.UPlayerJoinEvent;
import su.plugin.core.common.api.event.c.player.UPlayerLoginEvent;
import su.plugin.core.common.api.event.c.player.UPlayerQuitEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.DebugUtil;

public class UPlayerListener implements Listener {

	@Getter
	private List<String> newPlayers = new ArrayList<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		
		//
		DebugUtil.log(p.getName() + ": PlayerKey 처리 시작");
		long now = System.currentTimeMillis();
		
		PlayerKey playerKey = null;
		
		UUID uuid = Core.getUUID(p);
		
		if(KCore.getNMSVersion().isAfter(NMSVersion.v1_6_R2) && uuid != null) {
			playerKey = Core.getSQLManager().getPlayerKey(uuid);
		}

		boolean newPlayer;

		if(newPlayer = (playerKey == null && (playerKey = Core.getSQLManager().getPlayerKey(p.getName())) == null)) {
			playerKey = Core.getSQLManager().createPlayerKey(p.getName(), uuid, Bukkit.getOnlineMode());
		}
		
		if(Core.getSQLManager().isUsePlayerKeyUpload()) {
			playerKey.updatePlayerKey(p.getName(), uuid, Bukkit.getOnlineMode());
		}
		
		DebugUtil.log(p.getName() + ": PlayerKey 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");
		//
		
		//
		DebugUtil.log(p.getName() + ": KPlayer 처리 시작");
		now = System.currentTimeMillis();
		
		KPlayer kp = new KPlayer(playerKey, p);
		
		String displayName = Core.getSQLManager().getDisplayName(playerKey);
		if(displayName != null) {
			kp.setDisplayName(displayName, false);
		}
		
		Core.getUPlayerManager().setUPlayer(playerKey, kp);
		
		DebugUtil.log(p.getName() + ": KPlayer 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");
		//
		
		//
		DebugUtil.log(p.getName() + ": 옵션 불러오기 시작");
		now = System.currentTimeMillis();
		
		Core.getOptionSQLManager().loadPlayerOptions(playerKey);

		DebugUtil.log(p.getName() + ": 옵션 목록: " + Core.getOptionManager().getPlayerOptions(playerKey));

		DebugUtil.log(p.getName() + ": 옵션 불러오기 완료 (" + (System.currentTimeMillis() - now) + "ms)");
		//
		
		//
		DebugUtil.log(p.getName() + ": UPlayerLoginEvent 시작");
		now = System.currentTimeMillis();
		
		UPlayerLoginEvent event = new UPlayerLoginEvent(kp);
		Core.getUEventManager().callEvent(event);
		
		if(event.isDisallow()) {
			e.disallow(Result.KICK_OTHER, event.getDisallowReason());
		}
		
		DebugUtil.log(p.getName() + ": UPlayerLoginEvent 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");
		//

		//
		if(newPlayer) {
			DebugUtil.log(p.getName() + " UNewPlayerLoginEvent 시작");
			now = System.currentTimeMillis();

			UNewPlayerLoginEvent nevent = new UNewPlayerLoginEvent(kp);
			Core.getUEventManager().callEvent(nevent);

			if(nevent.isDisallow()) {
				e.disallow(Result.KICK_OTHER, nevent.getDisallowReason());
			}

			DebugUtil.log(p.getName() + " UNewPlayerLoginEvent 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");

			newPlayers.add(p.getName());
		}
		//
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		PlayerKey playerKey = PlayerKey.getPlayerKey(p.getName());

		if (Core.getSQLManager().isUseConsoleLog()) {
			Core.log(p.getName() + " 님이 접속했습니다. (PlayerKey: id=" + playerKey + ", name=" + playerKey.getName() + ", uuid=" + playerKey.getUuid() + ", onlineMode=" + playerKey.isOnlineMode() + ")");
		}

		//
		DebugUtil.log(p.getName() + ": UPlayerJoinEvent 시작");
		long now = System.currentTimeMillis();

		UPlayerJoinEvent event = new UPlayerJoinEvent(Core.getUPlayer(playerKey), e.getJoinMessage());
		Core.getUEventManager().callEvent(event);

		e.setJoinMessage(event.getJoinMessage());

		DebugUtil
				.log(p.getName() + ": UPlayerJoinEvent 종료 (" + (System.currentTimeMillis() - now) + ")");
		//

		//
		if(newPlayers.contains(p.getName())) {
			newPlayers.remove(p.getName());

			DebugUtil.log(p.getName() + ": UNewPlayerJoinEvent 시작");
			now = System.currentTimeMillis();

			Core.getUEventManager().callEvent(new UNewPlayerJoinEvent(Core.getUPlayer(playerKey)));

			DebugUtil.log(p.getName() + ": UNewPlayerJoinEvent 종료 (" + (System.currentTimeMillis() - now) + ")");
		}
		//
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		PlayerKey playerKey = PlayerKey.getPlayerKey(p.getName());
		
		//
		DebugUtil.log(p.getName() + ": UPlayerQuitEvent 시작");
		long now = System.currentTimeMillis();
		
		UPlayerQuitEvent event = new UPlayerQuitEvent(Core.getUPlayer(playerKey), e.getQuitMessage());
		Core.getUEventManager().callEvent(event);
		
		e.setQuitMessage(event.getQuitMessage());
		
		DebugUtil.log(p.getName() + ": UPlayerQuitEvent 종료 (" + (System.currentTimeMillis() - now) + ")");
		//
		
		Core.getOptionManager().deletePlayerOptions(playerKey, false);
		
		Core.getUPlayerManager().removeUPlayer(playerKey);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e) {
		UPlayer up = Core.getUPlayer(e.getPlayer().getName());

		//

		DebugUtil.log(up.getName() + ": UPlayerChatEvent 시작");
		long now = System.currentTimeMillis();

		UPlayerChatEvent event = new UPlayerChatEvent(up, e.getMessage());
		Core.getUEventManager().callEvent(event);

		e.setCancelled(event.isCancelled());

		DebugUtil.log(up.getName() + ": UPlayerChatEvent 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");

		//

		if(event.isCancelled() || up == null || !up.hasDisplayName()) return;
		e.getPlayer().setDisplayName(up.getDisplayName());
	}
	
}