
package su.plugin.core.bungee.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import su.plugin.core.bungee.GCorePlugin;
import su.plugin.core.bungee.api.GCore;
import su.plugin.core.bungee.api.player.GPlayer;
import su.plugin.core.bungee.api.task.PluginMessageTask;
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
import su.plugin.core.common.api.util.NotDuplicatedArrayList;

public class UPlayerListener implements Listener {
	
	@Getter
	private List<String> connected = new ArrayList<>();

	@Getter
	private List<String> newPlayers = new NotDuplicatedArrayList<>();
	
	@EventHandler
	public void onLogin(LoginEvent e) {
		String name = e.getConnection().getName();

		if (!name.matches(Core.getAllowNicknameRegex())) {
			e.setCancelled(true);
			e.setCancelReason("사용할 수 없는 닉네임입니다.");

			DebugUtil.log("허용되지 않은 닉네임: " + name);

			return;
		}
		
		//
		DebugUtil.log(name + " PlayerKey 처리 시작");
		long now = System.currentTimeMillis();
		
		PlayerKey playerKey = null;
		
		UUID uuid = Core.getUUID(e.getConnection());
		
		if(uuid != null) {
			playerKey = Core.getSQLManager().getPlayerKey(uuid);
		}

		boolean newPlayer;

		if(newPlayer = (playerKey == null && (playerKey = Core.getSQLManager().getPlayerKey(name)) == null)) {
			playerKey = Core.getSQLManager().createPlayerKey(name, uuid, GCore.getOnlineMode(e.getConnection()));
		}
		
		if(Core.getSQLManager().isUsePlayerKeyUpload()) {
			playerKey.updatePlayerKey(name, uuid, GCore.getOnlineMode(e.getConnection()));
		}
		
		DebugUtil.log(name + " PlayerKey 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");
		//

		//
		DebugUtil.log(name + " GPlayer 처리 시작");
		now = System.currentTimeMillis();
		
		GPlayer gp = new GPlayer(playerKey, name, e.getConnection().getAddress().getAddress().getHostAddress());
		
		String displayName = Core.getSQLManager().getDisplayName(playerKey);
		if(displayName != null) {
			gp.setDisplayName(displayName, false);
		}
		
		Core.getUPlayerManager().setUPlayer(playerKey, gp);
		
		DebugUtil.log(name + " GPlayer 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");
		//
		
		//
		DebugUtil.log(name + " 옵션 불러오기 시작");
		now = System.currentTimeMillis();
		
		Core.getOptionSQLManager().loadPlayerOptions(playerKey);
		
		DebugUtil.log(name + " 옵션 불러오기 완료 (" + (System.currentTimeMillis() - now) + "ms)");
		//
		
		//
		DebugUtil.log(name + " UPlayerLoginEvent 시작");
		now = System.currentTimeMillis();

		UPlayerLoginEvent event = new UPlayerLoginEvent(gp);
		Core.getUEventManager().callEvent(event);

		if(event.isDisallow()) {
			e.setCancelled(true);
			e.setCancelReason(event.getDisallowReason());
		}
		
		DebugUtil.log(name + " UPlayerLoginEvent 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");
		//

		//
		if(newPlayer) {
			DebugUtil.log(name + " UNewPlayerLoginEvent 시작");
			now = System.currentTimeMillis();

			UNewPlayerLoginEvent nevent = new UNewPlayerLoginEvent(gp);
			Core.getUEventManager().callEvent(nevent);

			if(nevent.isDisallow()) {
				e.setCancelled(true);
				e.setCancelReason(nevent.getDisallowReason());
			}

			DebugUtil.log(name + " UNewPlayerLoginEvent 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");

			newPlayers.add(name.toLowerCase());

			if(Core.getSQLManager().isStableNewPlayerBukkitHandling()) {
				Core.getSQLManager().addNewPlayerHandle(playerKey);
			}
		}
		//
	}
	
	@EventHandler
	public void onJoin(ServerConnectedEvent e) {
		ProxiedPlayer p = e.getPlayer();

		PlayerKey playerKey = PlayerKey.getPlayerKey(p.getName());
		GPlayer gp = (GPlayer) Core.getUPlayer(playerKey);

		if (gp.hasDisplayName()) {
			p.setDisplayName(gp.getDisplayName());
		}

		if(connected.contains(p.getName().toLowerCase())) return;
		connected.add(p.getName().toLowerCase());

		gp.setProxiedPlayer(p);
		
		if(Core.getSQLManager().isUseConsoleLog()) {
			Core.log(p.getName() + " 님이 접속했습니다. (PlayerKey: id=" + playerKey + ", name=" + playerKey.getName() + ", uuid=" + playerKey.getUuid() + ", onlineMode=" + playerKey.isOnlineMode() + ")");
		}
		
		//
		DebugUtil.log(p.getName() + ": UPlayerJoinEvent 시작");
		long now = System.currentTimeMillis();

		Core.getUEventManager().callEvent(new UPlayerJoinEvent(gp, null));

		DebugUtil.log(p.getName() + ": UPlayerJoinEvent 종료 (" + (System.currentTimeMillis() - now) + "ms)");
		//

		//
		if(newPlayers.contains(p.getName().toLowerCase()) || (Core.getSQLManager().isStableNewPlayerBukkitHandling() && Core.getSQLManager().existsNewPlayerHandle(playerKey))) {
			DebugUtil.log(p.getName() + ": NewPlayerJoin 패킷 전송 시작");
			now = System.currentTimeMillis();

			ByteArrayDataOutput out = ByteStreams.newDataOutput();

			out.writeUTF("NewPlayerJoin");
			out.writeUTF(p.getName());

			new PluginMessageTask(GCorePlugin.getInstance(), e.getServer().getInfo(), "ucore:main", out.toByteArray()).runAsync();

			DebugUtil.log(p.getName() + ": NewPlayerJoin 패킷 전송 종료 (" + (System.currentTimeMillis() - now) + ")");

			newPlayers.remove(p.getName().toLowerCase());
			//

			DebugUtil.log(p.getName() + ": UNewPlayerJoinEvent 시작");
			now = System.currentTimeMillis();

			Core.getUEventManager().callEvent(new UNewPlayerJoinEvent(gp));

			DebugUtil.log(p.getName() + ": UNewPlayerJoinEvent 종료 (" + (System.currentTimeMillis() - now) + "ms)");
		}
		//
	}
	
	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		
		connected.remove(p.getName().toLowerCase());
		
		if(p == null) return;
		
		PlayerKey pk = PlayerKey.getPlayerKey(p.getName());
		
		//
		DebugUtil.log(p.getName() + ": UPlayerQuitEvent 시작");
		long now = System.currentTimeMillis();
		
		Core.getUEventManager().callEvent(new UPlayerQuitEvent(Core.getUPlayer(pk), null));
		
		DebugUtil.log(p.getName() + ": UPlayerQuitEvent 종료 (" + (System.currentTimeMillis() - now) + ")");
		//
		
		Core.getUPlayerManager().removeUPlayer(pk);
		
		Core.getOptionManager().deletePlayerOptions(pk, false);
	}

	@EventHandler
	public void onChat(ChatEvent e) {
		if(e.isCommand()) return;

		UPlayer up = Core.getUPlayerByPlatformPlayer(e.getSender());

		DebugUtil.log(up.getName() + ": UPlayerChatEvent 시작");
		long now = System.currentTimeMillis();

		UPlayerChatEvent event = new UPlayerChatEvent(up, e.getMessage().substring(1));
		Core.getUEventManager().callEvent(event);

		e.setCancelled(event.isCancelled());

		DebugUtil.log(up.getName() + ": UPlayerChatEvent 처리 완료 (" + (System.currentTimeMillis() - now) + "ms)");
	}
	
}