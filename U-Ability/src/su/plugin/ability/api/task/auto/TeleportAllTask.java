package su.plugin.ability.api.task.auto;

import lombok.Getter;
import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class TeleportAllTask extends UKRunnable {

	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private int count, tpAllCount;
	
	public TeleportAllTask(int tpAllCount) {
		super(AbilityPlugin.getInstance());
		this.tpAllCount = tpAllCount + 1;
	}
	
	public void run() {
		count++;
		
		if(count >= tpAllCount) {
			api.getGameManager().setTeleportedAll(api.isTeleportToMapOnManyPlayer() ? api.getPlayerManager().getOnlineJoinedPlayers().size() >= api.getMapTeleportPlayerCount() : true);
			for(Player ap : KCore.getOnlinePlayers()) {
				KCore.teleport(ap, api.getGameManager().isTeleportedAll() ? api.getMapManager().getPlayingMap().getTPAllLocation() : api.getMapManager().getPlayingMap().getMapLocation());
				for(GamePlayer asp : api.getPlayerManager().getOnlineJoinedPlayers()) {
					ap.showPlayer(asp.getPlayer());
				}
			}
			api.getBarManager().getBossBar().setText("모두 텔레포트되었습니다.");
			api.getBarManager().getBossBar().startTimer(5);
			Core.cbc(ChatColor.DARK_GREEN, "§a모두 텔레포트되었습니다.");
			
			if(api.isUseAutoTeleportRepeat()) {
				api.getTaskManager().runTeleportAllTask(20 * 3, api.getAutoTeleportRepeatCount());
			}
			return;
		}
		
		api.getBarManager().updateSideBarAllPlayer();
		
		api.getBarManager().getBossBar().setText(StringUtil.buildTimeString(getRemainingCount()  * 1000) + " 후 모두 텔레포트됩니다.");
		api.getBarManager().getBossBar().setProgress((float) 100 - (float) (count - 1) / (float) (tpAllCount - 1) * 100);
		if(getCount() == 1 || (getRemainingCount() <= 10 && count < tpAllCount)) {
			Core.cbc(ChatColor.DARK_GREEN, StringUtil.buildTimeString(getRemainingCount()  * 1000) + " §a후 모두 텔레포트됩니다.");
		}
	}
	
	public int getRemainingCount() {
		return tpAllCount - count;
	}
	
}