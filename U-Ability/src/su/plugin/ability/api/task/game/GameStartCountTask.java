package su.plugin.ability.api.task.game;

import lombok.Getter;
import org.bukkit.Sound;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class GameStartCountTask extends UKRunnable { // 게임 시작 (능력 추첨 후)
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private int count;
	
	private String startMessage = "잠시 후 게임이 시작됩니다.";
	private String countMessage = "초 후 게임이 시작됩니다.";
	private String startedMessage = "게임이 시작되었습니다.";
	
	public GameStartCountTask() {
		super(AbilityPlugin.getInstance());
	}
	
	public void run() {
		count++;
		if(count > 9) {
			api.playSoundToAll(Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
			Core.cbc(ChatColor.DARK_GREEN, "§a" + startedMessage);
			api.getBarManager().getBossBar().setText(startedMessage);
			api.getBarManager().getBossBar().startTimer(5);

			api.getGameManager().setGameState(GameState.PLAYING);

			if(api.isUseStartInvincibility()) {
				api.getTaskManager().runInvincibilityTask(0, api.getStartInvincibilityCount());
			} else {
				if(api.getGameManager().isAutoMode() && api.isUseAutoTeleport()) {
					api.getTaskManager().runTeleportAllTask(20 * 3, api.getAutoTeleportCount());
				}
			}
			if(api.isUseSupply()) {
				api.getTaskManager().runSupplyTask(api.getSupplyCreateCount());
			}
			if(api.isUseLocationNotifyMessage() || api.isUseLocationNotifyFirework()) {
				api.getTaskManager().runLocationNotifyTask(api.getLocationNotifyCount());
			}

			cancel();
			return;
		} else if(count > 6) {
			api.playSoundToAll(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			Core.cbc(ChatColor.DARK_GREEN, 10 - count + "§a" + countMessage);
			api.getBarManager().getBossBar().setText(10 - count + countMessage);
			api.getBarManager().getBossBar().setProgress((float) (10 - count) / 10 * 100);
			return;
		}

		api.getBarManager().getBossBar().setText(startMessage);
		api.getBarManager().getBossBar().setProgress((float) (10 - count) / 10 * 100);

		if(count > 1) return;
		api.getGameManager().setGameState(GameState.STARTING);

		api.playSoundToAll(Sound.ENTITY_ITEM_PICKUP, 1, 1);
		Core.nbc(" ");
		Core.cbc(ChatColor.DARK_GREEN, "§a" + startMessage);
	}
	
}