package su.plugin.ability.api.manager;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.event.GameStoppedEvent;
import su.plugin.ability.api.event.WinEvent;
import su.plugin.ability.api.object.GameMap;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.channel.bukkit.api.KChannelAPI;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.lib.VaultHandler;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class GameManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private long startTime;
	
	@Getter
	private GameState gameState = GameState.WAITING;
	
	@Setter
	@Getter
	private boolean gameStarted, autoMode, teleportedInMap, teleportedAll;

	public void setGameState(GameState gameState) {
		this.gameState = gameState;

		if(api.isUseChannel()) {
			KChannelAPI.getCurrentChannel().setETC("game_state", gameState.getProgress());
			KChannelAPI.updateThisChannelInfo();
		}
	}

	public long getPlayTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	public boolean startGame(boolean auto) {
		if(isGameStarted()) return false;
		
		gameStarted = true;
		autoMode = auto;
		startTime = System.currentTimeMillis();

		if(api.getGUIManager().getGameStartVoteGUI() != null) {
			api.getGUIManager().getGameStartVoteGUI().closeAll();
		}

		if(api.isUseMapVote()) {
			int mapCount = 0;
			GameMap gameMap = null;

			for(GameMap map : api.getMapManager().getMaps().values()) {
				int count = api.getVoteManager().getMapVoteCount(map);
				if(count > mapCount) {
					mapCount = count;
					gameMap = map;
				}
			}

			if(mapCount < api.getPlayerManager().getOnlineJoinedPlayers().size() - api.getVoteManager().getMapVote().size()) {
				gameMap = null;
			}

			if(gameMap != null) {
				api.getMapManager().setPlayingMap(gameMap);

				Core.cbc(ChatColor.DARK_AQUA, "§b투표로 인해 맵이 §f" + gameMap.getName() + "§b으로 설정되었습니다.");

				api.getVoteManager().getMapVote().clear();
			}
		}

		for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
			api.getTaskManager().stopEliminateTask(gp.getPlayerKey());

			gp.setRedrawCount(api.getRedrawCount());
		}

		api.getGameManager().setGameState(GameState.PREPARING);
		
		api.getTaskManager().runProjectilePassTask();
		
		if(api.isUseSideBar()) {
			api.getTaskManager().runSideBarUpdateTask();
		}
		
		if(auto) {
			api.getTaskManager().runAutoStartTask(0);
			return true;
		}
		api.getTaskManager().runNormalStartTask(0);
		return true;
	}
	
	public boolean stopGame() {
		if(!isGameStarted()) return false;
		
		gameStarted = false;
		teleportedAll = false;
		teleportedInMap = false;
		boolean temp = autoMode;
		autoMode = false;
		startTime = 0;
		api.setFirstBlood(true);
		
		api.setInvincibilityTime(false);
		
		api.getGameManager().setGameState(GameState.WAITING);
		
		api.getTaskManager().stopNormalStartTask();
		api.getTaskManager().stopGameStartCountTask();
		api.getTaskManager().stopDrawAbilityTask();
		api.getTaskManager().stopInvincbilityTask();
		api.getTaskManager().stopSideBarUpdateTask();
		
		api.getTaskManager().stopSupplyTask();
		api.getTaskManager().stopLocationNotifyTask();
		api.getTaskManager().stopProjectilePassTask();
		
		api.getTaskManager().stopAutoStartTask();
		api.getTaskManager().stopDrawSkipTask();
		api.getTaskManager().stopTeleportAllTask();
		
		api.getSupplyManager().getSupplyLogs().clear();

		if(api.isUseWaitingQuickBar()) {
			api.getBarManager().getWaitingQuickBar().update();
		}
		if(api.isUseGameStartVote()) {
			api.getGUIManager().updateGameStartVoteGUI();
		}
		if(api.isUseMapVote()) {
			api.getGUIManager().updateMapVoteGUI();
		}

		for(GamePlayer gp : api.getPlayerManager().getAllPlayers()) {
			gp.clearAbility();
			gp.setEliminate(false);
			gp.setReconnectEliminate(false);
			gp.setReconnectEliminateMessage(false);
			gp.setLastKillTime(0);
			gp.setLastKillType(null);

			api.getTaskManager().stopEliminateTask(gp.getPlayerKey());

			if(gp.isOnline()) {
				gp.getPlayer().setLevel(0);
				if(api.isUseWaitingQuickBar()) {
					api.getBarManager().getWaitingQuickBar().setTo(gp.getPlayer());
				} else {
					gp.clearInventory();
				}

				gp.getPlayer().setGameMode(Bukkit.getDefaultGameMode());
				
				if(gp.isWatchMode()) {
					Bukkit.getScheduler().runTaskLater(AbilityPlugin.getInstance(), () -> gp.toggleWatchMode(false, true), 2);
				} else {
					gp.setJoin(true);
				}
				
				if(temp && api.getMapManager().getSpawn() != null) {
					KCore.teleport(gp.getPlayer(), api.getMapManager().getSpawn());
				}
			}
		}
		
		api.getBarManager().updateSideBarAllPlayer();
		api.getBarManager().getBossBar().clearBar();
		
		Bukkit.getPluginManager().callEvent(new GameStoppedEvent());
		return true;
	}
	
	public boolean canFinish() {
		if(!api.getGameManager().isGameStarted() || !api.getGameManager().isAutoMode() || api.getGameManager().getGameState().getProgress() < GameState.PLAYING.getProgress() || api.getGameManager().getGameState().equals(GameState.END) || getPlayTime() < api.getWinMinCount() * 1000) return false;
		
		return api.getPlayerManager().getTeamAmount() < 2;
	}
	
	public boolean finish() {
		if(!canFinish()) return false;
		
		List<GamePlayer> winners = api.getPlayerManager().getOnlineJoinedPlayers();
		
		WinEvent event = new WinEvent(winners, api.getWinMoney(api.getPlayerManager().getEliminatedPlayers().size() + 1));
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return false;
		
		api.getGameManager().setGameState(GameState.END);
		
		StringBuilder builder = new StringBuilder();
		for(GamePlayer gp : winners) {
			builder.append(builder.length() < 1 ? gp.getDisplayName(): ChatColor.WHITE + ", §f" + gp.getDisplayName());
			VaultHandler.giveMoney(gp.getPlayer(), Math.round(event.getWinMoney() / winners.size()));
			Core.cmsg(gp.getPlayer(), ChatColor.DARK_AQUA, "게임에서 승리했습니다. 축하합니다!");
			Core.cmsg(gp.getPlayer(), ChatColor.DARK_AQUA, "§a+ " + Math.round(event.getWinMoney() / winners.size()) + "원 (우승)");
		}
		
		Core.cbc(ChatColor.DARK_GREEN, builder.toString() + " 님께서 승리했습니다. 축하합니다!");
		return true;
	}
	
}