package su.plugin.ability.api.task.auto;

import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.event.GameStartedEvent;
import su.plugin.ability.api.object.GameMap;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class AutoStartTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private int count;
	
	private String startMessage = "잠시 후 게임이 시작됩니다.";
	private String countMessage = "초 후 게임이 시작됩니다.";

	public AutoStartTask() {
		super(AbilityPlugin.getInstance());
	}
	
	@Override
	public void run() {
		count++;
		if(count > 9) {
			GameMap map = api.getMapManager().getPlayingMap() == null ? api.getMapManager().getRandomMap() : api.getMapManager().getPlayingMap();
			if(map == null) {
				Core.cbc(ChatColor.RED, "§c설정된 맵이 없어 게임이 종료됩니다.");
				api.getGameManager().stopGame();
				return;
			}
			api.getMapManager().setPlayingMap(map);
			
			List<Player> teleported = new NotDuplicatedArrayList<>();
			for(GamePlayer agp : api.getPlayerManager().getOnlinePlayers()) {
				KCore.getGUIManager().clearQuickBar(agp.getPlayer());

				agp.clearInventory();

				if(agp.isWatchMode() && api.isUseWatchModeQuickBar()) {
					api.getBarManager().getWatchModeQuickBar().setTo(agp.getPlayer());
				}
				
				if(!agp.isWatchMode() && !agp.isEliminate()) {
					agp.getKPlayer().showPlayer();

					agp.getPlayer().setGameMode(GameMode.SURVIVAL);
				}

				if(teleported.contains(agp.getPlayer())) continue;

				if(api.isUseGParty() && map.isRandomTeleport()) {
					PartyPlayer pp = KGPartyAPI.getPlayerManager().getPartyPlayers().get(agp.getPlayerKey());
					if(pp != null && pp.hasParty()) {
						Location rLoc = map.getRandomLocation(false);

						pp.getParty().getPlayers().stream().map(ptp -> (Player) ptp.getPlayerKey().getPlatformPlayer()).forEach(pap -> {
							KCore.teleport(pap, rLoc);
							teleported.add(pap);
						});

						continue;
					}
				}

				KCore.teleport(agp.getPlayer(), map.isRandomTeleport() ? map.getRandomLocation(false) : map.getMapLocation());
				teleported.add(agp.getPlayer());
			}

			Bukkit.getScheduler().runTaskLater(AbilityPlugin.getInstance(), () -> {
				if(api.isUseStartItem()) {
					api.getItemManager().giveStartItemAll();
				}

				if(api.isUseRankItem()) {
					api.getItemManager().giveRankItemAll();
				}
			}, 20);

			api.playSoundToAll(Sound.EXPLODE, 1, 1);
			Core.cbc(ChatColor.DARK_GREEN, "§a게임이 시작되었습니다. (게임 참여자: " + api.getPlayerManager().getOnlineJoinedPlayers().size() + "명)");
			
			api.getGameManager().setTeleportedInMap(true);
			
			String teleportMessage = map.getName() + " §a맵으로 이동되었습니다.";
			api.getBarManager().getBossBar().setText(ChatColor.stripColor(teleportMessage));
			api.getBarManager().getBossBar().setProgress(100);
			Core.cbc(ChatColor.DARK_GREEN, teleportMessage);
			
			api.getBarManager().updateSideBarAllPlayer();
			
			try {
				Bukkit.getPluginManager().callEvent(new GameStartedEvent());
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			api.getTaskManager().runDrawAbilityTask(20, 3);
			cancel();
			return;
		} else if(count > 6) {
			api.playSoundToAll(Sound.ORB_PICKUP, 1, 1);
			Core.cbc(ChatColor.DARK_GREEN, 10 - count + "§a" + countMessage);
			api.getBarManager().getBossBar().setText(10 - count + countMessage);
			api.getBarManager().getBossBar().setProgress((float) (10 - count) / 10 * 100);
			return;
		}
		
		api.getBarManager().getBossBar().setText(startMessage);
		api.getBarManager().getBossBar().setProgress((float) (10 - count) / 10 * 100);
		
		if(count > 1) return;
		api.playSoundToAll(Sound.ITEM_PICKUP, 1, 1);
		Core.cbc(ChatColor.DARK_GREEN, "§a" + startMessage);
	}
	
}